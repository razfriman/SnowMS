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

package net.sf.odinms.net.channel.handler;

import java.net.InetAddress;

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.GameConstants.FieldLimitBits;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class ChangeChannelHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	  int channel = slea.readByte() + 1;
	  c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 500);
	  c.getPlayer().cancelAllBuffs();//TODO Check  on buffs (foreign)
	  ChannelServer chnSrv = ChannelServer.getInstance(channel);
	  if (c.getPlayer().getMap().checkFieldLimit(FieldLimitBits.CHANNEL_SWITCH)) {
		c.disconnect();
		return;
	  } else if (chnSrv == null || chnSrv.isShutdown()) {
		c.getPlayer().dropMessage(5, "That server is not available at this time. Try again later.");
		c.getSession().write(MaplePacketCreator.enableActions());
		return;
	  }
	  String ip = ChannelServer.getInstance(c.getChannel()).getIP(channel);
	  String[] socket = ip.split(":");
	  if (c.getPlayer().getTrade() != null) {
		MapleTrade.cancelTrade(c.getPlayer());
	  }

	  c.getPlayer().saveToDB(true);

	  if (c.getPlayer().getCheatTracker() != null) {
		c.getPlayer().getCheatTracker().dispose();
	  }

	  if (c.getPlayer().getMessenger() != null) {
		WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
		MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
		try {
		    wci.silentLeaveMessenger(c.getPlayer().getMessenger().getId(), messengerplayer);
		} catch (RemoteException e) {
		    c.getChannelServer().reconnectWorld();
		}
	  }

	  c.getPlayer().getMap().removePlayer(c.getPlayer());

	  ChannelServer.getInstance(c.getChannel()).removePlayer(c.getPlayer());

	  c.updateLoginState(MapleClient.ClientStatus.LOGIN_SERVER_TRANSITION);
	  try {
		MaplePacket packet = MaplePacketCreator.getChannelChange(
			  InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
		c.getSession().write(packet);
		c.getSession().close();
	  } catch (Exception e) {
		throw new RuntimeException(e);
	  }
    }
}
