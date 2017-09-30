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

package net.sf.odinms.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;

/**
 *
 * @author Matze
 */
public class MonsterSearch implements DataSearch {

	private List<Integer> results = new ArrayList<Integer>();
	private static MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
	private String name = null;
	private int minLevel = -1;
	private int maxLevel = -1;
	private int minHp = -1;
	private int maxHp = -1;
	private int minMp = -1;
	private int maxMp = -1;
	private int minExp = -1;
	private int maxExp = -1;
	private int skillId = -1;
	private int skillLevel = -1;
	private boolean statConditions = false;
	
	public List<Integer> executeQuery() {
		results.clear();
		MapleData root = stringData.getData("Mob.img");
		String lowerName = name;
		if (lowerName != null) {
			lowerName = name.toLowerCase();
		}
		for (MapleData mob : root.getChildren()) {
			try {
				if (lowerName != null && !(MapleDataTool.getString("name", mob, "").toLowerCase().contains(lowerName)))
					continue;
				MapleMonster monster = MapleLifeFactory.getMonster(Integer.parseInt(mob.getName()));
				
				if (statConditions) {
					if (minLevel > -1 && monster.getLevel() < minLevel)
						continue;
					if (maxLevel > -1 && monster.getLevel() > maxLevel)
						continue;
					if (minHp > -1 && monster.getHp() < minHp)
						continue;
					if (maxHp > -1 && monster.getHp() > maxHp)
						continue;
					if (minMp > -1 && monster.getMp() < minMp)
						continue;
					if (maxMp > -1 && monster.getMp() > maxMp)
						continue;
					if (minExp > -1 && monster.getExp() < minExp)
						continue;
					if (maxExp > -1 && monster.getExp() > maxExp)
						continue;
					if(skillId >  -1 && skillLevel > -1 && !monster.hasSkillEntry(skillId, skillLevel))
						continue;
				}
			} catch (Exception e) {
				continue;
			}
			results.add(Integer.parseInt(mob.getName()));
		}
		SearchCache.getInstance().add(this);
		return results;
	}

	public List<Integer> getResults() {
		return results;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		if (this.minLevel > -1)
			this.statConditions = true;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		if (this.maxLevel > -1)
			this.statConditions = true;
	}

	public int getMinHp() {
		return minHp;
	}

	public void setMinHp(int minHp) {
		this.minHp = minHp;
		if (this.minHp > -1)
			this.statConditions = true;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
		if (this.maxHp > -1)
			this.statConditions = true;
	}

	public int getMinMp() {
		return minMp;
	}

	public void setMinMp(int minMp) {
		this.minMp = minMp;
		if (this.minMp > -1)
			this.statConditions = true;
	}

	public int getMaxMp() {
		return maxMp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
		if (this.maxMp > -1)
			this.statConditions = true;
	}

	public int getMinExp() {
		return minExp;
	}

	public void setMinExp(int minExp) {
		this.minExp = minExp;
		if (this.minExp > -1)
			this.statConditions = true;
	}

	public int getMaxExp() {
		return maxExp;
	}

	public void setMaxExp(int maxExp) {
		this.maxExp = maxExp;
		if (this.maxExp > -1)
			this.statConditions = true;
	}
	
	public int getSkillId() {
	    return skillId;
	}
	
	public void setSkillId(int skillId) {
	    this.skillId = skillId;
	    if(this.skillId > -1) {
		this.statConditions = true;
	    }
	}
	
	public int getSkillLevel() {
	    return skillLevel;
	}
	
	public void setSkillLevel(int skillLevel) {
	    this.skillLevel = skillLevel;
	    if(this.skillLevel > -1) {
		this.statConditions = true;
	    }
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MonsterSearch)) return false;
		MonsterSearch o = (MonsterSearch) obj;
		return (name == null && o.name == null || name != null && o.name != null && name.equals(o.name)) &&
			minLevel == o.minLevel && maxLevel == o.maxLevel && minHp == o.minHp && maxHp == o.maxHp &&
			minMp == o.minMp && maxMp == o.maxMp && minExp == o.minExp && maxExp == o.maxExp && skillId == o.skillId && skillLevel == o.skillLevel;
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : 0) + minLevel + maxLevel * 2 + minHp * 3 + maxHp * 4 + minMp * 5 + maxMp * 6 + minExp * 7 + maxExp * 8 + skillId * 9 + skillLevel * 11;
	}

	@Override
	public String toString() {
		StringBuilder sInfo = new StringBuilder("Monster [");
		if (name != null && name.length() > 0) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("name=");
			sInfo.append(name);
		}
		if (minLevel > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("minLevel=");
			sInfo.append(minLevel);			
		}
		if (maxLevel > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("maxLevel=");
			sInfo.append(maxLevel);			
		}
		if (minHp > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("minHp=");
			sInfo.append(minHp);			
		}
		if (maxHp > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("maxHp=");
			sInfo.append(maxHp);			
		}
		if (minMp > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("minMp=");
			sInfo.append(minMp);			
		}
		if (maxMp > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("maxMp=");
			sInfo.append(maxMp);			
		}
		if (minExp > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("minExp=");
			sInfo.append(minExp);			
		}
		if (maxExp > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("maxExp=");
			sInfo.append(maxExp);			
		}
		if (skillId > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("skillId=");
			sInfo.append(skillId);
		}
		if (skillLevel > -1) {
			if (sInfo.length() > 11) sInfo.append(",");
			sInfo.append("skillLevel=");
			sInfo.append(skillLevel);
		}
		sInfo.append("]");
		return sInfo.toString();
	}

}
