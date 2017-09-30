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

package net.sf.odinms.client;

import java.util.HashMap;
import java.util.Map;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.tools.StringUtil;

public class SkillFactory {
	private static final Map<Integer, ISkill> skills = new HashMap<Integer, ISkill>();
	private static MapleDataProvider datasource = MapleDataProviderFactory.getWzFile("Skill.wz");
	private static MapleData stringData = MapleDataProviderFactory.getWzFile("String.wz").getData("Skill.img");

	/**
	 * Get a skill
	 * @param id the skillid.
	 * @return An ISkill of id.
	 */
	public static ISkill getSkill(int id) {
		ISkill ret = skills.get(Integer.valueOf(id));
		if (ret != null) {
			return ret;
		}
		synchronized (skills) {
			// see if someone else that's also synchronized has loaded the skill by now
			ret = skills.get(Integer.valueOf(id));
			if (ret == null) {
				int job = id / 10000;
				MapleData skillroot = datasource.getData(StringUtil.getLeftPaddedStr(String.valueOf(job), '0', 3) + ".img");
				MapleData skillData = skillroot.getChildByPath("skill/" + StringUtil.getLeftPaddedStr(String.valueOf(id), '0', 7));
				if (skillData != null) {
					ret = Skill.loadFromData(id, skillData);
				}
				skills.put(Integer.valueOf(id), ret);
			}
			return ret;
		}
	}

	/**
	 * Get the skill's name
	 * @param id the skill's id
	 * @return the skill's name
	 */
	public static String getSkillName(int id){
	    String strId = Integer.toString(id);
	    strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
	    MapleData skillroot = stringData.getChildByPath(strId);
	    if (skillroot != null) {
	    	return MapleDataTool.getString(skillroot.getChildByPath("name"), "");
	    }
	    return null;
	}
}
