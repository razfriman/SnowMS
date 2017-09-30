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

package net.sf.odinms.server.maps;

/**
 * 
 * @author Raz
 */
public class MapleMapInformationProvider {

	private static MapleMapInformationProvider instance = new MapleMapInformationProvider();

	protected MapleMapInformationProvider() {
	}

	public static MapleMapInformationProvider getInstance() {
		return instance;
	}

	public boolean isMiniDungeonMap(int mapId) {
		switch (mapId) {
			case 100020000:
			case 105040304:
			case 105050100:
			case 221023400:
				return true;
			default:
				return false;
		}
	}

	public boolean isMapleTVMap(MapleMap map) {
		int tvIds[] = {9250042, 9250043, 9250045, 9250044, 9270001, 9270002, 9250023, 9250024, 9270003, 9270004, 9250026, 9270006, 9270007, 9250046, 9270000, 9201066};
		for (int id : tvIds) {
			if (map.containsNPC(id)) {
				return true;
			}
		}
		return false;
	}
}
