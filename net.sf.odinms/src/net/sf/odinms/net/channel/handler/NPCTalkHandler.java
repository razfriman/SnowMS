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

import java.awt.Point;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class NPCTalkHandler extends AbstractMaplePacketHandler {
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {			
		int oid = slea.readInt();
		Point playerPos = slea.readPoint();
		MapleNPC npc = (MapleNPC) c.getPlayer().getMap().getMapObject(oid);
		NPCScriptManager.getInstance().dispose(c);

		if (npc.getPosition().distanceSq(playerPos) > MapleCharacter.MAX_VIEW_RANGE_SQ) {
		    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
		    return;
		} else if (npc.hasShop()) {
			npc.sendShop(c);
		} else if (npc.getStats().getTrunkPut() > 0) {
		    c.getPlayer().getStorage().sendStorage(c, npc.getId());
		} else if (npc.getStats().getGuildRank() > 0) {
		    MapleGuild.displayGuildRanks(c, npc.getId());
		} else {
			NPCScriptManager.getInstance().start(c, npc);
		}
		
	}
}
