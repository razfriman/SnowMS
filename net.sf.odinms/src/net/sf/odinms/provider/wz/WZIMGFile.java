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

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import net.sf.odinms.provider.MapleEmptyDataEntity;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.RandomAccessByteStream;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Ported Code, see WZFile.java for more info
 */
public class WZIMGFile {
	private Logger log = LoggerFactory.getLogger(WZIMGFile.class);
	private WZFileEntry file;
	private WZIMGEntry root;
	private boolean provideImages;
	private boolean provideMusic;
	private boolean modernImg;
	private WZTool wzTool;

	public WZIMGFile(File wzFile, WZFileEntry file, int version, boolean provideImages, boolean provideMusic, boolean modernImg, byte[] keyIv) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(wzFile, "r");
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));
		slea.seek(file.getOffset());
		this.file = file;
		this.provideImages = provideImages;
		this.provideMusic = provideMusic;
		root = new WZIMGEntry(file.getParent());
		root.setName(file.getName());
		root.setType(MapleDataType.EXTENDED);
		wzTool = new WZTool(version, keyIv);
		this.modernImg = modernImg;
		parseExtended(root, slea, 0);
		root.finish();
		raf.close();
	}

	public WZIMGFile(File wzFile, int version, boolean provideImages, boolean provideMusic, boolean modernImg, byte[] keyIv) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(wzFile, "r");
		SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new RandomAccessByteStream(raf));

		this.provideImages = provideImages;
		this.provideMusic = provideMusic;
		this.modernImg = modernImg;
		this.file = new WZFileEntry(wzFile.getName(), (int) wzFile.length(), -1, 0, new MapleEmptyDataEntity());
		root = new WZIMGEntry(new MapleEmptyDataEntity());
		root.setName(wzFile.getName());
		root.setType(MapleDataType.EXTENDED);
		wzTool = new WZTool(version, keyIv);
		parseExtended(root, slea, 0);
		root.finish();
		raf.close();
	}

	protected void dumpImg(OutputStream out, SeekableLittleEndianAccessor slea) throws IOException {
		DataOutputStream os = new DataOutputStream(out);
		long oldPos = slea.getPosition();
		slea.seek(file.getOffset());
		for (int x = 0; x < file.getSize(); x++) {
			os.write(slea.readByte());
		}
		slea.seek(oldPos);
	}

	public WZIMGEntry getRoot() {
		return root;
	}

	private void parse(WZIMGEntry entry, SeekableLittleEndianAccessor slea) {
		byte marker = slea.readByte();
		switch (marker) {
			case 0: {
				String name = wzTool.readDecodedString(slea);
				entry.setName(name);
				break;
			} case 1: {
				String name = wzTool.readDecodedStringAtOffsetAndReset(slea, file.getOffset() + slea.readInt(), modernImg);
				entry.setName(name);
				break;
			} default: {
			    throw new RuntimeException("Unknown image identifier: " + marker + " at offset " + (slea.getPosition() - file.getOffset()) + " - " + file.getName() + "(" + slea.getPosition() + ")");
			}
		}

		marker = slea.readByte();
		switch (marker) {
			case 0:
				entry.setType(MapleDataType.IMG_0x00);
				break;
			case 2:
			case 11: //??? no idea, since 0.49
				entry.setType(MapleDataType.SHORT);
				entry.setData(Short.valueOf(slea.readShort()));
				break;
			case 3:
				entry.setType(MapleDataType.INT);
				entry.setData(Integer.valueOf(WZTool.readValue(slea)));
				break;
			case 4:
				entry.setType(MapleDataType.FLOAT);
				entry.setData(Float.valueOf(WZTool.readFloatValue(slea)));
				break;
			case 5:
				entry.setType(MapleDataType.DOUBLE);
				entry.setData(Double.valueOf(slea.readDouble()));
				break;
			case 8:
				entry.setType(MapleDataType.STRING);
				byte iMarker = slea.readByte();
				if (iMarker == 0) {
					entry.setData(wzTool.readDecodedString(slea));
				} else if (iMarker == 1) {
					entry.setData(wzTool.readDecodedStringAtOffsetAndReset(slea, slea.readInt() + file.getOffset(), modernImg));
				} else {
					log.error("Unknown String type {}", iMarker);
				}
				break;
			case 9:
				entry.setType(MapleDataType.EXTENDED);
				long endOfExtendedBlock = slea.readInt();
				endOfExtendedBlock += slea.getPosition();
				parseExtended(entry, slea, endOfExtendedBlock);
				break;
			default:
				log.error("Unknown Image type {}", marker);
				
		}
	}

	private void parseExtended(WZIMGEntry entry, SeekableLittleEndianAccessor slea, long endOfExtendedBlock) {
		byte marker = slea.readByte();

		String type;
		switch (marker) {
			case 0x73:
				type = wzTool.readDecodedString(slea);
				break;
			case 0x1B:
				type = wzTool.readDecodedStringAtOffsetAndReset(slea, file.getOffset() + slea.readInt(), modernImg);
				break;
			default:
				throw new RuntimeException("Unknown extended image identifier: " + marker + " at offset " + slea.getPosition() + " fileoffset: " + file.getOffset() + "file: " + getRoot().getName());
		}
		
		/*
		 * "Shape2D#Vector2D"
		 * "Shape2D#Convex2D"
		 * "Property"
		 * "Sound_DX8"
		 * "Canvas"
		 * "UOL"
		 */
		if (type.equals("Property")) {
			entry.setType(MapleDataType.PROPERTY);
			slea.readByte();
			slea.readByte();
			int children = WZTool.readValue(slea);
			for (int i = 0; i < children; i++) {
				WZIMGEntry cEntry = new WZIMGEntry(entry);
				parse(cEntry, slea);
				cEntry.finish();
				entry.addChild(cEntry);
			}
		} else if (type.equals("Canvas")) {
			entry.setType(MapleDataType.CANVAS);
			slea.readByte();
			marker = slea.readByte();
			if (marker == 0) {
				// do nothing
			} else if (marker == 1) {
				slea.readByte();
				slea.readByte();
				int children = WZTool.readValue(slea);
				for (int i = 0; i < children; i++) {
					WZIMGEntry child = new WZIMGEntry(entry);
					parse(child, slea);
					child.finish();
					entry.addChild(child);
				}
			} else {
				log.warn("Canvas marker != 1 ({})", marker);
			}
			int width = WZTool.readValue(slea);
			int height = WZTool.readValue(slea);
			int format = WZTool.readValue(slea);
			int format2 = slea.readByte();
			slea.readInt();
			int dataLength = slea.readInt() - 1;
			slea.readByte();
			
			if (provideImages) {
				byte[] pngdata = modernImg ? getBlocks(slea, dataLength) : slea.read(dataLength);
				entry.setData(new PNGMapleCanvas(width, height, dataLength, format + format2, pngdata));
			} else {
				entry.setData(new PNGMapleCanvas(width, height, dataLength, format + format2, null));
				slea.seek(endOfExtendedBlock);
			}
		} else if (type.equals("Shape2D#Vector2D")) {
			entry.setType(MapleDataType.VECTOR);
			int x = WZTool.readValue(slea);
			int y = WZTool.readValue(slea);
			entry.setData(new Point(x, y));
		} else if (type.equals("Shape2D#Convex2D")) {
			int children = WZTool.readValue(slea);
			for (int i = 0; i < children; i++) {
				WZIMGEntry cEntry = new WZIMGEntry(entry);
				parseExtended(cEntry, slea, 0);
				cEntry.finish();
				entry.addChild(cEntry);
			}
		} else if (type.equals("Sound_DX8")) {
			entry.setType(MapleDataType.SOUND);
			slea.readByte();
			int dataLength = WZTool.readValue(slea);
			int microSeconds = WZTool.readValue(slea);// - 1 digit(12345 -> 1234)(matches dataLength ^)
			entry.setData(new ImgMapleSound(dataLength, (int) slea.getPosition() - file.getOffset(), provideMusic ? slea.read(dataLength) : null));
			slea.seek(endOfExtendedBlock);//Dont need this if everything works right :D
		} else if (type.equals("UOL")) {
			entry.setType(MapleDataType.UOL);
			slea.readByte();
			byte uolmarker = slea.readByte();
			switch (uolmarker) {
				case 0:
					entry.setData(wzTool.readDecodedString(slea));
					break;
				case 1:
					entry.setData(wzTool.readDecodedStringAtOffsetAndReset(slea, file.getOffset() + slea.readInt(), modernImg));
					break;
				default:
					log.error("Unknown UOL marker: {} {}", uolmarker, entry.getName());
			}
		} else {
			throw new RuntimeException("Unhandeled extended type: " + " MARKER=" + Integer.toString(marker, 16) + " TYPE=" + type + " FILE=" + file.getName());
		}
	}
	
    public byte[] getBlocks(SeekableLittleEndianAccessor slea, int dataLength) {
	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    long endOfImage = slea.getPosition() + dataLength;
	    while (slea.getPosition() < endOfImage) {
		int blocksize = slea.readInt();
		baos.write(wzTool.decrypt(slea.read(blocksize)));
	    }
	    return baos.toByteArray();
	} catch (Exception e) {
	    log.error("Error getting image blocks");
	    return null;
	}
    }
}
