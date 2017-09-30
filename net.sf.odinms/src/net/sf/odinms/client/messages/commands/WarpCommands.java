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

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleClient.ClientStatus;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;

public class WarpCommands implements Command {

	private static Map<String, Integer> maps = new HashMap<String, Integer>();


	static {
		maps.put("henesys", 100000000);
		maps.put("gmmap", 180000000);
		maps.put("ellinia", 101000000);
		maps.put("perion", 102000000);
		maps.put("kerning", 103000000);
		maps.put("sleepywood", 105040300);
		maps.put("orbis", 200000000);
		maps.put("aquarium", 230000000);
		maps.put("elnath", 211000000);
		maps.put("ariant", 260000000);
		maps.put("singapore", 540000000);
		maps.put("quay", 541000000);
		maps.put("crimsonwood", 610020006);
		maps.put("florina", 110000000);
		maps.put("fm", 910000000);
		maps.put("showa", 801000000);
		maps.put("4th", 240010501);
		maps.put("armory", 801040004);
		maps.put("shrine", 800000000);
		maps.put("mansion", 682000000);
		maps.put("lith", 104000000);
		maps.put("ludi", 220000000);
		maps.put("kft", 222000000);
		maps.put("omega", 221000000);
		maps.put("leafre", 240000000);
		maps.put("mulung", 250000000);
		maps.put("herbtown", 251000000);
		maps.put("nlc", 600000000);
		maps.put("amoria", 680000000);
		maps.put("happyville", 209000000);
		// Boss maps
		maps.put("ergoth", 990000900);
		maps.put("pap", 220080001);
		maps.put("zakum", 280030000);
		maps.put("horntail", 240060200);
		maps.put("lordpirate", 925100500);
		maps.put("alishar", 922010900);
		maps.put("papapixie", 920010800);
		maps.put("kingslime", 103000804);
		maps.put("pianus", 230040420);
		maps.put("manon", 240020401);
		maps.put("griffey", 240020101);
		maps.put("jrbalrog", 105090900);
		maps.put("grandpa", 801040100);
		maps.put("anego", 801040003);
		maps.put("tengu", 800020130);
		maps.put("ereve", 130000200);
	}

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		ChannelServer cserv = c.getChannelServer();
		if (splitted[0].equals("!warp")) {
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
			if (victim != null) {
				if (splitted.length == 2) {
					MapleMap target = victim.getMap();
					c.getPlayer().changeMap(target, target.findClosestSpawnpoint(victim.getPosition()));
				} else {
					int mapid = Integer.parseInt(splitted[2]);
					MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
					victim.changeMap(target, target.getPortal(0));
				}
			} else {
				try {
					victim = c.getPlayer();
					WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
					if (loc != null) {
						mc.dropMessage("You will be cross-channel warped. This may take a few seconds.");
						// WorldLocation loc = new WorldLocation(40000, 2);
						MapleMap target = c.getChannelServer().getMapFactory().getMap(loc.map);
						c.getPlayer().cancelAllBuffs();
						String ip = c.getChannelServer().getIP(loc.channel);
						c.getPlayer().getMap().removePlayer(c.getPlayer());
						victim.setMap(target);
						String[] socket = ip.split(":");
						if (c.getPlayer().getTrade() != null) {
							MapleTrade.cancelTrade(c.getPlayer());
						}
						c.getPlayer().saveToDB(true);
						if (c.getPlayer().getCheatTracker() != null) {
							c.getPlayer().getCheatTracker().dispose();
						}
						ChannelServer.getInstance(c.getChannel()).removePlayer(c.getPlayer());
						c.updateLoginState(ClientStatus.LOGIN_SERVER_TRANSITION);
						try {
							MaplePacket packet = MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
							c.getSession().write(packet);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} else {
						int map = Integer.parseInt(splitted[1]);
						MapleMap target = cserv.getMapFactory().getMap(map);
						c.getPlayer().changeMap(target, target.getPortal(0));
					}
				} catch (/* Remote */Exception e) {
					mc.dropMessage("Something went wrong " + e.getMessage());
				}
			}
		} else if (splitted[0].equals("!warphere")) {
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
			victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
		} else if (splitted[0].equals("!lolcastle")) {
			if (splitted.length != 2) {
				mc.dropMessage("Syntax: !lolcastle level (level = 1-5)");
			}
			MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" +
					splitted[1]).getMapFactory().getMap(990000300, false, false);
			c.getPlayer().changeMap(target, target.getPortal(0));
		} else if (splitted[0].equals("!jail")) {
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
			int mapid = 200090300; // mulung ride
			if (splitted.length > 2 && splitted[1].equals("2")) {
				mapid = 980000404; // exit for CPQ; not used
				victim = cserv.getPlayerStorage().getCharacterByName(splitted[2]);
			}
			if (victim != null) {
				MapleMap target = cserv.getMapFactory().getMap(mapid);
				MaplePortal targetPortal = target.getPortal(0);
				victim.changeMap(target, targetPortal);
				mc.dropMessage(victim.getName() + " was jailed!");
			} else {
				mc.dropMessage(splitted[1] + " not found!");
			}
		} else if (splitted[0].equals("!map")) {
			int mapid = Integer.parseInt(splitted[1]);
			MapleMap target = cserv.getMapFactory().getMap(mapid);
			MaplePortal targetPortal = null;
			if (splitted.length > 2) {
				try {
					targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
				} catch (IndexOutOfBoundsException ioobe) {
					// noop, assume the gm didn't know how many portals there are
				} catch (NumberFormatException nfe) {
					// noop, assume that the gm is drunk
				}
			}
			if (targetPortal == null) {
				targetPortal = target.getPortal(0);
			}
			c.getPlayer().changeMap(target, targetPortal);

		} else if (splitted[0].equals("!warpallhere")) {
			Collection<MapleCharacter> allCharacters = new LinkedList<MapleCharacter>(c.getChannelServer().getPlayerStorage().getAllCharacters());
			for (MapleCharacter mch : allCharacters) {
				if (mch.getMapId() != c.getPlayer().getMapId()) {
					mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
				}
			}
		} else if (splitted[0].equals("!pq")) {
			if (splitted.length > 1) {
				String option = splitted[1];
				MapleMap map;
				MaplePortal portal;
				if (option.equalsIgnoreCase("lmpq")) {
					map = cserv.getMapFactory().getMap(220000000);
					portal = map.getPortal(9);
					c.getPlayer().changeMap(map, portal);
				} else if (option.equalsIgnoreCase("zpq")) {
					map = cserv.getMapFactory().getMap(211042300);
					portal = map.getPortal(2);
					c.getPlayer().changeMap(map, portal);
				} else if (option.equalsIgnoreCase("kpq")) {
					map = cserv.getMapFactory().getMap(103000000);
					portal = map.getPortal(31);
					c.getPlayer().changeMap(map, portal);
				} else if (option.equalsIgnoreCase("lpq")) {
					map = cserv.getMapFactory().getMap(221024500);
					portal = map.getPortal(2);
					c.getPlayer().changeMap(map, portal);
				} else if (option.equalsIgnoreCase("apq")) {
					map = cserv.getMapFactory().getMap(670010000);
					portal = map.getPortal(2);
					c.getPlayer().changeMap(map, portal);
				} else if (option.equalsIgnoreCase("gq")) {
					map = cserv.getMapFactory().getMap(101030104);
					portal = map.getPortal(1);
					c.getPlayer().changeMap(map, portal);
				} else {
					mc.dropMessage("Current PQ's: " + "LMPQ" + " | " + "ZPQ" + " | " + "KPQ" + " | " + "LPQ" + " | " + "APQ" + " | " + "GQ");
				}
			} else {
				mc.dropMessage("Current PQ's: " + "LMPQ" + " | " + "ZPQ" + " | " + "KPQ" + " | " + "LPQ" + " | " + "APQ" + " | " + "GQ");
			}
		} else if (splitted[0].equals("!port")) {
			if (splitted.length > 1) {
				String portalName = splitted[1];
				MaplePortal portal = c.getPlayer().getMap().getPortal(portalName);
				if (portal != null) {
					c.getSession().write(MaplePacketCreator.portToPort(portal.getId()));
				} else {
					mc.dropMessage("Invalid Portal");
				}
			} else {
				mc.dropMessage("Error : Please enter a Portal-Id");
			}
		} else if (splitted[0].equals("!town") || splitted[0].equals("!goto")) {
			if (splitted.length > 1) {
				String name = splitted[1];
				if (maps.containsKey(name)) {
					int mapid = maps.get(name);
					MapleMap to = cserv.getMapFactory().getMap(mapid);
					c.getPlayer().changeMap(to, to.getPortal(0));
				} else {
					mc.dropMessage("Invalid Map Name");
				}
			} else {
				mc.dropMessage("Please Enter A Map Name: ");
				for (Entry<String, Integer> map : maps.entrySet()) {
					mc.dropMessage(map.getKey() + " (" + map.getValue() + ")");
				}
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("warp", "playername [targetid]", "Warps yourself to the player with the given name. When targetid is specified warps the player to the given mapid", 100),
					new CommandDefinition("warphere", "playername", "Warps the player with the given name to yourself", 100),
					new CommandDefinition("town", "String [Town]", "Warps you to [Town]", 100),
					new CommandDefinition("goto", "String [Town]", "Warps you to [Town]", 100),
					new CommandDefinition("lolcastle", "[1-5]", "Warps you into Field of Judgement with the given level", 100),
					new CommandDefinition("jail", "[2] playername", "Warps the player to a map that he can't leave", 100),
					new CommandDefinition("map", "mapid", "Warps you to the given mapid (use /m instead)", 100),
					new CommandDefinition("warpallhere", "", "Warps all characters on your channel to the map you are currently in", 100),
					new CommandDefinition("pq", "pq-name", "Warp you to the start of the chosen pq", 100),
					new CommandDefinition("port", "portal-id", "Warps you to the portal defined (intra-map warp", 100),};
	}
}
