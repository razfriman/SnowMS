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

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class UseMountFoodHandler extends AbstractMaplePacketHandler {

     @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
	byte slot = (byte) slea.readShort();
	int itemid = slea.readInt();
	IItem item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
	if (item.getItemId() != itemid || item.getQuantity() == 0) {
	    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
	    return; 
	}
	//TODO - STUFF
	MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
     }
}
