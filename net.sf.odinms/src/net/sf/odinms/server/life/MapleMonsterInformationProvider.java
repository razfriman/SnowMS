/*
	This file is part of the Odin Snow Maple Story Server
    Copyright (C) 2008 Terry Han <than@nautilusport.net> 

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
package net.sf.odinms.server.life;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.DropEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matze
 */
public class MapleMonsterInformationProvider {

	public static final int APPROX_FADE_DELAY = 90;
	private static MapleMonsterInformationProvider instance = null;
	private Map<Integer, List<DropEntry>> drops = new HashMap<Integer, List<DropEntry>>();
	private static final Logger log = LoggerFactory.getLogger(MapleMonsterInformationProvider.class);

	private MapleMonsterInformationProvider() {
	}

	public static MapleMonsterInformationProvider getInstance() {
		if (instance == null) {
			instance = new MapleMonsterInformationProvider();
		}
		return instance;
	}

	public List<DropEntry> retrieveDropChances(int monsterId) {
		if (drops.containsKey(monsterId)) {
			return drops.get(monsterId);
		}
		List<DropEntry> ret = new LinkedList<DropEntry>();
		try {
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM monsterdrops WHERE (monsterid = ? AND chance >= 0) OR (monsterid <= 0)");
			ps.setInt(1, monsterId);
			ResultSet rs = ps.executeQuery();
			MapleMonster theMonster = null;
			while (rs.next()) {
				int itemId = rs.getInt("itemid");
				int questId = rs.getInt("questid");
				int rowMonsterId = rs.getInt("monsterid");
				int chance = rs.getInt("chance");
				int amount = rs.getInt("amount");
				if (rowMonsterId != monsterId && rowMonsterId != 0) {
					if (theMonster == null) {
						theMonster = MapleLifeFactory.getMonster(monsterId);
					}
					chance += theMonster.getLevel() * rowMonsterId;
				}
				ret.add(new DropEntry(itemId, questId, chance, amount));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			log.error("lulz", e);
		}
		drops.put(monsterId, ret);
		return ret;
	}

	public void clearDrops() {
		drops.clear();
	}

	public boolean isZakumBody(int monsterId) {
		return monsterId == 8800000 || monsterId == 8800001 || monsterId == 8800002;
	}

	public boolean isZakumArm(int monsterId) {
		return monsterId == 8800003 || monsterId == 8800004 || monsterId == 8800005 || monsterId == 8800006 || monsterId == 8800007 || monsterId == 8800008 || monsterId == 8800009 || monsterId == 8800010;
	}

	public boolean isZakum(int monsterId) {
		return isZakumBody(monsterId) || isZakumArm(monsterId);
	}
}
