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

import java.util.Collections;
import java.util.List;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
*
* @author Raz
*/
public class TouchSnowballHandler extends AbstractMaplePacketHandler {
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.STUN, 0));
			c.getPlayer().setBuffedValue(MapleBuffStat.STUN, 0);
            MapleStatEffect statEffect = MapleStatEffect.getEmptyStatEffect();
			statEffect.setSourceid(0);
			statEffect.setDuration(0);
			statEffect.setStatsups(stat);
			statEffect.setStatType(MapleStatEffect.MapleStatEffectType.PLAYER_SKILL);
			statEffect.setMorph(0);
            
            c.getSession().write(MaplePacketCreator.sendKnockbackLeft());
            c.getSession().write(MaplePacketCreator.giveBuff(statEffect));
			c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.giveForeignBuff(c.getPlayer().getId(), stat), false);
	}
}
