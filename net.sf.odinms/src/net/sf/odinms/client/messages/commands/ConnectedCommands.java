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
import java.util.Map;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;

public class ConnectedCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {

		if (splitted[0].equals("!connected")) {
			try {
				Map<Integer, Integer> connected = c.getChannelServer().getWorldInterface().getConnected();
				StringBuilder conStr = new StringBuilder("Connected Clients: ");
				boolean first = true;
				for (int i : connected.keySet()) {
					if (!first) {
						conStr.append(", ");
					} else {
						first = false;
					}
					if (i == 0) {
						conStr.append("Total: ");
						conStr.append(connected.get(i));
					} else {
						conStr.append("Ch");
						conStr.append(i);
						conStr.append(": ");
						conStr.append(connected.get(i));
					}
				}
				mc.dropMessage(conStr.toString());
			} catch (RemoteException e) {
				c.getChannelServer().reconnectWorld();
			}
		} else if (splitted[0].equals("!online")) {

			StringBuilder builder = new StringBuilder("Ch" + c.getChannel() + " (" + c.getChannelServer().getConnectedClients() + ") : ");

			for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
				if (builder.length() > 150) {
					builder.setLength(builder.length() - 2);
					mc.dropMessage(builder.toString());
					builder = new StringBuilder();
				}
				builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
				builder.append(" | ");
			}
			builder.setLength(builder.length() - 2);
			c.getSession().write(MaplePacketCreator.serverNotice(6, builder.toString()));


		} else if (splitted[0].toLowerCase().equals("!onlineall")) {
			StringBuilder sb;
			/* Retrieve an array of channels from the server (May not work in distributed server) */
			for (ChannelServer cs : ChannelServer.getAllInstances()) {

				/* Retrieve a character list from the current channel */
				sb = new StringBuilder("[Channel " + cs.getChannel() + "]");
				mc.dropMessage(sb.toString());

				sb = new StringBuilder();

				for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {

					/* Keep messages short */
					if (sb.length() > 150) {
						sb.setLength(sb.length() - 2);
						mc.dropMessage(sb.toString());
						sb = new StringBuilder();
					}

					sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
					sb.append(" | ");

				}
				if (cs.getPlayerStorage().getAllCharacters().size() == 0) {
					sb.append("(NONE)");
				}

				/* Send Characters Online */
				mc.dropMessage(sb.toString());

			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("connected", "", "Shows how many players are connected on each channel", 200),
					new CommandDefinition("online", "", "Shows the current players online, in your channel", 200),
					new CommandDefinition("onlineall", "", "Shows the current players online, in your the game-server", 200),};
	}
}
