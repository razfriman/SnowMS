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
import java.util.List;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;

public class MonsterInfoCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		if (splitted[0].equals("!monsterdebug")) {
			MapleMap map = c.getPlayer().getMap();
			double range = Double.POSITIVE_INFINITY;
			if (splitted.length > 1) {
				int irange = Integer.parseInt(splitted[1]);
				range = irange * irange;
			}
			List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
			for (MapleMapObject monstermo : monsters) {
				MapleMonster monster = (MapleMonster) monstermo;
				mc.dropMessage("Monster " + monster.toString());
			}
		} else if (splitted[0].equals("!killall")) {
			MapleMap map = c.getPlayer().getMap();
			boolean drops = getOptionalIntArg(splitted, 1, 0) > 0;
            int killed = map.killAllMonsters(c.getPlayer(), drops);
			mc.dropMessage("Killed " + killed + " monsters");
		} else if (splitted[0].equals("!mobinfo")) {
			int mobId = Integer.parseInt(splitted[1]);
			MapleMonster mob = MapleLifeFactory.getMonster(mobId);
			if (mob != null) {
				mc.dropMessage(mob.getName() + "-" + mob.getId());
			} else {
				mc.dropMessage("Invalid Mob-ID [" + mobId + "]");
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("killall", "drops - fakechar", "kill monsters with no drops", 100),
					new CommandDefinition("monsterdebug", "[range]", "", 100),
					new CommandDefinition("mobinfo", "mobid", "Shows the Mob's name", 100),};
	}
}