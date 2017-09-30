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

package net.sf.odinms.exttools.wzextract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.provider.wz.WZTool;
import net.sf.odinms.tools.PropertyTool;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class XMLWzRepack {

	private boolean packPNG;
	private boolean packMP3;
	private int version = -1;
	private boolean hadError = false;
	private List<byte[]> imgFilesData = new ArrayList<byte[]>();
	private Map<String, byte[]> imgFilesDataMap = new HashMap<String, byte[]>();
	private Map<String, Integer> dataCache = new HashMap<String, Integer>();
	private int offset = 0;
	private WZTool wzTool;
	private WZIMGTool wzImgTool;

	public XMLWzRepack(int version, boolean packPNG, boolean packMP3) throws Exception {
		this.version = version;
		this.wzTool = new WZTool(version);
		this.wzImgTool = new WZIMGTool(wzTool);
		this.packPNG = packPNG;
		this.packMP3 = packMP3;
	}

	public void repackWZ(File wzFile, File outputBaseDirectory) {

		outputBaseDirectory.mkdir();
		dataCache.clear();
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		System.out.println("REPACKING WZFILE -> " + wzFile.getName());

		//WZ File Header
		mplew.writeAsciiString("PKG1");
		mplew.writeLong(-1);
		mplew.writeInt(60);
		mplew.writeNullTerminatedAsciiString("Package file v1.0 Copyright 2002 Wizet, ZMS");

		mplew.writeShort(0xEC);//Encrypted Version

		MapleDataProvider dataProv = MapleDataProviderFactory.getWzFile(wzFile.getName(), packPNG, packMP3);
		
		FileOutputStream fs;
		File outputFile;
		try {
			outputFile = new File(outputBaseDirectory, wzFile.getName());
			outputFile.createNewFile();
			fs = new FileOutputStream(outputFile, false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		List<MapleDataFileEntry> childFiles = dataProv.getRoot().getFiles();
		//List<MapleDataDirectoryEntry> subDirectories = dataProv.getRoot().getSubdirectories();
		List<MapleDataEntry> children = new ArrayList<MapleDataEntry>();
		children.addAll(childFiles);
		//children.addAll(subDirectories);
		WZTool.writeValue(mplew, children.size());

		LittleEndianWriter tempLew = new MaplePacketLittleEndianWriter();
		tempLew.write(mplew.toByteArray());

		for (MapleDataEntry childEntry : children) {
			if (childEntry instanceof MapleDataFileEntry) {
				MapleData root = dataProv.getData(childEntry.getName());
				String fullPath = MapleDataTool.getFullDataPath(root);
				imgFilesDataMap.put(fullPath, wzImgTool.getImgFileData(dataProv.getData(childEntry.getName())));
			} else {
				throw new UnsupportedOperationException("NOT SUPPORTING FOLDER YET");
				//imgFileData = getAllImgFileData(dataProv, (MapleDataDirectoryEntry) childEntry, "");
			}
			
		}

		//Preload img file data
		for (MapleDataEntry childEntry : children) {
			byte[] imgFileData = null;
			if (childEntry instanceof MapleDataFileEntry) {
				System.out.println("IMGFILE -> " + childEntry.getName());
				imgFileData = wzImgTool.getImgFileData(dataProv.getData(childEntry.getName()));
			} else {
				throw new UnsupportedOperationException("NOT SUPPORTING FOLDER YET");
				//imgFileData = getAllImgFileData(dataProv, (MapleDataDirectoryEntry) childEntry, "");
			}
			imgFilesData.add(imgFileData);
		}

		//write file header information
		int k = 0;
		for (MapleDataEntry childEntry : children) {
			byte[] imgFileData = null;
			if (childEntry instanceof MapleDataFileEntry) {
				imgFileData = imgFilesData.get(k);
			} else {
				throw new UnsupportedOperationException("NOT SUPPORTING FOLDER YET");
				//imgFileData = getAllImgFileData(dataProv, (MapleDataDirectoryEntry) childEntry, "");
			}
			if (childEntry instanceof MapleDataFileEntry) {
				writeStringValue(tempLew, childEntry.getName(), false, 4, 2);
			} else {
				tempLew.write(3);
				wzTool.writeEncodedString(tempLew, childEntry.getName());
			}
			tempLew.writeNullData(WZTool.getValueLength(imgFileData.length));
			tempLew.writeNullData(WZTool.getValueLength(WZTool.createChecksum32(imgFileData)));
			tempLew.writeInt(0);
			k++;
		}

		dataCache.clear();
		offset = tempLew.getSize();//Starting offset of img file data
		int j = 0;
		for (MapleDataEntry childEntry : children) {
			if (childEntry instanceof MapleDataFileEntry) {
				System.out.println("IMGFILE -> " + childEntry.getName());
			} else {
				System.out.println("DIRECTORY -> " + childEntry.getName());
			}
			
			byte[] imgFileData = null;
			if (childEntry instanceof MapleDataFileEntry) {
				imgFileData = imgFilesData.get(j);
			} else {
				throw new UnsupportedOperationException("NOT SUPPORTING FOLDER YET");
				//imgFileData = getAllImgFileData(dataProv, (MapleDataDirectoryEntry) childEntry, "");
			}
			if (childEntry instanceof MapleDataFileEntry) {
				writeStringValue(mplew, childEntry.getName(), false, 4, 2);
			} else {
				mplew.write(3);
				wzTool.writeEncodedString(mplew, childEntry.getName());
			}
			WZTool.writeValue(mplew, imgFileData.length);
			WZTool.writeValue(mplew, WZTool.createChecksum32(imgFileData));
			mplew.writeInt(wzTool.encryptOffset(mplew.getSize(), offset));
			offset += imgFileData.length;
			j++;
		}

		byte[] tempBuff = mplew.toByteArray();
		mplew.clear();
		mplew.writeLong(tempBuff.length - 60 + getArraySize(imgFilesData));
		System.arraycopy(mplew.toByteArray(), 0, tempBuff, 4, 8);//Add file size back in

		try {
			fs.write(tempBuff);
			for (int i = 0; i < imgFilesData.size(); i++) {
				fs.write(imgFilesData.get(i));
			}
			fs.flush();
			System.out.println("SIZE -> " + dataProv.getRoot().getName() + " -> " + outputFile.length());
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public boolean isHadError() {
		return hadError;
	}

	public void writeStringValue(LittleEndianWriter lew, String s) {
		writeStringValue(lew, s, false, 0, 1);
	}

	public void writeStringValue(LittleEndianWriter lew, String s, boolean unicode) {
		writeStringValue(lew, s, unicode, 0, 1);
	}

	public void writeStringValue(LittleEndianWriter lew, String s, boolean unicode, int withoutOffset, int withOffset) {
		if (s.length() > 4 && dataCache.containsKey(s)) {
			lew.write(withOffset);
			lew.writeInt(dataCache.get(s));
		} else {
			lew.write(withoutOffset);
			int sOffset = lew.getSize();
			wzTool.writeEncodedString(lew, s, unicode);
			if (!dataCache.containsKey(s)) {
				dataCache.put(s, sOffset);
			}
		}
	}

	public boolean isUnicode(String s) {
		for (char chr : s.toCharArray()) {
			if (chr > 127) {
				return true;
			}
		}
		return false;
	}

	public int getArraySize(List<byte[]> data) {
		int ret = 0;
		for (byte[] b : data) {
			ret += b.length;
		}
		return ret;
	}

	public static void main(String args[]) {
		boolean packPng = false;
		boolean packMp3 = false;
		boolean hadError = false;
		String outputDir = null;
		String inputDir = null;
		int version = -1;
		Properties settings = new Properties();
		long startTime = System.currentTimeMillis();

		try {//Load Settings
			settings.load(new FileInputStream(new File("settings.properties")));
		} catch (Exception e) {
			System.out.println("Unable to find settings.properties");
			return;
		}
		PropertyTool propTool = new PropertyTool(settings);

		version = propTool.getSettingInt("VERSION", -1);
		packPng = propTool.getSettingInt("PACK_PNG", 0) > 0;
		packMp3 = propTool.getSettingInt("PACK_MP3", 0) > 0;
		outputDir = propTool.getSettingStr("OUTPUT_PATH", "wzout");
		inputDir = propTool.getSettingStr("INPUT_PATH", "xmlin");
		System.setProperty("net.sf.odinms.wzpath", new File(inputDir).getAbsolutePath());
		String[] files = propTool.getSettingStr("FILES", "").split(" ");
		for (String file : files) {
			try {
				XMLWzRepack wzRepack = new XMLWzRepack(version, packPng, packMp3);
				wzRepack.repackWZ(new File(inputDir, file), new File(outputDir));
				hadError |= wzRepack.isHadError();
			} catch (Exception e) {
				hadError = true;
				System.out.println("Exception occured while dumping " + file + " continuing with next file");
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		double elapsedSeconds = (endTime - startTime) / 1000.0;
		int elapsedSecs = (((int) elapsedSeconds) % 60);
		int elapsedMinutes = (int) (elapsedSeconds / 60.0);

		String withErrors = "";
		if (hadError) {
			withErrors = " with errors";
		}
		System.out.println("Finished" + withErrors + " in " + elapsedMinutes + " minutes " + elapsedSecs + " seconds");
	}
}
