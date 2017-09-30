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
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.GameConstants;
import net.sf.odinms.server.GameConstants.FieldLimitBits;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class UseTeleportRockHandler extends AbstractMaplePacketHandler {

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte slot = (byte) slea.readShort();
		int itemid = slea.readInt();
		IItem item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
		
		if (item.getQuantity() < 1) {
		    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
		}
		int type = itemid == GameConstants.Items.VIP_TELEPORT_ROCK.getValue() ? 1 : 0;
		handleTeleportRock(slea, c, type);
		MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
	}

    public static boolean checkRockDestination(MapleCharacter chr, int mapid) {
        boolean valid = false;
        for(int i = 0; i < chr.getTeleportMaps().length; i++) {
            if (chr.getTeleportMaps()[i] == mapid) {
                valid = true;
                break;
            }
        }
        for(int i = 0; i < chr.getVipTeleportMaps().length; i++) {
            if (chr.getVipTeleportMaps()[i] == mapid) {
                valid = true;
                break;
            }
        }
        return valid;
    }

	public static boolean handleTeleportRock(SeekableLittleEndianAccessor slea, MapleClient c, int type) {
		byte mode = slea.readByte();
        int mapId = -1;
        boolean result = false;
		if (mode == 0) {
			mapId = slea.readInt();
            if (!checkRockDestination(c.getPlayer(), mapId)) {
                c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
                return false;
            }
		} else {
			String name = slea.readMapleAsciiString();
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
            if (chr != null && chr != c.getPlayer()) {
                mapId = chr.getMapId();
            } else if (chr == null) {
                c.getSession().write(MaplePacketCreator.sendTeleportRockError(6, type));
            } else if (chr == c.getPlayer()) {
                c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
                return false;
            }
		}
        if (mapId != -1) {
            MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
            if (map.checkFieldLimit(FieldLimitBits.VIP_ROCK)) {
                c.getSession().write(MaplePacketCreator.sendTeleportRockError(8, type));
            } else if (c.getPlayer().getMap().checkFieldLimit(FieldLimitBits.VIP_ROCK)) {
                c.getSession().write(MaplePacketCreator.sendTeleportRockError(8, type));
            } else if (type == 0 && map.getContinent() != c.getPlayer().getMap().getContinent()) {
                c.getSession().write(MaplePacketCreator.sendTeleportRockError(8, type));
            } else {
                c.getPlayer().changeMap(map, map.getRandomPortal());
                result = true;
            }
        }
        return result;
	}
}