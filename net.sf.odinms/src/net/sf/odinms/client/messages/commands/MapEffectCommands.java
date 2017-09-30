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

import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.life.SpawnPoint;
import net.sf.odinms.server.maps.MapleGenericPortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapEffectCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {

		MapleMap curMap = c.getPlayer().getMap();

		if (splitted[0].equals("!cleardrops")) {
			double range = Double.POSITIVE_INFINITY;
			List<MapleMapObject> items = curMap.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.ITEM));
			for (MapleMapObject itemmo : items) {
				curMap.removeMapObject(itemmo);
				curMap.broadcastMessage(MaplePacketCreator.removeItemFromMap(itemmo.getObjectId(), 0, c.getPlayer().getId()));
			}
			mc.dropMessage("Destroyed " + items.size() + " drops <3");
		} else if (splitted[0].equals("!clear")) {
			curMap.pqSign(true);
		} else if (splitted[0].equals("!wrong")) {
			curMap.pqSign(false);
		} else if (splitted[0].equals("!win")) {
			curMap.carnivalSign(true);
		} else if (splitted[0].equals("!lose")) {
			curMap.carnivalSign(false);
		} else if (splitted[0].equals("!victory")) {
			curMap.eventSign(true);
		} else if (splitted[0].equals("!loose")) {
			curMap.eventSign(false);
		} else if (splitted[0].equals("!animation")) {
			curMap.broadcastMessage(MaplePacketCreator.effectEnvironment(splitted[1], 3));
		} else if (splitted[0].equals("!sound")) {
			curMap.broadcastMessage(MaplePacketCreator.effectEnvironment(splitted[1], 4));
		} else if (splitted[0].equals("!effect")) {
			if (splitted.length > 2) {
				int type = Integer.parseInt(splitted[1]);
				String string = splitted[2];
				curMap.broadcastMessage(MaplePacketCreator.effectEnvironment(string, type));
			} else {
				mc.dropMessage("Error: !effect <TYPE> <STRING>");
			}
                } else if (splitted[0].equals("!movie")) {
                    c.getSession().write(MaplePacketCreator.showMovieEffect(splitted[1]));
		} else if (splitted[0].equals("!setportal")) {
			if (splitted.length > 2) {
				String name = splitted[1];
				String scriptName = splitted[2];
				MaplePortal portal = curMap.getPortal(name);
				if (portal == null) {
					mc.dropMessage("Invalid Portal NAME");
				} else {
					MapleGenericPortal gportal = (MapleGenericPortal) portal;
					gportal.getTargetMapId();
					if (scriptName.equalsIgnoreCase("null")) {
						gportal.setScriptName(null);
					} else {
						gportal.setScriptName(scriptName);
					}
					mc.dropMessage(portal.getName() + " | " + portal.getScriptName());
				}
			}
		} else if (splitted[0].equals("!allportals")) {
			mc.dropMessage("Portals on Map - " + curMap.getId() + "(" + curMap.getPortals().size() + ")");
			for (MaplePortal p : curMap.getPortals()) {
				mc.dropMessage(p.getName() + " | " + p.getId() + " | " + p.getScriptName() + " [" + p.getPosition().x + "|" + p.getPosition().y + "]");
			}
		} else if (splitted[0].equals("!allspawnpoints")) {
			mc.dropMessage("SpawnPoints on Map - " + curMap.getId() + "(" + curMap.getMonsterSpawns().size() + ")");
			int i = 1;
			for (SpawnPoint sp : curMap.getMonsterSpawns()) {
				mc.dropMessage(i + " | " + sp.getPosition().x + " | " + sp.getPosition().y);
				i++;
			}
		} else if (splitted[0].equals("!gate")) {
            String effect = "gate";
			if (splitted.length > 1) {
                effect = splitted[1];
			}
				curMap.broadcastMessage(MaplePacketCreator.effectEnvironment(effect, 2));
		} else if (splitted[0].equals("!song")) {
			String songName = splitted[1];
			curMap.broadcastMessage(MaplePacketCreator.effectEnvironment(songName, 6));

		} else if (splitted[0].equals("!mapinfo")) {

			int mapId = getOptionalIntArg(splitted, 1, curMap.getId());
			MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
			if (map != null) {
				mc.dropMessage(map.getId() + " - " + map.getMapName() + " - " + map.getStreetName());
			} else {
				mc.dropMessage("Invalid Map-ID [" + mapId + "]");
			}
		} else if (splitted[0].equals("!eventinstruct")) {
			int level = 0;
			if (splitted.length > 1) {
				level = Integer.parseInt(splitted[1]);
			}
			MaplePacket packet = MaplePacketCreator.eventInstruction();
			if (level == 0) {
				c.getSession().write(MaplePacketCreator.eventInstruction());
				mc.dropMessage("Event Instruction sent to you");
			} else if (level == 1) {
				curMap.broadcastMessage(packet);
				mc.dropMessage("Event Instruction sent to map");
			} else if (level == 2) {
				curMap.broadcastMessage(c.getPlayer(), packet, false);
				mc.dropMessage("Event Instruction sent to others on map");
			}

		} else if (splitted[0].equals("!healall")) {//REMOVE FROM HERE
			Collection<MapleCharacter> mapCharacters = new LinkedList<MapleCharacter>(curMap.getCharacters());
			for (MapleCharacter chr : mapCharacters) {
				chr.setHp(chr.getMaxHp());
				chr.setMp(chr.getMaxMp());
				chr.updateSingleStat(MapleStat.MP, chr.getMaxMp());
				chr.updateSingleStat(MapleStat.HP, chr.getMaxHp());
			}
		} else if (splitted[0].equals("!lockallportals")) {
			int type = getOptionalIntArg(splitted, 2, 1);
			curMap.blockPortals(type);
		} else if (splitted[0].equals("!lockportal")) {
			MaplePortal portal = curMap.getPortal(getOptionalIntArg(splitted, 1, 0));
			int type = getOptionalIntArg(splitted, 2, 1);
			if (portal == null) {
				mc.dropMessage("Invalid Portal");
			} else {
				curMap.blockPortal(portal, type);
			}
		} else if (splitted[0].equals("!unlockallportals")) {
			curMap.unblockPortals();
		} else if (splitted[0].equals("!unlockportal")) {
			MaplePortal portal = curMap.getPortal(getOptionalIntArg(splitted, 1, 0));
			if (portal == null) {
				mc.dropMessage("Invalid Portal");
			} else {
				curMap.unblockPortal(portal);
			}
		} else if (splitted[0].equals("!clock")) {
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(getOptionalIntArg(splitted, 1, 60)));
        } else if (splitted[0].equals("!oxquiz")) {
            boolean askQuestion = getOptionalIntArg(splitted, 1, 1) > 0;
            int questionSet = getOptionalIntArg(splitted, 2, 1);
            int questionId = getOptionalIntArg(splitted, 3, 1);
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showOXQuiz(questionSet, questionId, askQuestion));
        }
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("cleardrops", "", "clear drops on the map", 100),
					new CommandDefinition("clear", "", "clear sign", 100),
					new CommandDefinition("wrong", "", "wrong sign", 100),
					new CommandDefinition("win", "", "win sign", 100),
					new CommandDefinition("lose", "", "lose sign", 100),
					new CommandDefinition("victory", "", "victory sign", 100),
					new CommandDefinition("loose", "", "loose sign", 100),
					new CommandDefinition("animation", "animation-name", "", 100),
					new CommandDefinition("sound", "sound-name", "", 100),
					new CommandDefinition("effect", "type | message", "", 100),
					new CommandDefinition("setportal", "portal-id | script-name", "", 100),
					new CommandDefinition("allportals", "", "list/debug all the portals on the map", 100),
					new CommandDefinition("allspawnpoints", "", "", 100),
					new CommandDefinition("gate", "[#]", "", 100),
					new CommandDefinition("song", "song-name", "", 100),
					new CommandDefinition("mapinfo", "map-id", "tells you the map-street and mapname", 100),
					new CommandDefinition("healall", "", "Heal all players in your map to full HP and MP", 100),
					new CommandDefinition("lockallportals", "NONE/TYPE", "Lock all portals on the map", 100),
					new CommandDefinition("lockportal", "portal-id, none/type", "Lock [portalid] with [type]", 100),
					new CommandDefinition("unlockallportals", "", "Unlock all portals on the map", 100),
					new CommandDefinition("unlockportal", "portal-id", "Unlock [portal-id]", 100),
					new CommandDefinition("eventinstruct", "level", "Send an event instruction message", 100),
                                        new CommandDefinition("movie", "scene", "Send a movie effect", 100),
                    new CommandDefinition("clock", "[time]", "Shows a clock to everyone in the map, a negative amount of time will stop the clock", 1000),
                    new CommandDefinition("oxquiz", "[ask/asnwer] [question set] [question id]", "Asks/Answer an OX Quiz Question", 100),
        };
	}
}
