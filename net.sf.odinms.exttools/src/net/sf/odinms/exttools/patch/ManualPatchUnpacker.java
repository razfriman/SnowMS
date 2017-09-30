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

package net.sf.odinms.exttools.patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.PropertyTool;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class ManualPatchUnpacker {

	//at 0x004084C of patcher.exe
	public static final byte[] PATCHER_KEY = HexTool.getByteArrayFromHexString("00000000B71DC1046E3B8209D926430DDC7604136B6BC517B24D861A0550471EB8ED08260FF0C922D6D68A2F61CB4B2B649B0C35D386CD310AA08E3CBDBD4F3870DB114CC7C6D0481EE09345A9FD5241ACAD155F1BB0D45BC2969756758B5652C836196A7F2BD86EA60D9B6311105A6714401D79A35DDC7D7A7B9F70CD665E74E0B6239857ABE29C8E8DA191399060953CC0278B8BDDE68F52FBA582E5E66486585B2BBEEF46EABA3660A9B7817D68B3842D2FAD3330EEA9EA16ADA45D0B6CA0906D32D42770F3D0FE56B0DD494B71D94C1B36C7FB06F7C32220B4CE953D75CA28803AF29F9DFBF646BBB8FBF1A679FFF4F63EE143EBFFE59ACDBCE82DD07DEC77708634C06D4730194B043DAE56C539AB0682271C1B4323C53D002E7220C12ACF9D8E1278804F16A1A60C1B16BBCD1F13EB8A01A4F64B057DD00808CACDC90C07AB9778B0B6567C69901571DE8DD475DBDD936B6CC0526FB5E6116202FBD066BF469F5E085B5E5AD17D1D576660DC5363309B4DD42D5A490D0B1944BA16D84097C6A5AC20DB64A8F9FD27A54EE0E6A14BB0A1BFFCAD60BB258B23B69296E2B22F2BAD8A98366C8E41102F83F60DEE87F35DA9994440689D9D662B902A7BEA94E71DB4E0500075E4892636E93E3BF7ED3B6BB0F38C7671F7555032FAE24DF3FE5FF0BCC6E8ED7DC231CB3ECF86D6FFCB8386B8D5349B79D1EDBD3ADC5AA0FBD8EEE00C6959FDCD6D80DB8E6037C64F643296087A858BC97E5CAD8A73EBB04B77560D044FE110C54B383686468F2B47428A7B005C3D66C158E4408255535D43519E3B1D252926DC21F0009F2C471D5E28424D1936F550D8322C769B3F9B6B5A3B26D6150391CBD40748ED970AFFF0560EFAA011104DBDD014949B93192386521D0E562FF1B94BEEF5606DADF8D7706CFCD2202BE2653DEAE6BC1BA9EB0B0668EFB6BB27D701A6E6D3D880A5DE6F9D64DA6ACD23C4DDD0E2C004F6A1CDB3EB60C97E8D3EBDC990FFB910B6BCB4A7AB7DB0A2FB3AAE15E6FBAACCC0B8A77BDD79A3C660369B717DF79FA85BB4921F4675961A163288AD0BF38C742DB081C330718599908A5D2E8D4B59F7AB085440B6C95045E68E4EF2FB4F4A2BDD0C479CC0CD43217D827B9660437F4F460072F85BC176FD0B86684A16476C93300461242DC565E94B9B115E565A1587701918306DD81C353D9F0282205E065B061D0BEC1BDC0F51A69337E6BB52333F9D113E8880D03A8DD097243ACD5620E3EB152D54F6D4297926A9C5CE3B68C1171D2BCCA000EAC8A550ADD6124D6CD2CB6B2FDF7C76EEDBC1CBA1E376D660E7AFF023EA18EDE2EE1DBDA5F0AAA064F4738627F9C49BE6FD09FDB889BEE0798D67C63A80D0DBFB84D58BBC9A62967D9EBBB03E930CADFF97B110B0AF060D71ABDF2B32A66836F3A26D66B4BCDA7B75B8035D36B5B440F7B1");
	private int oldVer;
	private int newVer;
	private String oldVerStr;
	private String newVerStr;
	private File patch;
	private File patchDataFile;
	private FileOutputStream patchDataOutputStream;
	private boolean unpackPatch;
	private boolean extractPatch;
	private byte[] patchDataBuffer;
	private Inflater inflater;
	//private SeekableLittleEndianAccessor slea;

	public ManualPatchUnpacker(int oldVer, int newVer, boolean unpackPatch, boolean extractPatch) {
		this.oldVer = oldVer;
		this.newVer = newVer;
		this.unpackPatch = unpackPatch;
		this.extractPatch = extractPatch;
	}

	public boolean loadVariables() {
		try {
			oldVerStr = StringUtil.getLeftPaddedStr(Integer.toString(oldVer), '0', 5);
			newVerStr = StringUtil.getLeftPaddedStr(Integer.toString(newVer), '0', 5);
			patch = new File(oldVerStr + "to" + newVerStr + ".patch");
			patchDataFile = new File(oldVerStr + "to" + newVerStr + "_UNPACKED.dat");
			patchDataFile.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(patchDataFile, "r");
			patchDataBuffer = new byte[(int) raf.length()];
			raf.readFully(patchDataBuffer);
			raf.close();
			inflater = new Inflater();
			return true;
		} catch (Exception e) {
			System.out.println("Error: Loading Variables");
			e.printStackTrace();
			return false;
		}
	}

	public boolean unpack() {
		try {
			patchDataOutputStream = new FileOutputStream(patchDataFile, false);
			RandomAccessFile raf = new RandomAccessFile(patch, "r");
			patchDataBuffer = new byte[(int) raf.length() - 16];
			raf.skipBytes(12);
			System.out.println("CHECKSUM = " + raf.readInt());
			raf.readFully(patchDataBuffer);
			inflater.setInput(patchDataBuffer);
			byte[] tempBuffer = new byte[(int) raf.length() * 2];
			int len = inflater.inflate(tempBuffer);
			inflater.end();
			patchDataOutputStream.write(tempBuffer, 0, len);
			patchDataBuffer = new byte[len];
			System.arraycopy(tempBuffer, 0, patchDataBuffer, 0, len);
			patchDataOutputStream.close();
			return true;
		} catch (DataFormatException ex) {
			throw new RuntimeException("ZLib fucked up", ex);
		} catch (Exception e) {
			System.out.println("Error: Unpacking Patch Data");
			e.printStackTrace();
			return false;
		}
	}

	public boolean extract(GenericSeekableLittleEndianAccessor slea) {
		if (patchDataFile.length() == 0) {
			throw new RuntimeException("Invalid Patch Data File");
		}

		String fileName = "";
		while (slea.available() > 0) {
			byte b = slea.readByte();
			switch (b) {
				case 0:
					parseType00(slea, fileName);
					fileName = "";
					break;
				case 1:
					parseType01(slea, fileName);
					fileName = "";
					break;
				case 2:
					parseType02(slea, fileName);
					fileName = "";
					break;
				default:
					fileName += (char) b;
			}
		}
		return true;
	}

	public void parseType00(SeekableLittleEndianAccessor slea, String fileName) {
		//OVERWRITE A FILE
		//CREATE A NEW DIRECTORY
		System.out.println("00 - NEW FILE/DIRECTORY - " + fileName);
		int len = slea.readInt();
		int checksum = slea.readInt();
		slea.read(len);//Data
	}

	public void parseType01(SeekableLittleEndianAccessor slea, String fileName) {
		//REBUILD ENTIRE FILE
		System.out.println("01 - REBUILD - " + fileName);
		int checksumOldFile = slea.readInt();
		int checksumNewFile = slea.readInt();
		while (true) {
			int command = slea.readInt();
			int interpretCommand = command >>> 0x18;
			if (interpretCommand == 0x80) {//WRITE
				int len = command & 0x07FFFFFF;
				slea.read(len);
			} else if (interpretCommand == 0xC0) {//INSERT MULTIPLE BYTES
				int len = (command >> 8) & 0x3FFFFF;
				int value = command & 0xFF;
			} else if (command != 0) {//READ
				int len = command;
				int baseOffset = slea.readInt();
			} else {
				break;
			}
		//compareChecksums
		}
	}

	public void parseType02(SeekableLittleEndianAccessor slea, String fileName) {
		//DELETE FILE
		System.out.println("02 - DELETE - " + fileName);
	}

	public int calculateChecksum(SeekableLittleEndianAccessor slea, int len) {
		/*long oldPos = slea.getPosition();
		int pos = 0;
		int blockSize = 0x10000;
		int checksum = 0;
		while (pos != len) {
		if (pos + blockSize > len) {
		blockSize = len - pos;
		}
		pos += blockSize;
		byte[] data = slea.read(blockSize);
		for (byte b : data) {
		int index = (checksum >> 0x18) ^ b;
		checksum = (checksum << 0x08) ^ PATCHER_KEY[index];
		//checksum &= 0xFFFFFFFF;
		}
		}
		slea.seek(oldPos);
		return checksum;*/
		return -1;
	}

	public static void main(String args[]) {
		Properties settings = new Properties();
		try {
			FileInputStream inputStream = new FileInputStream("settings.properties");
			settings.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			System.out.println("Cannot Find File - settings.properties");
			return;
		}
		PropertyTool propTool = new PropertyTool(settings);
		int oldVer = propTool.getSettingInt("OLD_VER", -1);
		int newVer = propTool.getSettingInt("NEW_VER", -1);
		boolean unpackPatch = propTool.getSettingInt("UNPACK_PATCH", 1) > 0;
		boolean extractPatch = propTool.getSettingInt("EXTRACT_PATCH", 1) > 0;
		ManualPatchUnpacker patch = new ManualPatchUnpacker(oldVer, newVer, unpackPatch, extractPatch);

		if (patch.loadVariables()) {
			System.out.println("Loaded Variables");
		} else {
			return;
		}

		if (unpackPatch) {
			if (patch.unpack()) {
				System.out.println("Patch Data Unpacked");
			}
		}

		if (extractPatch) {
			if (patch.extract(new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(patch.patchDataBuffer)))) {
				System.out.println("Patch Data Extracted");
			}
		}
	}
}
