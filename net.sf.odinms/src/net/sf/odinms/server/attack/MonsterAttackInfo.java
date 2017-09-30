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

package net.sf.odinms.server.attack;

import java.util.HashMap;
import java.util.Map;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Raz
 */
public class MonsterAttackInfo {

	private int monsterId;
	private byte attackId;
	private int mpConsume;
	private int mpBurn;
	private int disease;
	private byte level;
	private boolean deadlyAttack;
	protected static Map<Pair<Integer, Integer>, MonsterAttackInfo> monsterAttacks = new HashMap<Pair<Integer, Integer>, MonsterAttackInfo>();

	public MonsterAttackInfo() {
	}
	//tremble
	//jumpAttack
	//elemAttr
	//attackAfter
	//effectAfter
	//magic
	//type
	//doFirst
	//knockback
	//level

	public static MonsterAttackInfo loadFromData(MapleMonster monster, int attackId) {
		if (monster == null) {
			return null;
		}
		MonsterAttackInfo ret = monsterAttacks.get(new Pair<Integer, Integer>(monster.getId(), Integer.valueOf(attackId)));
		if (ret != null) {
			return ret;
		}
		ret = new MonsterAttackInfo();
		MapleData mobData = monster.getMobData();
		MapleData attackData = mobData.getChildByPath("attack" + attackId + "/" + "info");
		if (attackData == null) {
			return null;
		}

		//TODO HANDLE LINK INFO

		ret.monsterId = monster.getId();
		ret.attackId = (byte) attackId;
		ret.mpConsume = MapleDataTool.getIntConvert("conMP", attackData, 0);
		ret.mpBurn = MapleDataTool.getIntConvert("mpBurn", attackData, 0);
		ret.disease = MapleDataTool.getIntConvert("disease", attackData, 0);
		ret.level = (byte) MapleDataTool.getIntConvert("level", attackData, 0);
		ret.deadlyAttack = MapleDataTool.getIntConvert("deadlyAttack", attackData, 0) > 0;
		monsterAttacks.put(new Pair<Integer, Integer>(monster.getId(), Integer.valueOf(attackId)), ret);
		return ret;
	}

	public byte getAttackId() {
		return attackId;
	}

	public void setAttackId(byte attackId) {
		this.attackId = attackId;
	}

	public boolean isDeadlyAttack() {
		return deadlyAttack;
	}

	public void setDeadlyAttack(boolean deadlyAttack) {
		this.deadlyAttack = deadlyAttack;
	}

	public int getDisease() {
		return disease;
	}

	public void setDisease(int disease) {
		this.disease = disease;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getMpBurn() {
		return mpBurn;
	}

	public void setMpBurn(int mpBurn) {
		this.mpBurn = mpBurn;
	}

	public int getMpConsume() {
		return mpConsume;
	}

	public void setMpConsume(int mpConsume) {
		this.mpConsume = mpConsume;
	}
}
