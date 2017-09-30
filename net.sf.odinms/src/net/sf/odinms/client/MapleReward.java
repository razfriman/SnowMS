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

import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 * 
 * @author StellarAshes
 */
public class MapleReward {

    public int itemid;
    public short quantity;
    public int chance;

    /**
     * Creates a new MapleReward
     * @param id itemid
     * @param q quantity
     * @param c chance
     */
    public MapleReward(int id, int q, int c) {
	itemid = id;
	quantity = (short) q;
	chance = c;
    }

    /**
     * Chooses a reward for the character
     * @param rewards the list of possible MapleRewards
     * @param c the MapleCharacter to receive the reward
     */
    public static void giveReward(MapleReward rewards[], MapleCharacter c) {
	int totalChance = 0;
	for (MapleReward r : rewards) {
	    totalChance += r.chance;
	}

	int sel = (int) (Math.random() * (double) totalChance);
	for (int i = 0; i < rewards.length; i++) {
	    if (sel < rewards[i].chance) {
		MapleInventoryManipulator.addById(c.getClient(), rewards[i].itemid, rewards[i].quantity, "");
		c.getClient().getSession().write(MaplePacketCreator.getShowItemGain(rewards[i].itemid, rewards[i].quantity, true));
		return;
	    }
	    sel -= rewards[i].chance;
	}
    }
}
