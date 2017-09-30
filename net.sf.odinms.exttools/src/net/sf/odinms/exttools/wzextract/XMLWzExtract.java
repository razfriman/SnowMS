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

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.odinms.provider.MapleCanvas;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.wz.ImgMapleSound;
import ch.ubique.inieditor.IniEditor;

public class XMLWzExtract {

    private boolean dumpPNG;
    private boolean dumpMP3;
    private File pngBaseDir = null;
    private File mp3BaseDir = null;
    private boolean hadError = false;
    private boolean overwriteOld;
    private int version = -1;

    public XMLWzExtract(int version, boolean dumpPNG, boolean dumpMP3, boolean overwriteOld) throws Exception {
	this.version = version;
	this.dumpPNG = dumpPNG;
	this.dumpMP3 = dumpMP3;
	this.overwriteOld = overwriteOld;
    }

    public void extractWZ(File wzFile, File outputBaseDirectory) {
	if (!outputBaseDirectory.exists()) {
	    outputBaseDirectory.mkdir();
	} else if (!overwriteOld) {
	    System.out.println("skipping " + wzFile.getName() + " (directory exists)");
	    return;
	}
	MapleDataProvider dataProv;

	dataProv = MapleDataProviderFactory.getDataProvider(wzFile, dumpPNG, dumpMP3);

	MapleDataDirectoryEntry root = dataProv.getRoot();

	dumpDirectory(dataProv, "", root, outputBaseDirectory);
    }

    private void dumpDirectory(MapleDataProvider dataProv, String path, MapleDataDirectoryEntry root, File outputBaseDirectory) {
	File file = new File(outputBaseDirectory, root.getName());
	file.mkdir();
	for (MapleDataEntry entry : root.getFiles()) {
	    File xmlOutFile = new File(file, entry.getName() + ".xml");
	    try {
		String filePath = path;
		if (filePath.length() > 0) {
		    filePath += "/";
		}
		filePath += entry.getName();
		xmlOutFile.createNewFile();
		pngBaseDir = file;
		mp3BaseDir = file;
		System.out.println("Dumping: " + filePath + " from " + dataProv.getRoot().getName() + " to " + xmlOutFile.getPath());
		dumpImg(dataProv.getData(filePath), new FileOutputStream(xmlOutFile));
	    } catch (FileNotFoundException e) {
		hadError = true;
		e.printStackTrace();
	    } catch (IOException e) {
		hadError = true;
		e.printStackTrace();
	    }
	}
	for (MapleDataDirectoryEntry child : root.getSubdirectories()) {
	    dumpDirectory(dataProv, path + (path.equals("") ? "" : "/") + child.getName(), child, file);
	}
    }

    public void dumpImg(MapleData wzFile, OutputStream os) {
	//
	PrintWriter pw = new PrintWriter(os);
	pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	dumpData(wzFile, pw, 0, "");
	pw.flush();
    }

    private void dumpData(MapleData data, PrintWriter pw, int level, String pathInImg) {
	switch (data.getType()) {
	    case PROPERTY:
		pw.println(indentation(level) + openNamedTag("imgdir", data.getName(), true));
		dumpDataList(data.getChildren(), pw, level + 1, pathInImg + data.getName() + "/");
		pw.println(indentation(level) + closeTag("imgdir"));
		break;
	    case EXTENDED:
		pw.println(indentation(level) + openNamedTag("extended", data.getName(), true));
		dumpDataList(data.getChildren(), pw, level + 1, pathInImg + data.getName() + "/");
		pw.println(indentation(level) + closeTag("extended"));
		break;
	    case CANVAS:
		MapleCanvas canvas = (MapleCanvas) data.getData();
		pw.println(indentation(level) + openNamedTag("canvas", data.getName(), false, false) +
			attrib("width", Integer.toString(canvas.getWidth())) +
			attrib("height", Integer.toString(canvas.getHeight()), true, false));

		if (dumpPNG) {
		    File pngDir = new File(pngBaseDir, pathInImg);
		    if (!pngDir.exists()) {
			pngDir.mkdirs();
		    }
		    File pngFile = new File(pngDir, data.getName() + ".png");
		    //System.out.println("Dumping canvas data to " + pngFile.getAbsolutePath());
		    
		    try {
			ImageIO.write(canvas.getImage(), "png", pngFile);
		    } catch (FileNotFoundException e) {
			//usually a space in the path EG(a.img/ladder /0/0.png)
			hadError = true;
			e.printStackTrace();
		    } catch (IOException e) {
			hadError = true;
			e.printStackTrace();
		    }
		}

		dumpDataList(data.getChildren(), pw, level + 1, pathInImg + data.getName() + "/");
		pw.println(indentation(level) + closeTag("canvas"));
		break;
	    case CONVEX:
		pw.println(indentation(level) + openNamedTag("convex", data.getName(), true));
		dumpDataList(data.getChildren(), pw, level + 1, pathInImg + data.getName() + "/");
		pw.println(indentation(level) + closeTag("convex"));
		break;
	    case SOUND:
		ImgMapleSound mapleSound = (ImgMapleSound) data.getData();
		pw.println(indentation(level) + emptyNamedTag("sound", data.getName()));

		if (dumpMP3) {
		    File mp3Dir = new File(mp3BaseDir, pathInImg);
		    if (!mp3Dir.exists()) {
			mp3Dir.mkdirs();
		    }
		    File mp3File = new File(mp3Dir, data.getName() + ".mp3");
		    //System.out.println("Dumping sound data to " + mp3File.getAbsolutePath());
		    FileOutputStream outputStream = null;
		    try {
			outputStream = new FileOutputStream(mp3File);
			outputStream.write(mapleSound.getSoundData());
		    } catch (FileNotFoundException e) {
			hadError = true;
			e.printStackTrace();
		    } catch (IOException e) {
			hadError = true;
			e.printStackTrace();
		    } finally {
			try {
			    if (outputStream != null) {
				outputStream.close();
			    }
			} catch (IOException e) {
			    hadError = true;
			    e.printStackTrace();
			}
		    }
		}
		break;
	    case UOL:
		pw.println(indentation(level) + emptyNamedValuePair("uol", data.getName(), data.getData().toString()));
		break;
	    case DOUBLE:
		pw.println(indentation(level) +
			emptyNamedValuePair("double", data.getName(), data.getData().toString()));
		break;
	    case FLOAT:
		pw.println(indentation(level) + emptyNamedValuePair("float", data.getName(), data.getData().toString()));
		break;
	    case INT:
		pw.println(indentation(level) + emptyNamedValuePair("int", data.getName(), data.getData().toString()));
		break;
	    case SHORT:
		pw.println(indentation(level) + emptyNamedValuePair("short", data.getName(), data.getData().toString()));
		break;
	    case STRING:
		pw.println(indentation(level) +
			emptyNamedValuePair("string", data.getName(), data.getData().toString()));
		break;
	    case VECTOR:
		Point tPoint = (Point) data.getData();
		pw.println(indentation(level) + openNamedTag("vector", data.getName(), false, false) +
			attrib("x", Integer.toString(tPoint.x)) + attrib("y", Integer.toString(tPoint.y), true, true));
		break;
	    case IMG_0x00:
		pw.println(indentation(level) + emptyNamedTag("null", data.getName()));
		break;
	    default:
		throw new RuntimeException("Unexpected img data type " + data.getType() + " path: " + pathInImg);
	}
    }

