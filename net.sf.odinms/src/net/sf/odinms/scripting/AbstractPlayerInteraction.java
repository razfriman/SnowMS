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
package net.sf.odinms.scripting;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleQuestRecord;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.MapleReward;
import net.sf.odinms.client.MapleSkinColor;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.scripting.event.EventManager;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.pq.MaplePQRewards;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.Randomizer;

public class AbstractPlayerInteraction {

	private MapleClient c;
	private ChannelServer cserv;

	public AbstractPlayerInteraction(MapleClient c) {
		this.c = c;
		this.cserv = c.getChannelServer();
	}

	protected MapleClient getClient() {
		return c;
	}

	public MapleCharacter getPlayer() {
		return c.getPlayer();
	}

	public ChannelServer getCServ() {
		return this.cserv;
	}

	public void warp(int map) {
		MapleMap target = getWarpMap(map);
		c.getPlayer().changeMap(target, target.getPortal(0));
	}

	public void warpMembers(int mapId, List<MapleCharacter> members) {
		MapleMap target = getWarpMap(mapId);
		for (MapleCharacter chr : members) {
			chr.changeMap(target, target.getPortal(0));
		}
	}

	public void warpMembers(int mapId, int portal, List<MapleCharacter> members) {
		MapleMap target = getWarpMap(mapId);
		for (MapleCharacter chr : members) {
			chr.changeMap(target, target.getPortal(portal));
		}
	}

	public void warpMembers(int mapId, String portal, List<MapleCharacter> members) {
		MapleMap target = getWarpMap(mapId);
		for (MapleCharacter chr : members) {
			chr.changeMap(target, target.getPortal(portal));
		}
	}

	public void warpMembers(int mapId, MaplePortal portal, List<MapleCharacter> members) {
		MapleMap target = getWarpMap(mapId);
		for (MapleCharacter chr : members) {
			chr.changeMap(target, portal);
		}
	}

	public void warpMembers(MapleMap target, List<MapleCharacter> members) {
		for (MapleCharacter chr : members) {
			chr.changeMap(target, target.getPortal(0));
		}
	}

	public void warpMembers(MapleMap target, int portal, List<MapleCharacter> members) {
		for (MapleCharacter chr : members) {
			chr.changeMap(target, target.getPortal(portal));
		}
	}

	public void warpMembers(MapleMap target, String portal, List<MapleCharacter> members) {
		for (MapleCharacter chr : members) {
			chr.changeMap(target, target.getPortal(portal));
		}
	}

	public void warpMembers(MapleMap target, MaplePortal portal, List<MapleCharacter> members) {
		for (MapleCharacter chr : members) {
			chr.changeMap(target, portal);
		}
	}

	public void warp(int map, int portal) {
		MapleMap target = getWarpMap(map);
		c.getPlayer().changeMap(target, target.getPortal(portal));
	}

	public void warp(int map, String portal) {
		MapleMap target = getWarpMap(map);
		c.getPlayer().changeMap(target, target.getPortal(portal));
	}

	public void warp(MapleMap map, MaplePortal portal) {
		c.getPlayer().changeMap(map, portal);
	}

	public void warp(MapleMap map) {
		c.getPlayer().changeMap(map, map.getPortal(0));
	}

	public void warpRandom(int mapid) {
		MapleMap map = getWarpMap(mapid);
		MaplePortal portal = map.getRandomPortal();
		c.getPlayer().changeMap(map, portal);
	}

	private MapleMap getWarpMap(int map) {
		MapleMap target;
		if (getPlayer().getEventInstance() == null) {
			target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(map);
		} else {
			target = getPlayer().getEventInstance().getMapInstance(map);
		}
		return target;
	}

	public MapleMap getMap(int mapid) {
		return getWarpMap(mapid);
	}

	public boolean haveItem(int itemid) {
		return haveItem(itemid, 1);
	}

	public boolean haveItem(int itemid, int quantity) {
		return haveItem(itemid, quantity, false, true);
	}

