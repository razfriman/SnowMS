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
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Danny (Leifde)
 */
public class PetDataFactory {
	
	private static MapleDataProvider dataRoot = MapleDataProviderFactory.getWzFile("Item.wz");
	private static final Map<Pair<Integer, Integer>, PetCommand> petCommands = new HashMap<Pair<Integer, Integer>, PetCommand>();
	private static final Map<Integer, Integer> petHunger = new HashMap<Integer, Integer>();
	
	/**
	 * Gets a petCommand
	 * @param petId petId of the pet
	 * @param skillId skillid of the command
	 * @return a new PetCommand of the skill and pet
	 */
	public static PetCommand getPetCommand(int petId, int skillId) {
		PetCommand ret = petCommands.get(new Pair<Integer, Integer>(Integer.valueOf(petId), Integer.valueOf(skillId)));
		if (ret != null) {
			return ret;
		}
		synchronized (petCommands) {
			// see if someone else that's also synchronized has loaded the skill by now
			ret = petCommands.get(new Pair<Integer, Integer>(Integer.valueOf(petId), Integer.valueOf(skillId)));
			if (ret == null) {
				MapleData skillData = dataRoot.getData("Pet/" + petId + ".img");
				int prob = 0;
				int inc = 0;
				if (skillData != null) {
					prob = MapleDataTool.getInt("interact/" + skillId + "/prob", skillData, 0);
					inc = MapleDataTool.getInt("interact/" + skillId + "/inc", skillData, 0);
				}
				ret = new PetCommand(petId, skillId, prob, inc);
				petCommands.put(new Pair<Integer, Integer>(Integer.valueOf(petId), Integer.valueOf(skillId)), ret);
			}
			return ret;
		}
	}
	
	/**
	 * 
	 * @param petId 
	 * @return the pet's hunger.
	 */
	public static int getHunger(int petId) {
		Integer ret = petHunger.get(Integer.valueOf(petId));
		if (ret != null) {
			return ret;
		}
		synchronized (petHunger) {
			ret = petHunger.get(Integer.valueOf(petId));
			if (ret == null) {
				MapleData hungerData = dataRoot.getData("Pet/" + petId + ".img").getChildByPath("info/hungry");
				ret = Integer.valueOf(MapleDataTool.getInt(hungerData, 1));
			}
			return ret;
		}
	}
}