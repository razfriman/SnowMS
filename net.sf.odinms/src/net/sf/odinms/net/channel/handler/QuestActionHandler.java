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

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class QuestActionHandler extends AbstractMaplePacketHandler {
	
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte action = slea.readByte();
		short quest = slea.readShort();
		MapleCharacter player = c.getPlayer();
		if (action == 1) { //START QUEST
			int npc = slea.readInt();
			MapleQuest.getInstance(quest).start(player, npc);
		} else if (action == 2) { //COMPLETE QUEST
			int npc = slea.readInt();
			if (slea.available() >= 4) {
				int selection = slea.readInt();
				MapleQuest.getInstance(quest).complete(player, npc, selection);
			} else {
				MapleQuest.getInstance(quest).complete(player, npc);
			}
			// c.getSession().write(MaplePacketCreator.completeQuest(c.getPlayer(), quest));
			//c.getSession().write(MaplePacketCreator.updateQuestInfo(c.getPlayer(), quest, npc, (byte)14));
			// 6 = start quest
			// 7 = unknown error
			// 8 = equip is full
			// 9 = not enough mesos
			// 11 = due to the equipment currently being worn wtf o.o
			// 12 = you may not posess more than one of this item
		} else if (action == 3) { // forfeit quest
			MapleQuest.getInstance(quest).forfeit(player);
		} else if (action == 4) {
			int npc = slea.readInt();
			//91 ff 12 01//18022289
		} else {
			System.out.println("Action:" + action + " Quest:" + quest + " Packet:" + slea.toString());
		}
	}
}
