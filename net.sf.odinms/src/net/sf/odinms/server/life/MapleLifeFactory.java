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

package net.sf.odinms.server.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.provider.wz.MapleDataType;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleLifeFactory {
	private static Logger log = LoggerFactory.getLogger(MapleMapFactory.class);
	private static MapleDataProvider mobData = MapleDataProviderFactory.getWzFile("Mob.wz");
	private static MapleDataProvider stringDataWZ = MapleDataProviderFactory.getWzFile("String.wz");
	private static MapleDataProvider npcData = MapleDataProviderFactory.getWzFile("Npc.wz");
	private static MapleDataProvider etcDataWZ = MapleDataProviderFactory.getWzFile("Etc.wz");
	private static MapleData mobStringData = stringDataWZ.getData("Mob.img");
	private static MapleData npcStringData = stringDataWZ.getData("Npc.img");
	private static MapleData npcLocationData = etcDataWZ.getData("NpcLocation.img");
	private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<Integer, MapleMonsterStats>();

	public static AbstractLoadedMapleLife getLife(int id, String type) {
		if (type.equalsIgnoreCase("n")) {
			return getNPC(id);
		} else if (type.equalsIgnoreCase("m")) {
			return getMonster(id);
		} else {
			log.warn("Unknown Life type: {}", type);
			return null;
		}
	}
	
	public static MapleData getMobData(int mid) {
	    return mobData.getData(StringUtil.getLeftPaddedStr(Integer.toString(mid), '0', 7) + ".img");
	}
	
	public static MapleMonster getMonster (int mid) {
		MapleMonsterStats stats = monsterStats.get(Integer.valueOf(mid));
		if (stats == null) {
			MapleData monsterData = mobData.getData(StringUtil.getLeftPaddedStr(Integer.toString(mid) + ".img", '0', 11));
			if (monsterData == null) {
				return null;
			}
			MapleData monsterInfoData = monsterData.getChildByPath("info");
			stats = new MapleMonsterStats();
			stats.setHp(MapleDataTool.getIntConvert("maxHP", monsterInfoData));
			stats.setMp(MapleDataTool.getIntConvert("maxMP", monsterInfoData));
			stats.setExp(MapleDataTool.getIntConvert("exp", monsterInfoData, 0));
			stats.setLevel(MapleDataTool.getIntConvert("level", monsterInfoData));
			stats.setBoss (MapleDataTool.getIntConvert("boss", monsterInfoData, 0) > 0);
			stats.setExplosive(MapleDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0);
			stats.setFfaLoot(MapleDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0);

            MapleData firstAttackData = monsterInfoData.getChildByPath("firstAttack");
            if (firstAttackData != null) {
				if (firstAttackData.getType() == MapleDataType.FLOAT) {
					stats.setAutoAggro(MapleDataTool.getFloat(firstAttackData) > 0);
				} else {
					stats.setAutoAggro(MapleDataTool.getIntConvert(firstAttackData) > 0);
				}
			}
            try {
			    stats.setAutoAggro(MapleDataTool.getIntConvert("firstAttack", monsterInfoData, 0) > 0);
			} catch(ClassCastException cce) {//Some monster have firstAttack as float rather than int
			    stats.setAutoAggro(MapleDataTool.getFloat(monsterInfoData.getChildByPath("firstAttack"), 0) > 0);
			}
			stats.setUndead (MapleDataTool.getIntConvert("undead", monsterInfoData, 0) > 0);
			stats.setName(MapleDataTool.getString(mid + "/name", mobStringData, "MISSINGNO"));
			stats.setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", monsterInfoData, -1));
			stats.setSummonType(MapleDataTool.getIntConvert("summonType", monsterInfoData, 0));
			stats.setBuffToGive(MapleDataTool.getIntConvert("buff", monsterInfoData, -1));
			stats.setCPGain(MapleDataTool.getIntConvert("getCP", monsterInfoData, -1));
            stats.setDamagedByMob(MapleDataTool.getIntConvert("damagedByMob", monsterInfoData, 0) > 0);
			stats.setDropItemPeriod(MapleDataTool.getIntConvert("dropItemPeriod", monsterInfoData, -1));
			stats.setSummonEffect(0);

            MapleData banishData = monsterInfoData.getChildByPath("ban");
            if (banishData != null) {
                String message = MapleDataTool.getString("banMsg", banishData);
                int mapid = MapleDataTool.getInt("banMap/0/field", banishData, -1);
                String portal = MapleDataTool.getString("banMap/0/portal", banishData, "sp");
                stats.setBanishInfo(new MapleMonsterBanishInfo(message, mapid, portal));
            }
			//fixedDamage
			//onlyNormalAttack
			//hpRecovery
			//mpRecovery
			//noregen
			//speed
			//chaseSpeed
			//selfDestruction - action - hp - removeAfter
			//loseItem - id - prop - x
			//damagedByMob
			//damagedBySelectedMob
			//doNotRemove
			if (stats.isBoss()) {
				MapleData hpTagColor = monsterInfoData.getChildByPath("hpTagColor");
				MapleData hpTagBgColor = monsterInfoData.getChildByPath("hpTagBgcolor");
				if (hpTagBgColor == null || hpTagColor == null) {
					log.trace("Monster " + stats.getName() + " (" + mid + ") flagged as boss without boss HP bars.");
					stats.setTagColor(0);
					stats.setTagBgColor(0);
				} else {
					stats.setTagColor(MapleDataTool.getIntConvert("hpTagColor", monsterInfoData));
					stats.setTagBgColor(MapleDataTool.getIntConvert("hpTagBgcolor", monsterInfoData));
				}
			}
			
			for (MapleData idata : monsterData) {
				if (!idata.getName().equals("info")) {
					int delay = 0;
					for (MapleData pic : idata.getChildren()) {
						delay += MapleDataTool.getIntConvert("delay", pic, 0);
					}
					stats.setAnimationTime(idata.getName(), delay);
				}
			}
			
			MapleData reviveInfo = monsterInfoData.getChildByPath("revive");
			if (reviveInfo != null) {
				List<Integer> revives = new LinkedList<Integer>();
				for (MapleData mData : reviveInfo) {			
					revives.add(MapleDataTool.getInt(mData));		
				}
				stats.setRevives(revives);
			}
			
			MapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
			if (monsterSkillData != null) {
				int i = 0;
				List<Pair<Integer, Integer>> skills = new ArrayList<Pair<Integer, Integer>>();
				while(monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
				    int skill = MapleDataTool.getIntConvert(i + "/skill", monsterSkillData, 0);
				    int level = MapleDataTool.getIntConvert(i + "/level", monsterSkillData, 0);
					skills.add(new Pair<Integer, Integer>(skill, level));
					i++;
				}
				stats.setSkillEntries(skills);
				stats.loadSkills();
			}
			
			decodeElementalString(stats, MapleDataTool.getString("elemAttr", monsterInfoData, ""));
			
			monsterStats.put(Integer.valueOf(mid), stats);
		}
		MapleMonster ret = new MapleMonster(mid, stats);
		return ret;
	}

	public static void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
		for (int i = 0; i < elemAttr.length(); i += 2) {
			Element e = Element.getFromChar(elemAttr.charAt(i));
			ElementalEffectiveness ee = ElementalEffectiveness.getByNumber(Integer.valueOf(String.valueOf(elemAttr.charAt(i + 1))));
			stats.setEffectiveness(e, ee);
		}
	}

	public static MapleNPC getNPC(int nid) {
		MapleNPCStats stats = new MapleNPCStats();
		MapleData npcStringInfoData = npcStringData.getChildByPath(Integer.toString(nid));
		MapleData npcImgData = npcData.getData(StringUtil.getLeftPaddedStr(Integer.toString(nid), '0', 7) + ".img");

		stats.setName(MapleDataTool.getString("name", npcStringInfoData, "MISSINGNO"));
		stats.setFunction(MapleDataTool.getString("func", npcStringInfoData, null));

		MapleData npcInfoData = npcImgData.getChildByPath("info");

		if (npcInfoData != null) {
		    stats.setTrunkPut(MapleDataTool.getIntConvert("trunkPut", npcInfoData, 0));
		    stats.setGuildRank(MapleDataTool.getIntConvert("guildRank", npcInfoData, 0));
		    try {
			    stats.setScript(MapleDataTool.getString("script/0/script", npcInfoData, null));
		    } catch (Exception e) {
			    stats.setScript(null);
		    }
		}

		//Load Location Data
		MapleData npcLocationInfoData = npcLocationData.getChildByPath(Integer.toString(nid));
		if (npcLocationInfoData != null) {
			for (MapleData map : npcLocationInfoData.getChildren()) {
				int mapid = MapleDataTool.getInt(map, -1);
				if (mapid != -1) {
					stats.addMap(mapid);
				}
			}
		}
		return new MapleNPC(nid, stats);
	}
}