	public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
		return c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
	}

	public boolean canHold(int itemid) {
		MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
		MapleInventory iv = c.getPlayer().getInventory(type);
		return iv.getNextFreeSlot() > -1;
	}

	public MapleJob getJob() {
		return getPlayer().getJob();
	}

	public int getLevel() {
		return getPlayer().getLevel();
	}

	public MapleQuestStatus.Status getQuestStatus(int id) {
		return getPlayer().getQuestStatus(id).getStatus();
	}

	public boolean isQuestCompleted(int id) {
		return getQuestStatus(id) == MapleQuestStatus.Status.COMPLETED;
	}

	/**
	 * Gives item with the specified id or takes it if the quantity is negative. Note that this does NOT take items from the equipped inventory.
	 * @param id
	 * @param quantity
	 */
	public void gainItem(int id, short quantity) {
		if (quantity >= 0) {
			StringBuilder logInfo = new StringBuilder(c.getPlayer().getName());
			logInfo.append(" received ");
			logInfo.append(quantity);
			logInfo.append(" from a scripted PlayerInteraction (");
			logInfo.append(this.toString());
			logInfo.append(")");
			MapleInventoryManipulator.addById(c, id, quantity, logInfo.toString());
		} else {
			MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
		}
		c.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
	}

	public int mapCount(int mapid) {
		MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
		return target.getCharacters().size();
	}

	public void clear() {
		getPlayer().getMap().pqSign(true);
	}

	public void wrong() {
		getPlayer().getMap().pqSign(false);
	}

	public void win() {
		getPlayer().getMap().carnivalSign(true);
	}

	public void lose() {
		getPlayer().getMap().carnivalSign(false);
	}

	public void victory() {
		getPlayer().getMap().eventSign(true);
	}

	public void loose() {
		getPlayer().getMap().eventSign(false);
	}

	public void showClockS(int seconds) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(seconds));
	}

	public void showClockM(int minutes) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(minutes * 60));
	}

	public void changeMusic(String songName) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectEnvironment(songName, 6));
	}

	// default playerMessage and mapMessage to use type 5
	public void playerMessage(String message) {
		playerMessage(5, message);
	}

	public void mapMessage(String message) {
		mapMessage(5, message);
	}

	public void playerMessage(int type, String message) {
		getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(type, message));
	}

	public void mapMessage(int type, String message) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
	}

	public void guildMessage(String message) {
		guildMessage(5, message);
	}

	public void guildMessage(int type, String message) {
		MapleCharacter chr;
		//getPlayer().getGuild().broadcast(packet);
		for (MapleGuildCharacter mgc : getPlayer().getGuild().getMembers()) {
			for (ChannelServer cs : ChannelServer.getAllInstances()) {
				if (cs.getPlayerStorage().getCharacterById(mgc.getId()) != null) {
					chr = cs.getPlayerStorage().getCharacterById(mgc.getId());
					chr.getClient().getSession().write(MaplePacketCreator.serverNotice(type, message));
				}
			}
		}
	}

	public void hintMessage(String message) {
		getPlayer().getClient().getSession().write(MaplePacketCreator.sendHint(message));
	}

	public void hintMessage(String message, int x, int y) {
		getPlayer().getClient().getSession().write(MaplePacketCreator.sendHint(message, 0, 10,true, x, y));
	}

	public void hintMessageMap(String message) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.sendHint(message));
	}

	public void hintMessageMap(String message, int x, int y) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.sendHint(message, 0, 10,true, x, y));
	}

	public void spawnMob(int mapid, int mid, int xpos, int ypos) {
		MapleMap map = ChannelServer.getInstance(getClient().getChannel()).getMapFactory().getMap(mapid);
		MapleMonster mob = MapleLifeFactory.getMonster(mid);
		Point spawnpoint = new Point(xpos, ypos);
		map.spawnMonsterOnGroundBelow(mob, spawnpoint);
	}

	public void spawnMobRandomPos(MapleMap map, int mid) {
		MapleMonster mob = MapleLifeFactory.getMonster(mid);
		map.spawnMonsterOnGroundBelow(mob, map.getRandomSpawnPosition());
	}

	public boolean isPartyLeader() {
		return getPlayer().isPartyLeader();
	}

	/**
	 * Returns how much of an item the player has.
	 * @param itemid
	 */
	public int itemQuantity(int itemid) {
		MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
		MapleInventory iv = c.getPlayer().getInventory(type);
		int possesed = iv.countById(itemid);
		return possesed;
	}

	public void KPQReward(MapleCharacter chr) {
		MaplePQRewards.giveKPQReward(chr);
	}

	public void LMPQReward(MapleCharacter chr) {
		MaplePQRewards.giveLMPQReward(chr);
	}

	public MapleParty getParty() {
		return getPlayer().getParty();
	}

	public boolean isLeader() {
        if (getParty() == null) {
            return false;
        }
		return (getParty().getLeader().equals(new MaplePartyCharacter(c.getPlayer())));
	}

    public boolean hasParty() {
        return getParty() != null;
    }

    public int getPartySize() {
        return getParty() != null ? getParty().getMembers().size() : 0;
    }

    public boolean checkPartySize(int min, int max) {
        int size = getPartySize();
        return size >= min && size <= max;
    }

    public boolean checkPartyLevels(int min, int max) {
       if (getParty() == null) {
           return false;
       } else {
           for(MaplePartyCharacter mpc : getParty().getMembers()) {
               if (mpc.getLevel() < min || mpc.getLevel() > max) {
                   return false;
               }
           }
       }
       return true;
    }

	//PQ methods: give items/exp to all party members
	public void givePartyItems(int id, short quantity, List<MapleCharacter> party) {
		for (MapleCharacter chr : party) {
			MapleClient cl = chr.getClient();
			if (quantity >= 0) {
				StringBuilder logInfo = new StringBuilder(cl.getPlayer().getName());
				logInfo.append(" received ");
				logInfo.append(quantity);
				logInfo.append(" from event ");
				logInfo.append(chr.getEventInstance().getName());
				MapleInventoryManipulator.addById(cl, id, quantity, logInfo.toString());
			} else {
				MapleInventoryManipulator.removeById(cl, MapleItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
			}
			cl.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
		}
	}

	//PQ gain EXP: Multiplied by channel rate here to allow global values to be input direct into NPCs
	public void givePartyExp(int amount, List<MapleCharacter> party) {
		for (MapleCharacter chr : party) {
			chr.gainExp(amount * c.getChannelServer().getExpRate(), true, true);
		}
	}

	//PQ gain EXP: Multiplied by channel rate here to allow global values to be input direct into NPCs
	public void givePartyExp(int amount, List<MapleCharacter> party, boolean withrate) {
		for (MapleCharacter chr : party) {
			if (withrate) {
				chr.gainExp(amount * c.getChannelServer().getExpRate(), true, true);
			} else {
				chr.gainExp(amount, true, true);
			}
		}
	}

	//remove all items of type from party
	//combination of haveItem and gainItem
	public void removeFromParty(int id, List<MapleCharacter> party) {
		for (MapleCharacter chr : party) {
			MapleClient cl = chr.getClient();
			MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(id);
			MapleInventory iv = cl.getPlayer().getInventory(type);
			int possesed = iv.countById(id);

			if (possesed > 0) {
				MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(id), id, possesed, true, false);
				cl.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possesed, true));
			}
		}
	}

	//remove all items of type from character
	//combination of haveItem and gainItem
	public void removeAll(int id) {
		MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(id);
		MapleInventory iv = c.getPlayer().getInventory(type);
		int possesed = iv.countById(id);

		if (possesed > 0) {
			MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(id), id, possesed, true, false);
			c.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possesed, true));
		}
	}

	public void giveReward(MapleReward[] rewards, MapleCharacter chr) {
		MapleReward.giveReward(rewards, chr);
	}

	public void giveRewards(MapleReward[] rewards, List<MapleCharacter> members) {
		for (MapleCharacter member : members) {
			MapleReward.giveReward(rewards, member);
		}
	}

	public void resetReactors(int mapid) {
		MapleMap map = getWarpMap(mapid);
		map.resetReactors();
	}

	public boolean containsReactor(int reactorId) {
		MapleMap map = getPlayer().getMap();
		return map.containsReactor(reactorId);
	}

	public int getReactorState(MapleMap map, int rId) {
		int state = 999;
		for (MapleReactor r : map.getReactors()) {
			if (r.getId() == rId) {
				state = (int) r.getState();
			}
		}
		return state;
	}

	public int getReactorState(int mapid, int rId) {
		int state = 999;
		MapleMap map = getWarpMap(mapid);
		for (MapleReactor r : map.getReactors()) {
			if (r.getId() == rId) {
				state = (int) r.getState();
			}
		}
		return state;
	}

	public MapleReactor getReactor(MapleMap map, int id) {
		for (MapleReactor r : map.getReactors()) {
			if (r.getId() == id) {
				return r;
			}
		}
		return null;
	}

	public MapleReactor getReactor(MapleMap map, String name) {
		for (MapleReactor r : map.getReactors()) {
			if (r.getName() != null && r.getName().equalsIgnoreCase(name)) {
				return r;
			}
		}
		return null;
	}

	public void setReactorState(MapleMap map, int rId, int state) {
		for (MapleReactor r : map.getReactors()) {
			if (r.getId() == rId) {
				r.setState((byte) state);
			}
		}
	}

	public void setReactorState(int mapid, int rId, int state) {
		MapleMap map = getWarpMap(mapid);
		for (MapleReactor r : map.getReactors()) {
			if (r.getId() == rId) {
				r.setState((byte) state);
			}
		}
	}

	public void setHair(int hair) {
		getPlayer().setHair(hair);
		getPlayer().updateSingleStat(MapleStat.HAIR, hair);
		getPlayer().equipChanged();
	}

	public void setFace(int face) {
		getPlayer().setFace(face);
		getPlayer().updateSingleStat(MapleStat.FACE, face);
		getPlayer().equipChanged();
	}

	public void setSkin(int color) {
		getPlayer().setSkinColor(MapleSkinColor.getById(color));
		getPlayer().updateSingleStat(MapleStat.SKIN, color);
		getPlayer().equipChanged();
	}

	/**
	 * Spawns an NPC at the players location
	 * @param npcId
	 */
	public void spawnNpc(int npcId) {
		spawnNpc(npcId, c.getPlayer().getPosition());
	}

	/**
	 * Spawns an NPC at a custom position
	 * @param npcId
	 * @param x
	 * @param y
	 */
	public void spawnNpc(int npcId, int x, int y) {
		spawnNpc(npcId, new Point(x, y));
	}

	/**
	 * Spawns an NPC at a custom position
	 * @param npcId
	 * @param pos
	 */
	public void spawnNpc(int npcId, Point pos) {
		MapleNPC npc = MapleLifeFactory.getNPC(npcId);
		npc.setCustom(true);
		c.getPlayer().getMap().spawnNpc(npcId, pos);
	}

	public void gainMeso(int gain) {
		getPlayer().gainMeso(gain, true, false, true);
	}

	public void gainExp(int gain) {
		getPlayer().gainExp(gain, true, true);
	}

	public void unequipEverything() {
		MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
		MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
		List<Byte> ids = new LinkedList<Byte>();
		for (IItem item : equipped.list()) {
			ids.add(item.getPosition());
		}
		for (byte id : ids) {
			MapleInventoryManipulator.unequip(getClient(), id, equip.getNextFreeSlot());
		}
	}

	public void teachSkill(int id, int level, int masterlevel) {
		getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
	}

	public void rechargeStars() {
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		IItem projectiles = getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) 1);
		//TODO GIVE BONUS HERE FOR RECHARGE
		if (MapleItemInformationProvider.isThrowingStar(projectiles.getItemId())) {
			projectiles.setQuantity(ii.getSlotMax(projectiles.getItemId()));
		} else if (MapleItemInformationProvider.isBullet(projectiles.getItemId())) {
			projectiles.setQuantity(ii.getSlotMax(projectiles.getItemId()));
		}
		getClient().getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, (Item) projectiles));
	}

	public void changeJob(MapleJob job) {
		getPlayer().changeJob(job);
	}

    public void effectTremble(int type, int delay) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectTremble(type, delay));
    }

    public void effectObject(String effect) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectEnvironment(effect, 2));
	}

    public void effectSound(String effect) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectEnvironment(effect, 4));
    }

    public void effectScreen(String effect) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectEnvironment(effect, 3));
    }

	public EventManager getEventManager(String event) {
		return getClient().getChannelServer().getEventSM().getEventManager(event);
	}

    public void lockPortal(String portal, int status) {
        c.getPlayer().getMap().getPortal(portal).setLocked(status);
    }

	public int random(int min, int max) {
		if (min > max) {
			return -1;
		}
		return Randomizer.randomInt(min, max);
	}

	public void blockPortal(String name, int type) {
		getPlayer().getMap().getPortal(name).setLocked(type);
	}

	public void playSound(String sound) {
		getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.effectEnvironment(sound, 4));
	}

	public MapleCharacter getChr(String name) {
		MapleCharacter chr = cserv.getPlayerStorage().getCharacterByName(name);
		if (chr != null) {
			return chr;
		} else {
			return null;
		}
	}

	public MapleCharacter getChr(int id) {
		MapleCharacter chr = cserv.getPlayerStorage().getCharacterById(id);
		if (chr != null) {
			return chr;
		} else {
			return null;
		}
	}

	public void openShop(int id) {
		MapleShopFactory.getInstance().getShop(id).sendShop(getClient());
	}

	public void portToPort(int portal) {
		if (getPlayer().getMap().getPortal(portal) != null) {
			getClient().getSession().write(MaplePacketCreator.portToPort(portal));
		}
	}

	public void portToPort(MapleCharacter chr, int portal) {
		if (chr.getMap().getPortal(portal) != null) {
			chr.getClient().getSession().write(MaplePacketCreator.portToPort(portal));
		}
	}

        public void portToPort(String portal) {
            MaplePortal p = getPlayer().getMap().getPortal(portal);
		if (p != null) {
                    getClient().getSession().write(MaplePacketCreator.portToPort(p.getId()));
		}
	}

	public void portToPort(MapleCharacter chr, String portal) {
            MaplePortal p = chr.getMap().getPortal(portal);
		if (p != null) {
			chr.getClient().getSession().write(MaplePacketCreator.portToPort(p.getId()));
		}
	}

	public int getMapId() {
		return c.getPlayer().getMap().getId();
	}

	public int getHour() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

	public int getMin() {
		Calendar cal = Calendar.getInstance();
		int min = cal.get(Calendar.MINUTE);
		return min;
	}

	public int getSec() {
		Calendar cal = Calendar.getInstance();
		int sec = cal.get(Calendar.SECOND);
		return sec;
	}

	public int getDay() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		return day;
	}

	/**
	 * Get a certain time<br>
	 * 0 = Seconds<br>
	 * 1 = Minutes<br>
	 * 2 = Hour<br>
	 * @param type The type of time
	 * @return the time
	 */
	public int getTime(int type) {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		if (type == 0) {
			return sec;
		} else if (type == 1) {
			return min;
		} else if (type == 2) {
			return hour;
		} else {
			return 0;
		}
	}

	public int mobCount(int mapid) {
		MapleMap map = getWarpMap(mapid);
		List<MapleMapObject> mobs = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
		int count = mobs.size();
		return count;
	}

	public int mobCount(MapleMap map) {
		List<MapleMapObject> mobs = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
		int count = mobs.size();
		return count;
	}

	public void updateBuddyCapacity(int capacity) {
		getPlayer().getBuddylist().getCapacity();
		getClient().getSession().write(MaplePacketCreator.updateBuddyCapacity(capacity));
	}

	public int getBuddyCapacity() {
		return getPlayer().getBuddyCapacity();
	}

	public void killAll() {
		killAll(getPlayer(), false);
	}

	public void killAll(boolean drops) {
        killAll(getPlayer(), drops);
    }

    public void killAll(MapleCharacter chr, boolean drops) {
		getPlayer().getMap().killAllMonsters(chr, drops);
	}

    public void killAllNoOwner(boolean drops) {
        getPlayer().getMap().killAllMonsters(null, drops);
    }

	public int getPlayerCount() {
		return getPlayer().getMap().getCharacters().size();
	}

	public int getPlayerCount(int mapid) {
		return getMap(mapid).getPlayerCount();
	}

	public int getCurrentPartyId(int mapid) {
		return getMap(mapid).getCurrentPartyId();
	}

	public void gainCloseness(int closeness, int index) {
		MaplePet pet = getPlayer().getPet(index);
		if (pet != null) {
			pet.gainCloseness(closeness);
			pet.update();
		}
	}

	public void gainClosenessAll(int closeness) {
		for (MaplePet pet : getPlayer().getActivePets()) {
			pet.gainCloseness(closeness);
			pet.update();
		}
	}

	public void resetAp() {
		List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>();
		MapleCharacter player = getPlayer();
		int totAp = player.getRemainingAp() + player.getStr() + player.getDex() + player.getInt() + player.getLuk();
		player.setStr(4);
		player.setDex(4);
		player.setInt(4);
		player.setLuk(4);
		player.setRemainingAp(totAp - 16);
		statup.add(new Pair<MapleStat, Integer>(MapleStat.STR, 4));
		statup.add(new Pair<MapleStat, Integer>(MapleStat.DEX, 4));
		statup.add(new Pair<MapleStat, Integer>(MapleStat.LUK, 4));
		statup.add(new Pair<MapleStat, Integer>(MapleStat.INT, 4));
		statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, player.getRemainingAp()));
		getClient().getSession().write(MaplePacketCreator.updatePlayerStats(statup));
	}
	
	public int[] getRandomAreaCombo(int amount) {
		//TODO: add ability to stack people on areas
		List<Rectangle> areas = getPlayer().getMap().getAreas();
		List<Rectangle> areasRectanglePicked = new ArrayList<Rectangle>(amount);
		int[] areasPicked = new int[areas.size()];
		int picked = 0;
		if (amount > areas.size()) {
			return null;
		}
		
		while (picked < amount) {
			Rectangle area = (Rectangle) Randomizer.randomSelection(areas.toArray());
			if (!areasRectanglePicked.contains(area)) {
				areasRectanglePicked.add(area);
				picked++;
			}
		}

		for (int i = 0; i < areas.size(); i++) {
			for (Rectangle rec : areasRectanglePicked) {
				if (rec == areas.get(i)) {
					areasPicked[i]++;
				}
			}
		}
		return areasPicked;
	}

        public String intArrayToString(int[] array) {
            String ret = "";
            for(int i = 0; i < array.length; i++) {
                ret += array[i];
                if (i + 1 != array.length) {
                    ret += " ";
                }
            }
            return ret;
        }

        public int[] stringToIntArray(String str) {
            String[] strArray = str.split(" ");
            int[] ret = new int[strArray.length];
            for(int i = 0; i < strArray.length; i++) {
                ret[i] = Integer.parseInt(strArray[i]);
            }
            return ret;
        }

	public String shuffleCombo(String comboStr) {
                int[] combo = stringToIntArray(comboStr);
                int length = combo.length;
                int[] newCombo = new int[length];
                int[] usedPositions = new int[length];
                
                for(int i = 0; i < length; i++) {
                    int rnd = Randomizer.randomInt(length);
                    while (usedPositions[rnd] == 1) {
                        rnd = Randomizer.randomInt(length);
                    }
                    usedPositions[rnd] = 1;
                    newCombo[rnd] = combo[i];
                }
		return intArrayToString(newCombo);
	}

	public int checkAreas(int players, String comboStr) {
                int[] combo = stringToIntArray(comboStr);
		List<Rectangle> areas = getPlayer().getMap().getAreas();
		int totalPlayers = 0;
		if (areas.size() != combo.length) {
			return -1;
		}
		int[] objSet = new int[areas.size()];
		for (int i = 0; i < areas.size(); i++) {
			Rectangle rec = areas.get(i);
			for (MaplePartyCharacter mpc : getPlayer().getParty().getMembers()) {
				boolean inArea = rec.contains(mpc.getChar().getPosition());
				if (inArea) {
					objSet[i]++;
					totalPlayers++;
				}
			}
		}
		if (totalPlayers == players) {
			for (int i = 0; i < objSet.length; i++) {
				if (combo[i] != objSet[i]) {
					return 0;
				}
			}
                        return 1;

		}
		return -1;
	}

    public MapleQuestRecord getQuestRecord() {
        return c.getPlayer().getQuestRecord();
    }

    public boolean isRunningNPC() {
        return NPCScriptManager.getInstance().isRunningNPC(c);
    }

    public void spawnMonsterBag(int itemId, int x, int y) {
        MapleItemInformationProvider.getInstance().spawnMonsterBag(getPlayer(), itemId, new Point(x, y));
    }

    public int getMonsterCount(int monsterid) {
        return getPlayer().getMap().getMonsterCount(monsterid);
    }

    public void giveItemBuff(int itemId) {
	  MapleItemInformationProvider.getInstance().getItemEffect(itemId).applyTo(getPlayer());
	  c.getSession().write(MaplePacketCreator.getItemBuffGain(itemId));
    }

    public boolean startNPCScript(int npcId) {
        MapleNPC npc = MapleLifeFactory.getNPC(npcId);
        if (!isRunningNPC() && npc != null) {
            NPCScriptManager.getInstance().start(c, npc);
            return true;
        }
        return false;
    }
}
