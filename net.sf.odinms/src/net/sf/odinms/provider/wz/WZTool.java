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

import java.io.ByteArrayOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import net.sf.odinms.tools.BitTools;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class WZTool {

	public final static byte[] MP3_MAGIC = HexTool.getByteArrayFromHexString("02 83 EB 36 E4 4F 52 CE 11 9F 53 00 20 AF 0B A7 70 8B EB 36 E4 4F 52 CE 11 9F 53 00 20 AF 0B A7 70 00 01 81 9F 58 05 56 C3 CE 11 BF 01 00 AA 00 55 59 5A");
	public final static boolean removeNonPrintables = true;
	public final static byte[] GMS_IV = HexTool.getByteArrayFromHexString("4D 23 C7 2B");
	public final static byte[] MSEA_IV = HexTool.getByteArrayFromHexString("B9 7D 63 E9");
	private int mapleVersion;

	private int[] mapleVersions;
	private int[] hashVersions;
	
	private byte[] ivKey;
	private int offsetKey;
	private byte[] WZ_KEY;

	/**
	 * Creates a new Instance of WZTool
	 * assumes it will decrypt GlobalMS
	 * @param mapleVersion MapleStory Version decrypting
	 */
	public WZTool(int mapleVersion) {
		this(mapleVersion, GMS_IV);
	}

	public WZTool(int mapleVersion, byte[] ivKey) {
		this.mapleVersion = mapleVersion;
		this.offsetKey = getVersionOffsetKey();
		this.ivKey = ivKey != null ? ivKey : GMS_IV;
		initKeys();
	}

	public void initKeys() {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec skeySpec = new SecretKeySpec(MapleAESOFB.MAPLE_AES_KEY, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] iv = BitTools.multiplyBytes(ivKey, 4, 4);
			for (int i = 0; i < 65536; i += 16) {
				iv = cipher.doFinal(iv, 0, 16);
				baos.write(iv, 0, 16);
			}
			WZ_KEY = baos.toByteArray();
		} catch (Exception e) {
			System.out.println("Error: Generating Keys");
			e.printStackTrace();
		}
	}

	public void setVersionAndHash(int encVer) {
		int[] ver = new int[5];
		int[] h_ver = new int[5];
		int start_ver = -1;
		for (int i = 0; i < 5; i++) {
			ver[i] = findVersion(encVer, start_ver);
			h_ver[i] = findVersionHash(encVer, ver[i]);
			start_ver = ver[i];
		}

		this.mapleVersions = ver;
		this.hashVersions = h_ver;
		this.offsetKey = h_ver[0];
		this.mapleVersion = ver[0];
		if (ver[0] >= 70) {
			//this.offsetKey = h_ver[1];
			//this.mapleVersion = ver[1];
		}
	}

	public void setOffsetKey(int offsetKey) {
	    this.offsetKey = offsetKey;
	}

	public int findVersion(int encver, int start_version) {
		int sum;
		String versionStr;
		int a = 0, b = 0, c = 0, d = 0, e = 0, i = start_version, l = 0;
		do {
			i++;
			sum = 0;
			versionStr = Integer.toString(i);
			l = versionStr.length();
			for (int j = 0; j < l; j++) {
				sum <<= 5;
				sum += (int) versionStr.charAt(j) + 1;
			}
			a = (sum >> 24) & 0xFF;
			b = (sum >> 16) & 0xFF;
			c = (sum >> 8) & 0xFF;
			d = sum & 0xFF;
			e = 0xFF ^ a ^ b ^ c ^ d;
		} while (e != encver);

		return i;
	}

	public int findVersionHash(int encver, int realver) {
		int EncryptedVersionNumber = encver;
		int VersionNumber = realver;
		int VersionHash = 0;
		int DecryptedVersionNumber = 0;
		String VersionNumberStr;
		int a = 0, b = 0, c = 0, d = 0, l = 0;

		VersionNumberStr = Integer.toString(VersionNumber);

		l = VersionNumberStr.length();
		for (int i = 0; i < l; i++) {
			VersionHash = (32 * VersionHash) + (int) VersionNumberStr.charAt(i) + 1;
		}
		a = (VersionHash >> 24) & 0xFF;
		b = (VersionHash >> 16) & 0xFF;
		c = (VersionHash >> 8) & 0xFF;
		d = VersionHash & 0xFF;
		DecryptedVersionNumber = 0xff ^ a ^ b ^ c ^ d;

		if (EncryptedVersionNumber == DecryptedVersionNumber) {
			return VersionHash;
		} else {
			return 0;
		}
	}

	public void writeEncodedString(LittleEndianWriter leo, String s) {
		writeEncodedString(leo, s, false);
	}

	public void writeEncodedString(LittleEndianWriter leo, String s, boolean unicode) {
		if (s.equals("")) {
			leo.write(0);
			return;
		}


		if (unicode) {
			//UNI-CODE
			if (s.length() <= 127) {
				leo.write(s.length());
			} else {
				leo.write(0x7F);
				leo.writeInt(s.length());
			}
		} else {
			//NON UNI-CODE
			if (s.length() <= 127) {
				leo.write(-s.length());
			} else {
				leo.write(-128);
				leo.writeInt(s.length());
			}
		}
		encodeStringData(leo, s, unicode);
	}

	public String readDecodedString(LittleEndianAccessor lea) {
		int strLength;
		byte b = lea.readByte();
		String ret = "";

		if (b == 0) {
			return ret;
		} else if (b > 0) {
			//UNI-CODE
			if (b == 0x7F) {
				strLength = lea.readInt();
			} else {
				strLength = (int) b;
			}
			byte[] decrypted = decrypt(lea.read(strLength * 2));
			ret = transStr16KMST(decrypted);

		} else {
			//NON UNI-CODE
			if (b == -128) {
				strLength = lea.readInt();
			} else {
				strLength = (int) -b;
			}
			byte mask = (byte) 0xAA;
			for (int i = 0; i < strLength; i++) {
				byte charByte = lea.readByte();
				charByte ^= mask;
				if (mapleVersion > 55) {
					charByte ^= WZ_KEY[i];
				}
				ret += (char) charByte;
				mask++;
			}
		}

		assert strLength >= 0 : "can never be < 0 (?)";

		//return decodeStringData(lea, strLength, unicode);
		return ret;
	}

	public String readDecodedStringAtOffset(SeekableLittleEndianAccessor slea, int offset) {
		slea.seek(offset);
		return readDecodedString(slea);
	}

	public String readDecodedStringAtOffsetAndReset(SeekableLittleEndianAccessor slea, int offset, boolean modernImg) {
		long pos = slea.getPosition();
		slea.seek(offset);
		String ret = readDecodedString(slea);
		slea.seek(pos);
		return ret;
	}

	public void encodeStringData(LittleEndianWriter lew, String s, boolean unicode) {
		if (unicode) {
			short p = (short) 0xAAAA;
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

			for (int i = 0; i < s.length(); i++) {
				mplew.writeShort(s.charAt(i) ^ p);
				p++;
			}
			byte[] data = mplew.getPacket().getBytes();
			if (mapleVersion > 55) {
				data = decrypt(data);
			}
			lew.write(data);
		} else {
			byte mask = (byte) 0xAA;
			for (int i = 0; i < s.length(); i++) {
				byte b2 = (byte) s.charAt(i);
				b2 ^= mask;
				if (mapleVersion > 55) {
					b2 ^= WZ_KEY[i];
				}
				lew.write(b2);
				mask++;
			}
		}
	}

	public String decodeStringData(LittleEndianAccessor lea, int length, boolean unicode) {
		char[] str = new char[length];
		if (unicode) {
			short mask = (short) 0xAAAA;
			for (int i = 0; i < length; i++) {
				char chr = lea.readChar();
				chr ^= mask;
				if (mapleVersion > 55) {
					chr ^= WZ_KEY[i];
				}
				if (removeNonPrintables) {
					chr = ((char) Math.max(chr, 32));
				}
				str[i] = chr;
				mask++;
			}
		} else {
			byte mask = (byte) 0xAA;
			for (int i = 0; i < length; i++) {
				byte b = lea.readByte();
				b ^= mask;
				if (mapleVersion > 55) {
					b ^= WZ_KEY[i];
				}
				if (removeNonPrintables) {
					b = ((byte) Math.max(b, 32));
				}
				str[i] = (char) (b & 0xFF);
				mask++;
			}
		}
		return String.valueOf(str);
	}

	public String transStr16KMST(byte[] input) {//Unicode Encryption
		short p = (short) 0xAAAA;
		byte pASCII = (byte) 0xAA;
		int length = input.length / 2;
		String s = "";
		for (int i = 0; i < length; i++) {
			int firstB = input[i * 2];
			int secondB = input[(i * 2) + 1];
			if (secondB == 0xAA) {
				int asciiChar = (firstB ^ pASCII) & 0xFF;
				s += asciiChar < 0x7F ? (char) asciiChar : "&#" + Integer.toString(asciiChar) + ";";
			} else {
				int unicodeChar = (BitTools.getShort(input, i * 2) ^ p) & 0x0000FFFF;//Should be a short
				s += "&#" + Integer.toString(unicodeChar) + ";";
			}
			p++;
			pASCII++;
		}
		return s;
	}

	public byte[] decrypt(byte[] input) {
		byte[] ret = new byte[input.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (input[i] ^ WZ_KEY[i]);
		}
		return ret;
	}

	public static int readValue(LittleEndianAccessor lea) {
		byte b = lea.readByte();
		if (b == -128) {
			return lea.readInt();
		} else {
			return ((int) b);
		}
	}

	public static void writeValue(LittleEndianWriter lew, int val) {
		if (val < 128) {
			lew.write(val);
		} else {
			lew.write(-128);
			lew.writeInt(val);
		}
	}

	public static int getValueLength(int val) {
		return val < 128 ? 1 : 5;
	}

	public static float readFloatValue(LittleEndianAccessor lea) {
		byte b = lea.readByte();
		if (b == -128) {
			return lea.readFloat();
		} else {
			return 0;
		}
	}

	public static void writeFloatValue(LittleEndianWriter lew, float val) {
		if (val != 0) {
			lew.write(-128);
			lew.writeInt(Float.floatToIntBits(val));
		} else {
			lew.write(0);
		}
	}

	public static int getDecryptedVersion(int encryptedVer) {
		int sum;
		char[] versionChar;
		int a, b, c, d, e = 0;
		int i = -1;//startver

		do {
			i++;
			sum = 0;
			versionChar = Integer.toString(i).toCharArray();
			for (int j = 0; j < versionChar.length; j++) {
				sum <<= 5;
				sum += versionChar[j] + 1;
			}
			a = (sum >> 24) & 0xFF;
			b = (sum >> 16) & 0xFF;
			c = (sum >> 8) & 0xFF;
			d = sum & 0xFF;
			e = 0xFF ^ a ^ b ^ c ^ d;
		} while (encryptedVer != e);

		return i;
	}

	public int getEncryptedVersion(int decryptedVer) {
		return -1;
	}

	public int getMapleVersion() {
		return mapleVersion;
	}

	public int decryptOffset(int pos, int offsetE) {
		pos -= 0x3C;
		pos ^= 0xFFFFFFFF;
		pos *= offsetKey;
		pos -= 0x581C3F6D;//1478246253
		pos = Integer.rotateLeft(pos, pos & 0x1F) & 0xFFFFFFFF;
		pos ^= offsetE;
		pos += 2 * 0x3C;
		return pos;
	}

	public int encryptOffset(int currentPos, int dataPos) {
		currentPos -= 0x3C;
		currentPos ^= 0xFFFFFFFF;
		currentPos = (currentPos * offsetKey) & 0xFFFFFFFF;
		currentPos -= 0x581C3F6D;//1478246253
		currentPos = Integer.rotateLeft(currentPos, currentPos & 0x1F) & 0xFFFFFFFF;
		currentPos = (currentPos ^ (dataPos - (2 * 0x3C))) & 0xFFFFFFFF;
		return currentPos;
	}

	public int getVersionOffsetKey() {
		if (mapleVersion < 1) {
			return -1;
		}
		String versionStr = Integer.toString(mapleVersion);
		int hash = 0;
		for (char chr : versionStr.toCharArray()) {
			hash = (32 * hash) + chr + 1;
		}
		return hash;
	}

	public static int createChecksum32(byte[] data) {
		int sum = 0;
		for (byte b : data) {
			sum += b & 0xFF;
		}
		return sum;
	}
}
