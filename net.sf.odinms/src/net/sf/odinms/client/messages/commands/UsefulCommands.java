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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.MaplePacketHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.PacketProcessor.Mode;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleIRC;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.maps.MapleReactorFactory;
import net.sf.odinms.server.maps.MapleReactorStats;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class UsefulCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		if (splitted[0].equals("!pos")) {
			mc.dropMessage("X:" + c.getPlayer().getPosition().x + " Y:" + c.getPlayer().getPosition().y);
		} else if (splitted[0].equals("!mark")) {
			if (splitted.length > 2) {
				int x = Integer.parseInt(splitted[1]);
				int y = Integer.parseInt(splitted[2]);
				Point pos = new Point(x, y);
				int rid = 2000;
				MapleReactorStats stats = MapleReactorFactory.getReactor(rid);
				MapleReactor reactor = new MapleReactor(stats, rid);
				reactor.setDelay(-1);
				reactor.setPosition(pos);
				c.getPlayer().getMap().spawnReactor(reactor);
			}

		} else if (splitted[0].equals("!packet")) {
			String packet = StringUtil.joinStringFrom(splitted, 1);
			c.getSession().write(MaplePacketCreator.getPacketFromHexString(packet));

		} else if (splitted[0].equals("!packet-m")) {
			String packet = StringUtil.joinStringFrom(splitted, 1);
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getPacketFromHexString(packet));

		} else if (splitted[0].equals("!cpacket")) {
			String hex = StringUtil.joinStringFrom(splitted, 1);
			MaplePacket packet = MaplePacketCreator.getPacketFromHexString(hex);
			SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(packet.getBytes()));
			SeekableLittleEndianAccessor pHeader = slea;
			Short header = pHeader.readShort();
			MaplePacketHandler packetHandler = PacketProcessor.getProcessor(Mode.CHANNELSERVER).getHandler(header);
			if (packetHandler == null) {
				mc.dropMessage("Unknown Header");
			} else {
				packetHandler.handlePacket(slea, c);
			}

		} else if (splitted[0].equals("!time")) {
			Calendar cal = Calendar.getInstance();
			String time = "";
			time += (cal.get(Calendar.HOUR_OF_DAY));
			time += ":";
			time += (cal.get(Calendar.MINUTE));
			time += "  ";
			time += (cal.get(Calendar.SECOND));
			mc.dropMessage(time);
		} else if (splitted[0].equals("!npctalk")) {
			int npcId = 9900000;
			String message = StringUtil.joinStringFrom(splitted, 1);
			c.getSession().write(MaplePacketCreator.serverMessage(7, -1, message, false, false, npcId));
		} else if (splitted[0].equals("!toint")) {
			int ret = 0;
			byte[] intbytes = new byte[4];
			intbytes = HexTool.getByteArrayFromHexString(StringUtil.joinStringFrom(splitted, 1));
			ret = (intbytes[3] << 24) + (intbytes[2] << 16) + (intbytes[1] << 8) + intbytes[0];
			mc.dropMessage(Integer.toString(ret));
			} else if (splitted[0].equals("!killnpc")) {
			NPCScriptManager.getInstance().dispose(c);
		} else if (splitted[0].equals("!addmonsterdrop")) {
			if (splitted.length > 5) {
				try {
					int monsterid = Integer.parseInt(splitted[1]);
					int itemid = Integer.parseInt(splitted[2]);
					int questid = Integer.parseInt(splitted[3]);
					int chance = Integer.parseInt(splitted[4]);
					int amount = Integer.parseInt(splitted[5]);
					Connection con = DatabaseConnection.getConnection();
					PreparedStatement ps;
					ps = con.prepareStatement("INSERT INTO monsterdrops (monsterid, itemid, questid, chance, amount) VALUES (?, ?, ?, ?, ?");
					ps.setInt(0, monsterid);
					ps.setInt(1, itemid);
					ps.setInt(2, questid);
					ps.setInt(3, chance);
					ps.setInt(4, amount);
					ps.executeUpdate();
					ps.close();
					mc.dropMessage("Added Drop " + itemid + " for " + monsterid);
				} catch (Exception e) {
					e.printStackTrace();
					mc.dropMessage("Error: Adding monster drop - " + e.getMessage());
				}
			} else {
				mc.dropMessage("Syntax: <monsterid> <itemid> <questid> <chance> <amount>");
			}
		} else if (splitted[0].equals("!irc")) {
			if (splitted.length > 1) {
				MapleIRC irc = c.getChannelServer().getMapleIRC();
				if (irc == null) {
					mc.dropMessage("The MapleIRC is currently offline");
					return;
				}
				String command = splitted[1].toLowerCase();
				if (command.equals("on")) {
					if (irc.register(c.getPlayer())) {
						mc.dropMessage("Successfully connected to IRC: " + irc.getServer() + irc.getChannel());
					} else {
						mc.dropMessage("Error connecting to IRC");
					}
				} else if (command.equals("off")) {
					if (irc.deRegister(c.getPlayer())) {
						mc.dropMessage("Successfully disconnected from IRC");
					} else {
						mc.dropMessage("Error disconnecting from IRC");
					}
				} else if (command.equals("chat")) {
					if (!irc.sendUserMessage(c.getPlayer().getName(), StringUtil.joinStringFrom(splitted, 2))) {
						mc.dropMessage("Error sending message to IRC");
					}
				} else if (command.equals("online")) {
					irc.listUsers(c.getPlayer());
				}
			} else {
				mc.dropMessage("Commands: on | off | chat<msg> | online");
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("pos", "", "Shows your current position(X,Y)", 100),
					new CommandDefinition("mark", "X Y", "Marks the coordinate with a reactor", 100),
					new CommandDefinition("packet", "Hex-String", "Sends a packet to you, containg the hex-string inputted", 100),
					new CommandDefinition("packet-m", "Hex-String", "Sends a packet to your map, containg the hex-string inputted", 100),
					new CommandDefinition("time", "", "Tells you the current time", 100),
					new CommandDefinition("npctalk", "Sentence for NPC to show", "Show an npc-chat with your input", 100),
					new CommandDefinition("toint", "Bytes", "Convert bytes to an int", 100),
					new CommandDefinition("killnpc", "", "Fix getting stuck from npcs", 100),
					new CommandDefinition("addmonsterdrop", "monsterid - itemid - questid - chance - amount", "Fix getting stuck from npcs", 100),
					new CommandDefinition("irc", "", "general irc command", 100),
		};
	}
}
