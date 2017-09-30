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
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.GameConstants.PlayerInteractionType;
import net.sf.odinms.server.MapleInteractionType;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleMiniGame;
import net.sf.odinms.server.MaplePlayerShop;
import net.sf.odinms.server.MaplePlayerShopItem;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.MapleMiniGame.MapleMiniGamePieceType;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class PlayerInteractionHandler extends AbstractMaplePacketHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayerInteractionHandler.class);	
	
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte actionByte = slea.readByte();
		PlayerInteractionType action = PlayerInteractionType.getById(actionByte);
		switch(action) {
		    case CREATE:
		    {
			MapleInteractionType createType = MapleInteractionType.getById(slea.readByte());
			switch(createType) {
			    //6E 00 - 00 -  02| 03 00(72 61 7A) 01 03 00 72 61 7A 00
			    case OMOK_GAME:
			    case MATCH_CARD_GAME:
			    {
				String description = slea.readMapleAsciiString();
				String password = null;
				boolean hasPassword = slea.readByte() > 0;
				if (hasPassword) {
				    password = slea.readMapleAsciiString();
				}
				int piece = slea.readByte();
				MapleMiniGame game = new MapleMiniGame(c.getPlayer(), description, password);
				MapleMiniGamePieceType pieceType = MapleMiniGamePieceType.getById(piece, createType);
				switch(pieceType) {
				    case MATCH_CARD_4x3:
					game.setMatchesToWin(6);
					break;
				    case MATCH_CARD_5x4:
					game.setMatchesToWin(10);
					break;
				    case MATCH_CARD_6x5:
					game.setMatchesToWin(15);
					break;
				}
				game.setGameType(createType);
				game.setPieceType(pieceType);
				c.getPlayer().setMiniGame(game);
				//c.getPlayer().getMap().addMapObject(game);
				//c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.addMiniGameBox(c.getPlayer()));
				game.openGame(c);
				break;
			    }
			    case TRADE:
			    {
				MapleTrade.startTrade(c.getPlayer());
				break;
			    }
			    case PLAYER_SHOP:
			    {
				String desc = slea.readMapleAsciiString();
				@SuppressWarnings("unused")
				byte uk1 = slea.readByte(); // maybe public/private 00
				@SuppressWarnings("unused")
				short uk2 = slea.readShort(); // 01 00
				@SuppressWarnings("unused")
				int uk3 = slea.readInt(); // 20 6E 4E
				MaplePlayerShop shop = new MaplePlayerShop(c.getPlayer(), desc);
				c.getPlayer().setPlayerShop(shop);
				c.getPlayer().getMap().addMapObject(shop);
				shop.sendShop(c);
				break;
			    }
			}
			break;
		    }
		    case INVITE:
		    {
			int otherPlayer = slea.readInt();
			MapleCharacter otherChar = c.getPlayer().getMap().getCharacterById(otherPlayer);
			MapleTrade.inviteTrade(c.getPlayer(), otherChar);
			break;
		    }
		    case DECLINE:
		    {
			MapleTrade.declineTrade(c.getPlayer());
			break;
		    }
		    case VISIT:
		    {
			// we will ignore the trade oids for now
			if (c.getPlayer().getTrade() != null && c.getPlayer().getTrade().getPartner() != null) {
				MapleTrade.visitTrade(c.getPlayer(), c.getPlayer().getTrade().getPartner().getChr());
			} else {
				int oid = slea.readInt();
				MapleMapObject ob = c.getPlayer().getMap().getMapObject(oid);
				if (ob instanceof MaplePlayerShop) {
					MaplePlayerShop shop = (MaplePlayerShop) ob;
					if (shop.hasFreeSlot() && !shop.isVisitor(c.getPlayer())) {
						shop.addVisitor(c.getPlayer());
						c.getPlayer().setPlayerShop(shop);
						shop.sendShop(c);
					}
				}
			}
			break;
		    }
		    case CHAT:
		    {
			MapleTrade trade = c.getPlayer().getTrade();
			MaplePlayerShop shop = c.getPlayer().getPlayerShop();
			MapleMiniGame game = c.getPlayer().getMiniGame();
			if (trade != null) {
			    trade.chat(slea.readMapleAsciiString());
			} else if (shop != null) {
			    shop.chat(c, slea.readMapleAsciiString());
			} else if (game != null) {
			    game.chat(c.getPlayer(), slea.readMapleAsciiString());
			}
			break;
		    }
		    case EXIT:
		    {
			MaplePlayerShop shop = c.getPlayer().getPlayerShop();
			MapleMiniGame game = c.getPlayer().getMiniGame();
			if (c.getPlayer().getTrade() != null) {
				MapleTrade.cancelTrade(c.getPlayer());
			} else if (game != null) {
			    c.getPlayer().setMiniGame(null);
			    //REMOVE PACKET
			} else if (shop != null) {
			/*MaplePlayerShop shop = c.getPlayer().getPlayerShop();
				if (shop != null) {
					c.getPlayer().setPlayerShop(null);
					if (shop.isOwner(c.getPlayer())) {
						c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.updateCharBox(c.getPlayer()));
						// return the items not sold
						for (MaplePlayerShopItem item : shop.getItems()) {
							IItem iItem = item.getItem().copy();
							iItem.setQuantity((short) (item.getBundles() * iItem.getQuantity()));
							StringBuilder logInfo = new StringBuilder("Returning items not sold in ");
							logInfo.append(c.getPlayer().getName());
							logInfo.append("'s shop");
							MapleInventoryManipulator.addFromDrop(c, iItem, logInfo.toString());
						}
					} else {
						shop.removeVisitor(c.getPlayer());
					}
				}*/
			}
			break;
		    }
		    case OPEN:
		    {
			MaplePlayerShop shop = c.getPlayer().getPlayerShop();
			if (shop != null && shop.isOwner(c.getPlayer())) {
				@SuppressWarnings("unused")
				byte uk1 = slea.readByte(); // 01
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.updateCharBox(c.getPlayer()));
			}
			break;
		    }
		    case SET_MESO:
		    {
			c.getPlayer().getTrade().setMeso(slea.readInt());
			break;
		    }
		    case SET_ITEMS:
		    {
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
			MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
			IItem item = c.getPlayer().getInventory(ivType).getItem((byte) slea.readShort());
			short quantity = slea.readShort();
			byte targetSlot = slea.readByte();
			if (c.getPlayer().getTrade() != null) {
				if ((quantity <= item.getQuantity() && quantity >= 0) || ii.isRechargable(item.getItemId())) {
				    if (!c.getChannelServer().allowUndroppablesDrop() && ii.isDropRestricted(item.getItemId())) {//BLOCK TRADE ITEMS HERE
						c.getSession().write(MaplePacketCreator.enableActions());
						return;
					}
					IItem tradeItem = item.copy();
					if (ii.isRechargable(item.getItemId())) {
						tradeItem.setQuantity(item.getQuantity());
						MapleInventoryManipulator.removeFromSlot(c, ivType, item.getPosition(), item.getQuantity(), true);
					} else {
						tradeItem.setQuantity(quantity);
						MapleInventoryManipulator.removeFromSlot(c, ivType, item.getPosition(), quantity, true);
					}
					tradeItem.setPosition(targetSlot);
					c.getPlayer().getTrade().addItem(tradeItem);
				} else if (quantity < 0) {
					log.info("[h4x] {} Trading negative amounts of an item", c.getPlayer().getName());
				}
			}
			break;
		    }
		    case CONFIRM:
		    {
			MapleTrade.completeTrade(c.getPlayer());
			break;
		    }
		    case ADD_ITEM:
		    {
			MaplePlayerShop shop = c.getPlayer().getPlayerShop();
			if (shop != null && shop.isOwner(c.getPlayer())) {
				MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
				byte slot = (byte) slea.readShort();
				short bundles = slea.readShort();
				short perBundle = slea.readShort();
				int price = slea.readInt();
				IItem ivItem = c.getPlayer().getInventory(type).getItem(slot);
				if (ivItem != null && ivItem.getQuantity() >= bundles * perBundle) {
					IItem sellItem = ivItem.copy();
					sellItem.setQuantity(perBundle);
					MaplePlayerShopItem item = new MaplePlayerShopItem(
						shop, sellItem, bundles, price);
					shop.addItem(item);
					// can be put in addItem without faek o.o
					MapleInventoryManipulator.removeFromSlot(c, type, slot, 
						(short) (bundles * perBundle), true);
					c.getSession().write(MaplePacketCreator.getPlayerShopItemUpdate(shop));
				}
				
			}	
			break;
		    }
		    case REMOVE_ITEM:
		    {
			/*MaplePlayerShop shop = c.getPlayer().getPlayerShop();
			if (shop != null && shop.isOwner(c.getPlayer())) {
				int slot = slea.readShort();
				MaplePlayerShopItem item = shop.getItems().get(slot);
				IItem ivItem = item.getItem().copy();
				shop.removeItem(slot);
				ivItem.setQuantity((short) (item.getBundles() * ivItem.getQuantity()));
				StringBuilder logInfo = new StringBuilder("Taken out from player shop by ");
				logInfo.append(c.getPlayer().getName());
				MapleInventoryManipulator.addFromDrop(c, ivItem, logInfo.toString());
				c.getSession().write(MaplePacketCreator.getPlayerShopItemUpdate(shop));
			}*/		
			break;
		    }
		    case READY:
		    case NOT_READY:
		    {
			c.getPlayer().getMiniGame().broadcast(MaplePacketCreator.getMiniGameStatus(action.getValue()));
			break;
		    }
			default:
				System.out.println("Unknown Mode: " + actionByte + " ::: Packet = " + slea.toString());
				break;
		}	
	}
}
