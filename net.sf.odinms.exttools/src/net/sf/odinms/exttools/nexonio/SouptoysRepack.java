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
import java.util.Properties;

import net.sf.odinms.tools.PropertyTool;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class SouptoysRepack {

    private File inputFile;
    private File outputDir;

    public SouptoysRepack(File inputFile, File outputDir) {
	this.inputFile = inputFile;
	this.outputDir = outputDir;
    }

    public void repack() {
	File toyFile = new File(inputFile.getName());
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

	mplew.write(new byte[76]);
	mplew.writeNullTerminatedAsciiString("SOUPTOYS.COM TOY FORMAT");
	mplew.writeInt(4);
	String version = "";
	mplew.writeIntPrefixedAsciiString("Souptoys file version ST_CURRENT_VERSION");
	mplew.writeInt(0);
	mplew.writeIntPrefixedAsciiString("Souptoys Pty Ltd.");
	mplew.write(new byte[23]);
	mplew.writeInt(1);
	mplew.writeIntPrefixedAsciiString("license.Free");
	mplew.writeInt(4);
	mplew.writeIntPrefixedAsciiString("SouplabsBouncePad.license");
	mplew.writeInt(2);
	mplew.writeIntPrefixedAsciiString("Free");
	mplew.writeIntPrefixedAsciiString("SouplabsBouncePad.toypack");
	mplew.writeInt(2);
	mplew.writeIntPrefixedAsciiString("SoupLabs");
	mplew.writeIntPrefixedAsciiString("SouplabsZoompad.license");
	mplew.writeInt(2);
	mplew.writeIntPrefixedAsciiString("Free");
	mplew.writeIntPrefixedAsciiString("SouplabsZoompad.toypack");
	mplew.writeInt(2);
	mplew.writeIntPrefixedAsciiString("SoupLabs");
	mplew.writeInt(0);
	mplew.writeShort(0);
	mplew.writeInt(2);
	mplew.writeIntPrefixedAsciiString("SouplabsBouncePad");
	mplew.writeInt(0);
	mplew.writeShort(0);
	//49024
	//
	mplew.writeInt(2);
	mplew.writeIntPrefixedAsciiString("../graphics/custom/pogo/essbeebox_icon");//Len + Name
	mplew.writeInt(0);//???
	mplew.writeInt(0);//Prefix Len
	mplew.writeInt(-1);//Offset
	mplew.writeInt(1);
	mplew.writeInt(0);
	mplew.writeInt(1);
	mplew.writeInt(0);
	mplew.writeIntPrefixedAsciiString("SouplabsBouncePad");
	mplew.writeInt(0x0A);
	mplew.writeIntPrefixedAsciiString("SouplabsZoompad");


	//149206
	try {
	    FileOutputStream fos = new FileOutputStream(toyFile);
	    fos.write(mplew.toByteArray());
	    fos.close();
	} catch (Exception e) {
	    System.out.println("Error: Writing Data To File");
	    e.printStackTrace();
	    return;
	}

    }

    public static void main(String args[]) {
	long startTime = System.currentTimeMillis();
	Properties settings = new Properties();
	File inputDir;
	File outputDir;
	try {
	    FileInputStream inputStream = new FileInputStream("settings.properties");
	    settings.load(inputStream);
	    inputStream.close();
	} catch (Exception e) {
	    System.out.println("Cannot Find: File - settings.properties");
	    return;
	}
	PropertyTool propTool = new PropertyTool(settings);

	String inputDirName = propTool.getSettingStr("INPUT_DIR", null);
	String outputDirName = propTool.getSettingStr("OUTPUT_DIR", null);

	if (inputDirName == null || outputDirName == null) {
	    System.out.println("Cannot find: input/output files");
	    return;
	}

	try {//LOAD FILES
	    inputDir = new File(inputDirName);
	    outputDir = new File(outputDirName);
	    inputDir.mkdir();
	    outputDir.mkdir();

	    for (File toyFile : inputDir.listFiles()) {
		File outputFile = new File(toyFile.getName());
		SouptoysRepack repack = new SouptoysRepack(toyFile, outputDir);
		repack.repack();
	    }

	} catch (Exception e) {
	    System.out.println("Error: Repacking Files");
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}

	long timeSpent = System.currentTimeMillis() - startTime;
	timeSpent /= 1000;
	System.out.println("\r\nFinished in: " + timeSpent + " Seconds");

    }
}
