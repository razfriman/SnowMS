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
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Raz
 */
public class GMRankCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		if (splitted[0].equals("!makegm")) {
			if (splitted.length <= 1) {
				mc.dropMessage("Please enter a character name");
			} else {
				MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
				if (victim != null) {
					if (victim.getId() == c.getPlayer().getId()) {
						mc.dropMessage("You may not make yourself a gm");
					} else if (victim.isGM()) {
						mc.dropMessage(victim.getName() + " is already a GM");
					} else {
						victim.setGm(true);
						victim.getClient().setGm(true);
						victim.saveToDB(true);
						c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[" + victim.getName() + "] is now a GM(" + victim.getGMLevel() + ")"));
					}
				} else {
					mc.dropMessage("Cannot Find: " + splitted[1]);
				}
			}
		} else if (splitted[0].equals("!setgm")) {
			if (splitted.length < 3) {
				mc.dropMessage("Please enter a character name and level");
			} else {
				MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
				if (victim != null) {
					if (!victim.isGM()) {
						mc.dropMessage(victim.getName() + " is not a GM, so you may not change his GM-Level");
					} else {
						try {
							int gmLevel = Integer.parseInt(splitted[2]);
							if (victim.getId() == c.getPlayer().getId()) {
								mc.dropMessage("You may not set your own GM-Level");
							} else if (victim.getGMLevel() >= c.getPlayer().getGMLevel()) {
								mc.dropMessage("You may not set higher-ranked GM's GM-Level");
							} else if (gmLevel > c.getPlayer().getGMLevel()) {
								mc.dropMessage("You may not set a GM-Level this high");
							} else if (gmLevel == 0) {
								mc.dropMessage("You may not set the GM-Level to (0)");
							} else {
								String change = gmLevel > victim.getGMLevel() ? "raised" : "dropped";
								victim.setGmLevel(gmLevel);
								victim.saveToDB(true);
								c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[" + victim.getName() + "]'s GM-Level has been " + change + " to " + gmLevel));
							}
						} catch (Exception e) {
							mc.dropMessage("Please enter a valid GM-Level");
						}
					}
				} else {
					mc.dropMessage("Cannot Find: " + splitted[1]);
				}
			}
		} else if (splitted[0].equals("!isgm")) {
			if (splitted.length < 2) {
				mc.dropMessage("Please enter a character name");
			} else {
				MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
				if (victim != null) {
					if (victim.isGM()) {
						mc.dropMessage("[" + victim.getName() + "] is a GM(" + victim.getGMLevel() + ")");
					} else {
						mc.dropMessage("[" + victim.getName() + "] is a Normal Player");
					}
				} else {
					mc.dropMessage("Cannot Find: " + splitted[1]);
				}
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("makegm", "victim", "Makes the victim a gm", 1000),
					new CommandDefinition("setgm", "victim | level", "Sets the victim's GM-Level", 100),
					new CommandDefinition("isgm", "victim", "Checks if the player is a GM", 100),};
	}
}