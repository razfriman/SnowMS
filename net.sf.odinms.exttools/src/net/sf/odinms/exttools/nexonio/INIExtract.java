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
package net.sf.odinms.exttools.nexonio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

import javax.crypto.Cipher;

import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.PropertyTool;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class INIExtract {

    public static String version = "0.1";
    public static int KEY1 = 847324706;//22 26 81 32
    public static int KEY2 = 847324705;//21 26 81 32
    public static byte[] GG_KEY = HexTool.getByteArrayFromHexString("B7 99 2C 95 8C BA EB 02 86 00 F9 E9 63 1A FC 1F F9 30 86 88 D9 73 E0 90 F8 F6 3E 53 8B 84 67 40 61 29 BA 13 67 39 39 D9 09 4C D7 5E D2 27 51 35 9C FD A2 76 35 E4 A1 4D 9B A7 FB 00 41 09 DE DC 60 8C A1 EA D5 15 A6 AD 38 3B 8B B0 5F B7 CD CF E0 87 FE 41 87 C8 E4 EE EC FE 5D C0 53 A6 82 A9 44 CD 95 51 A9 72 1C 1F 14 B2 DA 95 8C 19 FA 7E 28 A0 AF DA 5E 76 A5 15 67 F7 37 5B B3 FD D1 B2 09 6E 8E 97 2C E6 22 3D 11 4F 88 53 75 19 0E FA CC 70 99 54 9A C8 AF B3 05 2F DF 27 A7 F5 0F 2A 91 45 C0 34 DD 44 6E 78 A0 A4 6D 9A F0 69 6F 77 6E D9 BE 8E 77 00 D7 32 A3 12 A6 86 FD C6 F1 5E 10 25 81 29 AD CE C4 78 EE FF C6 FC 59 C0 90 64 D0 C4 36 D6 C2 57 FA 25 3E FC FC 4B 70 CA 69 32 D4 DC 11 08 9C FA A7 A9 8E 1F 1E 8B E8 98 41 26 D4 CF 62 2B BB 51 C6 20 2A 62 F0 BD 3C BC 01 46 66 EE 2E 4E FC 14 AF 4D 1F F6 39 36 3B FB F9 89 38 7A EA 04 49 C8 83 43 F0 A1 8D A7 CA A2 CA 55 9C 01 55 57 7F E6 2D 4C DD F2 33 78 F4 F0 90 38");
    public static byte[] KEY = HexTool.getByteArrayFromHexString("06 02 00 00 00 24 00 00 52 53 41 31 00 02 00 00 01 00 01 00 FB E3 FC 09 AF AE 65 8C 96 4C C5 37 D2 A4 77 E7 4C 41 C2 CF F2 FE 2D 9C 80 94 0C 88 6D B3 84 9F 8C 22 A0 C9 CD C0 AB 30 65 82 42 3C EE 3C A8 B7 11 D6 22 FA FB 23 F7 72 CD E7 D0 6F 6A 8E 96 E3");
    private static Cipher cipher;
    
    public static void main(String args[]) {
	long startTime = System.currentTimeMillis();
	Properties settings = new Properties();
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	byte[] buffer;
	byte[] dataBuffer;
	File inputFile;
	File outputFile;
	FileOutputStream outputFileStream;
	try {
	    FileInputStream inputStream = new FileInputStream("settings.properties");
	    settings.load(inputStream);
	    inputStream.close();
	} catch (Exception e) {
	    System.out.println("Cannot Find: File - settings.properties");
	    return;
	}
	PropertyTool propTool = new PropertyTool(settings);

	String inputFileName = propTool.getSettingStr("INPUT_FILE", null);
	String outputFileName = propTool.getSettingStr("OUTPUT_FILE", null);

	if (inputFileName == null || outputFileName == null) {
	    System.out.println("Cannot find: input/output files");
	    return;
	}

	try {//LOAD FILES
	    inputFile = new File(inputFileName);
	    outputFile = new File(outputFileName);
	    cipher = Cipher.getInstance("RSA");///FIX INIT CIPHER
	} catch (Exception e) {
	    System.out.println("Error: Loading Files");
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}


	try {
	    RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
	    int inputFileLength = (int) inputFile.length();
	    buffer = new byte[(int) inputFileLength];
	    raf.readFully(buffer);
	    raf.close();
	    SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(buffer));

	    long tempPos = 0;

	    //File Info
	    slea.seek(inputFileLength - 16);
	    tempPos = slea.getPosition();
	    int internalKey1 = slea.readInt();
	    int internalNameLength = slea.readInt();
	    int signatureLength = slea.readInt();
	    int internalKey2 = slea.readInt();
	    if (internalKey1 != KEY1 || internalKey2 != KEY2) {
		//Signs dun match
		return;
	    }

	    //File Signature - Hash
	    slea.seek(tempPos - signatureLength);
	    tempPos = slea.getPosition();
	    //Signature(similar to md5
	    //Sha-384? or 256?
	    byte[] signature = slea.read(64);

	    //File Name
	    slea.seek(tempPos - internalNameLength);
	    tempPos = slea.getPosition();
	    String internalName = slea.readNullTerminatedAsciiString().trim();
	    if (!inputFile.getName().equals(internalName)) {
		System.out.println("Error: File names don't match");
		System.out.println("InputFile=" + inputFile.getName() + " :: InternalName=" + internalName);
		return;
	    }

	    //File Data - Encrypted
	    slea.seek(0);
	    dataBuffer = slea.read((int) tempPos);
	    cipher.doFinal(dataBuffer);

	    //Output File Info
	    System.out.println("Input File: " + inputFile.getName());
	    System.out.println("Filesize: " + inputFile.length());
	    System.out.println("Internal Filename: " + internalName);
	    System.out.println("Output File: " + outputFile.getName());

	    //Store Decrypted Data
	    mplew.write(dataBuffer);

	    //Create Output File
	    outputFile.createNewFile();
	    outputFileStream = new FileOutputStream(outputFile, false);
	} catch (Exception e) {
	    System.out.println("Error: Creating Files");
	    System.out.println(e.getMessage());
	    return;
	}

	try {
	    //Write Output File
	    outputFileStream.write(mplew.toByteArray());
	    outputFileStream.flush();
	    outputFileStream.close();
	} catch (Exception e) {
	    System.out.println("Error: Writing Data.");
	    System.out.println(e.getMessage());
	    return;
	}

	long timeSpent = System.currentTimeMillis() - startTime;
	timeSpent /= 1000;
	System.out.println("Finished in: " + timeSpent + " Seconds");

    }
}
