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

package net.sf.odinms.provider.wz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.sf.odinms.net.MapleServer;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericLittleEndianAccessor;
import net.sf.odinms.tools.data.input.InputStreamByteStream;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListWZFile {
	private LittleEndianAccessor lea;
	// will be decorated as Unmodifiable after loading is done
	private List<String> entries = new ArrayList<String>();
	private static Collection<String> modernImgs = new HashSet<String>();
	private static Logger log = LoggerFactory.getLogger(ListWZFile.class);

	public ListWZFile(File listwz) throws FileNotFoundException {
		lea = new GenericLittleEndianAccessor(new InputStreamByteStream(new BufferedInputStream(new FileInputStream(listwz))));
		LittleEndianAccessor llea;
		WZTool wzTool = new WZTool(MapleServer.MAPLE_VERSION);
		while (lea.available() > 0) {
			int l = lea.readInt();
			byte[] chunk = wzTool.decrypt(lea.read((l + 1) * 2));
			llea = new GenericLittleEndianAccessor(new ByteArrayByteStream(chunk));
			String value = "";
			for (int i = 0; i < l; i++) {
			    value += llea.readChar();
			}
			llea.readChar();//00 00 - string terminator
			entries.add(value);
		}
		entries = Collections.unmodifiableList(entries);
	}
	
	public List<String> getEntries() {
		return entries;
	}
	
	public static void init() {
		final String listWz = System.getProperty("net.sf.odinms.listwz");
		if (listWz != null) {
			ListWZFile listwz;
			try {
				listwz = new ListWZFile(MapleDataProviderFactory.fileInWZPath("List.wz"));
				modernImgs = new HashSet<String>(listwz.getEntries());
			} catch (FileNotFoundException e) {
				log.info("net.sf.odinms.listwz is set but the List.wz could not be found", e);
			}
		}
	}
	
	public static boolean isModernImgFile(String path) {
		return modernImgs.contains(path);
	}
}
