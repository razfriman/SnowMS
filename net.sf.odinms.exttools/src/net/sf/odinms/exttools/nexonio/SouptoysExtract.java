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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.odinms.tools.PropertyTool;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class SouptoysExtract {

    private List<File> inputFiles;
    private File outputDir;

    public SouptoysExtract(List<File> inputFiles, File outputDir) {
	this.inputFiles = inputFiles;
	this.outputDir = outputDir;
    }

    public List<File> getInputFiles() {
	return inputFiles;
    }

    public void extract(File inputFile) {
	System.out.println("\r\nExtracting: " + inputFile.getName());
	try {
	    outputDir = new File(outputDir, inputFile.getName());
	    RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
	    int inputFileLength = (int) inputFile.length();
	    byte[] buffer = new byte[(int) inputFileLength];
	    raf.readFully(buffer);
	    raf.close();
	    SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(buffer));

	    //File Info
	    slea.skip(76);
	    if (!slea.readAsciiString(23).equals("SOUPTOYS.COM TOY FORMAT")) {
		System.out.println("Skipping File: Irregular File Format (*.toy) - " + inputFile.getName());
		return;
	    }

	    slea.skip((int) slea.available() - 4);
	    int endHeaderOffset = slea.readInt();
	    endHeaderOffset += 72;
	    slea.seek(endHeaderOffset);

	    int fileTableOffset = slea.readInt();
	    fileTableOffset += 76;
	    slea.seek(fileTableOffset);

	    int files = slea.readInt();
	    FileOutputStream fos = null;
	    for (int i = 0; i < files; i++) {
		String name = slea.readAsciiString(slea.readInt());
		String[] path = name.split("/");
		System.out.println(name);
		int offset = slea.readInt();
		offset += 76;
		int extensionLength = slea.readInt();
		String extension = slea.readAsciiString(extensionLength);
		int size = slea.readInt();
		long pos = slea.getPosition();
		slea.seek(offset);
		File file = null;
		if (path.length > 1) {
		    String fullPathStr = "";
		    for (int j = 1; j < path.length - 1; j++) {
			fullPathStr += path[j] + "/";
		    }
		    new File(outputDir, fullPathStr).mkdirs();
		    file = new File(outputDir, fullPathStr + "/" + path[path.length - 1]);
		} else {
		    file = new File(outputDir, path[0]);
		}

		fos = new FileOutputStream(file);
		fos.write(slea.read(size));
		fos.flush();
		slea.seek(pos);
	    }


	} catch (Exception e) {
	    System.out.println("Error: Extracting Files");
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}
    }

    public static void main(String args[]) {
	long startTime = System.currentTimeMillis();
	Properties settings = new Properties();
	List<File> inputFiles = new ArrayList<File>();
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

	String[] inputFileNames = propTool.getSettingStr("INPUT_FILES", null).split(" ");
	String outputDirName = propTool.getSettingStr("OUTPUT_DIR", null);

	if (inputFileNames == null || outputDirName == null) {
	    System.out.println("Cannot find: input/output files");
	    return;
	}

	try {//LOAD FILES
	    outputDir = new File(outputDirName);
	    outputDir.mkdir();

	    for (String inputFileName : inputFileNames) {
		File inputFile = new File(inputFileName);
		if (inputFile.getName().equals("*.toy")) {
		    for (File child : inputFile.getAbsoluteFile().getParentFile().listFiles()) {
			if (child.getName().endsWith(".toy")) {
			    inputFiles.add(child);
			}
		    }
		} else {
		    inputFiles.add(inputFile);
		}
	    }

	} catch (Exception e) {
	    System.out.println("Error: Loading Files");
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}

	SouptoysExtract extract = new SouptoysExtract(inputFiles, outputDir);
	for (File file : extract.getInputFiles()) {
	    extract.extract(file);
	}




	long timeSpent = System.currentTimeMillis() - startTime;
	timeSpent /= 1000;
	System.out.println("\r\nFinished in: " + timeSpent + " Seconds");

    }
}

//int - str - SID
//int - 0
//int - str - PATH

