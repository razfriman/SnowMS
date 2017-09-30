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

package net.sf.odinms.console.irc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.odinms.net.RecvPacketOpcode;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

import org.jibble.jmegahal.JMegaHal;
import org.jibble.pircbot.PircBot;

import ch.ubique.inieditor.IniEditor;

/**
 *
 * @author Raz
 */
public class IRCBot extends PircBot {

	private String masterPassword;
	private String server;
	private String[] startupChannels;
	private Random random = new Random();
	private JMegaHal hal;
	private List<String> owners = new ArrayList<String>();
	private List<String> ignored = new ArrayList<String>();
	private Map<String, String> definitions = new HashMap<String, String>();
	private List<QuoteEntry> quotes = new ArrayList<QuoteEntry>();
	private boolean muted = false;
	private static char commandChar = '!';

	public IRCBot() {
		IniEditor ini = new IniEditor();
		try {//Load Settings
			ini.load("settings.ini");
		} catch (Exception e) {
			System.out.println("Unable to find settings.properties");
			return;
		}

		setName(ini.get("REG_IRC", "NAME"));
		server = ini.get("REG_IRC", "SERVER");
		startupChannels = ini.get("REG_IRC", "CHANNELS").split(" ");
		masterPassword = ini.get("REG_IRC", "MASTER_PW");
		String[] staticOwners = ini.get("REG_IRC", "OWNERS").split(" ");
		for (String owner : staticOwners) {
			this.owners.add(owner);
		}
		hal = new JMegaHal();
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (message.charAt(0) != commandChar) {
			return;
		}
		String[] splitted = message.substring(1).split(" ");
		if (splitted[0].equalsIgnoreCase("master")) {
			if (splitted.length > 1) {
				if (splitted[1].equals(masterPassword) && !isOwner(sender)) {
					sendMessage(sender, "Password granted - Added to owner list");
					owners.add(sender);
				}
			}
		} else if (splitted[0].equalsIgnoreCase("resign")) {
			if (splitted.length > 1 && isOwner(sender)) {
				sendMessage(sender, "Removed from owner list");
				owners.remove(sender);
			}
		}
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (message.charAt(0) != commandChar) {
			return;
		}
		String[] splitted = message.substring(1).split(" ");
		if (splitted[0].equalsIgnoreCase("mute")) {
			if (isOwner(sender)) {
				muted = !muted;
				sendMessage(channel, "I am " + (muted ? "muted" : "unmuted"));
			}
		}
		if (!muted && !isIgnored(sender)) {
			if (splitted[0].equalsIgnoreCase("gay")) {
				if (splitted.length > 1) {
					String name = splitted[1];
					sendMessage(channel, name, random.nextInt(101) + "% gay");
				}
			} else if (splitted[0].equalsIgnoreCase("love")) {
				if (splitted.length > 1) {
					String name = splitted[1];
					sendMessage(channel, sender + " loves " + name + " " + random.nextInt(101) + "% ");
				}
			} else if (splitted[0].equalsIgnoreCase("hate")) {
				if (splitted.length > 1) {
					String name = splitted[1];
					sendMessage(channel, sender + " hates " + name + " " + random.nextInt(101) + "% ");
				}
			} else if (splitted[0].equalsIgnoreCase("flipcoin")) {
				sendMessage(channel, sender, random.nextBoolean() ? "Heads" : "Tails");
			} else if (splitted[0].equalsIgnoreCase("voice")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String target = splitted[1];
						voice(channel, target);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("devoice")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String target = splitted[1];
						deVoice(channel, target);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("op")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String target = splitted[1];
						op(channel, target);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("deop")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String target = splitted[1];
						deOp(channel, target);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("rejoin")) {
				if (isOwner(sender)) {
					partChannel(channel);
					joinChannel(channel);
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("nick")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String newNick = splitted[1];
						changeNick(newNick);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("join")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String newChannel = splitted[1];
						joinChannel(newChannel);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("part")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String newChannel = splitted[1];
						partChannel(newChannel);
					} else {
						partChannel(channel);
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("quit")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						quitServer(StringUtil.joinStringFrom(splitted, 1));
					} else {
						quitServer();
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("ignore")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String target = splitted[1];
						ignored.add(target);
						sendMessage(channel, sender, target + " is ignored");
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("unignore")) {
				if (isOwner(sender)) {
					if (splitted.length > 1) {
						String target = splitted[1];
						if (ignored.remove(target)) {
							sendMessage(channel, sender, target + " is not ignored");
						}
					}
				} else {
					sendMessage(channel, sender, "You are not one of my owners");
				}
			} else if (splitted[0].equalsIgnoreCase("send")) {
				SendPacketOpcode op = SendPacketOpcode.UNKNOWN;
				try {
					if (splitted.length > 1) {
						String input = splitted[1];
						op = SendPacketOpcode.getByType(Integer.decode(input));
						if (op == SendPacketOpcode.UNKNOWN) {
							op = SendPacketOpcode.getByName(input);
						}
					}
				} catch (Exception e) {
					//User is not good at inputting things
				}
				if (op == SendPacketOpcode.UNKNOWN) {
					sendMessage(channel, sender, op.name());
				} else {
					sendMessage(channel, sender, op.name() + "(0x" + Integer.toHexString(op.getValue()) + ")");
				}
			} else if (splitted[0].equalsIgnoreCase("recv")) {
				RecvPacketOpcode op = RecvPacketOpcode.UNKNOWN;
				try {
					if (splitted.length > 1) {
						String input = splitted[1];
						op = RecvPacketOpcode.getByType(Integer.decode(input));
						if (op == RecvPacketOpcode.UNKNOWN) {
							op = RecvPacketOpcode.getByName(input);
						}
					}
				} catch (Exception e) {
					//User is not good at inputting things
				}
				if (op == RecvPacketOpcode.UNKNOWN) {
					sendMessage(channel, sender, op.name());
				} else {
					sendMessage(channel, sender, op.name() + "(0x" + Integer.toHexString(op.getValue()) + ")");
				}
			} else if (splitted[0].equalsIgnoreCase("mapleasciistring")) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.writeMapleAsciiString(StringUtil.joinStringFrom(splitted, 1));
				sendMessage(channel, sender, HexTool.toString(mplew.getPacket().getBytes()));
			} else if (splitted[0].equalsIgnoreCase("owners")) {
				String ownerNames = "";
				for (String owner : owners) {
					ownerNames += " " + owner;
				}
				sendMessage(channel, "(" + owners.size() + ")" + ownerNames);
			} else if (splitted[0].equalsIgnoreCase("conversation")) {
				hal.add(StringUtil.joinStringFrom(splitted, 1));
				sendMessage(channel, sender, hal.getSentence());
			} else if (splitted[0].equalsIgnoreCase("define")) {
				if (splitted.length > 2) {
					String key = splitted[1];
					String value = StringUtil.joinStringFrom(splitted, 2);
					if (definitions.containsKey(key.toLowerCase())) {
						sendMessage(channel, sender, key + "is already defined");
					} else {
						definitions.put(key.toLowerCase(), value);
						sendMessage(channel, sender, key + " is defined");
					}
				}
			} else if (splitted[0].equalsIgnoreCase("lookup")) {
				if (splitted.length > 1) {
					String key = splitted[1];
					String value = definitions.get(key.toLowerCase());
					if (value != null) {
						sendMessage(channel, key, value);
					} else {
						sendMessage(channel, sender, key + " is not defined yet");
					}
				}
			} else if (splitted[0].equalsIgnoreCase("redefine")) {
				if (splitted.length > 2 && isOwner(sender)) {
					String key = splitted[1];
					String value = StringUtil.joinStringFrom(splitted, 2);
					definitions.put(key.toLowerCase(), value);
					sendMessage(channel, sender, key + " is defined");
				}
			} else if (splitted[0].equalsIgnoreCase("undefine")) {
				if (splitted.length > 1) {
					if (isOwner(sender)) {
						String key = splitted[1];
						definitions.remove(key.toLowerCase());
						sendMessage(channel, sender, key + " is undefined");
					} else {
						sendMessage(channel, sender, "You are not one of my owners");
					}
				}
			} else if (splitted[0].equalsIgnoreCase("roll")) {
				int i = random.nextInt(6) + 1;
				int j = random.nextInt(6) + 1;
				sendMessage(channel, sender + " throws " + i + " and " + j);
			} else if (splitted[0].equalsIgnoreCase("setCommandChar")) {
				if (splitted.length > 1) {
					if (isOwner(sender)) {
						String newCommandChar = splitted[1];
						commandChar = newCommandChar.charAt(0);
						sendMessage(channel, "My command char is " + commandChar);
					} else {
						sendMessage(channel, sender, "You are not one of my owners");
					}
				}
			}
		}
	}

	public void sendMessage(String dest, String target, String message) {
		sendMessage(dest, target + ": " + message);
	}

	public String getServerName() {
		return server;
	}

	public String[] getStartupChannels() {
		return startupChannels;
	}

	public boolean isOwner(String user) {
		return owners.contains(user);
	}

	public boolean isIgnored(String user) {
		return ignored.contains(user);
	}

	public static void main(String[] args) {
		IRCBot bot = new IRCBot();
		bot.setVerbose(true);
		try {
			bot.connect(bot.getServerName());
		} catch (Exception e) {
			System.out.println("Unable to connect to: " + bot.getServerName());
			return;
		}
		for (String channel : bot.getStartupChannels()) {
			bot.joinChannel(channel);
		}
	}

	public class QuoteEntry {
		//TODO
	}
}
