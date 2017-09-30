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

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleItemMask;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.GameConstants;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.maps.MapleTV;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class UseCashItemHandler extends AbstractMaplePacketHandler {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UseCashItemHandler.class);

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte mode = slea.readByte();
		slea.readByte();//TICK?
		int itemId = slea.readInt();
		int itemType = itemId / 10000;
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		IItem item = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(itemId);
		if (item.getQuantity() < 1) {
			c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.WZ_EDIT);
		}
		boolean used = true;
		try {

			switch (itemType) {
				case 504://TELEPORT ROCK
				{
					if (!UseTeleportRockHandler.handleTeleportRock(slea, c, itemId == GameConstants.Items.VIP_TELEPORT_ROCK.getValue() ? 1 : 0)) {
                        used = false;
                    }
					break;
				}
				case 505://AP/SP RESETS
				{
                    if (itemId == GameConstants.Items.AP_RESET.getValue()) {
                        int toStat = slea.readInt();
                        int fromStat = slea.readInt();
                        if (c.getPlayer().getStat(MapleStat.getByValue(fromStat)) < 1 || c.getPlayer().getStat(MapleStat.getByValue(toStat)) >= c.getChannelServer().getMaxAP()) {
                            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
                            return;
                        }
                        c.getPlayer().addStat(MapleStat.getByValue(toStat), 1, true);
                        c.getPlayer().addStat(MapleStat.getByValue(fromStat), -1, true);
                    } else {
                        int skillIdTo = slea.readInt();
                        int skillIdFrom = slea.readInt();
                        ISkill skillTo = SkillFactory.getSkill(skillIdTo);
                        ISkill skillFrom = SkillFactory.getSkill(skillIdFrom);
                        if (c.getPlayer().getSkillLevel(skillFrom) < 1 || c.getPlayer().getSkillLevel(skillTo) > skillTo.getMaxLevel()) {
                            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.PACKET_EDIT);
                            return;
                        }
                        c.getPlayer().addSkillLevel(skillTo, 1);
                        c.getPlayer().addSkillLevel(skillFrom, -1);
                    }
					break;
				}
				case 506://ITEM NAMETAG/ITEM GUADE/INCUBATOR
				{
                    if (itemId == 5060000) {//name tag

                    } else if (itemId == 5060001) {//seal
                    MapleInventoryType lockInvType = MapleInventoryType.getByType((byte) slea.readInt());
                    IItem lockItem = c.getPlayer().getInventory(lockInvType).getItem((byte) slea.readInt());
                    // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                    if (lockItem == null) {
                        return;
                    }
                    lockItem.addMask(MapleItemMask.LOCKED);
                    //c.getSession().write(MaplePacketCreator.lockedItem(type, item));
                    } else if (itemId == 5060002) {//incubator
                        
                    }
					break;
				}
				case 507://MEGAPHONE
				{
					int megaType = itemId / 1000 % 10;
					if (megaType == 2) {//NORMAL MEGA
						c.getChannelServer().getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(3, c.getChannel(), c.getPlayer().getName() +
								" : " + slea.readMapleAsciiString(), slea.readByte() != 0).getBytes());
					} else if (megaType == 5) {//TV SMEGA
						int tvType = itemId % 10;
						boolean megaMessenger = false;
						boolean ear = false;
						MapleCharacter victim = null;

						if (tvType != 1) { // 1 is the odd one out since it doesnt allow 2 players.
							if (tvType >= 3) {
								megaMessenger = true;
								if (tvType == 3) {
									slea.readByte();//has Receiver
								}
								ear = slea.readByte() == 1;
							} else if (tvType != 2) {
								slea.readByte();
							}
							if (tvType != 4) {
								victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
							}
						}
						List<String> messages = new LinkedList<String>();
						StringBuilder builder = new StringBuilder();
						for (int i = 0; i < 5; i++) {
							String message = slea.readMapleAsciiString();
							if (megaMessenger) {
								builder.append(message);
								builder.append(" ");
							}
							messages.add(message);
						}
						if (megaMessenger) {
							builder.deleteCharAt(builder.length() - 1);
						}
						int ticks = slea.readInt();
						if (megaMessenger) {
							try {
							c.getChannelServer().getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(3, c.getChannel(), c.getPlayer().getName() + " : " + builder.toString(), ear).getBytes());
							} catch (Exception e) {
								e.printStackTrace();
								c.getChannelServer().reconnectWorld();
						}
						MapleTV.getInstance().addMessage(c.getPlayer(), victim, tvType, messages, ticks);
						} else {
							c.getPlayer().dropMessage("Please wait until the next MapleTV is finished broadcasting to use this item");
							used = false;
						}
					}
					break;
				}
				case 509://NOTES
				{
					String sendTo = slea.readMapleAsciiString();
					String msg = slea.readMapleAsciiString();
					/*try {
					//c.getPlayer().sendNote(sendTo, msg);
					} catch (SQLException e) {
					log.error("SAVING NOTE", e);
					}*/
					break;
				}
                case 510://JUKEBOX
                {
                    String[] path = MapleDataTool.getString(ii.getItemData(itemId), "info/path").split("/");
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectEnvironment(StringUtil.joinStringFrom(path, 1, "/"), 6));
                    break;
                }
				case 512://MAP EFFECTS
				{
					c.getPlayer().getMap().startMapEffect(ii.getMsg(itemId).replaceFirst("%s", c.getPlayer().getName()).replaceFirst("%s", slea.readMapleAsciiString()), itemId);
					break;
				}
				case 517://PET NAME TAG
				{
					MaplePet pet = c.getPlayer().getPet(0);
					if (pet == null) {
						c.getSession().write(MaplePacketCreator.enableActions());
						return;
					}
					String newName = slea.readMapleAsciiString();
					pet.setName(newName);
					pet.update();
					c.getSession().write(MaplePacketCreator.enableActions());
					c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.changePetName(c.getPlayer(), newName, 1), true);
					break;
				}
				case 520://MESO SACKS
				{
					if (Randomizer.randomBoolean()) {
						int meso = ii.getGiveMesos(itemId);
						c.getPlayer().gainMeso(meso, true);
						c.getSession().write(MaplePacketCreator.luckSackPass(meso));
					} else {
						c.getSession().write(MaplePacketCreator.luckSackFail());
					}
					break;
				}
				case 524://CASH PET FOOD
				{
					MaplePet pet = c.getPlayer().getPet(0);
					if (item == null || pet == null) {
						c.getSession().write(MaplePacketCreator.enableActions());
						return;
					}
					if (!pet.canConsume(itemId)) {
						pet = c.getPlayer().getPet(1);
						if (pet != null) {
							if (!pet.canConsume(itemId)) {
								pet = c.getPlayer().getPet(2);
								if (pet != null) {
									if (!pet.canConsume(itemId)) {
										c.getSession().write(MaplePacketCreator.enableActions());
										return;
									}
								} else {
									c.getSession().write(MaplePacketCreator.enableActions());
									return;
								}
							}
						} else {
							c.getSession().write(MaplePacketCreator.enableActions());
							return;
						}
					}
					pet.gainFullness(100);//all items have a 'inc' flag, get from that mabey?
					pet.gainCloseness(100 * c.getChannelServer().getPetExpRate());
					c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.commandResponse(c.getPlayer().getId(), (byte) 1, 0, true, true), true);
					break;
				}
				case 528://EFFECTS (PASSING GAS, ETC)
				{
					/*MapleStatEffect mse = ii.getItemEffect(itemId);
					mse.setSourceid(2111003);
					Rectangle rec = mse.calculateBoundingBox(c.getPlayer().getPosition(), c.getPlayer().isFacingLeft());
					MapleMist mist = new MapleMist(rec, c.getPlayer(), mse);
					c.getPlayer().getMap().spawnMist(mist, mse.getDuration(), false);*/
					break;
				}
				case 530://MORPHS
				{
					ii.getItemEffect(itemId).applyTo(c.getPlayer());
					break;
				}
				case 537://CHALK-BOARDS
				{
					String text = slea.readMapleAsciiString();
					slea.readInt();//?
					c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.useChalkBoard(c.getPlayer().getId(), text));
					break;
				}
				case 539://AVATAR SMEGA
				{
					List<String> lines = new LinkedList<String>();
					for (int i = 0; i < 4; i++) {
						lines.add(slea.readMapleAsciiString());
					}
					c.getChannelServer().getWorldInterface().broadcastMessage(null, MaplePacketCreator.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, lines).getBytes());
					break;
				}
				case 545://PORTABLE GENERAL STORE
				{
					MapleShopFactory sfact = MapleShopFactory.getInstance();
					MapleShop shop = sfact.getShopForNPC(MapleDataTool.getInt("info/npc", ii.getItemData(itemId)));
					shop.sendShop(c);
					break;
				}
				default://UNKNOWN CASH ITEM USAGE
				{
					used = false;
					System.out.println("Unhandled Cash Item Usages - " + itemId + " --- " + slea.toString());
					break;
				}
			}
			if (used) {
				MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
			} else {
				c.getSession().write(MaplePacketCreator.enableActions());
			}
		} catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
			log.error("THROW REMOTE EXCEPTION:", e);
		}
	}
}
