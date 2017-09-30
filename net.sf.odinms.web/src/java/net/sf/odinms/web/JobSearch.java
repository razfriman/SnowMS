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
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Matze
 */
public class JobSearch implements DataSearch {

	private List<Integer> results = new ArrayList<Integer>();
	private static MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
	private String name = null;
	
	public List<Integer> executeQuery() {
		results.clear();
		MapleData root = stringData.getData("Skill.img");
		String lowerName = name;
		if (lowerName != null) {
			name = (StringUtil.getLeftPaddedStr(name, '0', 3));//TODO - REMOVE??
			lowerName = name.toLowerCase();
		}
		for (MapleData jobD : root.getChildren()) {
			try {
				if (lowerName != null && !(MapleDataTool.getString("bookName", jobD, "").toLowerCase().contains(lowerName)))
					continue;
				
				
			} catch (Exception e) {
				continue;
			}
			results.add(Integer.parseInt(jobD.getName()));
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JobSearch)) return false;
		JobSearch o = (JobSearch) obj;
		return name.equals(o.name);
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}

	@Override
	public String toString() {
		StringBuilder sInfo = new StringBuilder("Job [");
		if (name != null && name.length() > 0) {
			if (sInfo.length() > 9) sInfo.append(",");
			sInfo.append("name=");
			sInfo.append(name);
		}
		sInfo.append("]");
		return sInfo.toString();
	}

}
