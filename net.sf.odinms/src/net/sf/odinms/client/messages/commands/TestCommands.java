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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

public class TestCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		if (splitted[0].equals("!monstercarnival")) {
			if (c.getPlayer().getMap().getId() == 980000201) {
				c.getSession().write(MaplePacketCreator.startMonsterCarnival(c.getPlayer()));
			} else {
				mc.dropMessage("Please go to map '980000201' to use this command");
				mc.dropMessage("Please beware with any other players in the map, all of you will DC.");
			}
		} else if (splitted[0].equals("!npcdel")) {
			for (MapleMapObject mmo : c.getPlayer().getMap().getAllObjects(MapleMapObjectType.NPC)) {
				MapleNPC npc = (MapleNPC) mmo;
				c.getSession().write(MaplePacketCreator.npcConfirm(npc.getObjectId()));
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("monstercarnival", "", "Shows you the Monster-Carnival points", 1000),
					new CommandDefinition("npcdel", "", "", 1000),
					};
	}
}
