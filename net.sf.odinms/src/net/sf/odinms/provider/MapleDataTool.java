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

package net.sf.odinms.provider;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.sf.odinms.provider.wz.MapleDataType;
import net.sf.odinms.tools.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleDataTool {

	private static Logger log = LoggerFactory.getLogger(MapleDataTool.class);
	
	public static MapleData get(String path) {

		try {
			String[] paths = path.split("/");
			int imgFileIndex = 1;
			for (int i = 0; i < paths.length; i++) {
				if (paths[i].endsWith(".img")) {
					imgFileIndex = i;
					break;
				}
			}

			MapleDataProvider dataProv = MapleDataProviderFactory.getWzFile(paths[0], true, false);
			return dataProv.getData(paths[imgFileIndex]).getChildByPath(StringUtil.joinStringFrom(paths, imgFileIndex + 1, "/"));
		} catch (Exception e) {
			log.error("Error getting maple-data", e);
			return null;
		}
	}

	public static String getString(MapleData data) {
		return ((String) data.getData());
	}

	public static String getString(MapleData data, String def) {
		if (data == null || data.getData() == null) {
			return def;
		} else {
			return ((String) data.getData());
		}
	}

	public static String getString(String path, MapleData data) {
		return getString(data.getChildByPath(path));
	}

	public static String getString(String path, MapleData data, String def) {
		return getString(data.getChildByPath(path), def);
	}

	public static double getDouble(MapleData data) {
		return ((Double) data.getData()).doubleValue();
	}

	public static int getInt(MapleData data) {
		return ((Integer) data.getData()).intValue();
	}

	public static int getInt(MapleData data, int def) {
		if (data == null || data.getData() == null) {
			return def;
		} else {
			return ((Integer) data.getData()).intValue();
		}
	}
	
	public static int getShort(MapleData data) {
		return ((Short) data.getData()).shortValue();
	}

	public static int getShort(MapleData data, short def) {
		if (data == null || data.getData() == null) {
			return def;
		} else {
			return ((Short) data.getData()).shortValue();
		}
	}
	
	public static float getFloat(MapleData data) {
	    return ((Float) data.getData()).floatValue();
	}
	
	public static float getFloat(MapleData data, float def) {
		if (data == null || data.getData() == null) {
			return def;
		} else {
			return ((Float) data.getData()).floatValue();
		}
	}

	public static int getInt(String path, MapleData data) {
		return getInt(data.getChildByPath(path));
	}

	public static int getIntConvert(MapleData data) {
		if (data.getType() == MapleDataType.STRING) {
			return Integer.parseInt(getString(data));
		} else {
			return getInt(data);
		}
	}

	public static int getIntConvert(String path, MapleData data) {
		MapleData d = data.getChildByPath(path);
		if (d.getType() == MapleDataType.STRING) {
			return Integer.parseInt(getString(d));
		} else {
			return getInt(d);
		}
	}

    public static int getIntConvert(MapleData data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        } else if (data.getType() == MapleDataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            return getInt(data);
        }
    }

	public static int getInt(String path, MapleData data, int def) {
		return getInt(data.getChildByPath(path), def);
	}

	public static int getIntConvert(String path, MapleData data, int def) {
		MapleData d = data.getChildByPath(path);
		if (d == null) {
			return def;
		}
		if (d.getType() == MapleDataType.STRING) {
			try {
				return Integer.parseInt(getString(d));
			} catch (NumberFormatException nfe) {
				return def;
			}
		} else {
			return getInt(d, def);
		}
	}

	public static BufferedImage getImage(MapleData data) {
		 return ((MapleCanvas) data.getData()).getImage();
	}
	
	public static byte[] getMp3Data(MapleData data) {
		return ((MapleSound) data.getData()).getSoundData();
	}
	
	public static Point getPoint(MapleData data) {
		return ((Point) data.getData());
	}
	
	public static Point getPoint(String path, MapleData data) {
		return getPoint(data.getChildByPath(path));
	}
	
	public static Point getPoint(String path, MapleData data, Point def){
	    final MapleData pointData = data.getChildByPath(path);
	    if(pointData == null) {
		return def;
	    }
	    return getPoint(pointData);
	}
	
	public static String getFullDataPath(MapleData data) { 
	    String path = ""; 
	    MapleDataEntity myData = data; 
		while (myData != null) { 
		    path = myData.getName() + "/" + path; 
		    myData = myData.getParent(); 
		} 
	    return path.substring(0, path.length() - 1); 
	}
}
