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
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
*
* @author Raz
*/
public class UseCatchItemHandler extends AbstractMaplePacketHandler {

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
		byte slot = (byte) slea.readShort();
		int itemId = slea.readInt();
		int moid = slea.readInt();
		MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(moid);
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		IItem item = c.getPlayer().getInventory(MapleInventoryType.USE).findById(itemId);
		if (item.getQuantity() < 1) {
		    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
			return;
		} else if (ii.getMobActivateId(itemId) != mob.getId()) {
			c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
			return;
		}

		MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
		if (mob != null && mob.getHpPercent() <= ii.getMobActivateHP(itemId)) {
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.catchMonster(moid, itemId, true));
				c.getPlayer().getMap().killMonster(mob, c.getPlayer());//should it still give exp?
				int createItemId = ii.getCreateItem(itemId);
				if (createItemId > -1) {
					MapleInventoryManipulator.addById(c, createItemId, (short) 1, "Adding create item");
				}
		} else {
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.catchMonster(moid, itemId, false));
				c.getSession().write(MaplePacketCreator.serverNotice(5, "You cannot catch the monster as it is too strong."));//TODO TO FIX TO ORIGINAL MESSAGE
		}
		c.getSession().write(MaplePacketCreator.enableActions());
	}
}