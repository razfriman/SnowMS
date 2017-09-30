/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.provider.wz;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.RandomAccessByteStream;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This is a rather straightforward port from Maplext xentax.com/uploads/author/mrmouse/Maplext.zip unfortunately I do
 * not know who the original author is. In any case: Thanks, your rock.
 */
public class WZFile implements MapleDataProvider {
	static {
		ListWZFile.init();
	}
	
	private File wzfile;
	private SeekableLittleEndianAccessor slea;
	private int version;
	private Logger log = LoggerFactory.getLogger(WZFile.class);
	private int headerSize;
	private WZDirectoryEntry root;
	private boolean provideImages;
	private boolean provideMusic;
	private byte[] keyIv;
	private WZTool wzTool;

	public WZFile(File wzfile, boolean provideImages, boolean provideMusic, byte[] keyIv) throws IOException {
	    this(wzfile, provideImages, provideMusic, keyIv, -1);
	}
	
	public WZFile(File wzfile, boolean provideImages, boolean provideMusic, byte[] keyIv, int version) throws IOException {
		this.wzfile = wzfile;
		RandomAccessFile raf = new RandomAccessFile(wzfile, "r");
		slea = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));
		root = new WZDirectoryEntry(wzfile.getName(), 0, 0, 0, null);
		this.provideImages = provideImages;
		this.provideMusic = provideMusic;
		this.keyIv = keyIv;
		//load(version);
		load(-1);
	}

	@SuppressWarnings("unused")
	private void load(int realVersion) throws IOException {
		String sPKG = slea.readAsciiString(4);//PKG1
		long size = slea.readLong();//File size = size + offset
		headerSize = slea.readInt();
		if(slea.getBytesRead() + slea.available() != size + headerSize) {
		    throw new RuntimeException("Irregular File Format - Size does not match - " + wzfile.getName());
		}
		String copyright = slea.readNullTerminatedAsciiString();

		wzTool = new WZTool(version, keyIv);
		int encVersion = slea.readShort();
		if (realVersion == -1) {
		wzTool.setVersionAndHash(encVersion);    
		} else {
		wzTool.setOffsetKey(wzTool.getVersionOffsetKey());
		}

		
		this.version = wzTool.getMapleVersion();
		parseDirectory(root);
	}

	private void parseDirectory(WZDirectoryEntry dir) {
		int entries = WZTool.readValue(slea);
		for (int i = 0; i < entries; i++) {
			byte marker = slea.readByte();

			String name = null;
			int size, checksum, offset;

			switch (marker) {
				case 2://File w/Offset
					name = wzTool.readDecodedStringAtOffsetAndReset(slea, slea.readInt() + this.headerSize + 1, false);
					size = WZTool.readValue(slea);
					checksum = WZTool.readValue(slea);
					offset = wzTool.decryptOffset((int) slea.getPosition(), slea.readInt());
					dir.addFile(new WZFileEntry(name, size, checksum, offset, dir));
					break;
				case 3://Directory
				case 4://File
					name = wzTool.readDecodedString(slea);
					size = WZTool.readValue(slea);
					checksum = WZTool.readValue(slea);//Checksum-32Bit
					offset = wzTool.decryptOffset((int) slea.getPosition(), slea.readInt());
					if (marker == 3) {
						dir.addDirectory(new WZDirectoryEntry(name, size, checksum, offset, dir));
					} else {
						dir.addFile(new WZFileEntry(name, size, checksum, offset, dir));
					}
					break;
				default:
					log.error("Default case in marker ({}) - {} :/", marker, slea.getPosition() + " PARENT_LEN: " + entries + " CUR_ENTRY: " + i);
			}
		}

		for (MapleDataDirectoryEntry idir : dir.getSubdirectories()) {
			parseDirectory((WZDirectoryEntry) idir);
		}
	}

	public WZIMGFile getImgFile(String path) throws IOException {
		String segments[] = path.split("/");

		WZDirectoryEntry dir = root;
		for (int x = 0; x < segments.length - 1; x++) {
			dir = (WZDirectoryEntry) dir.getEntry(segments[x]);
			if (dir == null) {
				// throw new IllegalArgumentException("File " + path + " not found in " + root.getName());
				return null;
			}
		}

		WZFileEntry entry = (WZFileEntry) dir.getEntry(segments[segments.length - 1]);
		if (entry == null) {
			return null;
		}
		String fullPath = wzfile.getName().substring(0, wzfile.getName().length() - 3).toLowerCase() + "/" + path;
		return new WZIMGFile(this.wzfile, entry, version, provideImages, provideMusic, ListWZFile.isModernImgFile(fullPath), keyIv);
	}

	// XXX see if we can prevent locking here without keeping multiple handles :/
	public synchronized MapleData getData(String path) {
		try {
			WZIMGFile imgFile = getImgFile(path);
			if (imgFile == null) {
				// throw new IllegalArgumentException("File " + path + " not found in " + root.getName());
				return null;
			}
			MapleData ret = imgFile.getRoot();
			return ret;
		} catch (IOException e) {
			log.error("THROW", e);
		}
		return null;
	}

	public MapleDataDirectoryEntry getRoot() {
		return root;
	}

}
