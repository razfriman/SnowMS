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
package net.sf.odinms.client.messages.commands;

import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;

public class CharCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
		MapleCharacter player = c.getPlayer();
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		if (splitted[0].equals("!lowhp")) {
			player.setHp(1);
			player.setMp(500);
			player.updateSingleStat(MapleStat.HP, 1);
			player.updateSingleStat(MapleStat.MP, 500);
		} else if (splitted[0].equals("!heal")) {
			player.setHp(player.getMaxHp());
			player.setMp(player.getMaxMp());
			player.updateSingleStat(MapleStat.MP, player.getMaxMp());
			player.updateSingleStat(MapleStat.HP, player.getMaxHp());

		} else if (splitted[0].equals("!skill")) {
			int skillid = Integer.parseInt(splitted[1]);
			int level = getOptionalIntArg(splitted, 2, 1);
			int masterlevel = getOptionalIntArg(splitted, 3, 1);
			ISkill skill = SkillFactory.getSkill(skillid);
			if (skill != null) {
				c.getPlayer().changeSkillLevel(skill, level, masterlevel);
			} else {
				mc.dropMessage("Error: Invalid skill");
			}
		} else if (splitted[0].equals("!sp")) {
			player.setRemainingSp(getOptionalIntArg(splitted, 1, 1));
			player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
		} else if (splitted[0].equals("!ap")) {
			player.setRemainingAp(getOptionalIntArg(splitted, 1, 1));
			player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
		} else if (splitted[0].equals("!job")) {
			c.getPlayer().changeJob(MapleJob.getById(Integer.parseInt(splitted[1])));
		} else if (splitted[0].equals("!whereami")) {
			new ServernoticeMapleClientMessageCallback(c).dropMessage("You are on map " +
					c.getPlayer().getMap().getId());
		} else if (splitted[0].equals("!shop")) {
			MapleShopFactory sfact = MapleShopFactory.getInstance();
			MapleShop shop = sfact.getShop(getOptionalIntArg(splitted, 1, 56));
			shop.sendShop(c);
		} else if (splitted[0].equals("!levelup")) {
			c.getPlayer().levelUp();
			int newexp = c.getPlayer().getExp();
			if (newexp < 0) {
				c.getPlayer().gainExp(-newexp, false, false);
			}

		} else if (splitted[0].equals("!level")) {
			c.getPlayer().setLevel(getOptionalIntArg(splitted, 1, 1));
			c.getPlayer().levelUp();
			int newexp = c.getPlayer().getExp();
			if (newexp < 0) {
				c.getPlayer().gainExp(-newexp, false, false);
			}
		} else if (splitted[0].equals("!item")) {
			int itemid = Integer.parseInt(splitted[1]);
			if (ii.getItemData(itemid) != null) {
				short quantity = (short) getOptionalIntArg(splitted, 2, 1);
				MapleInventoryManipulator.addById(c, itemid, quantity, c.getPlayer().getName() + "used !item with quantity " + quantity, player.getName());
			}
		} else if (splitted[0].equals("!equip")) {
			Pair<String, Short> stats = null;
			if (splitted.length > 1) {
				int itemid = Integer.parseInt(splitted[1]);
				Equip equip = (Equip) ii.getEquipById(itemid);
				for (int i = 2; i < splitted.length; i += 2) {
					if (splitted[i] != null && splitted[i + 1] != null) {
						try {
							stats = new Pair<String, Short>(splitted[i], Short.parseShort(splitted[i + 1]));
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (stats.getLeft().equalsIgnoreCase("acc")) {
							equip.setAcc(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("avoid")) {
							equip.setAvoid(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("dex")) {
							equip.setDex(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("hands")) {
							equip.setHands(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("hp")) {
							equip.setHp(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("int")) {
							equip.setInt(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("jump")) {
							equip.setJump(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("level")) {
							short original = stats.getRight();
							byte newbyte = (byte) original;
							equip.setLevel(newbyte);
						} else if (stats.getLeft().equalsIgnoreCase("luk")) {
							equip.setLuk(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("matk")) {
							equip.setMatk(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("mdef")) {
							equip.setMdef(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("mp")) {
							equip.setMp(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("speed")) {
							equip.setSpeed(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("str")) {
							equip.setStr(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("slots")) {
							short original = stats.getRight();
							byte newbyte = (byte) original;
							equip.setUpgradeSlots(newbyte);
						} else if (stats.getLeft().equalsIgnoreCase("watk")) {
							equip.setWatk(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("wdef")) {
							equip.setWdef(stats.getRight());
						}
					} else {
						break;
					}
				}
				MapleInventoryManipulator.addFromDrop(c, equip, "used equip command with custom stats");
			} else {
				mc.dropMessage("!equipt <itemid> <stats>");
			}
		} else if (splitted[0].equals("!drop")) {
			int itemId = Integer.parseInt(splitted[1]);
			short quantity = (short) (short) getOptionalIntArg(splitted, 2, 1);
			IItem toDrop;
			if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
				toDrop = ii.getEquipById(itemId);
			} else {
				toDrop = new Item(itemId, (byte) 0, (short) quantity);
			}
			StringBuilder logMsg = new StringBuilder("Created by ");
			logMsg.append(c.getPlayer().getName());
			logMsg.append(" using !drop. Quantity: ");
			logMsg.append(quantity);
			toDrop.log(logMsg.toString(), false);
			toDrop.setOwner(player.getName());
			c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
		} else if (splitted[0].equals("!clearinv")) {
			if (splitted.length > 1) {
				c.getPlayer().clearInv(getOptionalIntArg(splitted, 1, 1), getOptionalIntArg(splitted, 2, 1));
			} else {
				mc.dropMessage("1=Equips | 2=Use | 3=Setup | 4=Etc | 5=Cash");
			}
		} else if (splitted[0].equals("!dropmeso")) {
			int amount = Integer.parseInt(splitted[1]);
			c.getPlayer().getMap().spawnMesoDrop(amount, amount, c.getPlayer().getPosition(), c.getPlayer(), c.getPlayer(), true);

		} else if (splitted[0].equals("!dropmeso-me")) {
			int amount = Integer.parseInt(splitted[1]);
			c.getPlayer().getMap().spawnMesoDrop(amount, amount, c.getPlayer().getPosition(), c.getPlayer(), c.getPlayer(), false);
		} else if (splitted[0].equals("!mesos")) {
			int mesos = Integer.parseInt(splitted[1]);
			c.getPlayer().gainMeso(mesos, true);
		} else if (splitted[0].equals("!recharge")) {
			for (IItem star : c.getPlayer().getInventory(MapleInventoryType.USE).list()) {
				if (ii.isRechargable(star.getItemId())) {
					star.setQuantity(ii.getSlotMax(star.getItemId()));
					c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, star));
				}
			}
		} else if (splitted[0].equals("!storage")) {
			c.getPlayer().getStorage().sendStorage(c, 9900000);
		} else if (splitted[0].equals("!maxall")) {
			player.setHp(30000);
			player.setMp(30000);
			player.setMaxHp(30000);
			player.setMaxMp(30000);
			player.setStr(30000);
			player.setDex(30000);
			player.setInt(30000);
			player.setLuk(30000);
			player.setLevel(199);
			player.updateSingleStat(MapleStat.MAXHP, 30000);
			player.updateSingleStat(MapleStat.MAXMP, 30000);
			player.updateSingleStat(MapleStat.HP, 30000);
			player.updateSingleStat(MapleStat.MP, 30000);
			player.updateSingleStat(MapleStat.STR, 30000);
			player.updateSingleStat(MapleStat.DEX, 30000);
			player.updateSingleStat(MapleStat.INT, 30000);
			player.updateSingleStat(MapleStat.LUK, 30000);
		} else if (splitted[0].equals("!setstat")) {
			MapleStat stat = null;
			if (splitted.length == 1 || splitted.length == 2) {
				mc.dropMessage("!setstat <STR | DEX | INT | LUK> <Value>");
			} else if (splitted[1].equalsIgnoreCase("str")) {
				stat = MapleStat.STR;
			} else if (splitted[1].equalsIgnoreCase("dex")) {
				stat = MapleStat.DEX;
			} else if (splitted[1].equalsIgnoreCase("int")) {
				stat = MapleStat.INT;
			} else if (splitted[1].equalsIgnoreCase("luk")) {
				stat = MapleStat.LUK;
			}
			if (stat == null) {
				mc.dropMessage("STR | DEX | INT | LUK");
			} else {
				int value = Integer.parseInt(splitted[2]);
				if (stat == MapleStat.STR) {
					c.getPlayer().setStr(value);
					c.getPlayer().updateSingleStat(stat, c.getPlayer().getStr());
				} else if (stat == MapleStat.DEX) {
					c.getPlayer().setDex(value);
					c.getPlayer().updateSingleStat(stat, c.getPlayer().getDex());
				} else if (stat == MapleStat.INT) {
					c.getPlayer().setInt(value);
					c.getPlayer().updateSingleStat(stat, c.getPlayer().getInt());
				} else if (stat == MapleStat.LUK) {
					c.getPlayer().setLuk(value);
					c.getPlayer().updateSingleStat(stat, c.getPlayer().getLuk());
				}
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("lowhp", "", "", 100),
					new CommandDefinition("heal", "", "", 100),
					new CommandDefinition("skill", "", "", 100),
					new CommandDefinition("sp", "", "", 100),
					new CommandDefinition("ap", "", "", 100),
					new CommandDefinition("job", "", "", 100),
					new CommandDefinition("whereami", "", "", 100),
					new CommandDefinition("shop", "", "", 100),
					new CommandDefinition("levelup", "", "", 100),
					new CommandDefinition("level", "level", "", 100),
					new CommandDefinition("item", "", "", 100),
					new CommandDefinition("equip", "", "", 100),
					new CommandDefinition("drop", "", "", 100),
					new CommandDefinition("recharge", "", "", 100),
					new CommandDefinition("clearinv", "inv | amomunt", "Clear your inv", 100),
					new CommandDefinition("dropmeso", "amount", "Drops mesos, free for all loot", 100),
					new CommandDefinition("dropmeso-me", "amount", "Drops mesos, can use ME on it", 100),
					new CommandDefinition("mesos", "amount", "Gain mesos", 100),
					new CommandDefinition("storage", "", "Open storage", 100),
					new CommandDefinition("maxall", "", "Max all stats", 100),
					new CommandDefinition("setstat", "stat | value", "Set stat's value", 100),};
	}
}
