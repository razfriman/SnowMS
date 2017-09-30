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
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.IPlayerStorage;
import net.sf.odinms.tools.MaplePacketCreator;

public class OtherCharCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {

		IPlayerStorage storage = c.getChannelServer().getPlayerStorage();
		if (splitted[0].equals("!show")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			if (victim != null) {
				victim.cancelAllBuffs();
			}
		} else if (splitted[0].equals("!setsp")) {
			if (splitted.length > 2) {
				MapleCharacter victim = storage.getCharacterByName(splitted[1]);
				if (victim != null) {
					victim.setRemainingSp(getOptionalIntArg(splitted, 2, 1));
					victim.updateSingleStat(MapleStat.AVAILABLESP, victim.getRemainingSp());
				}
			}
		} else if (splitted[0].equals("!setjob")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			victim.changeJob(MapleJob.getById(Integer.parseInt(splitted[2])));
		} else if (splitted[0].equals("!setlevel")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			victim.setLevel(getOptionalIntArg(splitted, 2, 1));//Change 2, 1
			victim.levelUp();
			int newexp = victim.getExp();
			if (newexp < 0) {
				victim.gainExp(-newexp, false, false);
			}
		} else if (splitted[0].equals("!setclearinv")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			victim.clearInv(getOptionalIntArg(splitted, 2, 1), getOptionalIntArg(splitted, 3, 1));
		} else if (splitted[0].equals("!transform")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			if (!victim.getClient().isLoggedIn()) {
				mc.dropMessage("Error : Character is not online");
			}
			c.getSession().write(MaplePacketCreator.getCharInfo(victim));
			mc.dropMessage("Warning : This is Client-Sided");
		} else if (splitted[0].equals("!setskill")) {
			if (splitted.length > 4) {
				MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
				if (victim != null) {
					int skill = Integer.parseInt(splitted[2]);
					int level = getOptionalIntArg(splitted, 3, 1);
					int masterlevel = getOptionalIntArg(splitted, 4, 1);
					victim.changeSkillLevel(SkillFactory.getSkill(skill), level, masterlevel);
				}
			}
		} else if (splitted[0].equals("!unequip")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			if (victim != null) {
				victim.unequipEverything();
			}

		} else if (splitted[0].equals("!char")) {
			MapleCharacter victim = storage.getCharacterByName(splitted[1]);
			c.getSession().write(MaplePacketCreator.charInfo(victim, false));
		}



	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("show", "player", "reveals the player and cancells his/her's buffs)", 100),
					new CommandDefinition("setsp", "player | sp", "gives the player sp", 100),
					new CommandDefinition("setjob", "player | job", "change the players job", 100),
					new CommandDefinition("setlevel", "player | level", "set the players level", 100),
					new CommandDefinition("setclearinv", "player | inv | amount", "clear the players inv", 100),
					new CommandDefinition("transform", "player", "transform into a different character", 100),
					new CommandDefinition("setskill", "player | skill | skilllevel | maxskilllevel", "give a player a skill", 100),
					new CommandDefinition("unequip", "playername", "unequips thier inv", 100),
					new CommandDefinition("char", "playername", "shows the char-info panel for that character", 100),};
	}
}
