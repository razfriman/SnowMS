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

import java.rmi.RemoteException;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.world.remote.CheaterData;
import net.sf.odinms.tools.MaplePacketCreator;

public class CheaterHuntingCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		if (splitted[0].equals("!whosthere")) {
			MessageCallback callback = new ServernoticeMapleClientMessageCallback(c);
			StringBuilder builder = new StringBuilder("Players on Map: ");
			for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
				if (builder.length() > 150) { // wild guess :o
					builder.setLength(builder.length() - 2);
					callback.dropMessage(builder.toString());
					builder = new StringBuilder();
				}
				builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
				builder.append(", ");
			}
			builder.setLength(builder.length() - 2);
			c.getSession().write(MaplePacketCreator.serverNotice(6, builder.toString()));
		} else if (splitted[0].equals("!cheaters")) {
			try {
				List<CheaterData> cheaters = c.getChannelServer().getWorldInterface().getCheaters();
				for (int x = cheaters.size() - 1; x >= 0; x--) {
					CheaterData cheater = cheaters.get(x);
					mc.dropMessage(cheater.getInfo());
				}
			} catch (RemoteException e) {
				c.getChannelServer().reconnectWorld();
			}
		} else if (splitted[0].equals("!reportreasons")) {
			mc.dropMessage("REASONS: 1-Hacking | 2-Spamming | 3-Fake GM | 4-Harassment | 5-Advertising");
		} else if (splitted[0].equals("!ip")) {
			MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
			if (chr == null) {
				chr = c.getPlayer();
			}
			String IP = chr.getClient().getSession().getRemoteAddress().toString();
			mc.dropMessage(chr.getName() + " : " + IP);
		} else if (splitted[0].equals("!hide")) {
			c.getPlayer().setHidden(!c.getPlayer().isHidden());
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("whosthere", "", "Shows all the players in your map", 50),
					new CommandDefinition("cheaters", "", "Shows a list of all suspicous players", 50),
					new CommandDefinition("reportreasons", "", "List the reasons of being reported", 50),
					new CommandDefinition("ip", "player-name", "Get the IP of a certain player", 50),
					new CommandDefinition("hide", "", "Hide/Show you", 50),};
	}
}
