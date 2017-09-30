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
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.provider.wz.ImgMapleSound;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.StringUtil;
import ch.ubique.inieditor.IniEditor;

public class HTMLWzExtract {

	private boolean dumpPNG;
	private boolean dumpMP3;
	private File pngBaseDir = null;
	private File mp3BaseDir = null;
	private byte[] keyIv;
	private boolean hadError = false;
	private boolean overwriteOld;
	private int version;

	public HTMLWzExtract(int version, boolean dumpPNG, boolean dumpMP3, boolean overwriteOld, byte[] keyIv) throws Exception {
		this.version = version;
		this.dumpPNG = dumpPNG;
		this.dumpMP3 = dumpMP3;
		this.overwriteOld = overwriteOld;
		this.keyIv = keyIv;
	}

	public void extractWZ(File wzFile, File outputBaseDirectory) {
		if (!outputBaseDirectory.exists()) {
			outputBaseDirectory.mkdir();
		} else if (!overwriteOld) {
			System.out.println("skipping " + wzFile.getName() + " (directory exists)");
			return;
		}
		MapleDataProvider dataProv;

		dataProv = MapleDataProviderFactory.getDataProvider(wzFile, dumpPNG, dumpMP3, keyIv);

		MapleDataDirectoryEntry root = dataProv.getRoot();

		dumpDirectory(dataProv, "", root, outputBaseDirectory, 0);
	}

	private void dumpDirectory(MapleDataProvider dataProv, String path, MapleDataDirectoryEntry root, File outputBaseDirectory, int dirLevel) {
		File file = new File(outputBaseDirectory, root.getName());
		file.mkdir();
		System.out.println("[Dumping] " + root.getName());
		int i = 0;
		int imgFiles = root.getFiles().size();
		for (MapleDataEntry entry : root.getFiles()) {
			i++;
			File imgDirFile = new File(file, entry.getName());
			imgDirFile.mkdir();
			File htmlOutFile = new File(imgDirFile, "index.html");
			try {
				String filePath = path;
				if (filePath.length() > 0) {
					filePath += "/";
				}
				filePath += entry.getName();
				htmlOutFile.createNewFile();
				pngBaseDir = imgDirFile;
				mp3BaseDir = imgDirFile;
				System.out.println("[" + i + ":" + imgFiles + "] " + entry.getName());
				dumpImg(dataProv.getData(filePath), new FileOutputStream(htmlOutFile), dirLevel);
			} catch (FileNotFoundException e) {
				hadError = true;
				e.printStackTrace();
			} catch (IOException e) {
				hadError = true;
				e.printStackTrace();
			}
		}
		for (MapleDataDirectoryEntry child : root.getSubdirectories()) {
			dumpDirectory(dataProv, path + (path.equals("") ? "" : "/") + child.getName(), child, file, dirLevel + 1);
		}
	}

	public void dumpImg(MapleData wzFile, OutputStream os, int dirLevel) {
		PrintWriter pw = new PrintWriter(os);
		pw.println("<table>");
		dumpData(wzFile, pw, "", dirLevel);
		pw.println("</table>");
		pw.flush();
	}

	private void dumpData(MapleData data, PrintWriter pw, String pathInImg, int dirLevel) {
		String[] paths = MapleDataTool.getFullDataPath(data).split("/");
		String path = StringUtil.joinStringFrom(paths, 2 + dirLevel, ".");
		switch (data.getType()) {
			case PROPERTY:
			case EXTENDED:
			case CONVEX:
				dumpDataList(data.getChildren(), pw, pathInImg + data.getName() + "/", dirLevel);
				break;
			case CANVAS:
				pw.println("<tr><td>" + path + ".image<td><img src=" + path + ".png>");

				MapleCanvas canvas = (MapleCanvas) data.getData();
				if (dumpPNG) {
					File pngFile = new File(pngBaseDir, path + ".png");
					try {
						ImageIO.write(canvas.getImage(), "png", pngFile);
					} catch (FileNotFoundException e) {
						hadError = true;
						e.printStackTrace();
					} catch (IOException e) {
						hadError = true;
						e.printStackTrace();
					}
				}

				dumpDataList(data.getChildren(), pw, pathInImg + data.getName() + "/", dirLevel);
				break;
			case SOUND:
				pw.println("<tr><td>" + path + "<td>" + getHtmlLink(data.getName() + ".mp3", data.getName()));

				ImgMapleSound mapleSound = (ImgMapleSound) data.getData();
				if (dumpMP3) {
					File mp3File = new File(mp3BaseDir, data.getName() + ".mp3");
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
			case DOUBLE:
			case FLOAT:
			case INT:
			case SHORT:
			case STRING:
				pw.println("<tr><td>" + path + "<td>" + data.getData());
				break;
			case VECTOR:
				Point pos = (Point) data.getData();
				pw.println("<tr><td>" + path + ".x" + "<td>" + pos.x);
				pw.println("<tr><td>" + path + ".y" + "<td>" + pos.y);
				break;
			case IMG_0x00:
				pw.println("<tr><td>" + path + "<td>" + "-NONE-");
				break;
			default:
				throw new RuntimeException("Unexpected img data type " + data.getType() + " path: " + pathInImg);
		}
	}

	private void dumpDataList(List<MapleData> datalist, PrintWriter pw, String pathInImg, int dirLevel) {
		for (MapleData data : datalist) {
			dumpData(data, pw, pathInImg, dirLevel);
		}
	}

	public String getHtmlLink(String title, String dest) {
		return "<a href=\"" + title + "\">" + dest + "</a>";
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
		byte[] keyIv;
		int version = -1;
		IniEditor ini = new IniEditor();
		long startTime = System.currentTimeMillis();

		try {
			ini.load("settings.ini");
		} catch (Exception e) {
			System.out.println("Unable to find settings.ini");
			return;
		}

		version = Integer.parseInt(ini.get("GENERAL", "VERSION"));
		dumpPng = Integer.parseInt(ini.get("GENERAL", "DUMP_PNG")) > 0;
		dumpMp3 = Integer.parseInt(ini.get("GENERAL", "DUMP_MP3")) > 0;
		String keyIvStr = ini.get("GENERAL", "IV");
		if (keyIvStr != null) {
			keyIv = HexTool.getByteArrayFromHexString(keyIvStr);
		} else {
			keyIv = null;
		}
		overwriteOld = Integer.parseInt(ini.get("GENERAL", "OVERWRITE_OLD")) > 0;
		outputDir = ini.get("HTML", "OUTPUT_PATH");
		String[] fileNames = ini.get("GENERAL", "FILES").split(" ");
		for (String fileName : fileNames) {
			File file = new File(fileName);
			try {
				HTMLWzExtract wzExtract = new HTMLWzExtract(version, dumpPng, dumpMp3, overwriteOld, keyIv);
				if (fileName.endsWith("*.wz")) {
					for (File child : file.getParentFile().listFiles()) {
						if (child.getName().endsWith(".wz")) {
							wzExtract.extractWZ(child, new File(outputDir));
							hadError |= wzExtract.isHadError();
						}
					}
				} else {
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

		System.out.println("Finished" + (hadError ? " with errors" : "") + " in " + elapsedMinutes + " minutes " + elapsedSecs + " seconds");
	}
}
