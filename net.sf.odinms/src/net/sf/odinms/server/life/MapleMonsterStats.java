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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.odinms.tools.Pair;

/**
 * Bean ^__^ that holds monster stats - setters shouldn't be called after loading is complete.
 * 
 * @author Frz
 */
public class MapleMonsterStats {
	private int exp;
	private int hp, mp;
	private int level;
	private boolean boss;
	private boolean miniBoss;
	private boolean explosive;
	private boolean ffaLoot;
	private boolean undead;
	private String name;
	private Map<String, Integer> animationTimes = new HashMap<String, Integer>();
	private Map<Element, ElementalEffectiveness> resistance = new HashMap<Element, ElementalEffectiveness>();
	private List<Integer> revives = Collections.emptyList();
	private int removeAfter;
	private byte tagColor;
	private byte tagBgColor;
	private int summonType;
	private int summonEffect;
	private boolean AutoAggro;
	private int fixedDamage;
	private int buffToGive;
	private List<Pair<Integer, Integer>> skillEntries = new ArrayList<Pair<Integer, Integer>>();
	private List<MonsterSkill> skills = new ArrayList<MonsterSkill>();
	private int CPGain;
	private int dropItemPeriod;
    private MapleMonsterBanishInfo banishInfo;
    private boolean damagedByMob;

	public int getDropItemPeriod() {
		return dropItemPeriod;
	}

	public void setDropItemPeriod(int dropItemPeriod) {
		this.dropItemPeriod = dropItemPeriod;
	}

    public boolean isDamagedByMob() {
        return damagedByMob;
    }

    public void setDamagedByMob(boolean damagedByMob) {
        this.damagedByMob = damagedByMob;
    }

    public MapleMonsterBanishInfo getBanishInfo() {
        return banishInfo;
    }

    public void setBanishInfo(MapleMonsterBanishInfo banishInfo) {
        this.banishInfo = banishInfo;
    }

	public int getCPGain() {
		return CPGain;
	}

	public void setCPGain(int CPGain) {
		this.CPGain = CPGain;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setBoss(boolean boss) {
		this.boss = boss;
	}
	
	public void setMiniBoss(boolean miniBoss) {
	    this.miniBoss = miniBoss;
	}
	
	public void setExplosive(boolean Explosive) {
		this.explosive = Explosive;
	}
        
	public boolean isExplosive() {
		return explosive;
	}

	public boolean isBoss() {
		return boss;
	}
	
	public boolean isMiniBoss() {
		return miniBoss;
	}

	public void setFfaLoot(boolean ffaLoot){
	    this.ffaLoot = ffaLoot;
	}
	
	public boolean isFfaLoot(){
	    return ffaLoot;
	}
	
	public int getBuffToGive() {
	    return buffToGive;
	}
	
	public void setBuffToGive(int buffToGive) {
	    this.buffToGive = buffToGive;
	}

	public void setAnimationTime(String name, int delay) {
		animationTimes.put(name, delay);
	}

	public int getAnimationTime(String name) {
		Integer ret = animationTimes.get(name);
		if (ret == null) {
			return 500;
		}
		return ret.intValue();
	}
	
	public boolean isMobile() {
		return animationTimes.containsKey("move") || animationTimes.containsKey("fly");
	}

	public boolean canFly() {
	    return animationTimes.containsKey("fly");
	}

	public List<Integer> getRevives() {
		return revives;
	}

	public void setRevives(List<Integer> revives) {
		this.revives = revives;
	}

	public void setUndead(boolean undead) {
		this.undead = undead;
	}

	public boolean isUndead() {
		return undead;
	}
	
	public void setEffectiveness (Element e, ElementalEffectiveness ee) {
		resistance.put(e, ee);
	}
	
	public ElementalEffectiveness getEffectiveness (Element e) {
		ElementalEffectiveness elementalEffectiveness = resistance.get(e);
		if (elementalEffectiveness == null) {
			return ElementalEffectiveness.NORMAL;
		} else {
			return elementalEffectiveness;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public byte getTagColor() {
		 return tagColor;
	}
	
	public void setTagColor(int tagColor) {
		this.tagColor = (byte) tagColor;
	}
	
	public byte getTagBgColor() {
		return tagBgColor;
	}
        
	public void setTagBgColor(int tagBgColor) {
		this.tagBgColor = (byte) tagBgColor;
	}
	
	public void setRemoveAfter(int removeAfter) {
	    this.removeAfter = removeAfter;
	}
	
	public int getRemoveAfter() {
	    return removeAfter;
	}
	
	public void setSummonType(int summonType) {
	    this.summonType = summonType;
	}
	
	public int getSummonType() {
	    return summonType;
	}
	
	public void setSummonEffect(int summonEffect) {
	    this.summonEffect = summonEffect;
	}
	
	public int getSummonEffect() {
	    return summonEffect;
	}
	
	public void setAutoAggro(boolean AutoAggro) {
	    this.AutoAggro = AutoAggro;
	}

	public boolean isAutoAggro() {
	    return AutoAggro;
	}
	
	public void setFixedDamage(int fixedDamage) {
	    this.fixedDamage = fixedDamage;
	}
	
	public int getFixedDamage() {
	    return fixedDamage;
	}
	
	public void setSkillEntries(List<Pair<Integer, Integer>> skillEntries) {
		this.skillEntries = skillEntries;
	}
	
	public void addSkill(Pair<Integer, Integer> skillEntry) {
	    this.skillEntries.add(skillEntry);
	}
	
	public List<Pair<Integer, Integer>> getSkillEntries() {
		return Collections.unmodifiableList(skillEntries);
	}
	
	public int getSkillEntrySize() {
		return skillEntries.size();
	}
	
	public boolean hasSkillEntry(int skillId, int skillLevel) {
	    for(Pair<Integer, Integer> skillEntry : skillEntries) {
		if(skillEntry.getLeft() == skillId && skillEntry.getRight() == skillLevel)
		    return true;
	    }
	    return false;
	}
	
	public void loadSkills() {
	    for(Pair<Integer, Integer> skillEntry : getSkillEntries()) {
		    MonsterSkill skill = MonsterSkillFactory.getMonsterSkill(skillEntry.getLeft(), skillEntry.getRight());
			skills.add(skill);
		    }
	}
	
	public List<MonsterSkill> getSkills() {
	    return skills;
	}
}
