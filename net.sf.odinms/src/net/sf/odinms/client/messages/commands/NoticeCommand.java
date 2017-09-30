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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class NoticeCommand implements Command {

	private static int getNoticeType(String typestring) {
		if (typestring.equals("n")) {
			return 0;
		} else if (typestring.equals("p")) {
			return 1;
		} else if (typestring.equals("l")) {
			return 2;
		} else if (typestring.equals("nv")) {
			return 5;
		} else if (typestring.equals("v")) {
			return 5;
		} else if (typestring.equals("b")) {
			return 6;
		}
		return -1;
	}

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {

		if (splitted[0].equals("!notice")) {
			int joinmod = 1;

			int range = -1;
			if (splitted[1].equals("m")) {
				range = 0;
			} else if (splitted[1].equals("c")) {
				range = 1;
			} else if (splitted[1].equals("w")) {
				range = 2;
			}

			int tfrom = 2;
			if (range == -1) {
				range = 2;
				tfrom = 1;
			}
			int type = getNoticeType(splitted[tfrom]);
			if (type == -1) {
				type = 0;
				joinmod = 0;
			}
			String prefix = "";
			if (splitted[tfrom].equals("nv")) {
				prefix = "[Notice] ";
			}
			joinmod += tfrom;
			MaplePacket packet = MaplePacketCreator.serverNotice(type, prefix +
					StringUtil.joinStringFrom(splitted, joinmod));
			if (range == 0) {
				c.getPlayer().getMap().broadcastMessage(packet);
			} else if (range == 1) {
				ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
			} else if (range == 2) {
				try {
					ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(
							c.getPlayer().getName(), packet.getBytes());
				} catch (RemoteException e) {
					c.getChannelServer().reconnectWorld();
				}
			}
        } else if (splitted[0].equals("!mapletip")) {
            if(splitted.length > 1) {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.sendMapleTip(StringUtil.joinStringFrom(splitted, 1)));
            } else {
                mc.dropMessage("error: no message entered");
            }
		} else if (splitted[0].equals("!avatarsmega")) {
			mc.dropMessage("didnt add yet");
		} else if (splitted[0].equals("!me")) {
			String prefix = "[" + c.getPlayer().getName() + "] ";
			String message = prefix + StringUtil.joinStringFrom(splitted, 1);
			c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, message));
		}

	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("notice", "[mcw] [n/p/l/nv/v/b] message", "send out a notice", 100),
                    new CommandDefinition("mapletip", "message", "sends a mapletip", 100),
					new CommandDefinition("avatarsmega", "", "", 100),
					new CommandDefinition("me", "message", "send a message with your name as the prefix", 100),};
	}
}
