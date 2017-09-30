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

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class AdminCommandHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	  byte type = slea.readByte();
          System.out.println("GM Command: (" + type + ")");
          c.getSession().write(MaplePacketCreator.serverNotice(5, slea.toString()));
	  switch (type) {
		case 0x00: //create
		    int itemId = slea.readInt();
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    if (ii.getItemData(itemId) != null) {
			MapleInventoryManipulator.addById(c, itemId, (short)1, "GM Create command", c.getPlayer().getName());
                    }
		    break;
		case 0x01: //d
		{
		    byte invType = slea.readByte();
			MapleInventory inv = c.getPlayer().getInventory(MapleInventoryType.getByType(invType));
			for (byte i = 0; i < inv.getSlotLimit(); i++) {
				if (inv.removeItem(i, inv.getItemAmountBySlot(i), false)) {
					break;
				}
			}
		    break;
		}
		case 0x02: //exp
		{
		    int exp = slea.readInt();
		    c.getPlayer().gainExp(exp, false, false);
		    break;
		}
		case 0x03: //ban (disconects user)
		{
		    String name = slea.readMapleAsciiString();
			MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
			victim.getClient().getSession().close();
			victim.getClient().disconnect();
		    break;
		}
		case 0x04: //block (character name) (duration) (sort)
		{

			break;
		}
		case 0x06: //?
		{
		    String s = slea.readMapleAsciiString();
		    break;
		}
		case 0x09: //?
		{
		    byte b = slea.readByte(); //10
		    String s1 = slea.readMapleAsciiString();
		    String s2 = slea.readMapleAsciiString();
		    String s3 = slea.readMapleAsciiString();
		    break;
		}
		case 0x0E: //?
		{
		    byte b = slea.readByte();
		    break;
		}
		case 0x10: //h
		{
		    c.getPlayer().setHidden(slea.readByte() == 1);
		    break;
		}
                case 0x12: //send
                {
                   String name = slea.readMapleAsciiString();
                   int mapId = slea.readInt();
                   MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                   if (victim != null) {
                       MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapId);
                       victim.changeMap(target, target.getPortal(0));
                   } else {
                        c.getSession().write(MaplePacketCreator.sendGMInvalidCharacterName());
                   }

                    break;
                }
		case 0x13: //?
		{
		    String s = slea.readMapleAsciiString();
		    break;
		}
		case 0x16: //?
		{
		    //admin weather
		    //?
		    break;
		}
		case 0x17: //summon
		{
		    int monsterId = slea.readInt();
		    int amount = slea.readInt();
		    MapleMonster mob = MapleLifeFactory.getMonster(monsterId);
		    for (int i = 0; i < amount && i < 100; i++) {
			  c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
		    }
		}
		  case 0x1C: //snow (time: min=30 max=300)
		{
		    int time = slea.readInt();
			c.getPlayer().getMap().startMapEffect("", 5120000, time * 1000);
		    break;
		}
		case 0x1D: //w
		{
		    String name = slea.readMapleAsciiString();
		    String msg = slea.readMapleAsciiString();
		    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
		    victim.getClient().getSession().write(MaplePacketCreator.serverNotice(1, msg));
		    break;
		}
		case 0x1E: //log
		{
		    String name = slea.readMapleAsciiString();
		    byte enabled = slea.readByte();
		    break;
		}
		case 0x21: //dshop
		{
		    String s = slea.readMapleAsciiString();
		    break;
		}
		case 0x22: //setobjstate
		{
		    String name = slea.readMapleAsciiString();
		    int state = slea.readInt();
		    break;
		}
		default:
		{
		    System.out.println("Unhandled GM Command: " + slea.toString());
		    break;
		}
	  }
    }
}
