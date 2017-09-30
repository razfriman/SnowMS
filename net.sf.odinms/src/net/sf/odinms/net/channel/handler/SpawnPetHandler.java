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

import java.awt.Point;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.PetDataFactory;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class SpawnPetHandler extends AbstractMaplePacketHandler {
	
	/*	TODO:
	 *	1.  Move the equpping into a function.
	 */ 
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
		byte slot = (byte) slea.readShort();
		boolean lead = slea.readByte() == 1;

		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		IItem petItem = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
		
		// Handle dragons
		if (ii.getPetEvolutionInfo(petItem.getItemId()) != null) {//Always null?
			new ServernoticeMapleClientMessageCallback(5, c).dropMessage("Evolving pet cannot currently be spawned.");
			c.getSession().write(MaplePacketCreator.enableActions());
			return;
		}

		//Create a new MaplePet
		MaplePet pet = MaplePet.loadFromDb(c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot).getItemId(), slot, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot).getPetId());
		pet.setClient(c);
		
		// Assign the pet to the player, set stats
		if (c.getPlayer().getPetIndex(pet) != -1) {
			c.getPlayer().unequipPet(pet, true);
		} else {			
			if (c.getPlayer().getSkillLevel(8) == 0 && c.getPlayer().getPet(0) != null) {
				c.getPlayer().unequipPet(c.getPlayer().getPet(0), false);
			}

			if (lead) {
				c.getPlayer().shiftPetsRight();
			}
			
			Point pos = c.getPlayer().getPosition();
			pos.y -= 12;
			pet.setPos(pos);
			pet.setFh(c.getPlayer().getMap().getFootholds().findBelow(pet.getPos()).getId());
			pet.setStance(0);
			
			c.getPlayer().addPet(pet);
			
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showPet(c.getPlayer(), pet, false));
			c.getSession().write(MaplePacketCreator.petStatUpdate(c.getPlayer()));
			c.getSession().write(MaplePacketCreator.enableActions());
			
			int hunger = PetDataFactory.getHunger(pet.getItemId());
			c.getPlayer().startFullnessSchedule(hunger, pet, c.getPlayer().getPetIndex(pet));

		}
	}	
}
