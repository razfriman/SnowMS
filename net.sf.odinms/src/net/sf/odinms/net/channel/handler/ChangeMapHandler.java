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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeMapHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(ChangeMapHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {

	  if (slea.available() == 0) { //leave cash shop
		int channel = 1;
		String ip = ChannelServer.getInstance(c.getChannel()).getIP(channel);
		String[] socket = ip.split(":");
		c.getPlayer().saveToDB(true);
		c.getPlayer().setInCS(false);
		ChannelServer.getInstance(c.getChannel()).removePlayer(c.getPlayer());
		c.updateLoginState(MapleClient.ClientStatus.LOGIN_SERVER_TRANSITION);
		try {
		    MaplePacket packet = MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
		    c.getSession().write(packet);
		    c.getSession().close();
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
	  } else {
		@SuppressWarnings("unused")
		byte source = slea.readByte(); //curFieldKey
		int targetid = slea.readInt();

		String startwp = slea.readMapleAsciiString();
		MaplePortal portal = c.getPlayer().getMap().getPortal(startwp);

		if (targetid == 0 && !c.getPlayer().isAlive()) {
		    c.getPlayer().playerRevive();
		} else if (targetid == -1) {
		    if (portal != null) {
			  portal.enterPortal(c);
		    } else {
			  c.getSession().write(MaplePacketCreator.enableActions());
			  log.warn("Portal {} not found on map {}", startwp, c.getPlayer().getMap().getId());
		    }
		} else { //GM Map change (command '/m')
		    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
		    MaplePortal pto = to.getPortal(0);
		    c.getPlayer().changeMap(to, pto);
		}
	  }
    }
}
