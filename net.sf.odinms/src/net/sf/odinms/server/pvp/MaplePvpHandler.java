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

package net.sf.odinms.server.pvp;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.attack.AttackInfo;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleMonsterStats;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Raz
 */
public class MaplePvpHandler {

	//private final int pvpMobId = 9400713;//FINAL RELEASE
	private final int pvpMobId = 9400506;//TEST - visible
	private final int reviveTime = 5;//5 second respawn time
	private MaplePvpGameType gameType = null;
	private MapleMap gameMap;
	private MapleMapFactory pvpMapFactory;
	private List<MapleCharacter> players = new ArrayList<MapleCharacter>();
	private boolean open = true;
	
	private static List<MaplePvpHandler> openGames = new ArrayList<MaplePvpHandler>();

	private MaplePvpHandler(int mapid, MaplePvpGameType gameType) {
		this.pvpMapFactory = new MapleMapFactory(MapleDataProviderFactory.getWzFile("Map.wz"), MapleDataProviderFactory.getWzFile("String.wz"));
		this.gameType = gameType;
		this.gameMap = createMaps(mapid, gameType);
		openGames.add(this);
	}

	public static MaplePvpHandler createHandler(int mapid, MaplePvpGameType gameType) {
		return new MaplePvpHandler(mapid, gameType);
	}

	public static MaplePvpHandler findHandler(int mapid, MaplePvpGameType gameType, boolean createNew) {
		for(MaplePvpHandler openGame : openGames) {
			if (openGame != null && openGame.isOpen() && openGame.getGameMap().getId() == mapid && openGame.getGameType() == gameType) {
				return openGame;
			}
		}
		if (createNew) {
			createHandler(mapid, gameType);
		}
		return null;
	}

	public void addPlayer(MapleCharacter player) {
		players.add(player);
	}

	public void clearPlayers() {
		players.clear();
	}

	public void removePlayer(MapleCharacter player) {
		players.remove(player);
	}

	public List<MapleCharacter> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	public MapleMap getGameMap() {
		return gameMap;
	}

	public MaplePvpGameType getGameType() {
		return gameType;
	}

	public void setGameMap(MapleMap gameMap) {
		this.gameMap = gameMap;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}

	public void doDamage(MapleCharacter from, MapleMap map, AttackInfo attack) {
		for (MapleCharacter to : map.getCharacters()) {//ASDFASDFASDF
			if (to != from) {
				if (!from.isAlive() || !to.isAlive()) {
					break;
				}

				MapleMonster pvpMob = null;
				for (MapleMapObject mmo : map.getMapObjects()) {
					if (mmo.getType() == MapleMapObjectType.MONSTER) {
						MapleMonster mmoMonster = (MapleMonster) mmo;
						if (mmoMonster.getPvpOwner().getId() == to.getId()) {
							pvpMob = mmoMonster;
						}
					}
				}
				if (pvpMob == null) {
					pvpMob = MapleLifeFactory.getMonster(pvpMobId);
					pvpMob.setPvpOwner(to);
					MapleMonsterStats stats = pvpMob.getStats();
					stats.setExp(0);
					stats.setHp(to.getMaxHp());
					stats.setAutoAggro(false);
					stats.setBoss(false);
					pvpMob.setOverrideStats(stats);
					pvpMob.setControllerHasAggro(false);
					pvpMob.setControllerKnowsAboutAggro(false);
					pvpMob.setDropsEnabled(false);
					//pvpMob.setSummonEffect(15);//MAKE INVISIBLE
					map.spawnMonsterOnGroundBelow(pvpMob, pvpMob.getPosition());
				}
				pvpMob.setController(to);
				pvpMob.setPosition(to.getPosition());
				map.moveMonster(pvpMob, to.getPosition());
				pvpMob.addMoveid();
				if (pvpMob.getPvpOwner() != null) {
					map.broadcastMessage(MaplePacketCreator.moveMonsterResponse(pvpMob.getObjectId(), pvpMob.getMoveid(), pvpMob.getMp(), pvpMob.isControllerHasAggro()));
					//map.broadcastMessage(MaplePacketCreator.moveMonster(0, -1, pvpMob.getObjectId(), pvpMob.getPosition(), pvpMob.getPvpOwner().getLastRes()));
				}
				int damage = 0;
				for (Pair<Integer, List<Integer>> oned : attack.getAllDamage()) {
					MapleMonster monster = map.getMonsterByOid(oned.getLeft().intValue());
					if (monster != null) {
						int totDamageToOneMonster = 0;
						for (Integer eachd : oned.getRight()) {
							totDamageToOneMonster += eachd.intValue();
						}
						if (monster.equals(pvpMob)) {
							damage += totDamageToOneMonster;
						}
					}
				}
				damage /= from.getLevel();
				//damage = 1000;
				to.addHP(-damage);
				if (to.isAlive()) {//THE PLAYER IS STILL ALIVE
					//pvpMob.setHp(to.getHp());
				} else {//THE PLAYER DIED
					map.killMonster(pvpMob, to);
					//from.getPvp().addKill();
					//to.getPvp().addDeath();
					handlePoints();
				}

				//emulate the damage packets
				map.broadcastMessage(MaplePacketCreator.damageMonster(pvpMob.getObjectId(), damage));
				from.getClient().getSession().write(MaplePacketCreator.showBossHP(8800000, to.getHp(), to.getMaxHp(), (byte) 3, (byte) 1));
				from.getClient().getSession().write(MaplePacketCreator.showMonsterHP(pvpMob.getObjectId(), to.getMaxHp() / to.getHp()));
			}
		}
	}

