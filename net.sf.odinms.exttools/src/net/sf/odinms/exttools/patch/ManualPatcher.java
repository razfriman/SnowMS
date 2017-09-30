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

import net.sf.odinms.tools.PropertyTool;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class ManualPatcher {

    public static byte[] PATCH_MAGIC = new byte[]{(byte) 0xF3, (byte) 0xFB, (byte) 0xF7, (byte) 0xF2};

    public static void main(String args[]) {
	long startTime = System.currentTimeMillis();
	Properties settings = new Properties();
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	File base;
	File patch;
	File notice;
	File manualPatcher;
	FileOutputStream manualPatcherStream;
	try {//LOAD SETTINGS
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
	String oldVerStr = StringUtil.getLeftPaddedStr(Integer.toString(oldVer), '0', 5);
	String newVerStr = StringUtil.getLeftPaddedStr(Integer.toString(newVer), '0', 5);

	try {//LOAD FILES
	    base = new File("ManualPatch.base");
	    patch = new File(oldVerStr + "to" + newVerStr + ".patch");
	    notice = new File(newVerStr + ".txt");
	} catch (Exception e) {
	    System.out.println("Error Loading Files");
	    System.out.println(e.getMessage());
	    return;
	}

	manualPatcher = new File(oldVerStr + "to" + newVerStr + ".exe");
	try {
	    manualPatcher.createNewFile();
	    manualPatcherStream = new FileOutputStream(manualPatcher, true);
	} catch (Exception e) {
	    System.out.println("Error Creating Manual-Patcher");
	    System.out.println(e.getMessage());
	    return;
	}

	try {
	    writeToFile(base, manualPatcherStream);
	    writeToFile(patch, manualPatcherStream);
	    writeToFile(notice, manualPatcherStream);
	    mplew.writeInt((int) patch.length());
	    mplew.writeInt((int) notice.length());
	    mplew.write(PATCH_MAGIC);
	    manualPatcherStream.write(mplew.toByteArray());
	} catch (Exception e) {
	    System.out.println("Error Writing Data.");
	    System.out.println(e.getMessage());
	    return;
	}
	try {
	    manualPatcherStream.close();
	} catch (Exception e) {
	    System.out.println("Error Closing Manual-Patcher Stream");
	    System.out.println(e.getMessage());
	    return;
	}
	long timeSpent = System.currentTimeMillis() - startTime;
	timeSpent /= 1000;
	System.out.println("Patcher Created For V" + oldVer + " to V" + newVer + " | " + "Created File: " + manualPatcher.getName() + " | " + timeSpent + " Seconds");

    }

    public static void writeToFile(File inFile, FileOutputStream out) {
	try {
	    RandomAccessFile raf = new RandomAccessFile(inFile, "r");
	    long position = 0;
	    while(position < raf.length()) {
		  byte[] b = new byte[4096];
		  int amount = raf.read(b);
		  out.write(b, 0, amount);
		  position += amount;
	    }
	} catch (Exception e) {
	    System.out.println("Error Writing to File");
	    System.out.println(e.getMessage());
	    return;
	}
    }
}
