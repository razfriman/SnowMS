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
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Scanner;

import net.sf.odinms.tools.HexTool;

//LIVE DECRYPTOR - NO PCAP FILES
public class XorTool {

    private static Properties settings = new Properties();
    private static File settingsFile = new File("settings.properties");
    private static File outputFile = null;
    private static PrintWriter logStream = null;
    private static Scanner in = new Scanner(System.in);
    private static PrintStream out = System.out;

    public static void main(String args[]) throws IOException {

	out.println("Snow's Xor-Tool\r\n");

	try {//LOAD PROP FILE
	    settings.load(new FileInputStream(settingsFile.getName()));
	} catch (IOException e) {
	    out.println("Cannot find: settings.properties");
	    out.println("Creating file settings.properties...");
	    if (!settingsFile.exists()) {
		settingsFile.createNewFile();
		out.println("settings.properties created\r\n");
	    }
	}

	try {

	    //CREATE KEY
	    outputFile = new File(settings.getProperty("KEY_OUTPUT", "output.txt"));
	    if (!outputFile.exists()) {
		outputFile.createNewFile();
		out.println("Created File: " + outputFile.getName());
	    }
	    try {
		logStream = new PrintWriter(outputFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    generateKey();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void generateKey() {
	try {
	    byte[] input1Hex = HexTool.getByteArrayFromHexString(settings.getProperty("INPUT_1", "00"));
	    byte[] input2Hex = HexTool.getByteArrayFromHexString(settings.getProperty("INPUT_2", "00"));
	    byte[] outputHex = HexTool.xorBytes(input1Hex, input2Hex);
	    outputWithLogging("INPUT");
	    outputWithLogging(HexTool.toString(input2Hex));
	    outputWithLogging(HexTool.toString(input1Hex));
	    outputWithLogging("OUTPUT");
	    outputWithLogging(HexTool.toString(outputHex));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static byte[] xorByteArray(byte[] a, byte[] b) {
	int length = Math.min(a.length, b.length);
	if (a.length != b.length) {
	    System.out.println("Array out of bounds - xor'ing (" + length + ") bytes");
	}
	byte[] ret = new byte[length];
	for (int i = 0; i < length; i++) {
	    ret[i] = (byte) (a[i] ^ b[i]);
	}
	return ret;
    }

    public static void outputWithLogging(String buff) {
	out.println(buff);

	try {
	    logStream.write(buff + "\r\n");
	    logStream.flush();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    
    public static boolean setProperty(Properties props, String key, String value, boolean toFile) {
	if (props != null) {
	    props.setProperty(key, value);
	    if (toFile) {
		return updateFile(settings, "settings.properties");
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    public static boolean updateFile(Properties props, String fileName) {
	try {
	    props.store(new FileOutputStream(fileName), null);
	    return true;
	} catch (IOException e) {
	    return false;
	}
    }
}