	public MapleMapFactory getMapFactory() {
		return pvpMapFactory;
	}

	public MapleMap createMaps(int mapid, MaplePvpGameType gameType) {

		MapleMap map = pvpMapFactory.getMap(mapid, false, false, false);

		map.blockPortals(1);
		//LOAD SPECIAL REACTORS,ETC
		switch (gameType) {
			case ASSAULT:
				break;
			case CAPTURE_THE_FLAG:
				break;
			case FFA_DEATHMATCH:
				if (mapid == 100000000) {
					makeRefferee(map, new Point(2605, 334));
				}
				break;
			case KING_OF_THE_HILL:
				break;
			case ONE_VS_ONE:
				break;
			case TEAM_DEATHMATCH:
				break;
		}
		gameMap = map;
		return map;
	}

	public void cancelBuff(MapleCharacter chr, ISkill skill) {
		//ITEM EFFECTS - 5010029 5010030 5010028
		MapleStatEffect effect;
		effect = skill.getEffect(1);
		switch (skill.getId()) {
			//case 4111002:
			//break;
			default:
				chr.cancelEffect(effect, false, -1);
				break;
		}
	}

	public void usedSkill(MapleCharacter chr, ISkill skill) {
		//TODO
	}

	public void pickupItem(MapleCharacter chr, IItem item) {
		//TODO
	}

	public void dropItem(MapleCharacter chr, IItem item) {
		//TODO
	}

	public void movePlayer(MapleCharacter player) {
		//TODO
	}

	public void playerJoined(MapleCharacter player) {
		//player.getClient().getSession().write(MaplePacketCreator.musicChange("Bgm14/HonTale"));
		//player.getClient().getSession().write(MaplePacketCreator.serverMessage("SnowMS PVP - " + player.getPvp().getGameType().name()));
		//Interferes with health bar
	}

	public void usedItem(MapleCharacter player, IItem item) {
		//TODO
	}

	public void handlePoints() {
		//Collections.sort(players, new Comparator<MapleCharacter>() {
//
//			public int compare(MapleCharacter o1, MapleCharacter o2) {
//				if (o1.getPvp().getKills() < o2.getPvp().getKills()) {
//					return 1;
//				} else if (o1.getPvp().getKills() == o2.getPvp().getKills()) {
//					return 0;
//				} else {
//					return -1;
//				}
//			}
//		});
		gameMap.broadcastMessage(MaplePacketCreator.serverNotice(5, "--CURRENT SCORE--"));
		int i = 1;
		for (MapleCharacter player : players) {
			if (player.getClient().isLoggedIn()) {
				//gameMap.broadcastMessage(MaplePacketCreator.serverNotice(5, "(" + i + ") - " + player.getName() + " - " + player.getPvp().getKills()));
				i++;
			}
		}
	}

	public void makeRefferee(MapleMap map, Point pos) {
		map.spawnNpc(9000019, pos);
	}

	public void playerDied(final MapleCharacter player) {
		player.getClient().getSession().write(MaplePacketCreator.sendHint("You will be revived in " + reviveTime + " seconds"));
		player.getMap().broadcastMessage(player, MaplePacketCreator.getChatText(player.getId(), "I will revive in " + reviveTime + " seconds"), false);
		player.getClient().getSession().write(MaplePacketCreator.getClock(reviveTime));

		TimerManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				player.setHp(player.getMaxHp());
				MapleMap to = player.getMap();
				MaplePortal pto = to.getRandomPortal();
				player.setStance(0);
				player.changeMap(to, pto);
			}
		}, reviveTime * 1000);
	}

	public void playerRevived() {
		//TODO
		//?or handle in playerDied also?
	}

	public void playerAttacked() {
		//TODO
	}

	public void playerAttacking() {
		//TODO
	}
}
