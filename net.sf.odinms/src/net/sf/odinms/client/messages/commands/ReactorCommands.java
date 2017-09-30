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

import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.maps.MapleReactorFactory;
import net.sf.odinms.server.maps.MapleReactorStats;
import net.sf.odinms.tools.MaplePacketCreator;

public class ReactorCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		List<MapleMapObject> reactors = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));

		if (splitted[0].equals("!killreactors")) {
			MapleMap map = c.getPlayer().getMap();
			for (MapleMapObject reactormo : reactors) {
				MapleReactor reactor = (MapleReactor) reactormo;
				map.destroyReactor(reactor.getObjectId());
			}
			mc.dropMessage("Destroyed " + reactors.size() + " reactors <3");

		} else if (splitted[0].equals("!switchreactor")) {
			int state = Integer.parseInt(splitted[1]);
			MapleMap map = c.getPlayer().getMap();
			for (MapleMapObject reactormo : reactors) {
				MapleReactor reactor = (MapleReactor) reactormo;
				reactor.setState((byte) state);
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.triggerReactor(reactor, 1));//Show change to map
			}

		} else if (splitted[0].equals("!resetreactors")) {
			c.getPlayer().getMap().resetReactors();
			mc.dropMessage("Reactors reset");

		} else if (splitted[0].equals("!switchreactor1")) {
			int reactorId = Integer.parseInt(splitted[1]);
			int state = Integer.parseInt(splitted[2]);
			MapleMap map = c.getPlayer().getMap();
			for (MapleMapObject reactormo : reactors) {
				MapleReactor reactor = (MapleReactor) reactormo;
				if (reactor.getId() == reactorId) {
					reactor.setState((byte) state);
					map.broadcastMessage(MaplePacketCreator.triggerReactor(reactor, 1));
				}
			}

		} else if (splitted[0].equals("!reactor")) {
			int rid = Integer.parseInt(splitted[1]);
			MapleReactorStats stats = MapleReactorFactory.getReactor(rid);
			MapleReactor reactor = new MapleReactor(stats, rid);
			reactor.setDelay(-1);
			reactor.setPosition(c.getPlayer().getPosition());
			c.getPlayer().getMap().spawnReactor(reactor);

		} else if (splitted[0].equals("!reactorinfo")) {
			int reactorId = Integer.parseInt(splitted[1]);
			MapleReactorStats stats = MapleReactorFactory.getReactor(reactorId);
			MapleReactor reactor = new MapleReactor(stats, reactorId);
			if (reactor != null) {
				mc.dropMessage(reactor.getReactorId() + "-" + reactor.getName());
			} else {
				mc.dropMessage("Invalid Reactor-ID [" + reactorId + "]");
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("killreactors", "", "Kills all reactors on your map", 100),
					new CommandDefinition("resetreactors", "", "Resets all reactors on your map", 100),
					new CommandDefinition("switchreactor", "state", "SwitcKills all reactors on your map", 100),
					new CommandDefinition("switchreactor1", "reactorid | state", "Kills all reactors on your map", 100),
					new CommandDefinition("reactor", "reactorid", "Kills all reactors on your map", 100),
					new CommandDefinition("reactorinfo", "reactorid", "Kills all reactors on your map", 100),};
	}
}
