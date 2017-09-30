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

import java.io.File;
import java.io.IOException;

import net.sf.odinms.net.MapleServer;
import net.sf.odinms.provider.wz.WZFile;
import net.sf.odinms.provider.xmlwz.XMLWZFile;

public class MapleDataProviderFactory {
	
	private static MapleDataProvider getWZ(Object in, boolean provideImages, boolean provideMusic, byte[] keyIv) {
		if (in instanceof File) {
			File fileIn = (File) in;
			
			if (fileIn.getName().endsWith("wz") && !fileIn.isDirectory()) {
				try {
					//TODO fix if we want to use the same version, or generate it
				    return new WZFile(fileIn, provideImages, provideMusic, keyIv, MapleServer.MAPLE_VERSION);
				} catch (IOException e) {
					throw new RuntimeException("Loading WZ File failed", e);
				}
			} else {
				// always provides images as we do this lazily and it's
				// therefore cheap (assuming that the images don't get loaded
				// for fun)
				return new XMLWZFile(fileIn);
			}
		}
		throw new IllegalArgumentException("Can't create data provider for input " + in);
	}

	public static MapleDataProvider getDataProvider(Object in) {
		return getWZ(in, false, false, null);
	}
	
	public static MapleDataProvider getDataProvider(Object in, boolean provideImages, boolean provideMusic) {
		return getWZ(in, provideImages, provideMusic, null);
	}

	public static MapleDataProvider getImageProvidingDataProvider(Object in) {
		return getWZ(in, true, false, null);
	}
	
	public static MapleDataProvider getMusicProvidingDataProvider(Object in) {
		return getWZ(in, false, true, null);
	}
	
	public static MapleDataProvider getImageAndMusicProvidingDataProvider(Object in) {
		return getWZ(in, true, true, null);
	}

	public static MapleDataProvider getDataProvider(Object in, boolean provideImages, boolean provideMusic, byte[] keyIv) {
		return getWZ(in, true, true, keyIv);
	}

	public static File fileInWZPath(String filename) {
		return new File(System.getProperty("net.sf.odinms.wzpath") + "/" + filename);
	}

	public static MapleDataProvider getWzFile(String name) {
		try {
			return getWZ(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + name), false, false, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static MapleDataProvider getWzFile(String name, boolean images, boolean music) {
	    return getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + name), images, music);
	}
}
