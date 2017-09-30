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
import java.util.HashMap;
import java.util.Map;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.provider.wz.MapleDataType;
import net.sf.odinms.provider.wz.PNGMapleCanvas;
import net.sf.odinms.provider.wz.WZTool;
import net.sf.odinms.provider.xmlwz.FileStoredPngMapleCanvas;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Raz
 */
public class WZIMGTool {

	private WZTool wzTool;
	private Map<String, Integer> dataCache = new HashMap<String, Integer>();

	public WZIMGTool(WZTool wzTool) {
		this.wzTool = wzTool;
	}

	public byte[] getImgFileData(MapleData root) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		dataCache.clear();
		writeParsedExtendedData(mplew, root, 0);
		return mplew.toByteArray();
	}

	public void writeParsedExtendedData(LittleEndianWriter lew, MapleData data, int extended) {
		MapleDataType dataType = data.getType();
		writeStringValue(lew, dataType.getWzName(), false, extended, 0x73, 0x1B);
		switch (dataType) {
			case PROPERTY:
				lew.write(0);
				lew.write(0);
				WZTool.writeValue(lew, data.getChildren().size());
				for (MapleData dataChild : data.getChildren()) {
					writeParsedData(lew, dataChild, extended);
				}
				break;
			case CANVAS:
				System.out.println("PNG -> " + data.getName());
				lew.write(0);
				int marker = data.getChildren().size() == 0 ? 0 : 1;
				lew.write(marker);
				if (marker == 1) {
					lew.write(0);
					lew.write(0);
					WZTool.writeValue(lew, data.getChildren().size());
					for (MapleData dataChild : data.getChildren()) {
						writeParsedData(lew, dataChild, extended);
					}
				}
				if (data.getData() instanceof FileStoredPngMapleCanvas) {
					FileStoredPngMapleCanvas image = (FileStoredPngMapleCanvas) data.getData();
					image.loadImageIfNescessary();
					byte[] wzData = image.getWzData();
					WZTool.writeValue(lew, image.getWidth());
					WZTool.writeValue(lew, image.getHeight());
					WZTool.writeValue(lew, image.getFormat());
					lew.write(0);//format2
					lew.writeInt(0);
					lew.writeInt(wzData.length + 1);
					lew.write(0);
					//TODO: modernImg encrption
					lew.write(wzData);
				} else {
					PNGMapleCanvas image = (PNGMapleCanvas) data.getData();
					image.getImage();
					byte[] wzData = image.getData();
					WZTool.writeValue(lew, image.getWidth());
					WZTool.writeValue(lew, image.getHeight());
					WZTool.writeValue(lew, image.getFormat());//format
					lew.write(0);//format2
					lew.writeInt(0);
					lew.writeInt(wzData.length + 1);
					lew.write(0);
					//TODO: modernImg encrption
					lew.write(wzData);
				}
				break;
			case VECTOR:
				Point pos = MapleDataTool.getPoint(data);
				WZTool.writeValue(lew, pos.x);
				WZTool.writeValue(lew, pos.y);
				break;
			case UOL:
				lew.write(0);
				writeStringValue(lew, MapleDataTool.getString(data), false, extended);
				break;
			case SOUND:
				System.out.println("MP3 -> " + data.getName());
				lew.write(0);
				byte[] mp3Data = MapleDataTool.getMp3Data(data);
				WZTool.writeValue(lew, mp3Data.length);
				String lenStr = Integer.toString(mp3Data.length);
				WZTool.writeValue(lew, Integer.parseInt(lenStr.substring(0, lenStr.length() - 2)));//-1 digit
				lew.write(mp3Data);
				break;
			default:
				System.out.println("Unknown ParseExtendedDataType - " + data.getType().name() + " - " + data.getName());
				break;
		}
	}

	public void writeParsedData(LittleEndianWriter lew, MapleData data, int extended) {
		writeStringValue(lew, data.getName(), false, extended);
		switch (data.getType()) {
			case IMG_0x00:
				lew.write(0);
				break;
			case SHORT:
				lew.write(2);//or 11
				lew.writeShort(MapleDataTool.getShort(data));
				break;
			case INT:
				lew.write(3);
				WZTool.writeValue(lew, MapleDataTool.getInt(data));
				break;
			case FLOAT:
				lew.write(4);
				WZTool.writeFloatValue(lew, MapleDataTool.getFloat(data));
				break;
			case DOUBLE:
				lew.write(5);
				lew.writeDouble(MapleDataTool.getDouble(data));
				break;
			case STRING:
				lew.write(8);
				String dataStr = MapleDataTool.getString(data);
				dataStr = XmlUtil.unsanitizeText(dataStr);
				boolean unicode = isUnicode(dataStr);
				writeStringValue(lew, dataStr, unicode, extended);
				break;
			case PROPERTY:
			case CANVAS:
			case VECTOR:
			case SOUND:
			case UOL:
			case EXTENDED:
				lew.write(9);
				MaplePacketLittleEndianWriter extendLew = new MaplePacketLittleEndianWriter();
				writeParsedExtendedData(extendLew, data, extended + lew.getSize() + 4);
				lew.writeInt(extendLew.getSize());
				lew.write(extendLew.toByteArray());
				break;
			default:
				System.out.println("Unknown ParseDataType - " + data.getType().name() + " - " + data.getName());
				break;
		}
	}

	public void writeStringValue(LittleEndianWriter lew, String s) {
		writeStringValue(lew, s, false, 0, 0, 1);
	}

	public void writeStringValue(LittleEndianWriter lew, String s, boolean unicode) {
		writeStringValue(lew, s, unicode, 0, 0, 1);
	}

	public void writeStringValue(LittleEndianWriter lew, String s, boolean unicode, int extended) {
		writeStringValue(lew, s, unicode, extended, 0, 1);
	}

	public void writeStringValue(LittleEndianWriter lew, String s, boolean unicode, int extended, int withoutOffset, int withOffset) {
		if (s.length() > 4 && dataCache.containsKey(s)) {
			lew.write(withOffset);
			lew.writeInt(dataCache.get(s));
		} else {
			lew.write(withoutOffset);
			int sOffset = lew.getSize() + extended;
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
}
