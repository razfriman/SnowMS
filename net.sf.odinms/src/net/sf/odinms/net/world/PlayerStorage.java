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

package net.sf.odinms.net.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;

/**
 *
 * @author Raz
 */
public class PlayerStorage {

	private Map<Integer, MapleCharacter> players = new HashMap<Integer, MapleCharacter>();
	private static PlayerStorage instance = new PlayerStorage();

	private PlayerStorage() {
	}

	public static PlayerStorage getInstance() {
		return instance;
	}

	public void addPlayer(MapleCharacter chr) {
		players.put(chr.getId(), chr);
	}

	public MapleCharacter getPlayer(MapleCharacter chr) {
		return getPlayer(chr.getId());
	}

	public MapleCharacter getPlayer(int cid) {
		return players.get(cid);
	}

	public boolean removePlayer(MapleCharacter chr) {
		return removePlayer(chr.getId());
	}

	public boolean removePlayer(int cid) {
		if (players.containsKey(cid)) {
			players.remove(cid);
			return true;
		} else {
			return false;
		}
	}

	public boolean containsPlayer(MapleCharacter chr) {
		return containsPlayer(chr.getId());
	}

	public boolean containsPlayer(int cid) {
		if (players.isEmpty()) {
			return false;
		} else {
			return players.containsKey(cid);
		}
	}

	public void clearPlayers() {
		players.clear();
	}

	public boolean isEmpty() {
		return players.isEmpty();
	}

	public Collection<MapleCharacter> getPlayers() {
		return players.values();
	}

	public void savePlayers() {
		for (MapleCharacter chr : players.values()) {
			chr.saveToDB(true);
		}
	}

	public void initiatePlayer(MapleClient c, MapleCharacter chr) {
		c.setAccountName(chr.getClient().getAccountName());
		chr.setClient(c);
		MapleMapFactory mapFactory = ChannelServer.getInstance(c.getChannel()).getMapFactory();
		MapleMap map = mapFactory.getMap(chr.getMapId());
		if (map.getForcedReturn() != 999999999) {
			map = mapFactory.getMap(map.getForcedReturn());
		}
		chr.setMap(map);

		if (chr.getMap() == null) { //char is on a map that doesn't exist warp it to henesys
			System.out.println("CID:" + chr.getId() + " is on a null map, going to henesys - PLAYERSTORAGE");
			chr.setMap(mapFactory.getMap(100000000));
		}
		MaplePortal portal = chr.getMap().getPortal(chr.getInitialSpawnpoint());
		if (portal == null) {
			portal = chr.getMap().getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
			chr.setInitialSpawnPoint(0);
		}
		chr.setPosition(portal.getPosition());
	}
}
