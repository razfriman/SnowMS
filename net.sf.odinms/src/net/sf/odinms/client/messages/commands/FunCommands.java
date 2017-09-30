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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MonsterSkill;
import net.sf.odinms.server.life.MonsterSkillFactory;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

public class FunCommands implements Command {

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
			IllegalCommandSyntaxException {
		ChannelServer cserv = c.getChannelServer();
		MapleCharacter player = c.getPlayer();
		if (splitted[0].equals("!letter")) {
			Point dropPoint = c.getPlayer().getPosition();
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
			int itemId = 0;
			IItem toDrop;
			String words = StringUtil.joinStringFrom(splitted, 1);

			for (int i = 0; i < words.length(); i++) {
				String letter = words.substring(i, (i + 1));
				letter.toLowerCase();
				int letterA = 97;
				int letterZ = 122;
				int letterVal = Integer.valueOf(letter);
				if (letterVal >= letterA && letterVal <= letterZ) {
					itemId = 3991000 + (letterVal - letterA);
				} else if (letter.equals("0")) {
					itemId = 3990019;
				} else if (letter.equals("1")) {
					itemId = 3990010;
				} else if (letter.equals("2")) {
					itemId = 3990011;
				} else if (letter.equals("3")) {
					itemId = 3990012;
				} else if (letter.equals("4")) {
					itemId = 3990013;
				} else if (letter.equals("5")) {
					itemId = 3990014;
				} else if (letter.equals("6")) {
					itemId = 3990015;
				} else if (letter.equals("7")) {
					itemId = 3990016;
				} else if (letter.equals("8")) {
					itemId = 3990017;
				} else if (letter.equals("9")) {
					itemId = 3990018;
				} else if (letter.equals("*")) {
					itemId = 3992025;
				} else if (letter.equals("@")) {
					itemId = 3992024;
				} else if (letter.equals("+")) {
					itemId = 3990022;
				} else if (letter.equals("-")) {
					itemId = 3990023;
				} else if (letter.equals(".")) {
					itemId = 3992010;
				} else if (letter.equals(",")) {
					itemId = 3992014;
				} else if (letter.equals("?")) {
					itemId = 3992023;
				} else if (letter.equals(" ")) {
					itemId = 4001074;
				} else {
					itemId = 4001074;
				}

				toDrop = ii.getEquipById(itemId);
				c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, dropPoint, false, false);
				dropPoint.x += 30;
			}
		} else if (splitted[0].equals("!speakas")) {
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
			String message = StringUtil.joinStringFrom(splitted, 2);
			if (victim == null) {
				mc.dropMessage("unable to find '" + splitted[1] + "'");
			} else {
				victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), message));
			}
		} else if (splitted[0].equals("!kill")) {
			String who = "";
			if (splitted.length > 1) {
				who = splitted[1];
			}
			if (who.equalsIgnoreCase("players")) {
				for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
					if (!chr.isGM()) {
						chr.setHp(0);
						chr.updateSingleStat(MapleStat.HP, 0);
					}
				}
			} else if (who.equalsIgnoreCase("me")) {
				player.setHp(0);
				player.updateSingleStat(MapleStat.HP, 0);
			} else if (who.equalsIgnoreCase("gm") || (who.equalsIgnoreCase("gms"))) {
				for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
					if (chr.isGM()) {
						chr.setHp(0);
						chr.updateSingleStat(MapleStat.HP, 0);
					}
				}
			} else if (who.equalsIgnoreCase("all")) {
				for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
					chr.setHp(0);
					chr.updateSingleStat(MapleStat.HP, 0);
				}
			} else if (who.equalsIgnoreCase("player")) {
				MapleCharacter chr = cserv.getPlayerStorage().getCharacterByName(splitted[2]);
				if (chr != null) {
					chr.setHp(0);
					chr.updateSingleStat(MapleStat.HP, 0);
				}
			} else {
				mc.dropMessage("Invalid parameters: players | me | gm | all | player <name>");
			}
		} else if (splitted[0].equals("!note")) {
			MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
			String message = StringUtil.joinStringFrom(splitted, 2);
			if (victim != null) {
				victim.getClient().getSession().write(MaplePacketCreator.noteSend(c.getPlayer(), message, System.currentTimeMillis()));
			}
		} else if (splitted[0].equals("!hint")) {
			String hint = StringUtil.joinStringFrom(splitted, 1);
			c.getSession().write(MaplePacketCreator.sendHint(hint));
		} else if (splitted[0].equals("!hintmap")) {
			String hint = StringUtil.joinStringFrom(splitted, 1);
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.sendHint(hint));
		} else if (splitted[0].equals("!instanceme")) {
			int onoff = 1;

			if (splitted.length > 1) {
				onoff = Integer.parseInt(splitted[1]);
			}

			MapleMap map;
			MapleMapFactory mapFactory = new MapleMapFactory(MapleDataProviderFactory.getWzFile("Map.wz"), MapleDataProviderFactory.getWzFile("String.wz"));

			if (onoff > 0) {
				map = mapFactory.getMap(c.getPlayer().getMap().getId());
			} else {
				map = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getMap().getId());
			}

			c.getPlayer().changeMap(map, map.getPortal(0));
		} else if (splitted[0].equals("!slap")) {
			String obj = null;
			MapleCharacter chr = null;
			if (splitted.length == 1) {
				mc.dropMessage("Please enter a player name");
			} else {
				if (splitted.length == 2) {
					chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
					obj = "large trout";
				} else if (splitted.length > 2) {
					chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
					obj = StringUtil.joinStringFrom(splitted, 2);
				}
				if (chr != null && obj != null) {
					c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, c.getPlayer().getName() + " slaps " + chr.getName() + " around a bit with a " + obj));
				}
			}

		} else if (splitted[0].equals("!itembuff")) {
			if (splitted.length < 2) {
				mc.dropMessage("Please enter a itemid to buff with");
			} else {
				int itemid = Integer.parseInt(splitted[1]);
				MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
				MapleStatEffect buff = null;
				try {
					buff = ii.getItemEffect(itemid);
				} catch (Exception e) {
					buff = MapleStatEffect.getEmptyStatEffect();
					buff.setSourceid(itemid);
					buff.setStatType(MapleStatEffect.MapleStatEffectType.ITEM_BUFF);
					buff.setOverTime(true);
					buff.setMoveTo(-1);
				}
				Pair<String, Short> stats = null;
				buff.setIsEmptyStats(true);
				for (int i = 2; i < splitted.length; i += 2) {
					if (splitted[i] != null && splitted[i + 1] != null) {
						try {
							stats = new Pair<String, Short>(splitted[i], Short.parseShort(splitted[i + 1]));
						} catch (Exception e) {
							//ERROR
						}
						if (stats.getLeft().equalsIgnoreCase("acc")) {
							buff.setAcc(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("duration")) {
							buff.setDuration(stats.getRight() * 1000);
						} else if (stats.getLeft().equalsIgnoreCase("avoid")) {
							buff.setAvoid(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("hands")) {
							buff.setHands(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("jump")) {
							buff.setJump(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("matk")) {
							buff.setMatk(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("mdef")) {
							buff.setMdef(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("speed")) {
							buff.setSpeed(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("watk")) {
							buff.setWatk(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("wdef")) {
							buff.setWdef(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("cooldown")) {
							buff.setCooldown(stats.getRight());
						} else if (stats.getLeft().equalsIgnoreCase("buffstat")) {
							List<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();
							statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, 1));
							buff.setStatsups(statups);
						}
					}
				}
				buff.applyTo(c.getPlayer());
			}
		} else if (splitted[0].equals("!pnpcs")) {
			c.getSession().write(MaplePacketCreator.getPacketFromHexString("4E 00 01 C9 13 97 00 06 00 4B 6F 72 77 79 6E 00 00 86 4E 00 00 00 A4 75 00 00 01 CA 4A 0F 00 02 29 71 0F 00 04 58 BF 0F 00 05 50 E6 0F 00 06 99 34 10 00 07 B4 53 10 00 08 3B 83 10 00 09 D7 D0 10 00 0B D0 00 16 00 FF FF 9D F8 19 00 00 00 00 00 00 00 00 00 00 00 00 00"));
			c.getSession().write(MaplePacketCreator.getPacketFromHexString("4E 00 01 CA 13 97 00 07 00 4E 6F 76 69 6E 68 61 01 00 9B 53 00 00 00 9F 7B 00 00 01 39 46 0F 00 02 40 71 0F 00 04 73 BF 0F 00 05 EE E2 0F 00 06 C9 30 10 00 07 19 5C 10 00 08 1C 83 10 00 09 B1 D0 10 00 0A A8 A9 10 00 0B 51 53 14 00 FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
			c.getSession().write(MaplePacketCreator.getPacketFromHexString("4E 00 01 CC 13 97 00 05 00 78 55 41 45 78 00 00 E8 50 00 00 00 D8 77 00 00 01 CA 4A 0F 00 02 57 71 0F 00 04 5A BF 0F 00 05 D1 E6 0F 00 06 98 34 10 00 07 6A 5C 10 00 08 77 83 10 00 09 D4 D0 10 00 0B DE D9 15 00 FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));

		} else if (splitted[0].equals("!leetchat")) {
			String chat = StringUtil.joinStringFrom(splitted, 1);
			String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String leet = "48(d3f9h1jk1mn0PQR57uvwxyz@6cD3F9hiJk|Mn0pqr$+uvWXy2";
			for (int i = 0; i < chat.length(); i++) {
				for (int j = 0; j < 52; j++) {
					if (chat.charAt(i) == normal.charAt(j)) {
						chat = chat.replace(chat.charAt(i), leet.charAt(j));
					}
				}
			}
			if (chat.length() > 0) {
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), chat));
			}

		} else if (splitted[0].equals("!vac")) {
			for (MapleMapObject mmo : c.getPlayer().getMap().getMapObjects()) {
				if (mmo.getType() == MapleMapObjectType.MONSTER) {
					MapleMonster monster = (MapleMonster) mmo;
					//c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.moveMonster(0, -1, monster.getObjectId(), monster.getPosition(), c.getPlayer().getLastRes()));
					monster.setPosition(c.getPlayer().getPosition());
				}
			}
		} else if (splitted[0].equals("!debuff")) {
			if (splitted.length < 2) {
				mc.dropMessage("!debuff <SkillID> <SkillLevel> <Target>");
			} else {
				MapleCharacter victim = null;
				int skillid = -1;
				int level = -1;
				if (splitted.length > 2) {
					victim = c.getPlayer();
					skillid = Integer.parseInt(splitted[1]);
					level = Integer.parseInt(splitted[2]);
				}
				if (splitted.length > 3) {
					victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[3]);
				}
				if (skillid != -1 && level != -1 && victim != null) {
					MonsterSkill skill = MonsterSkillFactory.getMonsterSkill(skillid, level);
					victim.giveDebuffs(skill);
				}
			}
		}
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[]{
					new CommandDefinition("letter", "sentence to write", "Spells out a sentence you type in as items)", 100),
					new CommandDefinition("speakas", "", "", 1000),
					new CommandDefinition("kill", "group", "Kills a group of people", 1000),
					new CommandDefinition("note", "player - message", "Send a note to a players", 100),
					new CommandDefinition("hint", "message", "hint message box", 100),
					new CommandDefinition("hintmap", "message", "hint message box", 100),
					new CommandDefinition("instanceme", "1 or 0", "instaces you into your map", 100),
					new CommandDefinition("slap", "character name - object", "slaps a character with tuna, unless specified", 100),
					new CommandDefinition("itembuff", "itemid", "gives you a item buff", 100),
					new CommandDefinition("pnpcs", "", "spawns player npcs", 100),
					new CommandDefinition("leetchat", "text", "leetifies your text", 100),
					new CommandDefinition("vac", "", "vacs monsters to you", 100),
					new CommandDefinition("debuff", "skillid | level | victim", "debuffs you with the skill and level", 100),};
	}
}
