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
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MobToMobHitHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleMap map = c.getPlayer().getMap();
        int damageDealer = slea.readInt();
        int cid = slea.readInt();
        int damageTaker = slea.readInt();
        MapleMonster damageDealerMob = map.getMonsterByOid(damageDealer);
        MapleMonster damageTakerMob = map.getMonsterByOid(damageTaker);
        if (damageDealerMob != null && damageTakerMob != null) {
            int damage = damageDealerMob.getLevel() * Randomizer.randomInt(100);
            c.getPlayer().getMap().damageMonster(c.getPlayer(), damageTakerMob, damage);
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.damageMonster(damageTaker, damage, true, damageTakerMob.getHp(), damageTakerMob.getMaxHp()));
        }
    }
}
