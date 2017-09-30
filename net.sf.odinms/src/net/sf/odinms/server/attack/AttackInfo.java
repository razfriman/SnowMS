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

import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.tools.Pair;
/**
 *
 * @author Raz
 */
public class AttackInfo {

	private int numAttacked;
	private int numDamage;
	private int numAttackedAndDamage;
	private int skill;
	private int masteryId;
	private int stance;
	private int direction;
	private int charge;
	private int wSpeed = 4;
	private int projectile = 0;
	private int projectileDisplay;
	private AttackType attackType;
	private MapleCharacter player;
	private List<Pair<Integer, List<Integer>>> allDamage;

	public AttackInfo() {
	}

	public MapleStatEffect getAttackEffect(MapleCharacter chr, ISkill theSkill) {
		ISkill mySkill = theSkill;
		if (mySkill == null) {
			mySkill = SkillFactory.getSkill(skill);
		}
		int skillLvl = chr.getSkillLevel(mySkill);
		if (skillLvl == 0) {
			return null;
		}
		return mySkill.getEffect(skillLvl);
	}

	public MapleStatEffect getAttackEffect(MapleCharacter chr) {
		return getAttackEffect(chr, null);
	}

	public void splitNumAttackedAndDamage() {
		numAttacked = (numAttackedAndDamage >>> 4) & 0xF; // guess why there are no skills damaging more than 15 monsters...
		numDamage = numAttackedAndDamage & 0xF; // how often each single monster was attacked o.o
	}

	public int getNumAttacked() {
		return numAttacked;
	}

	public int getNumDamage() {
		return numDamage;
	}

	public int getNumAttackedAndDamage() {
		return numAttackedAndDamage;
	}

	public int getSkill() {
		return skill;
	}

	public int getStance() {
		return stance;
	}

	public int getDirection() {
		return direction;
	}

	public int getCharge() {
		return charge;
	}

	public int getWSpeed() {
		return wSpeed;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public int getProjectile() {
		return projectile;
	}

	public List<Pair<Integer, List<Integer>>> getAllDamage() {
		return allDamage;
	}

	public boolean addAllDamage(Pair<Integer, List<Integer>> damage) {
		return allDamage.add(damage);
	}

	public void setNumAttacked(int numAttacked) {
		this.numAttacked = numAttacked;
	}

	public void setNumDamage(int numDamage) {
		this.numDamage = numDamage;
	}

	public void setNumAttackedAndDamage(int numAttackedAndDamage) {
		this.numAttackedAndDamage = numAttackedAndDamage;
	}

	public void setSkill(int skill) {
		this.skill = skill;
	}

	public void setStance(int stance) {
		this.stance = stance;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public void setWSpeed(int wSpeed) {
		this.wSpeed = wSpeed;
	}

	public void setAllDamage(List<Pair<Integer, List<Integer>>> allDamage) {
		this.allDamage = allDamage;
	}

	public void setAttackType(AttackType attackType) {
		this.attackType = attackType;
	}

	public void setProjectile(int projectile) {
		this.projectile = projectile;
	}

	public int getProjectileDisplay() {
		return projectileDisplay;
	}

	public void setProjectileDisplay(int projectileDisplay) {
		this.projectileDisplay = projectileDisplay;
	}

	public int getMasteryId() {
		return masteryId;
	}

	public void setMasteryId(int masteryId) {
		this.masteryId = masteryId;
	}

	public MapleCharacter getPlayer() {
		return player;
	}

	public void setPlayer(MapleCharacter player) {
		this.player = player;
	}

	public static enum AttackType {

		CLOSE_RANGE,
		RANGED,
		MAGIC;
	}
}
