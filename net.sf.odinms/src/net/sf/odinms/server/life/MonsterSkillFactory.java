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
import java.util.Map;

import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.tools.ArrayMap;
import net.sf.odinms.tools.Pair;

/**
 * @author Raz
 */
public class MonsterSkillFactory {

	protected static final Map<Pair<Integer, Integer>, MonsterSkill> monsterSkills = new HashMap<Pair<Integer, Integer>, MonsterSkill>();
	private static MapleData skillRoot = MapleDataProviderFactory.getWzFile("Skill.wz").getData("MobSkill.img");

	public static MonsterSkill getMonsterSkill(int skillId, int level) {
		MonsterSkill ret = monsterSkills.get(new Pair<Integer, Integer>(Integer.valueOf(skillId), Integer.valueOf(level)));
		if (ret != null) {
			return ret;
		}
		synchronized (monsterSkills) {
			// see if someone else that's also synchronized has loaded the skill by now
			ret = monsterSkills.get(new Pair<Integer, Integer>(Integer.valueOf(skillId), Integer.valueOf(level)));
			if (ret == null) {
				MapleData skillData = skillRoot.getChildByPath(skillId + "/level/" + level);
				if (skillData != null) {
					ret = new MonsterSkill();
					for (int i = 0; i > -1; i++) {
						if (skillData.getChildByPath(String.valueOf(i)) == null) {
							break;
						}
						ret.toSummon.add(Integer.valueOf(MapleDataTool.getInt(skillData.getChildByPath(String.valueOf(i)), 0)));
					}
					ret.skillId = skillId;
					ret.skillLevel = level;
					ret.mpCon = MapleDataTool.getInt(skillData.getChildByPath("mpCon"), 0);
					ret.summonEffect = MapleDataTool.getInt(skillData.getChildByPath("summonEffect"), 0);
					ret.hp = MapleDataTool.getInt(skillData.getChildByPath("hp"), 100);
					ret.x = MapleDataTool.getInt("x", skillData, 1);
					ret.y = MapleDataTool.getInt("y", skillData, 0);
					ret.z = MapleDataTool.getInt("z", skillData, 0);
					int iprop = MapleDataTool.getInt("prop", skillData, 100);
					ret.prop = iprop / 100.0;
					ret.interval = MapleDataTool.getInt("interval", skillData, -1);//Also *1000?
					ret.limit = MapleDataTool.getInt("limit", skillData, -1);
					ret.duration = MapleDataTool.getInt("time", skillData, 0) * 1000;
					ret.lastUse = 0;
					ret.uses = 0;
					ret.delay = 0;
					ret.skillType = MonsterSkillType.NULL;
					ret.lt = MapleDataTool.getPoint("lt", skillData, null);
					ret.rb = MapleDataTool.getPoint("rb", skillData, null);

				}
				ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
				Map<MonsterStatus, Integer> monsterStatus = new ArrayMap<MonsterStatus, Integer>();
				switch (skillId) {
					//100 - MONSTER BUFF - SINGLE PLAYER\\
					case 100:
						monsterStatus.put(MonsterStatus.WEAPON_ATTACK_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 101:
						monsterStatus.put(MonsterStatus.MAGIC_ATTACK_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 102:
						monsterStatus.put(MonsterStatus.WEAPON_DEFENSE_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 103:
						monsterStatus.put(MonsterStatus.MAGIC_DEFENSE_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					//110 - MONSTER BUFF - ALL PLAYERS\\
					case 110:
						monsterStatus.put(MonsterStatus.WEAPON_ATTACK_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 111:
						monsterStatus.put(MonsterStatus.MAGIC_ATTACK_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 112:
						monsterStatus.put(MonsterStatus.WEAPON_DEFENSE_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 113:
						monsterStatus.put(MonsterStatus.MAGIC_DEFENSE_UP, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 114:
						ret.skillType = MonsterSkillType.MONSTER_HEAL;
						break;
					//120 - PLAYER DEBUFF\\
					case 120:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SEAL, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 121:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKNESS, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 122:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WEAKEN, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 123:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.STUN, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 124:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.CURSE, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 125:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.POISON, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 126:
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SLOW, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 127:// Dispel
						//Skill #127 : Fatal Attack. (Reduce HP & MP to 1)
						break;
					case 128:// Seduce
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SEDUCE, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
					case 129: // SendToTown (Banish)
                        ret.skillType = MonsterSkillType.SEND_PLAYER_TO_TOWN;
						break;
					//130 - ETC\\
					case 131: // Poison Mists
						break;
					case 132: // Crazy Skull
						statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.CRAZY_SKULL, ret.getX()));//GOOD
						ret.skillType = MonsterSkillType.DEBUFF;
						break;
                    case 133: // Zombify
                        break;
					case 140:
						monsterStatus.put(MonsterStatus.WEAPON_IMMUNITY, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 141:
						monsterStatus.put(MonsterStatus.MAGIC_IMMUNITY, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					//150 - MONSTER CARNIVAL\\
					case 150:
                        //McWeaponAttackUp
                        break;
					case 151:
                        //McMagicAttackUp
                        break;
					case 152:
                        //McWeaponDefenseUp
                        break;
					case 153:
                        //McMagicDefenseUp
                        break;
					case 154:
                        //McAccuracyUp
                        break;
					case 155:
                        //McAvoidUp
                        break;
					case 156:
                        //McSpeedUp
                        monsterStatus.put(MonsterStatus.SPEED, ret.getX());
						ret.skillType = MonsterSkillType.MONSTER_BUFF;
						break;
					case 157:
                        //Unknown
						break;
					case 200:
						ret.skillType = MonsterSkillType.MONSTER_SUMMON;
						break;
					default:
						break;
				}
				ret.monsterStatus = monsterStatus;
				ret.statups = statups;
				monsterSkills.put(new Pair<Integer, Integer>(Integer.valueOf(skillId), Integer.valueOf(level)), ret);
			}
			return ret;
		}
	}
}
