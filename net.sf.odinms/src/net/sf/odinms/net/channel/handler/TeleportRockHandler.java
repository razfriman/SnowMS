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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class TeleportRockHandler extends AbstractMaplePacketHandler {

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {

		boolean add = slea.readByte() == 1;
		byte source = slea.readByte();
		if (add) {
			if (source == 0) {
				c.getPlayer().addTeleportMap(c.getPlayer().getMapId());
			} else {
				c.getPlayer().addVipTeleportMap(c.getPlayer().getMapId());
			}
		} else {
			int mapid = slea.readInt();
			if (source == 0) {
				c.getPlayer().removeTeleportMap(mapid);
			} else {
				c.getPlayer().removeVipTeleportMap(mapid);
			}
		}
		c.getSession().write(MaplePacketCreator.updateTeleportMaps(c.getPlayer(), source == 1));
	}
}