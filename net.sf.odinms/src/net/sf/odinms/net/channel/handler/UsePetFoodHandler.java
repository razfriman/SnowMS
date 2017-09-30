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

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.GameConstants;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class UsePetFoodHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
        byte slot = (byte) slea.readShort();
        int itemId = slea.readInt();
        IItem item = c.getPlayer().getInventory(ii.getInventoryType(itemId)).getItem(slot);
        int previousFullness = 100;
        int index = 0;

        if (c.getPlayer().getPetsNumber() == 0) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        MaplePet[] pets = c.getPlayer().getAllPets();
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getFullness() < previousFullness) {
                    index = i;
                    previousFullness = pets[i].getFullness();
                }
            }
        }

        MaplePet pet = c.getPlayer().getPet(index);

        if (item == null || item.getQuantity() == 0 || item.getItemId() != itemId) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        boolean gainCloseness = Randomizer.randomBoolean();

        if (!pet.isFull()) {
            pet.gainFullness(GameConstants.Stats.PET_FFED_FULLNESS);
        }
        if (gainCloseness) {
            pet.gainCloseness(pet.isFull() ? -1 : 1 * c.getChannelServer().getPetExpRate());
        }
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.commandResponse(c.getPlayer().getId(), 1, index, !pet.isFull(), true));
        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemId, 1, true, false);
    }
}
