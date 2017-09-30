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

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;

public class UserCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		MapleCharacter player = c.getPlayer();
		if (splitted[0].equals("!addap")) {
			MapleStat stat = null;
			int chosenAP = -1;
			int currentAP = c.getPlayer().getRemainingAp();
			if (splitted.length > 2) {
				if (splitted[1].equalsIgnoreCase("str")) {
					stat = MapleStat.STR;
				} else if (splitted[1].equalsIgnoreCase("dex")) {
					stat = MapleStat.DEX;
				} else if (splitted[1].equalsIgnoreCase("int")) {
					stat = MapleStat.INT;
				} else if (splitted[1].equalsIgnoreCase("luk")) {
					stat = MapleStat.LUK;
				}
				try {
					chosenAP = Integer.parseInt(splitted[2]);
				} catch (Exception e) {
					mc.dropMessage("Error reading AP amount.");
				}

				if (chosenAP != -1 && stat != null) {
					if (chosenAP < 1) {
						mc.dropMessage("Please enter a number higher than 0.");
					} else if (chosenAP > currentAP) {
						mc.dropMessage("You don't have enough AP.");
					} else if (chosenAP > 999) {
						mc.dropMessage("Please enter a number lower than 999.");
					} else {
						c.getPlayer().setRemainingAp(currentAP - chosenAP);
						if (stat == MapleStat.STR) {
							c.getPlayer().setStr(chosenAP + c.getPlayer().getStr());
							c.getPlayer().updateSingleStat(stat, c.getPlayer().getStr());
						} else if (stat == MapleStat.DEX) {
							c.getPlayer().setDex(chosenAP + c.getPlayer().getDex());
							c.getPlayer().updateSingleStat(stat, c.getPlayer().getDex());
						} else if (stat == MapleStat.INT) {
							c.getPlayer().setInt(chosenAP + c.getPlayer().getInt());
							c.getPlayer().updateSingleStat(stat, c.getPlayer().getInt());
						} else if (stat == MapleStat.LUK) {
							c.getPlayer().setLuk(chosenAP + c.getPlayer().getLuk());
							c.getPlayer().updateSingleStat(stat, c.getPlayer().getLuk());
						}
						mc.dropMessage("You have added (" + chosenAP + ") points to " + stat.name());
					}

				}
			} else {
				mc.dropMessage("Wrong format: !addap <STAT_NAME> <AP>");
			}
		} else if (splitted[0].equals("!revive")) {
			if (c.getPlayer().isAlive()) {
				mc.dropMessage("Sorry you are still alive, please try using this command when you are dead and the revive button has failed you");
			} else {
				mc.dropMessage("You will be revived to the nearest town.");
				player.playerRevive();
			}
		} else if (splitted[0].equals("!save")) {
			try {
				c.getPlayer().saveToDB(true);
				mc.dropMessage("Your progress has been saved.");
			} catch (Exception e) {
				mc.dropMessage("Error saving progress.");
			}
		}


	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("addap", "stat | ap", "Adds AP automatically for you.", 0),
					new CommandDefinition("revive", "", "Revive you incase you get stuck", 0),
					new CommandDefinition("save", "", "saves progress", 0),};
	}
}
