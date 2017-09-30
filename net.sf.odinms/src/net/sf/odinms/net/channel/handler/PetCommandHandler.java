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
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.PetCommand;
import net.sf.odinms.client.PetDataFactory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PetCommandHandler extends AbstractMaplePacketHandler {
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {

		int petId = slea.readInt();
		MaplePet pet = c.getPlayer().getPetByUniqueId(petId);
		if (pet == null) {
		    //Hack?
		    return;
		}
		slea.readInt();
		slea.readByte();
		
		byte command = slea.readByte();
		PetCommand petCommand = PetDataFactory.getPetCommand(pet.getItemId(), (int) command);
		boolean success = false;
			
		if (Randomizer.randomBoolean(petCommand.getProb(), 100)) {
			success = true;
			pet.gainCloseness(petCommand.getInc() * c.getChannelServer().getPetExpRate());
		}
		
		c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.commandResponse(c.getPlayer().getId(), command, pet.getIndex(c.getPlayer()), success, false));
	}
}