    private void dumpDataList(List<MapleData> datalist, PrintWriter pw, int level, String pathInImg) {
	for (MapleData data : datalist) {
	    dumpData(data, pw, level, pathInImg);
	}
    }

    private String openNamedTag(String tag, String name, boolean finish) {
	return openNamedTag(tag, name, finish, false);
    }

    private String emptyNamedTag(String tag, String name) {
	return openNamedTag(tag, name, true, true);
    }

    private String emptyNamedValuePair(String tag, String name, String value) {
	return openNamedTag(tag, name, false, false) + attrib("value", value, true, true);
    }

    private String openNamedTag(String tag, String name, boolean finish, boolean empty) {
	return "<" + tag + " name=\"" + name + "\"" + (finish ? (empty ? "/>" : ">") : " ");
    }

    private String attrib(String name, String value) {
	return attrib(name, value, false, false);
    }

    private String attrib(String name, String value, boolean closeTag, boolean empty) {
	return name + "=\"" + XmlUtil.sanitizeText(value) + "\"" + (closeTag ? (empty ? "/>" : ">") : " ");
    }

    private String closeTag(String tag) {
	return "</" + tag + ">";
    }

    private String indentation(int level) {
	char[] indent = new char[level];
	for (int i = 0; i < indent.length; i++) {
	    indent[i] = '\t';
	}
	return new String(indent);
    }

    public boolean isHadError() {
	return hadError;
    }

    public static void main(String args[]) {
	boolean dumpPng = false;
	boolean dumpMp3 = false;
	boolean overwriteOld = false;
	boolean hadError = false;
	String outputDir = null;
	int version = -1;
	IniEditor ini = new IniEditor();
	long startTime = System.currentTimeMillis();

	try {//Load Settings
	    ini.load("settings.ini");
	} catch (Exception e) {
	    System.out.println("Unable to find settings.properties");
	    return;
	}

	version = Integer.parseInt(ini.get("GENERAL", "VERSION"));
	dumpPng = Integer.parseInt(ini.get("GENERAL", "DUMP_PNG")) > 0;
	dumpMp3 = Integer.parseInt(ini.get("GENERAL", "DUMP_MP3")) > 0;
	overwriteOld = Integer.parseInt(ini.get("GENERAL", "OVERWRITE_OLD")) > 0;
	outputDir = ini.get("XML", "OUTPUT_PATH");
	String[] fileNames = ini.get("GENERAL", "FILES").split(" ");
	for (String fileName : fileNames) {
	    File file = new File(fileName);
	    try {
		XMLWzExtract wzExtract = null;
		if (fileName.endsWith("*.wz")) {
		    for (File child : file.getParentFile().listFiles()) {
			if (child.getName().endsWith(".wz")) {
			    wzExtract = new XMLWzExtract(version, dumpPng, dumpMp3, overwriteOld);
			    wzExtract.extractWZ(child, new File(outputDir));
			    hadError |= wzExtract.isHadError();
			}
		    }
		} else {
		    wzExtract = new XMLWzExtract(version, dumpPng, dumpMp3, overwriteOld);
		    wzExtract.extractWZ(file, new File(outputDir));
		    hadError |= wzExtract.isHadError();
		}
	    } catch (Exception e) {
		hadError = true;
		System.out.println("Exception occured while dumping " + file + " continuing with next file");
		System.out.flush();
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
