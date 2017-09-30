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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.Randomizer;

/**
 *
 * @author Raz
 */
public class MonsterSkill {
	
	public int skillId;
	public int skillLevel;
	public int mpCon;
	public Map<MonsterStatus, Integer> monsterStatus;
	public List<Pair<MapleBuffStat, Integer>> statups;
	public List<Integer> toSummon = new ArrayList<Integer>();
	public int summonEffect;
	public double prop;
	public int hp;
	public int x, y, z;
	public int interval;
	public long lastUse;
	public int limit;
	public int uses;
	public int duration;
	public int delay;
	public Point lt, rb;
	public MonsterSkillType skillType;
	private MapleStatEffect statEffect = null;
	
	public MonsterSkill() {
	    
	}
	
	public int getSkillId() {
		return skillId;
	}
	
	public int getSkillLevel() {
		return skillLevel;
	}
	
	public int getMpCon() {
		return mpCon;
	}
	
	public Map<MonsterStatus, Integer> getMonsterStatus() {
		return monsterStatus;
	}
	
	public List<Integer> getSummons() {
		return toSummon;
	}
	
	public int getSummonEffect() {
		return summonEffect;
	}
	
	public double getProp() {
	    return prop;
	}
	
	public int getHp() {
	    return hp;
	}
	
	public int getX() {
	    return x;
	}
	
	public int getY() {
	    return y;
	}
	
	public int getZ() {
	    return z;
	}
	
	public int getInterval() {
	    return interval;
	}
	
	public long getLastUse() {
	    return lastUse;
	}
	
	public int getLimit() {
	    return limit;
	}
	
	public int getUses() {
	    return uses;
	}
	
	public long getDuration() {
	    return duration;
	}
	
	public int getDelay() {
	    return delay;
	}
	
	public Point getLt() {
		return lt;
	}
	
	public Point getRb() {
		return rb;
	}
	
	public MonsterSkillType getSkillType() {
	    return skillType;
	}
	
	public MapleStatEffect getStatEffect() {
	    statEffect = MapleStatEffect.getEmptyStatEffect();
	    statEffect.setStatType(MapleStatEffect.MapleStatEffectType.MONSTER_SKILL);
	    statEffect.setDuration(duration);
	    statEffect.setStatsups(statups);
	    statEffect.setSourceid(skillId);
	    statEffect.setSourceLevel(skillLevel);
	    statEffect.setX(x);
	    statEffect.setY(y);
	    statEffect.setZ(z);
	    statEffect.setDelay(delay);
	    return statEffect;
	}
	
	public Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
		Point mylt;
		Point myrb;
		if (facingLeft) {
			mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
			myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
		} else {
			myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
			mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
		}
		Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
		return bounds;
	}
	
	public List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
		Rectangle bounds = calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
		List<MapleMapObjectType> objectTypes = new ArrayList<MapleMapObjectType>();
		objectTypes.add(objectType);
		return monster.getMap().getMapObjectsInBox(bounds, objectTypes);
	}
	
	public boolean makeChanceResult() {
		return prop == 1.0 || Randomizer.randomBoolean(prop);
	}
	
	public void setDelay(int delay) {
	    this.delay = delay;
	}
	
	public void setLtRb(Point lt, Point rb) {
		this.lt = lt;
		this.rb = rb;
	}
	
	
}
