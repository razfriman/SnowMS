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
package net.sf.odinms.client.messages.commands;

import java.awt.Point;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;

public class NPCSpawningCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		if (splitted[0].equals("!npc")) {
			int npcId = Integer.parseInt(splitted[1]);
			if (!c.getPlayer().getMap().spawnNpc(npcId, c.getPlayer().getPosition())) {
				mc.dropMessage("You have entered an invalid Npc-Id");
			}
		} else if (splitted[0].equals("!removenpc")) {
            if (splitted.length > 1) {
                int oid = Integer.parseInt(splitted[1]);
                MapleMapObject mmo = c.getPlayer().getMap().getMapObject(oid);
                if (mmo != null && mmo.getType() == MapleMapObjectType.NPC) {
                    c.getPlayer().getMap().removeMapObject(oid);
                }
			}
		} else if (splitted[0].equals("!npcinfo")) {
			int npcId = Integer.parseInt(splitted[1]);
			MapleNPC npc = MapleLifeFactory.getNPC(npcId);
			if (npc != null && !npc.getName().equals("MISSINGNO")) {
				mc.dropMessage(npc.getName() + "-" + npc.getId());
			} else {
				mc.dropMessage("Invalid Npc-ID [" + npcId + "]");
			}
		} else if (splitted[0].equals("!mynpcpos")) {
			Point pos = c.getPlayer().getPosition();
			mc.dropMessage("CY: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getMap().getFootholds().findBelow(pos).getId());
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("npc", "npcid", "Spawns the npc with the given id at the player position", 500),
					new CommandDefinition("removenpc", "oid", "Removes npc from the map", 500),
					new CommandDefinition("npcinfo", "npcid", "Shows you the npc's name", 500),
					new CommandDefinition("mynpcpos", "", "Gets the info for making an npc", 100),};
	}
}
