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

import java.util.Collection;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MoveSummonHandler extends AbstractMovementPacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int oid = slea.readInt();
                MovementPath movementPath = parseMovement(slea);

		MapleCharacter player = c.getPlayer();
		Collection<MapleSummon> summons = player.getSummons().values();
		MapleSummon summon = null;
		for (MapleSummon sum : summons) {
			if (sum.getObjectId() == oid) {
				summon = sum;
			}
		}
		if (summon != null) {
			updatePosition(movementPath, summon, 0);
			player.getMap().broadcastMessage(player, MaplePacketCreator.moveSummon(player.getId(), oid, movementPath), summon.getPosition());
		}
	}
}