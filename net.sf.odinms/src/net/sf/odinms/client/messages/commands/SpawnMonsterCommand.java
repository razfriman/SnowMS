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

import java.util.Collections;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;

public class SpawnMonsterCommand implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		if (splitted[0].equals("!spawn") || splitted[0].equals("!summon")) {
			int mid = Integer.parseInt(splitted[1]);
			int num = Math.min(getOptionalIntArg(splitted, 2, 1), 500);//500 change for max
			int effect = getOptionalIntArg(splitted, 3, 0);
			for (int i = 0; i < num; i++) {
				MapleMonster mob = MapleLifeFactory.getMonster(mid);
				mob.setSummonEffect(effect);//might make all monsters have this effect
				c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
			}
		} else if (splitted[0].equals("!spawnhypno")) {
                int mid = Integer.parseInt(splitted[1]);
                int num = Math.min(getOptionalIntArg(splitted, 2, 1), 500);//500 change for max
                MonsterStatusEffect mse = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.HYPNOTIZE, 1), SkillFactory.getSkill(5221009), false);
                for (int i = 0; i < num; i++) {
                    MapleMonster mob = MapleLifeFactory.getMonster(mid);
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
                    mob.applyStatus(c.getPlayer(), mse, false, 60000);
                }
            }
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("spawn", "monsterid | amount | effect", "Spawns the monster with the given id", 200),
					new CommandDefinition("summon", "monsterid | amount | effect", "Spawns the monster with the given id", 200),
                    new CommandDefinition("spawnhypno", "monsterid | amount", "Spawns the monster with the hypnotized status", 200),};
	}
}
