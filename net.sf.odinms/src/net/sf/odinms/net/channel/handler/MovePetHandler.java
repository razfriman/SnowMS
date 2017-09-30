/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy  
                       Matthias Butz 
                       Jan Christian Meyer 

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
    along with this program.  If not, see .
*/

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MovePetHandler extends AbstractMovementPacketHandler {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MovePetHandler.class);

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int petId = slea.readInt();
		slea.readInt();
		MovementPath movementPath = parseMovement(slea);

		MapleCharacter player = c.getPlayer();
		int slot = player.getPetIndex(petId);
		if (slot == -1) {
			log.warn("[h4x] {} ({}) trying to move a pet he/she does not own.", c.getPlayer().getName(), c.getPlayer().getId());
			return;
		}
		player.getPet(slot).updatePosition(movementPath);
		player.getMap().broadcastMessage(player, MaplePacketCreator.movePet(player.getId(), petId, slot, movementPath), false);
	}
}