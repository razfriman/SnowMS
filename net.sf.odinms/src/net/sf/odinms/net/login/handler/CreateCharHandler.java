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

package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleSkinColor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.GameConstants;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CreateCharHandler extends AbstractMaplePacketHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreateCharHandler.class);
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		String name = slea.readMapleAsciiString();
                byte classType = slea.readByte();
		int face = slea.readInt();
		int hair = slea.readInt();
		int hairColor = slea.readInt();
		int skinColor = slea.readInt();
		int top = slea.readInt();
		int bottom = slea.readInt();
		int shoes = slea.readInt();
		int weapon = slea.readInt();
		byte gender = slea.readByte();

		MapleCharacter newchar = MapleCharacter.getDefault(c);
		newchar.setWorld(c.getWorld());
		newchar.setFace(face);
		newchar.setHair(hair + hairColor);
		newchar.setGender(gender);
		newchar.setStr(12);
		newchar.setDex(5);
		newchar.setInt(4);
		newchar.setLuk(4);
		newchar.setName(name);
		newchar.setGmLevel(c.isGm() ? 1 : 0);
		newchar.setSkinColor(MapleSkinColor.getById(skinColor));
		
		MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
		
                IItem eq_top = MapleItemInformationProvider.getInstance().getEquipById(top);
                eq_top.setPosition((byte) -GameConstants.EquipSlots.Top);
		equip.addFromDB(eq_top);

		IItem eq_bottom = MapleItemInformationProvider.getInstance().getEquipById(bottom);
		eq_bottom.setPosition((byte) -GameConstants.EquipSlots.Bottom);
		equip.addFromDB(eq_bottom);

		IItem eq_shoes = MapleItemInformationProvider.getInstance().getEquipById(shoes);
		eq_shoes.setPosition((byte) -GameConstants.EquipSlots.Shoe);
		equip.addFromDB(eq_shoes);

		IItem eq_weapon = MapleItemInformationProvider.getInstance().getEquipById(weapon);
		eq_weapon.setPosition((byte) -GameConstants.EquipSlots.Weapon);
		equip.addFromDB(eq_weapon);

		MapleInventory etc = newchar.getInventory(MapleInventoryType.ETC);
		etc.addItem(new Item(4161001, (byte) 0, (short) 1));

                MapleJob startJob = MapleJob.BEGINNER;
                int startMap = 0;
                
                switch (classType) {

                    case 0:
                        startJob = MapleJob.NOBLESSE;
                        startMap = 130030000;
                        break;
                    case 1:
                        startJob = MapleJob.BEGINNER;
                        startMap = 0;
                        break;
                    case 2:
                        startJob = MapleJob.LEGEND;
                        startMap = 914000000;
                        break;
                }

		newchar.setJob(startJob);
                newchar.setStartMap(startMap);

                boolean charOk = MapleCharacterUtil.isValidChar(classType, gender, face, hair, hairColor, skinColor, top, bottom, shoes, weapon);
                
		if (MapleCharacterUtil.canCreateChar(name, c.getWorld()) == 0) {
			newchar.saveToDB(false);
			c.getSession().write(MaplePacketCreator.addNewCharEntry(newchar, charOk));
		} else {
			log.warn(MapleClient.getLogMessage(c, "Trying to create a character with a name: {}", name));
		}
	}
}
