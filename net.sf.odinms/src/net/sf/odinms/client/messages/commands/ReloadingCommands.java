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

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.ExternalCodeTableGetter;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.RecvPacketOpcode;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.scripting.reactor.ReactorScriptManager;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.life.MapleMonsterInformationProvider;

public class ReloadingCommands implements Command {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReloadingCommands.class);

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		ChannelServer cserv = c.getChannelServer();
		if (splitted[0].equals("!reload")) {
			if (splitted.length == 1) {
				mc.dropMessage("!reload: <All> <Guilds> <Ops> <Portals> <Drops> <Reactors> <Shops> <Events> <Commands>");
			} else {
				String reload = splitted[1];
				if (reload.equalsIgnoreCase("guilds")) {
					try {
						mc.dropMessage("Attempting to reload all guilds... this may take a while...");
						cserv.getWorldInterface().clearGuilds();
						mc.dropMessage("Completed.");
					} catch (RemoteException re) {
						mc.dropMessage("RemoteException occurred while attempting to reload guilds.");
						log.error("RemoteException occurred while attempting to reload guilds.", re);
					}
				} else if (reload.equalsIgnoreCase("ops")) {
					try {
						ExternalCodeTableGetter.populateValues(SendPacketOpcode.getDefaultProperties(), SendPacketOpcode.values());
						ExternalCodeTableGetter.populateValues(RecvPacketOpcode.getDefaultProperties(), RecvPacketOpcode.values());
						PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER).reset(PacketProcessor.Mode.CHANNELSERVER);
						PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER).reset(PacketProcessor.Mode.CHANNELSERVER);
						mc.dropMessage("Packet-Opcodes reloaded");
					} catch (Exception e) {
						log.error("Failed to reload props", e);
					}
				} else if (reload.equalsIgnoreCase("portals")) {
					PortalScriptManager.getInstance().clearScripts();
					mc.dropMessage("Portals reloaded");
				} else if (reload.equalsIgnoreCase("drops")) {
					MapleMonsterInformationProvider.getInstance().clearDrops();
					mc.dropMessage("Monster-Drops reloaded");
				} else if (reload.equalsIgnoreCase("reactors")) {
					ReactorScriptManager.getInstance().clearDrops();
					mc.dropMessage("Reactor-Drops reloaded");
				} else if (reload.equalsIgnoreCase("shops")) {
					MapleShopFactory.getInstance().clear();
					mc.dropMessage("Shops reloaded");
				} else if (reload.equalsIgnoreCase("events")) {
					for (ChannelServer instance : ChannelServer.getAllInstances()) {
						instance.reloadEvents();
					}
					mc.dropMessage("Events reloaded");
				} else if (reload.equalsIgnoreCase("commands")) {
					CommandProcessor.getInstance().reloadCommands();
					mc.dropMessage("Commands reloaded");
				} else if (reload.equalsIgnoreCase("all")) {
					try {
						cserv.getWorldInterface().clearGuilds();
						ExternalCodeTableGetter.populateValues(SendPacketOpcode.getDefaultProperties(), SendPacketOpcode.values());
						ExternalCodeTableGetter.populateValues(RecvPacketOpcode.getDefaultProperties(), RecvPacketOpcode.values());
						PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER).reset(PacketProcessor.Mode.CHANNELSERVER);
						PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER).reset(PacketProcessor.Mode.CHANNELSERVER);
						PortalScriptManager.getInstance().clearScripts();
						MapleMonsterInformationProvider.getInstance().clearDrops();
						ReactorScriptManager.getInstance().clearDrops();
						MapleShopFactory.getInstance().clear();
						for (ChannelServer instance : ChannelServer.getAllInstances()) {
							instance.reloadEvents();
						}
						CommandProcessor.getInstance().reloadCommands();
						mc.dropMessage("Everything reloaded");
					} catch (Exception e) {
						mc.dropMessage("An error occured while reloading");
					}
				}

			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("reload", "object to reload", "realods objects", 100),};
	}
}
