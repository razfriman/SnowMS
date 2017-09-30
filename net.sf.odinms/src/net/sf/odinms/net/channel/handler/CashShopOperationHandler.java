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
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class CashShopOperationHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte command = slea.readByte();

        switch (command) {
            case 3: {
                //CUser__OnCashItemBuyRequest
                int currency = slea.readByte();
                int snCS = slea.readInt();
                CashItemInfo item = CashItemFactory.getItem(snCS);
                if (item.getPrice() > c.getPlayer().getCSPoints(currency)) {
                    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
                    return;
                }
                MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "Cash Item was purchased.");
                c.getSession().write(MaplePacketCreator.showBoughtCSItem(item.getId()));
                c.getPlayer().addCSPoints(currency, -item.getPrice());//Change Currency Types?
                c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
                c.getSession().write(MaplePacketCreator.enableCSUse0());
                c.getSession().write(MaplePacketCreator.enableCSUse1(c.getPlayer()));
                c.getSession().write(MaplePacketCreator.enableCSUse2());
                break;
            }
            case 5: {
                //Update WishList
                int[] wishList = new int[10];
                for (int i = 0; i < 10; i++) {
                    wishList[i] = slea.readInt();
                }
                c.getPlayer().setWishList(wishList);
                c.getSession().write(MaplePacketCreator.getWishList(c.getPlayer(), true));
                break;
            }
            case 8: {
                //CUser__OnCashItemBuyCharacterSlots
                slea.readByte();
                int currency = slea.readInt();
                int snId = slea.readInt();
                break;
            }
            case 27: {
                //CUser__OnCashItemCoupleRequest
                int birthday = slea.readInt();
                int currency = slea.readInt();
                int snId = slea.readInt();
                String partner = slea.readMapleAsciiString();
                String text = slea.readMapleAsciiString();
                break;
            }
            case 28: {
                //CUser::OnCashItemBuyPackageRequest
                slea.readByte();
                int currency = slea.readInt();
                int snId = slea.readInt();
                break;
            }
            case 31://Buy Quest Item
            {
                int snId = slea.readInt();
                CashItemInfo item = CashItemFactory.getItem(snId);
                if (!(c.getPlayer().getInventory(MapleItemInformationProvider.getInstance().getInventoryType(item.getId())).getNextFreeSlot() > -1)) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }

                if (c.getPlayer().getMeso() >= item.getPrice() && item.getOnSale() > 0 && item.getPrice() == 1) {
                    c.getPlayer().gainMeso(-item.getPrice(), false);
                    MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "Quest Item was purchased.", null);
                    MapleInventory etcInventory = c.getPlayer().getInventory(MapleInventoryType.ETC);
                    c.getSession().write(MaplePacketCreator.showBoughtCSQuestItem(etcInventory.findById(item.getId()).getPosition(), item.getId()));
                }
                break;
            }
            default:
                System.out.println("Unknown Command: " + command);
                System.out.println(slea.toString());
                break;
        }

    }
}
