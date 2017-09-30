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
package net.sf.odinms.server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.GameConstants.FieldLimitBits;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.life.SpawnPoint;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleMap {

    private static final int MAX_OID = 20000;
    private static final List<MapleMapObjectType> rangedMapobjectTypes = Arrays.asList(MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER, MapleMapObjectType.DOOR, MapleMapObjectType.SUMMON, MapleMapObjectType.REACTOR);
    /**
     * Holds a mapping of all oid -> MapleMapObject on this map. mapobjects is NOT a synchronized collection since it
     * has to be synchronized together with runningOid that's why all access to mapobjects have to be done trough an
     * explicit synchronized block
     */
    private final Map<Integer, MapleMapObject> mapobjects = new LinkedHashMap<Integer, MapleMapObject>();
    private List<SpawnPoint> monsterSpawn = new ArrayList<SpawnPoint>();
    private AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final Collection<MapleCharacter> characters = new LinkedHashSet<MapleCharacter>();
    private Map<Integer, MaplePortal> portals = new HashMap<Integer, MaplePortal>();
    private List<Rectangle> areas = new ArrayList<Rectangle>();
    private MapleFootholdTree footholds = null;
    private List<Integer> blockedMonsterSpawns = new ArrayList<Integer>();
    private int mapid;
    private int runningOid = 100;
    private int returnMapId;
    private int forcedReturn;
    private int channel;
    private int fieldType;
    private int fieldLimit;
    private float monsterRate;
    private int swim;
    private int everlast;
    private int continent;
    private boolean dropsDisabled = false;
    private boolean spawnEnabled = true;
    private boolean clock;
    private boolean shipDocked = false;
    private boolean shipIsBalrog = false;
    private boolean protectMobDamagedByMob = false;
    private String mapName;
    private String streetName;
    private int decHP;
    private int protectItem;
    private int lvLimit;
    private int timeLimit;
    private MapleMapEffect mapEffect;
    private static Logger log = LoggerFactory.getLogger(MapleMap.class);

    public MapleMap(int mapid, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = channel;
        this.returnMapId = returnMapId;
        if (monsterRate > 0) {
            this.monsterRate = monsterRate;
            boolean greater1 = monsterRate > 1.0;
            this.monsterRate = (float) Math.abs(1.0 - this.monsterRate);
            this.monsterRate = this.monsterRate / 2.0f;
            if (greater1) {
                this.monsterRate = 1.0f + this.monsterRate;
            } else {
                this.monsterRate = 1.0f - this.monsterRate;
            }
            TimerManager.getInstance().register(new RespawnWorker(), 5001 + (int) (30.0 * Math.random()));
        }
    }

    public int getChannel() {
        return channel;
    }

    public void toggleDrops() {
        dropsDisabled = !dropsDisabled;
    }

    public void toggleSpawn() {
        spawnEnabled = !spawnEnabled;
    }

    public boolean isSpawnEnabled() {
        return spawnEnabled;
    }

    public void setSpawnEnabled(boolean enabled) {
        if (enabled) {
            blockedMonsterSpawns.clear();
        }
        spawnEnabled = enabled;
    }

    public int getId() {
        return mapid;
    }

    public void setForcedReturn(int forcedReturn) {
        this.forcedReturn = forcedReturn;
    }

    public int getForcedReturn() {
        return forcedReturn;
    }

    public MapleMap getReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(returnMapId);
    }

    public void addMapObject(MapleMapObject mapobject) {
        synchronized (this.mapobjects) {
            mapobject.setObjectId(runningOid);
            this.mapobjects.put(Integer.valueOf(runningOid), mapobject);
            incrementRunningOid();
        }
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery) {
        spawnAndAddRangedMapObject(mapobject, packetbakery, null);
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        synchronized (this.mapobjects) {
            mapobject.setObjectId(runningOid);

            synchronized (characters) {
                for (MapleCharacter chr : characters) {
                    if (condition == null || condition.canSpawn(chr)) {
                        if (chr.getPosition().distanceSq(mapobject.getPosition()) <= MapleCharacter.MAX_VIEW_RANGE_SQ) {
                            packetbakery.sendPackets(chr.getClient());
                            chr.addVisibleMapObject(mapobject);
                        }
                    }
                }
            }

            this.mapobjects.put(Integer.valueOf(runningOid), mapobject);
            incrementRunningOid();
        }
    }

    private void incrementRunningOid() {
        runningOid++;
        for (int numIncrements = 1; numIncrements < MAX_OID; numIncrements++) {
            if (runningOid > MAX_OID) {
                runningOid = 100;
            }
            if (this.mapobjects.containsKey(Integer.valueOf(runningOid))) {
                runningOid++;
            } else {
                return;
            }
        }
        throw new RuntimeException("Out of OIDs on map " + mapid + " (channel: " + channel + ")");
    }

    public void removeMapObject(int num) {
        synchronized (this.mapobjects) {
            /*if (!mapobjects.containsKey(Integer.valueOf(num))) {
            log.warn("Removing: mapobject {} does not exist on map {}", Integer.valueOf(num), Integer
            .valueOf(getId()));
            }*/
            this.mapobjects.remove(Integer.valueOf(num));
        }
    }

    public void removeMapObject(MapleMapObject obj) {
        removeMapObject(obj.getObjectId());
    }

    private Point calcPointBelow(Point initial) {
        MapleFoothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s4 = Math.abs(initial.x - fh.getX1());
            double alpha = Math.atan(s2 / s1);
            double beta = Math.atan(s1 / s2);
            double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) s5;
            } else {
                dropY = fh.getY1() + (int) s5;
            }
        }
        return new Point(initial.x, dropY);
    }

    private Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 99));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    public void dropFromMonster(MapleCharacter dropOwner, MapleMonster monster) {
        if (dropsDisabled || !monster.isDropsEnabled()) {
            return;
        }
        /*
         * drop logic: decide based on monster what the max drop count is get drops (not allowed: multiple mesos,
         * multiple items of same type exception: event drops) calculate positions
         */
        int maxDrops;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final boolean isBoss = monster.isBoss();
        if (isBoss) {
            maxDrops = 10;
        } else {
            maxDrops = 4;
        }

        List<Integer> toDrop = new ArrayList<Integer>();
        for (int i = 0; i < maxDrops; i++) {
            toDrop.add(monster.getDrop());
        }

        Set<Integer> alreadyDropped = new HashSet<Integer>();
        for (int i = 0; i < toDrop.size(); i++) {
            if (toDrop.get(i) == -1) {
                if (alreadyDropped.contains(-1) && !isBoss) {
                    toDrop.remove(i);
                    i--;
                } else {
                    alreadyDropped.add(-1);
                }
            } else {
                MapleInventoryType type = ii.getInventoryType(toDrop.get(i));
                if (alreadyDropped.contains((int) type.getType()) && !isBoss) {
                    toDrop.remove(i);
                    i--;
                } else {
                    alreadyDropped.add((int) type.getType());
                }
            }
        }

        if (toDrop.size() > maxDrops) {
            toDrop = toDrop.subList(0, maxDrops);
        }
        Point[] toPoint = new Point[toDrop.size()];
        int shiftDirection = 0;
        int shiftCount = 0;

        int curX = Math.min(Math.max(monster.getPosition().x - 25 * (toDrop.size() / 2), footholds.getMinDropX() + 25),
                footholds.getMaxDropX() - toDrop.size() * 25);
        int curY = Math.max(monster.getPosition().y, footholds.getY1());
        //int monsterShift = curX -
        while (shiftDirection < 3 && shiftCount < 1000) {
            // TODO for real center drop the monster width is needed -.^
            if (shiftDirection == 1) {
                curX += 25;
            } else if (shiftDirection == 2) {
                curX -= 25;
            }
            // now do it
            for (int i = 0; i < toDrop.size(); i++) {
                MapleFoothold wall = footholds.findWall(new Point(curX, curY), new Point(curX + toDrop.size() * 25, curY));
                if (wall != null) {
                    //System.out.println("found a wall. wallX " + wall.getX1() + " curX " + curX);
                    if (wall.getX1() < curX) {
                        shiftDirection = 1;
                        shiftCount++;
                        break;
                    } else if (wall.getX1() == curX) {
                        if (shiftDirection == 0) {
                            shiftDirection = 1;
                        }
                        shiftCount++;
                        break;
                    } else {
                        shiftDirection = 2;
                        shiftCount++;
                        break;
                    }
                } else if (i == toDrop.size() - 1) {
                    //System.out.println("ok " + curX);
                    shiftDirection = 3;
                }
                final Point dropPos = calcDropPos(new Point(curX + i * 25, curY), new Point(monster.getPosition()));
                toPoint[i] = new Point(curX + i * 25, curY);
                final int drop = toDrop.get(i);

                if (drop == -1) { // meso

                    final int mesoRate = ChannelServer.getInstance(getChannel()).getMesoRate();
                    Random r = new Random();
                    double mesoDecrease = Math.pow(0.93, monster.getExp() / 300.0);
                    if (mesoDecrease > 1.0) {
                        mesoDecrease = 1.0;
                    } else if (mesoDecrease < 0.001) {
                        mesoDecrease = 0.005;
                    }
                    int tempmeso = Math.min(30000, (int) (mesoDecrease * (monster.getExp())
                            * (1.0 + r.nextInt(20)) / 10.0));
                    if (dropOwner != null && dropOwner.getBuffedValue(MapleBuffStat.MESOUP) != null) {
                        tempmeso = (int) (tempmeso * dropOwner.getBuffedValue(MapleBuffStat.MESOUP).doubleValue() / 100.0);
                    }

                    final int meso = tempmeso;

                    if (meso > 0) {
                        final MapleMonster dropMonster = monster;
                        final MapleCharacter dropChar = dropOwner;
                        TimerManager.getInstance().schedule(new Runnable() {

                            public void run() {
                                spawnMesoDrop(meso * mesoRate, meso, dropPos, dropMonster, dropChar, isBoss);
                            }
                        }, monster.getAnimationTime("die1"));
                    }
                } else {
                    IItem idrop;
                    MapleInventoryType type = ii.getInventoryType(drop);
                    final MapleMonster dropMonster = monster;
                    if (type.equals(MapleInventoryType.EQUIP)) {
                        Equip nEquip = ii.randomizeStats((Equip) ii.getEquipById(drop));
                        idrop = nEquip;
                    } else {
                        idrop = new Item(drop, (byte) 0, (short) dropMonster.getDropAmount(drop));
                        // Randomize quantity for certain items
                        if (MapleItemInformationProvider.isArrowForBow(drop) || MapleItemInformationProvider.isArrowForCrossBow(drop)) {
                            idrop.setQuantity((short) (1 + 100 * Math.random()));
                        }
                        if (MapleItemInformationProvider.isRechargable(drop)) {
                            idrop.setQuantity((short) 1);
                        }
                    }

                    StringBuilder logMsg = new StringBuilder("Created as a drop from monster ");
                    logMsg.append(monster.getObjectId());
                    logMsg.append(" (");
                    logMsg.append(monster.getId());
                    logMsg.append(") at ");
                    logMsg.append(dropPos.toString());
                    logMsg.append(" on map ");
                    logMsg.append(mapid);
                    idrop.log(logMsg.toString(), false);

                    final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, monster, dropOwner);
                    final MapleCharacter dropChar = dropOwner;
                    final TimerManager tMan = TimerManager.getInstance();
                    final int ownerId = isBoss | dropChar == null ? 0 : dropChar.getId();

                    activateItemReactors(mdrop);

                    tMan.schedule(new Runnable() {

                        public void run() {
                            spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

                                public void sendPackets(MapleClient c) {
                                    c.getSession().write(MaplePacketCreator.dropItemFromMapObject(drop, mdrop.getObjectId(), dropMonster.getObjectId(), ownerId, dropMonster.getPosition(), dropPos, (byte) 1, false));
                                }
                            });

                            tMan.schedule(new ExpireMapItemJob(mdrop), 60000);
                        }
                    }, monster.getAnimationTime("die1"));

                }
            }
        }
    }

    public boolean damageMonster(MapleCharacter chr, MapleMonster monster, int damage) {
        // double checking to potentially avoid synchronisation overhead
        if (monster.isAlive()) {
            boolean killMonster = false;

            synchronized (monster) {
                if (!monster.isAlive()) {
                    return false;
                }
                if (damage > 0) {
                    monster.damage(chr, damage, true);
                    if (!monster.isAlive()) { // monster just died
                        killMonster = true;
                    }
                }
            }
            // the monster is dead, as damageMonster returns immediately for dead monsters this makes
            // this block implicitly synchronized for ONE monster
            if (killMonster) {
                killMonster(monster, chr);
            }
            return true;
        }
        return false;
    }

    public List<MapleReactor> getReactors() {
        List<MapleReactor> reactorList = new ArrayList<MapleReactor>();
        synchronized (this.mapobjects) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    MapleReactor reactor = ((MapleReactor) o);
                    reactorList.add(reactor);
                }
            }
            return reactorList;
        }
        //Return here ? or there?
    }

    public void killMonster(MapleMonster monster, MapleCharacter chr) {
        killMonster(monster, chr, 1);
    }

    public void killMonster(MapleMonster monster, MapleCharacter chr, int animation) {
        removeMonster(monster, chr, animation, true);
    }

    public void removeMonster(MapleMonster monster, boolean drops) {
        removeMonster(monster, null, 1, drops);
    }

    public void removeMonster(MapleMonster monster, MapleCharacter chr, int animation, boolean drops) {
        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), 1), monster.getPosition());
        removeMapObject(monster);
        monster.handleDeathEvents();
        MapleCharacter dropOwner = monster.killBy(chr);
        if (drops) {
            dropFromMonster(dropOwner, monster);
        }
    }

    public int killAllMonsters(MapleCharacter chr, boolean drops) {
        int monstersKilled = 0;
        for (MapleMapObject monstermo : getAllObjects(MapleMapObjectType.MONSTER)) {
            MapleMonster monster = (MapleMonster) monstermo;
            if (isProtectMobDamagedByMob() ? !monster.getStats().isDamagedByMob() : true) {
                removeMonster(monster, chr, 1, drops);
                monstersKilled++;
            }
        }
        return monstersKilled;
    }

    public int removeMonsters(int monsterid, boolean drops) {
        int monstersKilled = 0;
        for (MapleMapObject monstermo : getAllObjects(MapleMapObjectType.MONSTER)) {
            MapleMonster monster = (MapleMonster) monstermo;
            if (monster.getId() == monsterid) {
                if (isProtectMobDamagedByMob() ? !monster.getStats().isDamagedByMob() : true) {
                    removeMonster(monster, null, 1, drops);
                    monstersKilled++;
                }
            }
        }
        return monstersKilled;
    }

    public List<MapleMapObject> getAllPlayers() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
    }

    public List<MapleMapObject> getAllObjects(MapleMapObjectType objectType) {
        return Collections.unmodifiableList(getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(objectType)));
    }

    public void destroyReactor(int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        TimerManager tMan = TimerManager.getInstance();
        broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        if (reactor.getDelay() > 0) {
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    /*
     * command to reset all item-reactors in a map to state 0 for GM/NPC use - not tested (broken reactors get removed
     * from mapobjects when destroyed) Should create instances for multiple copies of non-respawning reactors...
     */
    public void resetReactors() {
        synchronized (this.mapobjects) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    MapleReactor reactor = ((MapleReactor) o);
                    reactor.setState((byte) 0);
                    reactor.setTimerActive(false);
                    broadcastMessage(MaplePacketCreator.triggerReactor((MapleReactor) o, 0));
                }
            }
        }
    }

    /*
     * command to shuffle the positions of all reactors in a map for PQ purposes (such as ZPQ/LMPQ)
     */
    public void shuffleReactors() {
        List<Point> points = new LinkedList<Point>();
        synchronized (this.mapobjects) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    points.add(((MapleReactor) o).getPosition());
                }
            }

            Collections.shuffle(points);

            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    ((MapleReactor) o).setPosition(points.remove(0));
                }
            }
        }
    }

    /*
     * command to shuffle the positions of all reactors in a map by name for PQ purposes (such as  OPQ)
     */
    public void shuffleReactors(String name) {
        if (name == null || name.equals("")) {
            shuffleReactors();
        } else {
            List<Point> points = new LinkedList<Point>();
            List<MapleReactor> reactors = new LinkedList<MapleReactor>();
            reactors = getReactorsByName(name);
            for (MapleReactor reactor : reactors) {
                points.add(reactor.getPosition());
            }

            Collections.shuffle(points);

            for (MapleReactor reactor : reactors) {
                reactor.setPosition(points.remove(0));
            }
        }
    }

    public Point getRandomSpawnPosition() {
        Point ret = null;
        if (monsterSpawn.size() > 0) {
            ret = monsterSpawn.get(Randomizer.randomInt(monsterSpawn.size())).getPosition();
        }
        return ret;
    }

    /**
     * Automagically finds a new controller for the given monster from the chars on the map...
     *
     * @param monster
     */
    public void updateMonsterController(MapleMonster monster) {
        synchronized (monster) {
            if (!monster.isAlive()) {
                return;
            }
            if (monster.getController() != null) {
                // monster has a controller already, check if he's still on this map
                if (monster.getController().getMap() != this) {
                    log.warn("Monstercontroller wasn't on same map");
                    monster.getController().stopControllingMonster(monster);
                } else {
                    // controller is on the map, monster has an controller, everything is fine
                    return;
                }
            }
            int mincontrolled = -1;
            MapleCharacter newController = null;
            synchronized (characters) {
                for (MapleCharacter chr : characters) {
                    if (!chr.isHidden() && (chr.getControlledMonsters().size() < mincontrolled || mincontrolled == -1)) {
                        if (!chr.getName().equals("FaekChar")) { // TODO remove me for production release
                            mincontrolled = chr.getControlledMonsters().size();
                            newController = chr;
                        }
                    }
                }
            }
            if (newController != null) { // was a new controller found? (if not no one is on the map)
                if (monster.isAutoAggro()) {
                    newController.controlMonster(monster, true);
                    monster.setControllerHasAggro(true);
                    monster.setControllerKnowsAboutAggro(true);
                } else {
                    newController.controlMonster(monster, false);
                }
            }
        }
    }

    public Collection<MapleMapObject> getMapObjects() {
        return Collections.unmodifiableCollection(mapobjects.values());
    }

    public boolean containsNPC(int npcid) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.NPC) {
                    if (((MapleNPC) obj).getId() == npcid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsMonster(int monsterid) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.MONSTER) {
                    if (((MapleMonster) obj).getId() == monsterid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsReactor(int reactorId) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) obj).getId() == reactorId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getMonsterCount(int monsterid) {
        int count = 0;
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.MONSTER) {
                    if (((MapleMonster) obj).getId() == monsterid) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public MapleMapObject getMapObject(int oid) {
        return mapobjects.get(oid);
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns null
     *
     * @param oid
     * @return Monster with the oid.
     */
    public MapleMonster getMonsterByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid);
        if (mmo == null) {
            return null;
        }
        if (mmo.getType() == MapleMapObjectType.MONSTER) {
            return (MapleMonster) mmo;
        }
        return null;
    }

    public MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid);
        if (mmo != null && mmo.getType() == MapleMapObjectType.REACTOR) {
            return (MapleReactor) mmo;
        }
        return null;
    }

    public MapleReactor getReactorByName(String name) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    MapleReactor reactor = (MapleReactor) obj;
                    if (reactor.getName().equals(name)) {
                        return (MapleReactor) obj;
                    }
                }
            }
        }
        return null;
    }

    public List<MapleReactor> getReactorsByName(String name) {
        List<MapleReactor> ret = new ArrayList<MapleReactor>();
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    MapleReactor reactor = (MapleReactor) obj;
                    if (reactor.getName().equals(name)) {
                        ret.add(reactor);
                    }
                }
            }
        }
        return ret;
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);

        //some reactors end up spawning monsters below ground; this should fix it.
        while (spos == null) {
            pos.y -= 25;
            spos = new Point(pos.x, pos.y);
            spos = calcPointBelow(spos);
        }
        spos.y -= 1;
        mob.setPosition(spos);
        spawnMonster(mob);
    }

    public void spawnMonster(final MapleMonster monster) {
        monster.setMap(this);
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MaplePacketCreator.spawnMonster(monster, true));
                }
            });
            if (monster.hasBossHPBar()) {
                broadcastMessage(monster.makeBossHPBarPacket(), monster.getPosition());
            }
            monster.handleSpawningEvents();
            updateMonsterController(monster);
        }
        spawnedMonstersOnMap.incrementAndGet();

    }

    public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    public void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MaplePacketCreator.spawnFakeMonster(monster));
                }
            });
        }
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void makeMonsterReal(final MapleMonster monster) {
        monster.setFake(false);
        broadcastMessage(MaplePacketCreator.makeMonsterReal(monster));
        if (monster.hasBossHPBar()) {
            broadcastMessage(monster.makeBossHPBarPacket(), monster.getPosition());
        }
        updateMonsterController(monster);
    }

    public boolean spawnNpc(int npcId, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(npcId);
        if (npc != null && !npc.getName().equals("MISSINGNO")) {
            npc.setPosition(pos);
            npc.setCy(pos.y);
            npc.setRx0(pos.x + 50);
            npc.setRx1(pos.x - 50);
            npc.setFh(getFootholds().findBelow(pos).getId());
            npc.setCustom(true);
            spawnNpc(npc);
            return true;
        } else {
            return false;
        }
    }

    public void spawnNpc(MapleNPC npc) {
        addMapObject(npc);
        broadcastMessage(MaplePacketCreator.spawnNPC(npc, false, true));
    }

    public void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);
        synchronized (mapobjects) {
            spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(reactor.makeSpawnData());
                }
            });
        }
    }

    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnDoor(final MapleDoor door) {
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MaplePacketCreator.spawnDoor(door.getOwner().getId(), door.getTargetPosition(), false));
                    if (door.getOwner().getParty() != null && (door.getOwner() == c.getPlayer() || door.getOwner().getParty().containsMembers(new MaplePartyCharacter(c.getPlayer())))) {
                        c.getSession().write(MaplePacketCreator.partyPortal(door.getTown().getId(), door.getTarget().getId(), door.getTargetPosition()));
                    }
                    c.getSession().write(MaplePacketCreator.spawnPortal(door.getTown().getId(), door.getTarget().getId(), door.getTargetPosition()));
                    c.getSession().write(MaplePacketCreator.enableActions());
                }
            }, new SpawnCondition() {

                public boolean canSpawn(MapleCharacter chr) {
                    return chr.getMapId() == door.getTarget().getId()
                            || chr == door.getOwner() && chr.getParty() == null;
                }
            });
        }
    }

    public void spawnSummon(final MapleSummon summon) {
        spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                int skilLlevel = summon.getOwner().getSkillLevel(SkillFactory.getSkill(summon.getSkill()));
                c.getSession().write(MaplePacketCreator.spawnSpecialMapObject(summon, skilLlevel, true));
            }
        });
    }

    public void spawnMist(final MapleMist mist, final int duration, boolean poison) {
        addMapObject(mist);
        broadcastMessage(mist.makeSpawnData());
        TimerManager tMan = TimerManager.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = new Runnable() {

                @Override
                public void run() {
                    List<MapleMapObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER));
                    for (MapleMapObject mo : affectedMonsters) {
                        if (mist.makeChanceResult()) {
                            MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), false);
                            ((MapleMonster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                        }
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }

        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
                broadcastMessage(mist.makeDestroyData());
            }
        }, duration);
    }

    public void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner);
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, 0, dropper.getPosition(), droppos, (byte) 3, true), drop.getPosition());
    }

    public void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, Point pos, final boolean ffaDrop, final boolean expire) {
        TimerManager tMan = TimerManager.getInstance();
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner);
        final int ownerId = ffaDrop | owner == null ? 0 : owner.getId();
        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, ownerId, dropper.getPosition(), droppos, (byte) 1, false));
            }
        });
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, ownerId, dropper.getPosition(), droppos, (byte) 0, false), drop.getPosition());

        if (expire) {
            tMan.schedule(new ExpireMapItemJob(drop), 60000);
        }
        activateItemReactors(drop);
    }

    private void activateItemReactors(MapleMapItem drop) {
        if (drop.getOwner() == null) {
            return;
        }
        IItem item = drop.getItem();
        final TimerManager tMan = TimerManager.getInstance();
        //check for reactors on map that might use this item
        for (MapleMapObject o : mapobjects.values()) {
            if (o.getType() == MapleMapObjectType.REACTOR) {
                if (((MapleReactor) o).getReactorType() == 100) {
                    if (((MapleReactor) o).getReactItem().getLeft() == item.getItemId() && ((MapleReactor) o).getReactItem().getRight() <= item.getQuantity()) {
                        Rectangle area = ((MapleReactor) o).getArea();

                        if (area.contains(drop.getPosition())) {
                            MapleClient ownerClient = drop.getOwner().getClient();
                            MapleReactor reactor = (MapleReactor) o;
                            if (!reactor.isTimerActive()) {
                                tMan.schedule(new ActivateItemReactor(drop, reactor, ownerClient), 5000);
                                reactor.setTimerActive(true);
                            }
                        }
                    }
                }
            }
        }
    }

    public void spawnMesoDrop(final int meso, final int displayMeso, Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean ffaLoot) {
        spawnMesoDrop(meso, displayMeso, position, dropper, owner, ffaLoot, false);
    }

    public void spawnMesoDrop(final int meso, final int displayMeso, Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean ffaLoot, final boolean playerDrop) {
        TimerManager tMan = TimerManager.getInstance();
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, displayMeso, droppos, dropper, owner);
        final int ownerId = ffaLoot | owner == null ? 0 : owner.getId();
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropMesoFromMapObject(displayMeso, mdrop.getObjectId(), dropper.getObjectId(), ownerId, dropper.getPosition(), droppos, (byte) 1, playerDrop));
            }
        });
        tMan.schedule(new ExpireMapItemJob(mdrop), 60000);
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 30000);
    }

    public void startMapEffect(String msg, int itemId, int time) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());
        TimerManager tMan = TimerManager.getInstance();
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                mapEffect.setActive(false);
            }
        }, 20000);
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(mapEffect.makeDestroyData());
                mapEffect = null;
            }
        }, time);
    }

    public void pqSign(boolean clear) {
        String pathA;
        String pathS;
        if (clear) {
            pathA = "quest/party/clear";
            pathS = "Party1/Clear";
        } else {
            pathA = "quest/party/wrong_kor";
            pathS = "Party1/Failed";
        }
        broadcastMessage(MaplePacketCreator.effectEnvironment(pathA, 3));
        broadcastMessage(MaplePacketCreator.effectEnvironment(pathS, 4));
    }

    public void carnivalSign(boolean win) {
        String pathA;
        String pathS;
        if (win) {
            pathA = "quest/carnival/win";
            pathS = "Cokeplay/Victory";
        } else {
            pathA = "quest/carnival/lose";
            pathS = "Cokeplay/Failed";
        }
        broadcastMessage(MaplePacketCreator.effectEnvironment(pathA, 3));
        broadcastMessage(MaplePacketCreator.effectEnvironment(pathS, 4));
    }

    public void eventSign(boolean victory) {
        String pathA;
        String pathS;
        if (victory) {
            pathA = "event/coconut/victory";
            pathS = "Coconut/Victory";
        } else {
            pathA = "event/coconut/lose";
            pathS = "Coconut/Failed";
        }
        broadcastMessage(MaplePacketCreator.effectEnvironment(pathA, 3));
        broadcastMessage(MaplePacketCreator.effectEnvironment(pathS, 4));
    }

    /**
     * Adds a player to this map and sends nescessary data
     *
     * @param chr
     */
    public void addPlayer(MapleCharacter chr) {
        synchronized (characters) {
            this.characters.add(chr);
        }
        synchronized (this.mapobjects) {
            if (!chr.isHidden()) {
                broadcastMessage(chr, (MaplePacketCreator.spawnPlayerMapobject(chr)), false);
                for (MaplePet pet : chr.getActivePets()) {
                    chr.getClient().getSession().write(MaplePacketCreator.showPet(chr, pet, false, false));
                }
            }
            sendObjectPlacement(chr.getClient());
            // spawn self
            chr.getClient().getSession().write(MaplePacketCreator.spawnPlayerMapobject(chr));
            this.mapobjects.put(Integer.valueOf(chr.getObjectId()), chr);
        }

        if (chr.isHidden()) {
            chr.getClient().getSession().write(MaplePacketCreator.sendGMHide(chr.isHidden()));
        }
        if (chr.getPlayerShop() != null) {
            addMapObject(chr.getPlayerShop());
        }

        chr.sendMacros();

        handleFieldTypes(chr.getClient());

        if (MapleTV.getInstance().hasMessage() && MapleMapInformationProvider.getInstance().isMapleTVMap(this)) {
            chr.getClient().getSession().write(MapleTV.getInstance().getCurrentMessage().getPacket());
        }

        if (getDecHP() > 0) {
            chr.startHurtHp();
        }

        chr.clearUsedPortals();

        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
            chr.getClient().getSession().write(MaplePacketCreator.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
        }

        MapleStatEffect summonStat = chr.getStatForBuff(MapleBuffStat.SUMMON);
        if (summonStat != null) {
            MapleSummon summon = chr.getSummons().get(summonStat.getSourceId());
            summon.setPosition(chr.getPosition());
            summon.sendSpawnData(chr.getClient());
            chr.addVisibleMapObject(summon);
            addMapObject(summon);
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }

        //chr.getClient().getSession().write(MaplePacketCreator.enableReports(chr.getClient().getChannelServer().allowReports()));

        if (hasClock()) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            chr.getClient().getSession().write((MaplePacketCreator.getClockTime(hour, min, second)));
        }

        chr.receivePartyMemberHP();

    }

    public void removePlayer(MapleCharacter chr) {
        //log.warn("[dc] [level2] Player {} leaves map {}", new Object[] { chr.getName(), mapid });
        synchronized (characters) {
            characters.remove(chr);
        }
        removeMapObject(Integer.valueOf(chr.getObjectId()));
        broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        for (MapleMonster monster : chr.getControlledMonsters()) {
            monster.setController(null);
            monster.setControllerHasAggro(false);
            monster.setControllerKnowsAboutAggro(false);
            updateMonsterController(monster);
        }
        chr.leaveMap();
        chr.cancelMapTimeLimitTask();
        chr.cancelHurtHp();

        for (MapleSummon summon : chr.getSummons().values()) {
            if (summon.isPuppet()) {
                chr.cancelBuffStats(MapleBuffStat.PUPPET);
            } else {
                removeMapObject(summon);
            }
        }
    }

    /**
     * Broadcast a message to everyone in the map
     *
     * @param packet
     */
    public void broadcastMessage(MaplePacket packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Nonranged. Repeat to source according to parameter.
     *
     * @param source
     * @param packet
     * @param repeatToSource
     */
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * Ranged and repeat according to parameters.
     *
     * @param source
     * @param packet
     * @param repeatToSource
     * @param ranged
     */
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource, boolean ranged) {
        broadcastMessage(repeatToSource ? null : source, packet, ranged ? MapleCharacter.MAX_VIEW_RANGE_SQ
                : Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * Always ranged from Point.
     *
     * @param packet
     * @param rangedFrom
     */
    public void broadcastMessage(MaplePacket packet, Point rangedFrom) {
        broadcastMessage(null, packet, MapleCharacter.MAX_VIEW_RANGE_SQ, rangedFrom);
    }

    /**
     * Always ranged from point. Does not repeat to source.
     *
     * @param source
     * @param packet
     * @param rangedFrom
     */
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, Point rangedFrom) {
        broadcastMessage(source, packet, MapleCharacter.MAX_VIEW_RANGE_SQ, rangedFrom);
    }

    private void broadcastMessage(MapleCharacter source, MaplePacket packet, double rangeSq, Point rangedFrom) {
        synchronized (characters) {
            for (MapleCharacter chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.getClient().getSession().write(packet);
                        }
                    } else {
                        chr.getClient().getSession().write(packet);
                    }
                }
            }
        }
    }

    private boolean isNonRangedType(MapleMapObjectType type) {
        switch (type) {
            case NPC:
            case PLAYER:
            case MIST:
                //case REACTOR:
                return true;
        }
        return false;
    }

    private void sendObjectPlacement(MapleClient mapleClient) {
        for (MapleMapObject o : mapobjects.values()) {
            if (isNonRangedType(o.getType())) {
                // make sure not to spawn a dead reactor
                // if (o.getType() == MapleMapObjectType.REACTOR) {
                // if (reactors.get((MapleReactor) o)) {
                // o.sendSpawnData(mapleClient);
                // }
                // } else
                o.sendSpawnData(mapleClient);
            } else if (o.getType() == MapleMapObjectType.MONSTER) {
                updateMonsterController((MapleMonster) o);
            }
        }
        MapleCharacter chr = mapleClient.getPlayer();

        if (chr != null) {
            for (MapleMapObject o : getMapObjectsInRange(chr.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ, rangedMapobjectTypes)) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) o).isAlive()) {
                        o.sendSpawnData(chr.getClient());
                        chr.addVisibleMapObject(o);
                    }
                } else {
                    o.sendSpawnData(chr.getClient());
                    chr.addVisibleMapObject(o);
                }
            }
        } else {
            log.info("sendObjectPlacement invoked with null char");
        }
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> types) {
        List<MapleMapObject> ret = new LinkedList<MapleMapObject>();
        for (MapleMapObject l : mapobjects.values()) {
            if (types.contains(l.getType())) {
                if (from.distanceSq(l.getPosition()) <= rangeSq) {
                    ret.add(l);
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getMapObjectsInBox(Rectangle box, List<MapleMapObjectType> types) {
        List<MapleMapObject> ret = new LinkedList<MapleMapObject>();
        synchronized (mapobjects) {
            for (MapleMapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        List<MapleMapObject> ret = new LinkedList<MapleMapObject>();
        synchronized (mapobjects) {
            for (MapleMapObject l : mapobjects.values()) {
                if (l.getType() == MapleMapObjectType.ITEM) {
                    if (from.distanceSq(l.getPosition()) <= rangeSq) {
                        ret.add(l);
                    }
                }
            }
        }
        return ret;
    }

    public void addPortal(MaplePortal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    public MaplePortal getPortal(String portalname) {
        for (MaplePortal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public MaplePortal getPortal(int portalid) {
        return portals.get(portalid);
    }

    public MaplePortal getRandomPortal() {
        int totPortals = portals.size();
        MaplePortal ret = portals.get(Randomizer.randomInt(0, totPortals - 1));
        while (ret == null) {
            ret = getRandomPortal();
        }
        return ret;
    }

    public void addMapleArea(Rectangle rec) {
        areas.add(rec);
    }

    public List<Rectangle> getAreas() {
        return new ArrayList<Rectangle>(areas);
    }

    public Rectangle getArea(int index) {
        return areas.get(index);
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public MapleFootholdTree getFootholds() {
        return footholds;
    }

    /**
     * not threadsafe, please synchronize yourself
     *
     * @param monster
     */
    public void addMonsterSpawn(MapleMonster monster, int mobTime) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime);

        monsterSpawn.add(sp);
        if (sp.shouldSpawn(MapleMap.this) || mobTime == -1) {//-1 does not respawn and should not either but force ONE spawn
            sp.spawnMonster(this);
        }
    }

    public void addSpawnPoint(Point point) {
        MapleMonster monster = MapleLifeFactory.getMonster(100100);
        SpawnPoint sp = new SpawnPoint(monster, point, 0);
        monsterSpawn.add(sp);
    }

    public void addSpawnPoints(Point[] point) {
        MapleMonster monster = MapleLifeFactory.getMonster(100100);
        SpawnPoint sp = null;
        for (int i = 0; i < point.length; i++) {
            sp = new SpawnPoint(monster, point[i], 0);
        }
        if (sp != null) {
            monsterSpawn.add(sp);
        }
    }

    public List<SpawnPoint> getMonsterSpawns() {
        return Collections.unmodifiableList(monsterSpawn);
    }

    public void spawnMonsters(int[] mobid) {
        int i = 0;
        for (SpawnPoint sp : getMonsterSpawns()) {
            if (mobid[i] < 0) {
                return;
            }
            MapleMonster mob = MapleLifeFactory.getMonster(mobid[i]);
            if (mob == null) {
                return;
            }
            spawnMonsterOnGroundBelow(mob, sp.getPosition());
            i++;
        }
    }

    public float getMonsterRate() {
        return monsterRate;
    }

    public int getSpawnedMonsters() {
        return spawnedMonstersOnMap.get();
    }

    public Collection<MapleCharacter> getCharacters() {
        return Collections.unmodifiableCollection(this.characters);
    }

    public int getPlayerCount() {
        return characters.size();
    }

    public List<MapleCharacter> getMapleCharactersNearby(Point attacker, double maxRange, double maxHeight, Collection<MapleCharacter> chr) {
        List<MapleCharacter> character = new ArrayList<MapleCharacter>();
        for (MapleCharacter a : characters) {
            if (chr.contains(a.getClient().getPlayer())) {
                Point attackedPlayer = a.getPosition();
                MaplePortal Port = a.getMap().findClosestSpawnpoint(a.getPosition());
                Point nearestPort = Port.getPosition();
                double safeDis = nearestPort.distance(nearestPort);
                double distanceX = nearestPort.distance(attackedPlayer.getX(), attackedPlayer.getY());
                if (attacker.x < attackedPlayer.x && distanceX < maxRange && distanceX > 2
                        && attackedPlayer.y >= attacker.y - maxHeight && attackedPlayer.y <= attacker.y + maxHeight && safeDis > 250) {
                    character.add(a);
                }
            }
        }
        return character;
    }

    public MapleCharacter getCharacterById(int id) {
        for (MapleCharacter c : this.characters) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
        if (!chr.isMapObjectVisible(mo)) { // monster entered view range
            if (mo.getPosition().distanceSq(chr.getPosition()) <= MapleCharacter.MAX_VIEW_RANGE_SQ) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else { // monster left view range
            if (mo.getPosition().distanceSq(chr.getPosition()) > MapleCharacter.MAX_VIEW_RANGE_SQ) {
                chr.removeVisibleMapObject(mo);
                mo.sendDestroyData(chr.getClient());
            }
        }
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        synchronized (characters) {
            for (MapleCharacter chr : characters) {
                updateMapObjectVisibility(chr, monster);
            }
        }
    }

    public void movePlayer(MapleCharacter player, Point newPosition) {
        player.setPosition(newPosition);
        Collection<MapleMapObject> visibleObjects = player.getVisibleMapObjects();
        MapleMapObject[] visibleObjectsNow = visibleObjects.toArray(new MapleMapObject[visibleObjects.size()]);
        for (MapleMapObject mo : visibleObjectsNow) {
            if (mapobjects.get(mo.getObjectId()) == mo) {
                updateMapObjectVisibility(player, mo);
            } else {
                player.removeVisibleMapObject(mo);
            }
        }
        for (MapleMapObject mo : getMapObjectsInRange(player.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ,
                rangedMapobjectTypes)) {
            if (!player.isMapObjectVisible(mo)) {
                mo.sendSpawnData(player.getClient());
                player.addVisibleMapObject(mo);
            }
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public void spawnDebug(MessageCallback mc) {
        mc.dropMessage("Spawndebug...");
        synchronized (mapobjects) {
            mc.dropMessage("Mapobjects in map = " + mapobjects.size() + " | spawnedMonstersOnMap = "
                    + spawnedMonstersOnMap + " | spawnpoints =  " + monsterSpawn.size()
                    + " | maxRegularSpawn = " + getMaxRegularSpawn());
            int numMonsters = 0;
            for (MapleMapObject mo : mapobjects.values()) {
                if (mo instanceof MapleMonster) {
                    numMonsters++;
                }
            }
            mc.dropMessage("actual monsters: " + numMonsters);
        }
    }

    private int getMaxRegularSpawn() {
        return (int) (monsterSpawn.size() / monsterRate);
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean hasClock() {
        return clock;
    }

    public void setShip(boolean isDocked, boolean isBalrog) {
        boolean doShowShip = true;
        if (shipDocked == isDocked) {
            doShowShip = false;
        }

        shipDocked = isDocked;
        shipIsBalrog = isBalrog;

        if (doShowShip) {
            this.broadcastMessage(MaplePacketCreator.showShip(false, shipDocked, shipIsBalrog));
        }
    }

    public boolean isShipDocked() {
        return shipDocked;
    }

    public boolean isShipBalrog() {
        return shipIsBalrog;
    }

    public boolean isProtectMobDamagedByMob() {
        return protectMobDamagedByMob;
    }

    public void setProtectMobDamagedByMob(boolean protectMobDamagedByMob) {
        this.protectMobDamagedByMob = protectMobDamagedByMob;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public int getFieldLimit() {
        return fieldLimit;
    }

    public void unblockPortals() {
        for (MaplePortal mp : getPortals()) {
            mp.setLocked(0);
        }
    }

    public void unblockPortal(MaplePortal mp) {
        mp.setLocked(0);
    }

    public void blockPortals(int type) {
        for (MaplePortal mp : getPortals()) {
            mp.setLocked(type);
        }
    }

    public void blockPortal(MaplePortal mp, int type) {
        mp.setLocked(type);
    }

    public boolean canSwim() {
        return swim > 0;
    }

    public int getSwim() {
        return swim;
    }

    public void setSwim(int swim) {
        this.swim = swim;
    }

    public int getContinent() {
        return continent;
    }

    public void setContinent(int continent) {
        this.continent = continent;
    }

    public boolean isEverlast() {
        return everlast > 0;
    }

    public int getEverlast() {
        return everlast;
    }

    public void setEverlast(int everlast) {
        this.everlast = everlast;
    }

    public int getDecHP() {
        return decHP;
    }

    public void setDecHP(int decHP) {
        this.decHP = decHP;
    }

    public int getProtectItem() {
        return protectItem;
    }

    public void setProtectItem(int protectItem) {
        this.protectItem = protectItem;
    }

    public int getLvLimit() {
        return lvLimit;
    }

    public void setLvLimit(int lvLimit) {
        this.lvLimit = lvLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getCurrentPartyId() {
        for (MapleCharacter chr : this.getCharacters()) {
            if (chr.getPartyId() != -1) {
                return chr.getPartyId();
            }
        }
        return -1;
    }

    public boolean checkFieldLimit(FieldLimitBits limit) {
        return (getFieldLimit() & limit.getValue()) != 0;
    }

    public void handleFieldTypes(final MapleClient c) {
        switch (fieldType) {
            case 6:
                //MiniDungeons/Timed Maps
                if (getTimeLimit() > 0 && getForcedReturn() != 999999999) {
                    c.getSession().write(MaplePacketCreator.getClock(getTimeLimit()));
                    c.getPlayer().startMapTimeLimitTask(this, c.getChannelServer().getMapFactory().getMap(getForcedReturn()));
                }
                break;
            case 82:
                //Show Forced Equips (EG. Beginner Map - Show player as apple)
                c.getSession().write(MaplePacketCreator.showForcedMapEquip());
                break;
            default:
                break;
        }
    }

    public List<Integer> getBlockedMonsterSpawns() {
        return blockedMonsterSpawns;
    }

    public void addBlockedMonsterSpawn(int monsterid) {
        if (!blockedMonsterSpawns.contains(monsterid)) {
            blockedMonsterSpawns.add(monsterid);
        }
    }

    private class ExpireMapItemJob implements Runnable {

        private final MapleMapItem mapitem;

        public ExpireMapItemJob(MapleMapItem mapitem) {
            this.mapitem = mapitem;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                synchronized (mapitem) {
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0),
                            mapitem.getPosition());
                    MapleMap.this.removeMapObject(mapitem);
                    mapitem.setPickedUp(true);
                }
            }
        }
    }

    private class ActivateItemReactor implements Runnable {

        private final MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                synchronized (mapitem) {
                    TimerManager tMan = TimerManager.getInstance();
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0),
                            mapitem.getPosition());
                    MapleMap.this.removeMapObject(mapitem);
                    reactor.hitReactor(c);
                    if (reactor.getDelay() > 0) {
                        tMan.schedule(new Runnable() {

                            @Override
                            public void run() {
                                reactor.setState((byte) 0);
                                broadcastMessage(MaplePacketCreator.triggerReactor(reactor, 0));
                            }
                        }, reactor.getDelay());
                    }
                }
            }
        }
    }

    private class RespawnWorker implements Runnable {

        @Override
        public void run() {
            int playersOnMap = characters.size();

            if (playersOnMap == 0) {
                return;
            }

            if (!spawnEnabled) {
                return;
            }

            int ispawnedMonstersOnMap = spawnedMonstersOnMap.get();
            int numShouldSpawn = (int) Math.round(Math.random() * ((2 + playersOnMap / 1.5 + (getMaxRegularSpawn() - ispawnedMonstersOnMap) / 4.0)));
            if (numShouldSpawn + ispawnedMonstersOnMap > getMaxRegularSpawn()) {
                numShouldSpawn = getMaxRegularSpawn() - ispawnedMonstersOnMap;
            }

            if (numShouldSpawn <= 0) {
                return;
            }

            // k find that many monsters that need respawning and respawn them O.o
            List<SpawnPoint> randomSpawn = new ArrayList<SpawnPoint>(monsterSpawn);

            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn(MapleMap.this)) {
                    spawnPoint.spawnMonster(MapleMap.this);
                    spawned++;
                }
                if (spawned >= numShouldSpawn) {
                    break;
                }
            }
        }
    }

    private static interface DelayedPacketCreation {

        void sendPackets(MapleClient c);
    }

    private static interface SpawnCondition {

        boolean canSpawn(MapleCharacter chr);
    }
}
