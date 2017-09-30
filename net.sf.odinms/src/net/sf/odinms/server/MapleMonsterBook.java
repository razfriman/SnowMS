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

package net.sf.odinms.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;

/**
 *
 * @author Raz
 */
public class MapleMonsterBook {

	private static MapleMonsterBook instance;
	private Map<Integer, MonsterBookEntry> entries = new HashMap<Integer, MonsterBookEntry>();

	private MapleMonsterBook() {
		init();
	}

	public static MapleMonsterBook getInstance() {
		if (instance == null) {
			instance = new MapleMonsterBook();
		}
		return instance;
	}

	private void init() {
		MapleData book = MapleDataProviderFactory.getWzFile("String.wz").getData("MonsterBook.img");
		for (MapleData monsterData : book.getChildren()) {
			int mobid = Integer.parseInt(monsterData.getName());
			String episode = MapleDataTool.getString("episode", monsterData);
			List<Integer> maps = new ArrayList<Integer>();
			List<Integer> drops = new ArrayList<Integer>();
			for (MapleData mapData : monsterData.getChildByPath("map").getChildren()) {
				maps.add(MapleDataTool.getInt(mapData, -1));
			}
			for (MapleData rewardData : monsterData.getChildByPath("reward").getChildren()) {
				drops.add(MapleDataTool.getInt(rewardData, -1));
			}
			entries.put(mobid, new MonsterBookEntry(mobid, maps, drops, episode));
		}
	}

	public MonsterBookEntry getEntry(int mobid) {
		return entries.get(mobid);
	}

	public class MonsterBookEntry {

		private int mobid;
		private List<Integer> maps;
		private List<Integer> drops;
		private String episode;

		public MonsterBookEntry(int mobid, List<Integer> maps, List<Integer> drops, String episode) {
			this.mobid = mobid;
			this.maps = maps;
			this.drops = drops;
			this.episode = episode;
		}

		public int getMobid() {
			return mobid;
		}

		public List<Integer> getDrops() {
			return drops;
		}

		public String getEpisode() {
			return episode;
		}

		public List<Integer> getMaps() {
			return maps;
		}
	}
}
