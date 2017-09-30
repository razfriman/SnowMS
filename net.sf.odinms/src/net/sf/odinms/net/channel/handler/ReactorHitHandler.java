/*
 * This file is part of the OdinMS Maple Story Server
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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lerk
 */
public class ReactorHitHandler extends AbstractMaplePacketHandler {
	private static Logger log = LoggerFactory.getLogger(ReactorHitHandler.class);

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int oid = slea.readInt();
		int charPos = slea.readInt();
		short stance = slea.readShort();

		MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);
		if (reactor != null && reactor.isAlive()) {
			reactor.hitReactor(charPos, stance, c);
		} else { // player hit a destroyed reactor, likely due to lag
			log.trace(c.getPlayer().getName() + "<" + c.getPlayer().getId() +
				"> attempted to hit destroyed reactor with oid " + oid);
		}
	}
}
