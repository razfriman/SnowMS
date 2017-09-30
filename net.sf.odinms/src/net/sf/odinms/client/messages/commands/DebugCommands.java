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
import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleDoor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

public class DebugCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		MapleCharacter player = c.getPlayer();
		if (splitted[0].equals("!resetquest")) {
			MapleQuest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
		} else if (splitted[0].equals("!nearestsp")) {
			final MaplePortal portal = player.getMap().findClosestSpawnpoint(player.getPosition());
			mc.dropMessage(portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
		} else if (splitted[0].equals("!spawndebug")) {
			c.getPlayer().getMap().spawnDebug(mc);
		} else if (splitted[0].equals("!door")) {
			Point doorPos = new Point(player.getPosition());
			doorPos.y -= 270;
			MapleDoor door = new MapleDoor(c.getPlayer(), doorPos);
			door.getTarget().addMapObject(door);
			// c.getSession().write(MaplePacketCreator.spawnDoor(/*c.getPlayer().getId()*/ 0x1E47, door.getPosition(),
			// false));
			/* c.getSession().write(MaplePacketCreator.saveSpawnPosition(door.getPosition())); */
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(HexTool.getByteArrayFromHexString("B9 00 00 47 1E 00 00 0A 04 76 FF"));
			c.getSession().write(mplew.getPacket());
			mplew = new MaplePacketLittleEndianWriter();
			mplew.write(HexTool.getByteArrayFromHexString("36 00 00 EF 1C 0D 4C 3E 1D 0D 0A 04 76 FF"));
			c.getSession().write(mplew.getPacket());
			c.getSession().write(MaplePacketCreator.enableActions());
			door = new MapleDoor(door);
			door.getTown().addMapObject(door);
		} else if (splitted[0].equals("!timerdebug")) {
			TimerManager.getInstance().dropDebugInfo(mc);
		} else if (splitted[0].equals("!threads")) {
			Thread[] threads = new Thread[Thread.activeCount()];
			Thread.enumerate(threads);
			String filter = "";
			if (splitted.length > 1) {
				filter = splitted[1];
			}
			for (int i = 0; i < threads.length; i++) {
				String tstring = threads[i].toString();
				if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
					mc.dropMessage(i + ": " + tstring);
				}
			}
		} else if (splitted[0].equals("!showtrace")) {
			if (splitted.length < 2) {
				throw new IllegalCommandSyntaxException(2);
			}
			Thread[] threads = new Thread[Thread.activeCount()];
			Thread.enumerate(threads);
			Thread t = threads[Integer.parseInt(splitted[1])];
			mc.dropMessage(t.toString() + ":");
			for (StackTraceElement elem : t.getStackTrace()) {
				mc.dropMessage(elem.toString());
			}
		} else if (splitted[0].equals("!fakerelog")) {
			c.getSession().write(MaplePacketCreator.getCharInfo(player));
			player.getMap().removePlayer(player);
			player.getMap().addPlayer(player);
		} else if (splitted[0].equals("!toggleoffense")) {
			try {
				CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
				co.setEnabled(!co.isEnabled());
			} catch (IllegalArgumentException iae) {
				mc.dropMessage("Offense " + splitted[1] + " not found");
			}
		} else if (splitted[0].equals("!tdrops")) {
			player.getMap().toggleDrops();
		} else if (splitted[0].equals("!tspawn")) {
			player.getMap().toggleSpawn();
		} else if (splitted[0].equals("!partydebug")) {
			if (c.getPlayer().getParty() != null) {
				System.out.println("INFO: party listing in CommandProcessor");
				c.getPlayer().getParty().listParty();
			} else {
				System.out.println("INFO: You have no party");
			}
		} else if (splitted[0].equals("!debugmap")) {
			String what = "";
			double range = Double.POSITIVE_INFINITY;
			MapleMap map = c.getPlayer().getMap();
			StringBuilder builder = new StringBuilder();
			List<MapleMapObject> npcs = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.NPC));
			List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
			List<MapleMapObject> players = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.PLAYER));
			List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.REACTOR));

			if (splitted.length > 1) {
				what = splitted[1];
			}

			if (what.equalsIgnoreCase("npc") || what.equalsIgnoreCase("npcs")) {

				builder.append("NPCS (" + npcs.size() + ") : ");
				for (MapleMapObject npcmo : npcs) {
					MapleNPC npc = (MapleNPC) npcmo;
					builder.append(npc.getName() + "-" + npc.getId() + " | ");
				}
			} else if (what.equalsIgnoreCase("mob") || what.equalsIgnoreCase("mobs") || what.equalsIgnoreCase("monster") || (what.equalsIgnoreCase("monsters"))) {

				builder.append("MONSTERS (" + monsters.size() + ") : ");
				for (MapleMapObject monstermo : monsters) {
					MapleMonster monster = (MapleMonster) monstermo;
					builder.append(monster.getName() + "-" + monster.getId() + " | ");
				}
			} else if (what.equalsIgnoreCase("player") || what.equalsIgnoreCase("players") || what.equalsIgnoreCase("chr") || what.equalsIgnoreCase("chrs") || what.equalsIgnoreCase("character") || what.equalsIgnoreCase("characters")) {

				builder.append("PLAYERS (" + players.size() + ") : ");
				for (MapleMapObject playermo : players) {
					MapleCharacter playera = (MapleCharacter) playermo;
					builder.append(playera.getName() + "-" + playera.getId() + " | ");
				}
			} else if (what.equalsIgnoreCase("reactor") || what.equalsIgnoreCase("reactors")) {
				builder.append("REACTORS (" + reactors.size() + ") : ");
				for (MapleMapObject reactormo : reactors) {
					MapleReactor reactor = (MapleReactor) reactormo;
					builder.append(reactor.getId() + " | ");
				}
			} else if (what.equalsIgnoreCase("all")) {
				builder.append("PLAYERS (" + players.size() + ") : ");
				for (MapleMapObject playermo : players) {
					MapleCharacter playera = (MapleCharacter) playermo;
					builder.append(playera.getName() + "-" + playera.getId() + " | ");
				}
				mc.dropMessage(builder.toString());
				builder.delete(0, builder.length());
				builder.append("MONSTERS (" + monsters.size() + ") : ");
				for (MapleMapObject monstermo : monsters) {
					MapleMonster monster = (MapleMonster) monstermo;
					builder.append(monster.getName() + "-" + monster.getId() + " | ");
				}
				mc.dropMessage(builder.toString());
				builder.delete(0, builder.length());
				builder.append("NPCS (" + npcs.size() + ") : ");
				for (MapleMapObject npcmo : npcs) {
					MapleNPC npc = (MapleNPC) npcmo;
					builder.append(npc.getName() + "-" + npc.getId() + " | ");
				}
				mc.dropMessage(builder.toString());
				builder.delete(0, builder.length());
				builder.append("REACTORS (" + reactors.size() + ") : ");
				for (MapleMapObject reactormo : reactors) {
					MapleReactor reactor = (MapleReactor) reactormo;
					builder.append(reactor.getId() + "-" + reactor.getName() + " | ");
				}
				mc.dropMessage(builder.toString());
				builder.delete(0, builder.length());
			} else {
				builder.append("Error, Invalid Parameters!");
			}
			if (builder.length() != 0) {
				mc.dropMessage(builder.toString());
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("resetquest", "", "", 1000),
					new CommandDefinition("nearestsp", "", "", 1000),
					new CommandDefinition("spawndebug", "", "", 1000),
					new CommandDefinition("timerdebug", "", "", 1000),
					new CommandDefinition("threads", "", "", 1000),
					new CommandDefinition("showtrace", "", "", 1000),
					new CommandDefinition("toggleoffense", "", "turn autoban on and off", 1000),
					new CommandDefinition("fakerelog", "", "relog into the same map", 100),
					new CommandDefinition("tdrops", "", "toggle drops on and off", 100),
					new CommandDefinition("tspawn", "", "toggle spawns on and off", 100),
					new CommandDefinition("partydebug", "", "debug party info onto server console", 100),
					new CommandDefinition("debugmap", "[type]", "Debug map-objects on your map", 100),};
	}
}
