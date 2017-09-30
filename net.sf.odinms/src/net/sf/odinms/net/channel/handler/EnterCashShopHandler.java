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

import java.util.Calendar;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class EnterCashShopHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 500);
        if (c.getChannelServer().allowCashShop()) {
            c.getPlayer().getMap().removePlayer(c.getPlayer());
            int date = 0;
            Calendar cal = Calendar.getInstance();
            date += 10000 * cal.get(Calendar.YEAR);
            date += 100 * cal.get(Calendar.MONTH);
            date += cal.get(Calendar.DAY_OF_MONTH);
            c.getSession().write(MaplePacketCreator.warpCS(c, date));
            c.getPlayer().setInCS(true);
            c.getSession().write(MaplePacketCreator.enableCSUse0());
            c.getSession().write(MaplePacketCreator.enableCSUse1(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableCSUse2());
            c.getSession().write(MaplePacketCreator.getWishList(c.getPlayer(), false));
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getPlayer().saveToDB(true);
        } else {
            c.getSession().write(MaplePacketCreator.blockPortal(2, false));
        }
    }
}
