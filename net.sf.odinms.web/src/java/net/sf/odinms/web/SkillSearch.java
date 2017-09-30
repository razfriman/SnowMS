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
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;

/**
 *
 * @author Matze
 */
public class SkillSearch implements DataSearch {

	private List<Integer> results = new ArrayList<Integer>();
	private static MapleData stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")).getData("Skill.img");
	private String name = null;
	private int job = -1;
	
	public List<Integer> executeQuery() {
		results.clear();
		String lowerName = name;
		if (lowerName != null) {
			lowerName = name.toLowerCase();
		}
		for (MapleData skillD : stringData.getChildren()) {
			try {
				if (lowerName != null && !(MapleDataTool.getString("name", skillD, "").toLowerCase().contains(lowerName)))
					continue;
			} catch (Exception e) {
				continue;
			}
			int skillId = Integer.parseInt(skillD.getName());
			if (job != -1 && skillId / 10000 != job) {
				continue;
			}
			results.add(skillId);
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
	
	public void setJob(int job) {
		this.job = job;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SkillSearch)) return false;
		SkillSearch o = (SkillSearch) obj;
		boolean nameEquals = name == o.name;
		if (!nameEquals && name != null) {
			nameEquals = name.equals(o.name);
		}
		return nameEquals && o.job == this.job;
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() + 13 * job: 0);
	}

	@Override
	public String toString() {
		StringBuilder sInfo = new StringBuilder("Skill [");
		if (name != null && name.length() > 0) {
			if (sInfo.length() > 9) sInfo.append(",");
			sInfo.append("name=");
			sInfo.append(name);
		}
		sInfo.append("]");
		return sInfo.toString();
	}
}