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

package net.sf.odinms.client;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.odinms.client.anticheat.CheatTracker;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.database.DatabaseException;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.scripting.event.EventInstanceManager;
import net.sf.odinms.scripting.npc.NPCScriptInfo;
import net.sf.odinms.server.GameConstants;
import net.sf.odinms.server.MapleInteractionType;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleMiniGame;
import net.sf.odinms.server.MaplePlayerShop;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.MapleStorage;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.MapleStatEffect.MapleStatEffectType;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleMonsterBanishInfo;
import net.sf.odinms.server.life.MonsterSkill;
import net.sf.odinms.server.maps.AbstractAnimatedMapleMapObject;
import net.sf.odinms.server.maps.MapleDoor;
import net.sf.odinms.server.maps.MapleGenericPortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapInformationProvider;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.server.quest.MapleCustomQuest;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.MapleRandom;
import net.sf.odinms.tools.MockIOSession;
import net.sf.odinms.tools.Pair;

import net.sf.odinms.tools.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleCharacter extends AbstractAnimatedMapleMapObject implements InventoryContainer {

    private static Logger log = LoggerFactory.getLogger(PacketProcessor.class);
    public static final double MAX_VIEW_RANGE_SQ = 850 * 850;
    private int world;
    private int accountid;
    private int rank;
    private int rankMove;
    private int jobRank;
    private int jobRankMove;
    private String name;
    private int level;
    private int str, dex, luk, int_;
    private AtomicInteger exp = new AtomicInteger();
    private int hp, maxhp;
    private int mp, maxmp;
    private int mpApUsed, hpApUsed;
    private int hair, face;
    private int startMap;
    private AtomicInteger meso = new AtomicInteger();
    private int remainingAp, remainingSp;
    private int savedLocations[];
    private int fame;
    private long lastfametime;
    private List<Integer> lastmonthfameids;
    // local stats represent current stats of the player to avoid expensive operations
    private transient int localmaxhp, localmaxmp;
    private transient int localstr, localdex, localluk, localint_;
    private transient int magic, watk;
    private transient double speedMod, jumpMod;
    private transient int localmaxbasedamage;
    private int id;
    private MapleClient client;
    private MapleMap map;
    private MapleMap lastMap;
    private int initialSpawnPoint;
    private int currentFieldKey = 0;
    // mapid is only used when calling getMapId() with map == null, it is not updated when running in channelserver mode
    private int mapid;
    //SHOPS/TRADES/STORAGES
    private MapleShop shop;
    private MaplePlayerShop playerShop;
    private MapleStorage storage;
    private MapleTrade trade;
    private MapleMiniGame miniGame;
    private MapleMiniGameStats miniGameStats = new MapleMiniGameStats();
    //ETC
    private MapleSkinColor skinColor = MapleSkinColor.NORMAL;
    private MapleJob job = MapleJob.BEGINNER;
    private MapleGender gender = MapleGender.NULL;
    private int gmLevel;
    private boolean hidden;
    private boolean canDoor = true;
    private int muted;
    private int chair;
    private int itemEffect;
    private int expCardRate = 1;
    private int[] teleportMaps = new int[5];
    private int[] vipTeleportMaps = new int[10];
    private MapleRandom random = new MapleRandom();
    private NPCScriptInfo npcScriptInfo;
    //PARTY
    private MapleParty party;
    private boolean partyInvited = false;
    //EVENT
    private EventInstanceManager eventInstance;
    //INVENTORY
    private MapleInventory[] inventory;
    //QUESTS
    private Map<MapleQuest, MapleQuestStatus> quests;
    private MapleQuestRecord questRecord;
    private Set<MapleMonster> controlled = new LinkedHashSet<MapleMonster>();
    private Set<MapleMapObject> visibleMapObjects = new LinkedHashSet<MapleMapObject>();
    private Map<Integer, MapleKeyBinding> keymap = new LinkedHashMap<Integer, MapleKeyBinding>();
    private List<MapleDoor> doors = new ArrayList<MapleDoor>();
    private BuddyList buddylist;
    private List<MapleGenericPortal> usedPortals = new ArrayList<MapleGenericPortal>();
    //SKILLS
    private Map<ISkill, SkillEntry> skills = new LinkedHashMap<ISkill, SkillEntry>();
    private Map<MapleBuffStat, MapleBuffStatValueHolder> effects = new LinkedHashMap<MapleBuffStat, MapleBuffStatValueHolder>();
    private Map<Integer, MapleCoolDownValueHolder> cooldowns = new LinkedHashMap<Integer, MapleCoolDownValueHolder>();
    private Map<Integer, MapleSummon> summons = new LinkedHashMap<Integer, MapleSummon>();
    private MapleMacro[] skillMacros = new MapleMacro[5];
    private List<MapleBuffStat> diseases = new ArrayList<MapleBuffStat>();//possible bug when removing diseases
    private int buffCount = 0;
    private ScheduledFuture<?> hpDecreaseTask;
    private ScheduledFuture<?> mapTimeLimitTask;
    private ScheduledFuture<?> dragonBloodSchedule;
    //ANTICHEAT
    private CheatTracker anticheat;
    //GUILD
    private int guildid;
    private int guildrank;
    private MapleGuildCharacter mgc;
    //MAPLEMESSENGER
    private MapleMessenger messenger;
    private int messengerPosition = 4;
    //CASHSHOP
    private int nxcash;
    private int maplepoints;
    private int gifttokens;
    private boolean incs;
    private int[] wishList = new int[10];
    //PETS
    private MaplePet[] pets = new MaplePet[3];
    private ScheduledFuture<?>[] fullnessSchedule = new ScheduledFuture<?>[3];
    //MOUNTS
    private MapleMount mount;
    private ScheduledFuture<?> tirednessSchedule;
    //MONSTERBOOK
    private MapleMonsterBook monsterbook;
    //MARRIAGE
    private boolean married = false;
    List<MapleCharacterObject> characterObjects = new ArrayList<MapleCharacterObject>();

    private NumberFormat nf = new DecimalFormat("#,###,###,###");

    private MapleCharacter() {
        setStance(0);

        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type, (byte) 100);
        }

        savedLocations = new int[SavedLocationType.values().length];
        Arrays.fill(savedLocations, -1);

        Arrays.fill(teleportMaps, 999999999);
        Arrays.fill(vipTeleportMaps, 999999999);

        quests = new LinkedHashMap<MapleQuest, MapleQuestStatus>();
        anticheat = new CheatTracker(this);
        setPosition(new Point(0, 0));
    }

    /**
     * Loads a character from the Database.
     * @param charid
     * @param client
     * @param channelserver
     * @return MapleCharacter
     * @throws java.sql.SQLException
     */
    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) throws SQLException {
        MapleCharacter ret = new MapleCharacter();
        ret.client = client;
        ret.id = charid;

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?"); //TODO BIG BUG HERE - people can hack into characters
        ps.setInt(1, charid);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new RuntimeException("Loading the Char Failed (char not found)");
        }
        ret.name = rs.getString("name");
        ret.level = rs.getInt("level");
        ret.fame = rs.getInt("fame");
        ret.str = rs.getInt("str");
        ret.dex = rs.getInt("dex");
        ret.int_ = rs.getInt("int");
        ret.luk = rs.getInt("luk");
        ret.exp.set(rs.getInt("exp"));

        ret.hp = rs.getInt("hp");
        ret.maxhp = rs.getInt("maxhp");
        ret.mp = rs.getInt("mp");
        ret.maxmp = rs.getInt("maxmp");

        ret.hpApUsed = rs.getInt("hpApUsed");
        ret.mpApUsed = rs.getInt("mpApUsed");

        ret.remainingSp = rs.getInt("sp");
        ret.remainingAp = rs.getInt("ap");

        ret.meso.set(rs.getInt("meso"));

        ret.gmLevel = rs.getInt("gm");

        ret.skinColor = MapleSkinColor.getById(rs.getInt("skincolor"));
        ret.gender = MapleGender.getById(rs.getInt("gender"));
        ret.job = MapleJob.getById(rs.getInt("job"));

        ret.hair = rs.getInt("hair");
        ret.face = rs.getInt("face");

        ret.accountid = rs.getInt("accountid");

        ret.mapid = rs.getInt("map");
        ret.initialSpawnPoint = rs.getInt("spawnpoint");
        ret.world = rs.getInt("world");

        ret.muted = rs.getInt("muted");

        ret.rank = rs.getInt("rank");
        ret.rankMove = rs.getInt("rankMove");
        ret.jobRank = rs.getInt("jobRank");
        ret.jobRankMove = rs.getInt("jobRankMove");

        ret.guildid = rs.getInt("guildid");
        ret.guildrank = rs.getInt("guildrank");
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }

        ret.miniGameStats.setWins(rs.getInt("minigame_O_Win"), MapleInteractionType.OMOK_GAME);
        ret.miniGameStats.setTies(rs.getInt("minigame_O_Tie"), MapleInteractionType.OMOK_GAME);
        ret.miniGameStats.setLosses(rs.getInt("minigame_O_Loss"), MapleInteractionType.OMOK_GAME);
        ret.miniGameStats.setWins(rs.getInt("minigame_MC_Win"), MapleInteractionType.MATCH_CARD_GAME);
        ret.miniGameStats.setTies(rs.getInt("minigame_MC_Tie"), MapleInteractionType.MATCH_CARD_GAME);
        ret.miniGameStats.setLosses(rs.getInt("minigame_MC_Loss"), MapleInteractionType.MATCH_CARD_GAME);
        ret.miniGameStats.setPoints(rs.getInt("minigame_Points"));

        int buddyCapacity = rs.getInt("buddyCapacity");
        ret.buddylist = new BuddyList(buddyCapacity);
        ret.characterObjects.add(ret.buddylist);

        ret.monsterbook = new MapleMonsterBook(ret);
        ret.monsterbook.setCover(rs.getInt("monsterbookCover"));
        ret.characterObjects.add(ret.monsterbook);

        if (channelserver) {
            MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                log.warn("CID:" + ret.getId() + " is on a null map, going to henesys - LOADFROMDB");
                ret.map = mapFactory.getMap(100000000);
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            int partyid = rs.getInt("party");
            if (partyid >= 0) {
                try {
                    MapleParty party = client.getChannelServer().getWorldInterface().getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                } catch (RemoteException e) {
                    client.getChannelServer().reconnectWorld();
                }
            }
        }

        rs.close();
        ps.close();


        ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
        ps.setInt(1, ret.accountid);
        rs = ps.executeQuery();
        while (rs.next()) {
            ret.getClient().setAccountName(rs.getString("name"));
            ret.nxcash = rs.getInt("nxCash");
            ret.maplepoints = rs.getInt("mPoints");
            ret.gifttokens = rs.getInt("gTokens");
        }
        rs.close();
        ps.close();

        String sql = "SELECT * FROM inventoryitems " + "LEFT JOIN inventoryequipment USING (inventoryitemid) " + "WHERE characterid = ?";
        if (!channelserver) {
            sql += " AND inventorytype = " + MapleInventoryType.EQUIPPED.getType();
        }
        ps = con.prepareStatement(sql);
        ps.setInt(1, charid);
        // PreparedStatement itemLog = con.prepareStatement("SELECT msg FROM inventorylog WHERE inventoryitemid = ?");
        rs = ps.executeQuery();
        while (rs.next()) {
            MapleInventoryType type = MapleInventoryType.getByType((byte) rs.getInt("inventorytype"));
            // itemLog.setInt(1, rs.getInt("inventoryitemid"));
            // ResultSet rsItemLog = itemLog.executeQuery();
            // IItem logItem;
            if (type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.EQUIPPED)) {
                int itemid = rs.getInt("itemid");
                Equip equip = new Equip(itemid, (byte) rs.getInt("position"));
                equip.setOwner(rs.getString("owner"));
                equip.setQuantity((short) rs.getInt("quantity"));
                equip.setAcc((short) rs.getInt("acc"));
                equip.setAvoid((short) rs.getInt("avoid"));
                equip.setDex((short) rs.getInt("dex"));
                equip.setHands((short) rs.getInt("hands"));
                equip.setHp((short) rs.getInt("hp"));
                equip.setInt((short) rs.getInt("int"));
                equip.setJump((short) rs.getInt("jump"));
                equip.setLuk((short) rs.getInt("luk"));
                equip.setMatk((short) rs.getInt("matk"));
                equip.setMdef((short) rs.getInt("mdef"));
                equip.setMp((short) rs.getInt("mp"));
                equip.setSpeed((short) rs.getInt("speed"));
                equip.setStr((short) rs.getInt("str"));
                equip.setWatk((short) rs.getInt("watk"));
                equip.setWdef((short) rs.getInt("wdef"));
                equip.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                equip.setLevel((byte) rs.getInt("level"));
                ret.getInventory(type).addFromDB(equip);
                // logItem = equip;
            } else {
                Item item = new Item(rs.getInt("itemid"), (byte) rs.getInt("position"), (short) rs.getInt("quantity"), rs.getInt("petid"));
                item.setOwner(rs.getString("owner"));
                item.setMask((byte) rs.getInt("mask"));
                ret.getInventory(type).addFromDB(item);
                // logItem = item;
            }
        }
        rs.close();
        ps.close();
        // itemLog.close();

        if (channelserver) {
            ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            PreparedStatement pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?");
            while (rs.next()) {
                MapleQuest q = MapleQuest.getInstance(rs.getInt("quest"));
                MapleQuestStatus status = new MapleQuestStatus(q, MapleQuestStatus.Status.getById(rs.getInt("status")));
                long cTime = rs.getLong("time");
                if (cTime > -1) {
                    status.setCompletionTime(cTime * 1000);
                }
                status.setForfeited(rs.getInt("forfeited"));
                ret.quests.put(q, status);
                pse.setInt(1, rs.getInt("queststatusid"));
                ResultSet rsMobs = pse.executeQuery();
                while (rsMobs.next()) {
                    status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                }
                rsMobs.close();
            }
            rs.close();
            ps.close();
            pse.close();

            ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel FROM skills WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.skills.put(SkillFactory.getSkill(rs.getInt("skillid")), new SkillEntry(rs.getInt("skilllevel"), rs.getInt("masterlevel")));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int key = rs.getInt("key");
                int type = rs.getInt("type");
                int action = rs.getInt("action");
                ret.keymap.put(Integer.valueOf(key), new MapleKeyBinding(type, action));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                String locationType = rs.getString("locationtype");
                int mapid = rs.getInt("map");
                ret.savedLocations[SavedLocationType.valueOf(locationType).ordinal()] = mapid;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM wishlists WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < 10; i++) {
                    ret.wishList[i] = rs.getInt("item" + (i + 1));
                }
            }
            rs.close();
            ps.close();

            ps = con.prepareCall("SELECT * FROM teleportmaps WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int teleportMap = rs.getInt("mapid");
                if (teleportMap == 999999999) {
                    continue;
                }
                if (rs.getInt("source") == 0) {
                    ret.teleportMaps[rs.getInt("pos")] = teleportMap;
                } else {
                    ret.vipTeleportMaps[rs.getInt("pos")] = teleportMap;
                }
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            ret.lastfametime = 0;
            ret.lastmonthfameids = new ArrayList<Integer>(31);
            while (rs.next()) {
                ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                ret.lastmonthfameids.add(Integer.valueOf(rs.getInt("characterid_to")));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int skill1 = rs.getInt("skill1");
                int skill2 = rs.getInt("skill2");
                int skill3 = rs.getInt("skill3");
                String name = rs.getString("name");
                int shout = rs.getInt("shout");
                int position = rs.getInt("pos");
                MapleMacro macro = new MapleMacro(position, skill1, skill2, skill3, shout, name);
                ret.skillMacros[position] = macro;
            }
            ps.close();
            rs.close();

            ret.storage = MapleStorage.loadOrCreateFromDB(ret.accountid);
            ret.characterObjects.add(ret.storage);

            ret.questRecord = new MapleQuestRecord();
            ret.characterObjects.add(ret.questRecord);

            for (MapleCharacterObject characterObject : ret.characterObjects) {
                if (characterObject != null) {
                    try {
                        if (characterObject.getType() != MapleCharacterObjectType.STORAGE) {
                            characterObject.loadFromDB(charid);
                        }
                    } catch (Exception e) {
                        log.error("Error loading " + characterObject.getType().name(), e);
                    }
                }
            }

            if (ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null) {
                ret.mount = new MapleMount(ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18).getItemId(), 1004, ret);
                //TODO: Save/load mount data
                //ret.mount.setExp(mountexp);
                //ret.mount.setLevel(mountlevel);
                //ret.mount.setTiredness(mounttiredness);
            }
        }

        ret.recalcLocalStats();
        ret.silentEnforceMaxHpMp();
        return ret;
    }

    /**
     * Gets a default MapleCharacter
     * @param client
     * @param chrid
     * @return Maplecharacter
     */
    public static MapleCharacter getDefault(MapleClient client, int chrid) {
        MapleCharacter ret = getDefault(client);
        ret.id = chrid;
        return ret;
    }

    /**
     * Gets a default MapleCharacter
     * @param client
     * @return Maplecharacter
     */
    public static MapleCharacter getDefault(MapleClient client) {
        MapleCharacter ret = new MapleCharacter();
        ret.client = client;
        ret.hp = 50;
        ret.maxhp = 50;
        ret.mp = 50;
        ret.maxmp = 50;
        ret.map = null;
        // ret.map = ChannelServer.getInstance(client.getChannel()).getMapFactory().getMap(0);
        ret.exp.set(0);
        ret.gmLevel = 0;
        ret.job = MapleJob.BEGINNER;
        ret.meso.set(0);
        ret.level = 1;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList(25);
        ret.muted = 0;
        ret.nxcash = 0;
        ret.maplepoints = 0;
        ret.gifttokens = 0;
        ret.incs = false;

        ret.keymap.put(Integer.valueOf(18), new MapleKeyBinding(4, 0));
        ret.keymap.put(Integer.valueOf(65), new MapleKeyBinding(6, 106));
        ret.keymap.put(Integer.valueOf(2), new MapleKeyBinding(4, 10));
        ret.keymap.put(Integer.valueOf(23), new MapleKeyBinding(4, 1));
        ret.keymap.put(Integer.valueOf(3), new MapleKeyBinding(4, 12));
        ret.keymap.put(Integer.valueOf(4), new MapleKeyBinding(4, 13));
        ret.keymap.put(Integer.valueOf(5), new MapleKeyBinding(4, 18));
        ret.keymap.put(Integer.valueOf(6), new MapleKeyBinding(4, 21));
        ret.keymap.put(Integer.valueOf(16), new MapleKeyBinding(4, 8));
        ret.keymap.put(Integer.valueOf(17), new MapleKeyBinding(4, 5));
        ret.keymap.put(Integer.valueOf(19), new MapleKeyBinding(4, 4));
        ret.keymap.put(Integer.valueOf(25), new MapleKeyBinding(4, 19));
        ret.keymap.put(Integer.valueOf(26), new MapleKeyBinding(4, 14));
        ret.keymap.put(Integer.valueOf(27), new MapleKeyBinding(4, 15));
        ret.keymap.put(Integer.valueOf(29), new MapleKeyBinding(5, 52));
        ret.keymap.put(Integer.valueOf(31), new MapleKeyBinding(4, 2));
        ret.keymap.put(Integer.valueOf(34), new MapleKeyBinding(4, 17));
        ret.keymap.put(Integer.valueOf(35), new MapleKeyBinding(4, 11));
        ret.keymap.put(Integer.valueOf(37), new MapleKeyBinding(4, 3));
        ret.keymap.put(Integer.valueOf(38), new MapleKeyBinding(4, 20));
        ret.keymap.put(Integer.valueOf(40), new MapleKeyBinding(4, 16));
        ret.keymap.put(Integer.valueOf(43), new MapleKeyBinding(4, 9));
        ret.keymap.put(Integer.valueOf(44), new MapleKeyBinding(5, 50));
        ret.keymap.put(Integer.valueOf(45), new MapleKeyBinding(5, 51));
        ret.keymap.put(Integer.valueOf(46), new MapleKeyBinding(4, 6));
        ret.keymap.put(Integer.valueOf(50), new MapleKeyBinding(4, 7));
        ret.keymap.put(Integer.valueOf(56), new MapleKeyBinding(5, 53));
        ret.keymap.put(Integer.valueOf(59), new MapleKeyBinding(6, 100));
        ret.keymap.put(Integer.valueOf(60), new MapleKeyBinding(6, 101));
        ret.keymap.put(Integer.valueOf(61), new MapleKeyBinding(6, 102));
        ret.keymap.put(Integer.valueOf(62), new MapleKeyBinding(6, 103));
        ret.keymap.put(Integer.valueOf(63), new MapleKeyBinding(6, 104));
        ret.keymap.put(Integer.valueOf(64), new MapleKeyBinding(6, 105));

        ret.recalcLocalStats();

        return ret;
    }

    /**
     * Saves character to the Databse
     * @param update
     */
    public void saveToDB(boolean update) {
        Connection con = DatabaseConnection.getConnection();
        try {

            // clients should not be able to log back before their old state is saved (see MapleClient#getLoginState) so we are save to switch to a very low isolation level here
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            // connections are thread local now, no need to
            // synchronize anymore =)
            con.setAutoCommit(false);
            PreparedStatement ps;


            if (update) {/////////////////////////////////////
                ps = con.prepareStatement("UPDATE characters " + "SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, " + "exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, " + "gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, map = ?, " + "meso = ?, hpApUsed = ?, mpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, muted = ?, messengerid = ?, messengerposition = ?, minigame_O_Win = ?, minigame_O_Tie = ?, minigame_O_Loss = ?, minigame_MC_Win = ?, minigame_MC_Tie = ?, minigame_MC_Loss = ?, minigame_Points = ?, monsterbookCover = ? WHERE id = ?");

                ps.setInt(1, level);
                ps.setInt(2, fame);
                ps.setInt(3, str);
                ps.setInt(4, dex);
                ps.setInt(5, luk);
                ps.setInt(6, int_);
                ps.setInt(7, exp.get());
                ps.setInt(8, hp);
                ps.setInt(9, mp);
                ps.setInt(10, maxhp);
                ps.setInt(11, maxmp);
                ps.setInt(12, remainingSp);
                ps.setInt(13, remainingAp);
                ps.setInt(14, gmLevel);
                ps.setInt(15, skinColor.getId());
                ps.setInt(16, gender.getValue());
                ps.setInt(17, job.getId());
                ps.setInt(18, hair);
                ps.setInt(19, face);
                if (map == null) {
                    ps.setInt(20, 0);
                } else {
                    if (map.getForcedReturn() != 999999999) {
                        ps.setInt(20, map.getForcedReturn());
                    } else {
                        ps.setInt(20, map.getId());
                    }
                }
                ps.setInt(21, meso.get());
                ps.setInt(22, hpApUsed);
                ps.setInt(23, mpApUsed);
                if (map == null) {
                    ps.setInt(24, 0);
                } else {
                    MaplePortal closest = map.findClosestSpawnpoint(getPosition());
                    if (closest != null) {
                        ps.setInt(24, closest.getId());
                    } else {
                        ps.setInt(24, 0);
                    }
                }
                if (party != null) {
                    ps.setInt(25, party.getId());
                } else {
                    ps.setInt(25, -1);
                }
                ps.setInt(26, buddylist.getCapacity());
                ps.setInt(27, muted);
                ps.setInt(28, messenger != null ? messenger.getId() : 0);
                ps.setInt(29, messenger != null ? messengerPosition : 4);
                ps.setInt(30, miniGameStats.getWins(MapleInteractionType.OMOK_GAME));
                ps.setInt(31, miniGameStats.getTies(MapleInteractionType.OMOK_GAME));
                ps.setInt(32, miniGameStats.getLosses(MapleInteractionType.OMOK_GAME));
                ps.setInt(33, miniGameStats.getWins(MapleInteractionType.MATCH_CARD_GAME));
                ps.setInt(34, miniGameStats.getTies(MapleInteractionType.MATCH_CARD_GAME));
                ps.setInt(35, miniGameStats.getLosses(MapleInteractionType.MATCH_CARD_GAME));
                ps.setInt(36, miniGameStats.getPoints());
                ps.setInt(37, monsterbook.getCover());
                ps.setInt(38, id);




            } else {//////////////////////////////////////////////////////////////////////
                ps = con.prepareStatement("INSERT INTO characters (" + "level, fame, str, dex, luk, `int`, exp, hp, mp, " + "maxhp, maxmp, sp, ap, gm, skincolor, gender, job, hair, face, map, meso, hpApUsed, mpApUsed, spawnpoint, party, buddyCapacity, accountid, name, world" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                ps.setInt(1, level);
                ps.setInt(2, fame);
                ps.setInt(3, str);
                ps.setInt(4, dex);
                ps.setInt(5, luk);
                ps.setInt(6, int_);
                ps.setInt(7, exp.get());
                ps.setInt(8, hp);
                ps.setInt(9, mp);
                ps.setInt(10, maxhp);
                ps.setInt(11, maxmp);
                ps.setInt(12, remainingSp);
                ps.setInt(13, remainingAp);
                ps.setInt(14, gmLevel);
                ps.setInt(15, skinColor.getId());
                ps.setInt(16, gender.getValue());
                ps.setInt(17, job.getId());
                ps.setInt(18, hair);
                ps.setInt(19, face);
                if (map == null) {
                    ps.setInt(20, getStartMap());
                } else {
                    if (map.getForcedReturn() != 999999999) {
                        ps.setInt(20, map.getForcedReturn());
                    } else {
                        ps.setInt(20, map.getId());
                    }
                }
                ps.setInt(21, meso.get());
                ps.setInt(22, hpApUsed);
                ps.setInt(23, mpApUsed);
                if (map == null) {
                    ps.setInt(24, 0);
                } else {
                    MaplePortal closest = map.findClosestSpawnpoint(getPosition());
                    if (closest != null) {
                        ps.setInt(24, closest.getId());
                    } else {
                        ps.setInt(24, 0);
                    }
                }
                if (party != null) {
                    ps.setInt(25, party.getId());
                } else {
                    ps.setInt(25, -1);
                }
                ps.setInt(26, buddylist.getCapacity());
                ps.setInt(27, accountid);
                ps.setString(28, name);
                ps.setInt(29, world); // TODO store world somewhere ;)
            }/////////////////////////////////////

            int updateRows = ps.executeUpdate();
            if (!update) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    this.id = rs.getInt(1);
                } else {
                    throw new DatabaseException("Inserting char failed.");
                }
            } else if (updateRows < 1) {
                throw new DatabaseException("Character not in database (" + id + ")");
            }
            ps.close();

            for (MaplePet pet : pets) {
                if (pet != null) {
                    pet.saveToDb();
                }
            }


            ps = con.prepareStatement("DELETE FROM inventoryitems WHERE characterid = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO inventoryitems" + "(characterid, itemid, inventorytype, position, quantity, owner, petid, mask) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement pse = con.prepareStatement("INSERT INTO inventoryequipment " + "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            // PreparedStatement psl = con.prepareStatement("INSERT INTO inventorylog " + "VALUES (DEFAULT, ?, ?)");
            for (MapleInventory iv : inventory) {
                ps.setInt(3, iv.getType().getType());
                for (IItem item : iv.list()) {
                    // ps.setInt(1, item.getId());
                    ps.setInt(1, id);
                    ps.setInt(2, item.getItemId());
                    ps.setInt(4, item.getPosition());
                    ps.setInt(5, item.getQuantity());
                    ps.setString(6, item.getOwner());
                    ps.setInt(7, item.getPetId());
                    ps.setInt(8, item.getMask());
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    int itemid;
                    if (rs.next()) {
                        itemid = rs.getInt(1);
                    } else {
                        throw new DatabaseException("Inserting char failed.");
                    }

                    if (iv.getType().equals(MapleInventoryType.EQUIP)
                            || iv.getType().equals(MapleInventoryType.EQUIPPED)) {
                        pse.setInt(1, itemid);
                        IEquip equip = (IEquip) item;
                        pse.setInt(2, equip.getUpgradeSlots());
                        pse.setInt(3, equip.getLevel());
                        pse.setInt(4, equip.getStr());
                        pse.setInt(5, equip.getDex());
                        pse.setInt(6, equip.getInt());
                        pse.setInt(7, equip.getLuk());
                        pse.setInt(8, equip.getHp());
                        pse.setInt(9, equip.getMp());
                        pse.setInt(10, equip.getWatk());
                        pse.setInt(11, equip.getMatk());
                        pse.setInt(12, equip.getWdef());
                        pse.setInt(13, equip.getMdef());
                        pse.setInt(14, equip.getAcc());
                        pse.setInt(15, equip.getAvoid());
                        pse.setInt(16, equip.getHands());
                        pse.setInt(17, equip.getSpeed());
                        pse.setInt(18, equip.getJump());
                        pse.executeUpdate();
                    }
                }
            }
            ps.close();
            pse.close();

            ps = con.prepareStatement("REPLACE INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`) "
                    + " VALUES (DEFAULT, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, id);
            for (MapleQuestStatus q : quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus().getId());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                for (int mob : q.getMobKills().keySet()) {
                    pse.setInt(1, rs.getInt(1));
                    pse.setInt(2, mob);
                    pse.setInt(3, q.getMobKills(mob));
                    pse.executeUpdate();
                }
                rs.close();
            }
            ps.close();
            pse.close();


            deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
                ps.setInt(2, skill.getKey().getId());
                ps.setInt(3, skill.getValue().skillevel);
                ps.setInt(4, skill.getValue().masterlevel);//?
                ps.executeUpdate();
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (Entry<Integer, MapleKeyBinding> keybinding : keymap.entrySet()) {
                ps.setInt(2, keybinding.getKey().intValue());
                ps.setInt(3, keybinding.getValue().getType());
                ps.setInt(4, keybinding.getValue().getAction());
                ps.executeUpdate();
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM wishlists WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO wishlists (characterid, item1, item2, item3, item4, item5, item6, item7, item8, item9, item10) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            for (int i = 0; i < 10; i++) {
                ps.setInt(i + 2, wishList[i]);
            }
            ps.executeUpdate();
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM teleportmaps WHERE characterid = ?");
            ps = con.prepareCall("INSERT INTO teleportmaps (characterid, pos, source, mapid) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (int i = 0; i < 5; i++) {
                int teleportMap = teleportMaps[i];
                if (teleportMap == 999999999) {
                    continue;
                }
                ps.setInt(2, i);
                ps.setInt(3, 0);
                ps.setInt(4, teleportMap);
                ps.executeUpdate();
            }
            for (int i = 0; i < 10; i++) {
                int teleportMap = vipTeleportMaps[i];
                if (teleportMap == 999999999) {
                    continue;
                }
                ps.setInt(2, i);
                ps.setInt(3, 1);
                ps.setInt(4, teleportMap);
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            for (SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (savedLocations[savedLocationType.ordinal()] != -1) {
                    ps.setString(2, savedLocationType.name());
                    ps.setInt(3, savedLocations[savedLocationType.ordinal()]);
                    ps.executeUpdate();
                }
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO skillmacros (characterid, pos, name, shout, skill1, skill2, skill3) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            for (MapleMacro macro : skillMacros) {
                if (macro != null) {
                    ps.setInt(2, macro.getPos());
                    ps.setString(3, macro.getName());
                    ps.setInt(4, macro.getShout());
                    ps.setInt(5, macro.getSkill1());
                    ps.setInt(6, macro.getSkill2());
                    ps.setInt(7, macro.getSkill3());
                    ps.executeUpdate();
                }
            }
            ps.close();

            ps = con.prepareStatement("UPDATE accounts SET `nxCash` = ?, `mPoints` = ?, `gTokens` = ? WHERE id = ?");
            ps.setInt(1, nxcash);
            ps.setInt(2, maplepoints);
            ps.setInt(3, gifttokens);
            ps.setInt(4, client.getAccID());
            ps.executeUpdate();
            ps.close();

            for (MapleCharacterObject characterObject : characterObjects) {
                if (characterObject != null) {
                    try {
                        characterObject.saveToDB(this);
                    } catch (Exception e) {
                        log.error("Error saving " + characterObject.getType().name(), e);
                    }
                }
            }

            con.commit();
        } catch (Exception e) {
            log.error(MapleClient.getLogMessage(this, "[charsave] Error saving character data"), e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                log.error(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back"), e);
            }
        } finally {
            try {
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                log.error(MapleClient.getLogMessage(this, "[charsave] Error going back to autocommit mode"), e);
            }
        }
    }

    /**
     * Deletes a characterId
     * @param con
     * @param sql
     * @throws java.sql.SQLException
     */
    public void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Gets a MapleQuestStats
     * @param quest
     * @return status
     */
    public MapleQuestStatus getQuest(MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
        }
        return quests.get(quest);
    }

    /**
     * geta a MapleQuestStatus
     * @param questid The QuestID to get the status of.
     * @return the questStatus of the questid
     */
    public MapleQuestStatus getQuestStatus(int questid) {
        return getQuest(MapleQuest.getInstance(questid));
    }

    /**
     * Updates a quest
     * @param quest
     */
    public void updateQuest(MapleQuestStatus quest) {
        quests.put(quest.getQuest(), quest);
        if (!(quest.getQuest() instanceof MapleCustomQuest)) {
            if (quest.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
                client.getSession().write(MaplePacketCreator.startQuest(this, (short) quest.getQuest().getId()));
                client.getSession().write(MaplePacketCreator.updateQuestInfo(this, (short) quest.getQuest().getId(), quest.getNpc(), (byte) 8));
            } else if (quest.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
                client.getSession().write(MaplePacketCreator.completeQuest(this, (short) quest.getQuest().getId(), quest.getCompletionTime()));
                client.getSession().write(MaplePacketCreator.showOwnFinishQuest());
                getMap().broadcastMessage(this, MaplePacketCreator.showFinishQuest(this), false);
            } else if (quest.getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
                client.getSession().write(MaplePacketCreator.forfeitQuest(this, (short) quest.getQuest().getId()));
            }
        }
    }

    /**
     * Gets a characterId from a characterName
     * @param name
     * @param world
     * @return CharacterId
     */
    public static int getIdByName(String name, int world) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("SELECT id FROM characters WHERE name = ? AND world = ?");
            ps.setString(1, name);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                return -1;
            }
            int id = rs.getInt("id");
            ps.close();
            return id;
        } catch (SQLException e) {
            log.error("ERROR", e);
        }
        return -1;
    }

    public static boolean isNameExisting(String name) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("SELECT COUNT(*) FROM characters WHERE name = ?");
            ps.setString(1, name);
            int ret = ps.executeUpdate();
            ps.close();
            return ret > 0;
        } catch (SQLException e) {
            log.error("SQL ERROR", e);
            return false;
        }
    }

    /**
     * Gets the value of a buff
     * @param effect
     * @return value of buff
     */
    public Integer getBuffedValue(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return Integer.valueOf(mbsvh.value);
    }

    /**
     * Gets Buff from
     * @param stat
     * @param skill
     * @return isBuffFrom
     */
    public boolean isBuffFrom(MapleBuffStat stat, ISkill skill) {
        MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return false;
        }
        return mbsvh.effect.getStatType() == MapleStatEffect.MapleStatEffectType.PLAYER_SKILL && mbsvh.effect.getSourceId() == skill.getId();
    }

    /**
     * Gets the source of the Buff
     * @param stat
     * @return sourceId
     */
    public int getBuffSource(MapleBuffStat stat) {
        MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return -1;
        }
        return mbsvh.effect.getSourceId();
    }

    public void setBuffedValue(MapleBuffStat effect, int value) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public Long getBuffedStarttime(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return Long.valueOf(mbsvh.startTime);
    }

    public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect;
    }

    private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
        if (dragonBloodSchedule != null) {
            dragonBloodSchedule.cancel(false);
        }
        dragonBloodSchedule = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                addHP(-bloodEffect.getX());
                getClient().getSession().write(MaplePacketCreator.showOwnBuffEffectAuto(bloodEffect.getSourceId()));
                getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffEffectAuto(getId(), bloodEffect.getSourceId()), false);
            }
        }, 4000, 4000);
    }

    public void startFullnessSchedule(final int decrease, final MaplePet pet, int petSlot) {
        ScheduledFuture<?> schedule = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                int newFullness = pet.getFullness() - decrease;
                if (newFullness <= 5) {
                    pet.setFullness(15);
                    unequipPet(pet, true, true);
                } else {
                    pet.setFullness(newFullness);
                    pet.update();
                }
            }
        }, 60000, 60000);
        fullnessSchedule[petSlot] = schedule;
    }

    public void cancelFullnessSchedule(int petSlot) {
        if (fullnessSchedule[petSlot] != null) {
            fullnessSchedule[petSlot].cancel(false);
        }
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule) {
        if (effect.isDragonBlood()) {
            prepareDragonBlood(effect);
        }
        for (Pair<MapleBuffStat, Integer> statup : effect.getStatups()) {
            effects.put(statup.getLeft(), new MapleBuffStatValueHolder(effect, starttime, schedule, statup.getRight().intValue()));
        }

        recalcLocalStats();
    }

    private List<MapleBuffStat> getBuffStats(MapleStatEffect effect, long startTime) {
        List<MapleBuffStat> stats = new ArrayList<MapleBuffStat>();
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : effects.entrySet()) {
            MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime)) {
                stats.add(stateffect.getKey());
            }
        }
        return stats;
    }

    private void deregisterBuffStats(List<MapleBuffStat> stats) {
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<MapleBuffStatValueHolder>(stats.size());
        for (MapleBuffStat stat : stats) {
            MapleBuffStatValueHolder mbsvh = effects.get(stat);
            if (mbsvh != null) {
                effects.remove(stat);
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == MapleBuffStat.SUMMON || stat == MapleBuffStat.PUPPET) {
                    int summonId = mbsvh.effect.getSourceId();
                    MapleSummon summon = summons.get(summonId);
                    if (summon != null) {
                        getMap().broadcastMessage(MaplePacketCreator.removeSpecialMapObject(summon, true), summon.getPosition());
                        getMap().removeMapObject(summon);
                        removeVisibleMapObject(summon);
                        summons.remove(summonId);
                    }
                } else if (stat == MapleBuffStat.DRAGONBLOOD) {
                    dragonBloodSchedule.cancel(false);
                    dragonBloodSchedule = null;
                }
            }
        }
        for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty()) {
                cancelEffectCancelTasks.schedule.cancel(false);
            }
        }
    }

    /**
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime) {
        List<MapleBuffStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            List<Pair<MapleBuffStat, Integer>> statups = effect.getStatups();
            buffstats = new ArrayList<MapleBuffStat>(statups.size());
            for (Pair<MapleBuffStat, Integer> statup : statups) {
                buffstats.add(statup.getLeft());
            }
        }
        deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            // remove for all on maps
            if (!getDoors().isEmpty()) {
                MapleDoor door = getDoors().iterator().next();
                for (MapleCharacter chr : door.getTarget().getCharacters()) {
                    door.sendDestroyData(chr.getClient());
                }
                for (MapleCharacter chr : door.getTown().getCharacters()) {
                    door.sendDestroyData(chr.getClient());
                }
                for (MapleDoor destroyDoor : getDoors()) {
                    door.getTarget().removeMapObject(destroyDoor);
                    door.getTown().removeMapObject(destroyDoor);
                }
                clearDoors();
                silentPartyUpdate();
            }
        }

        if (effect.isMonsterRiding()) {
            //DEMOUNT
        }

        // check if we are still logged in O.o
        if (!overwrite) {
            cancelPlayerBuffs(buffstats);
        }
    }

    public void cancelBuffStats(MapleBuffStat stat) {
        List<MapleBuffStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList);
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat) {
        cancelEffect(effects.get(stat).effect, false, -1);
    }

    private void cancelPlayerBuffs(List<MapleBuffStat> buffstats) {
        if (getClient().getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) { // are we still connected ?
            recalcLocalStats();
            enforceMaxHpMp();
            getClient().getSession().write(MaplePacketCreator.cancelBuff(buffstats));
            getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), buffstats), false);
        }
    }

    public void cancelAllBuffs() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void dispel() {
        cancelBuffs(MapleStatEffectType.PLAYER_SKILL);
    }

    public void cancelBuffs(MapleStatEffectType statType) {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getStatType() == statType || mbsvh.effect.getStatType() == MapleStatEffectType.UNDEFINED) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void cancelMagicDoor() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void handleOrbgain() {
        int orbcount = getBuffedValue(MapleBuffStat.COMBO);
        ISkill combo = SkillFactory.getSkill(1111002);
        ISkill advcombo = SkillFactory.getSkill(1120003);

        MapleStatEffect ceffect = null;
        int advComboSkillLevel = getSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        } else {
            ceffect = combo.getEffect(getSkillLevel(combo));
        }

        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (advComboSkillLevel > 0 && ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;
                }
            }

            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, neworbcount));
            setBuffedValue(MapleBuffStat.COMBO, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

            MapleStatEffect statEffect = MapleStatEffect.getEmptyStatEffect();
            statEffect.setSourceid(1111002);
            statEffect.setDuration(duration);
            statEffect.setStatsups(stat);
            statEffect.setStatType(MapleStatEffect.MapleStatEffectType.PLAYER_SKILL);
            statEffect.setMorph(0);
            if (statEffect.getStatType() == MapleStatEffect.MapleStatEffectType.PLAYER_SKILL) {
                addBuffCount();
            }
            getClient().getSession().write(MaplePacketCreator.giveBuff(statEffect));
            getMap().broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat), false);
        }
    }

    public void handleOrbconsume() {
        ISkill combo = SkillFactory.getSkill(1111002);
        MapleStatEffect ceffect = combo.getEffect(getSkillLevel(combo));
        List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
        setBuffedValue(MapleBuffStat.COMBO, 1);
        int duration = ceffect.getDuration();
        duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

        MapleStatEffect statEffect = MapleStatEffect.getEmptyStatEffect();
        statEffect.setSourceid(1111002);
        statEffect.setDuration(duration);
        statEffect.setStatsups(stat);
        statEffect.setStatType(MapleStatEffect.MapleStatEffectType.PLAYER_SKILL);
        statEffect.setMorph(0);
        if (statEffect.getStatType() == MapleStatEffect.MapleStatEffectType.PLAYER_SKILL) {
            addBuffCount();
        }
        getClient().getSession().write(MaplePacketCreator.giveBuff(statEffect));
        getMap().broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat), false);
    }

    private void silentEnforceMaxHpMp() {
        setMp(getMp());
        setHp(getHp(), true);
    }

    private void enforceMaxHpMp() {
        List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>(2);
        if (getMp() > getCurrentMaxMp()) {
            setMp(getMp());
            stats.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(getMp())));
        }
        if (getHp() > getCurrentMaxHp()) {
            setHp(getHp());
            stats.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(getHp())));
        }
        if (stats.size() > 0) {
            getClient().getSession().write(MaplePacketCreator.updatePlayerStats(stats));
        }
    }

    public void dropMessage(int type, String message) {
        new ServernoticeMapleClientMessageCallback(type, getClient()).dropMessage(message);
    }

    public void dropMessage(String message) {
        new ServernoticeMapleClientMessageCallback(getClient()).dropMessage(message);
    }

    public MapleMap getMap() {
        return map;
    }

    public MapleMap getLastMap() {
        return lastMap;
    }

    public void setLastMap(MapleMap lastMap) {
        this.lastMap = lastMap;
    }

    /**
     * only for tests
     *
     * @param newmap
     */
    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    public int getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    public int getCurrentFieldKey() {
        System.out.println("CURRENT KEY: " + currentFieldKey);
        return currentFieldKey;
    }

    public void incrementCurrentFieldKey() {
        currentFieldKey += 1;
    }

    public void setCurrentFieldKey(int currentFieldKey) {
        this.currentFieldKey = currentFieldKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getRank() {
        return rank;
    }

    public int getRankMove() {
        return rankMove;
    }

    public int getJobRank() {
        return jobRank;
    }

    public int getJobRankMove() {
        return jobRankMove;
    }

    public int getFame() {
        return fame;
    }

    public int getStr() {
        return str;
    }

    public int getDex() {
        return dex;
    }

    public int getLuk() {
        return luk;
    }

    public int getInt() {
        return int_;
    }

    public MapleClient getClient() {
        return client;
    }

    public int getExp() {
        return exp.get();
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxhp;
    }

    public int getMp() {
        return mp;
    }

    public int getMaxMp() {
        return maxmp;
    }

    public int getRemainingAp() {
        return remainingAp;
    }

    public int getRemainingSp() {
        return remainingSp;
    }

    public int getMpApUsed() {
        return mpApUsed;
    }

    public void setMpApUsed(int mpApUsed) {
        this.mpApUsed = mpApUsed;
    }

    public int getHpApUsed() {
        return hpApUsed;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        getClient().getSession().write(MaplePacketCreator.sendGMHide(hidden));
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHpApUsed(int hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    public MapleSkinColor getSkinColor() {
        return skinColor;
    }

    public void setJob(MapleJob job) {
        this.job = job;
    }

    public MapleJob getJob() {
        return job;
    }

    public void setLevel(int level) {
        this.level = level - 1;
    }

    public void setClient(MapleClient client) {
        this.client = client;
    }

    public void setInitialSpawnPoint(int initialSpawnPoint) {
        this.initialSpawnPoint = initialSpawnPoint;
    }

    public int getGenderId() {
        return gender.getValue();
    }

    public MapleGender getGender() {
        return gender;
    }

    public int getHair() {
        return hair;
    }

    public int getFace() {
        return face;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStr(int str) {
        this.str = str;
        recalcLocalStats();
    }

    public void setDex(int dex) {
        this.dex = dex;
        recalcLocalStats();
    }

    public void setLuk(int luk) {
        this.luk = luk;
        recalcLocalStats();
    }

    public void setStat(MapleStat stat, int value) {
        switch (stat) {
            case STR:
                setStr(value);
                break;
            case DEX:
                setDex(value);
                break;
            case INT:
                setInt(value);
                break;
            case LUK:
                setLuk(value);
                break;
        }
    }

    public void addStat(MapleStat stat, int delta, boolean isReset) {
        int maxStat = getClient().getChannelServer().getMaxAP();
        boolean isSubtract = delta < 0;
        List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>();
        switch (stat) {
            case STR:
            case DEX:
            case INT:
            case LUK:
                if (getStat(stat) >= maxStat) {
                    return;
                }
                setStat(stat, getStat(stat) + delta);
            case MAXHP:
            case MAXMP:
                if (stat == MapleStat.MAXHP && this.getMaxHp() >= GameConstants.Stats.MAX_MAX_HP) {
                    return;
                }
                if (stat == MapleStat.MAXMP && this.getMaxMp() >= GameConstants.Stats.MAX_MAX_MP) {
                    return;
                }
                if (isSubtract && this.getHpApUsed() == 0) {
                    return;
                }

                ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                int improvingMaxHPLevel = getSkillLevel(improvingMaxHP);
                int improvingMaxMPLevel = getSkillLevel(improvingMaxMP);
                int y = 0;

                if (improvingMaxMPLevel > 0) {
                    y = improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                }

                int hpgain = 0;
                int mpgain = 0;
                if (getJob() == MapleJob.BEGINNER) {
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.BEGINNER_AP, 0);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.BEGINNER_AP, 0);
                } else if (getJob().isA(MapleJob.WARRIOR)) {
                    if (improvingMaxHPLevel > 0) {
                        y = improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                    }
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.WARRIOR_AP, y);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.WARRIOR_AP, 0);
                } else if (getJob().isA(MapleJob.MAGICIAN)) {
                    if (improvingMaxMPLevel > 0) {
                        y = improvingMaxHP.getEffect(improvingMaxMPLevel).getY();
                    }
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.MAGICIAN_AP, 0);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.MAGICIAN_AP, 2 * y);
                } else if (getJob().isA(MapleJob.BOWMAN)) {
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.BOWMAN_AP, 0);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.BOWMAN_AP, 0);
                } else if (getJob().isA(MapleJob.THIEF)) {
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.THIEF_AP, 0);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.THIEF_AP, 0);
                } else if (getJob().isA(MapleJob.PIRATE)) {
                    if (improvingMaxHPLevel > 0) {
                        y = improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                    }
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.PIRATE_AP, y);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.PIRATE_AP, 0);
                } else if (getJob().isA(MapleJob.GM)) {
                    hpgain = randomResetHp(isReset, isSubtract, GameConstants.BaseHp.GM_AP, 0);
                    mpgain = randomResetMp(isReset, isSubtract, GameConstants.BaseMp.GM_AP, 0);
                } else {
                    hpgain = 0;
                    mpgain = 0;
                }
                setHpApUsed(getHpApUsed() + delta);
                setMaxHp(getMaxHp() + hpgain);
                setMaxMp(getMaxMp() + mpgain);
                break;
            default:
                throw new UnsupportedOperationException("Cant add this stat");
        }
        statupdate.add(new Pair<MapleStat, Integer>(stat, getStat(stat)));
        if (!isReset) {
            setRemainingAp(getRemainingAp() - delta);
            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, getRemainingAp()));
        }
        getClient().getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true));
    }

    public int getStat(MapleStat stat) {
        switch (stat) {
            case SKIN:
                return getSkinColor().getId();
            case FACE:
                return getFace();
            case HAIR:
                return getHair();
            case LEVEL:
                return getLevel();
            case JOB:
                return getJob().getId();
            case STR:
                return getStr();
            case DEX:
                return getDex();
            case INT:
                return getInt();
            case LUK:
                return getLuk();
            case HP:
                return getHp();
            case MAXHP:
                return getMaxHp();
            case MP:
                return getMp();
            case MAXMP:
                return getMaxMp();
            case AVAILABLEAP:
                return getRemainingAp();
            case AVAILABLESP:
                return getRemainingSp();
            case EXP:
                return getExp();
            case FAME:
                return getFame();
            case MESO:
                return getMeso();
            default:
                return -1;
        }
    }

    public void setInt(int int_) {
        this.int_ = int_;
        recalcLocalStats();
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp = remainingSp;
    }

    public void setSkinColor(MapleSkinColor skinColor) {
        this.skinColor = skinColor;
    }

    public void setGender(int gender) {
        this.gender = MapleGender.getById(gender);
    }

    public void setGender(MapleGender gender) {
        this.gender = gender;
    }

    public void setGmLevel(int gmLevel) {
        this.gmLevel = gmLevel;
    }

    public void setGm(boolean gm) {
        this.gmLevel = gm ? 1 : 0;
    }

    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    public BuddyList getBuddylist() {
        return buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
    }

    public int getStartMap() {
        return startMap;
    }

    public void setStartMap(int startMap) {
        this.startMap = startMap;
    }

    public void addDisease(MapleBuffStat disease) {
        diseases.add(disease);
    }

    public void addDiseases(List<MapleBuffStat> diseases) {
        this.diseases.addAll(diseases);
    }

    public void addDisease(Pair<MapleBuffStat, Integer> disease) {
        addDisease(disease.getLeft());
    }

    public void addDiseasesWithStats(List<Pair<MapleBuffStat, Integer>> diseases) {
        for (Pair<MapleBuffStat, Integer> disease : diseases) {
            addDisease(disease);
        }
    }

    public List<MapleBuffStat> getDiseases() {
        return Collections.unmodifiableList(diseases);
    }

    public void removeDisease(MapleBuffStat disease) {
        if (diseases.contains(disease)) {
            List<MapleBuffStat> diseaseList = Collections.singletonList(disease);
            getClient().getSession().write(MaplePacketCreator.cancelBuff(diseaseList));
            getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), diseaseList), false);
            diseases.remove(disease);
        }
    }

    public void removeDiseases(List<MapleBuffStat> diseases) {
        for (MapleBuffStat disease : diseases) {
            removeDisease(disease);
        }
    }

    public void removeDisease(MonsterSkill skill) {
        for (Pair<MapleBuffStat, Integer> stat : skill.statups) {
            removeDisease(stat.getLeft());
        }
    }

    public void giveDebuffs(MonsterSkill skill) {
        addDiseasesWithStats(skill.statups);
        getClient().getSession().write(MaplePacketCreator.giveBuff(skill.getStatEffect()));
        getMap().broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(getId(), skill), false);

        final MonsterSkill skill_ = skill;
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                for (MapleBuffStat disease : getDiseases()) {
                    for (Pair<MapleBuffStat, Integer> stat : skill_.statups) {
                        if (stat.getLeft() == disease) {
                            removeDisease(skill_);
                        }
                    }
                }
            }
        }, skill.getDuration());
    }

    public void warpMapTo(int mapid) {
        MapleClient c = new MapleClient(null, null, new MockIOSession());
        ChannelServer cserv = c.getChannelServer();
        final MapleMap to = cserv.getMapFactory().getMap(mapid);
        final MaplePortal pos = to.getPortal(0);
        changeMap(to, pos);
    }

    public void changeMap(final MapleMap to, final Point pos) {
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, 0x80, this);
        changeMapInternal(to, pos, warpPacket);
    }

    public void changeMap(final MapleMap to, final MaplePortal pto) {
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    private void changeMapInternal(final MapleMap to, final Point pos, MaplePacket warpPacket) {
        warpPacket.setOnSend(new Runnable() {

            @Override
            public void run() {
                lastMap = map;
                map.removePlayer(MapleCharacter.this);
                if (getClient().getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
                    map = to;
                    setPosition(pos);
                    to.addPlayer(MapleCharacter.this);
                    if (party != null) {
                        silentPartyUpdate();
                        getClient().getSession().write(MaplePacketCreator.updateParty(getClient().getChannel(), party, PartyOperation.SILENT_UPDATE, null));
                        updatePartyMemberHP();
                    }
                }
            }
        });
        getClient().getSession().write(warpPacket);
    }

    public void leaveMap() {
        controlled.clear();
        visibleMapObjects.clear();
        chair = 0;
    }

    public void startHurtHp() {
        hpDecreaseTask = TimerManager.getInstance().register(new Runnable() {

            @Override
            public void run() {
                if (getMap().getDecHP() < 1 || !isAlive()) {
                    return;
                } else if (getEventInstance() != null && getEventInstance().getProperty("disableDecHP").equals("1")) {
                    return;
                } else if (getInventory(MapleInventoryType.EQUIPPED).findById(getMap().getProtectItem()) == null) {
                    addHP(-getMap().getDecHP());
                }
            }
        }, 10000, false);
    }

    public void cancelHurtHp() {
        if (hpDecreaseTask != null) {
            hpDecreaseTask.cancel(false);
        }
    }

    public void startMapTimeLimitTask(final MapleMap from, final MapleMap to) {
        if (to.getTimeLimit() > 0 && from != null) {
            final MapleCharacter chr = this;
            mapTimeLimitTask = TimerManager.getInstance().register(new Runnable() {

                @Override
                public void run() {
                    MaplePortal pfrom = null;
                    if (MapleMapInformationProvider.getInstance().isMiniDungeonMap(from.getId())) {
                        pfrom = from.getPortal("MD00");
                    } else {
                        pfrom = from.getPortal(0);
                    }
                    if (pfrom != null) {
                        chr.changeMap(from, pfrom);
                    }
                }
            }, from.getTimeLimit() * 1000, false);
        }
    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
        }
    }

    public void playerRevive() {
        boolean executeStandardPath = true;
        if (getEventInstance() != null) {
            executeStandardPath = getEventInstance().revivePlayer(this);
        }

        if (executeStandardPath) {
            setHp(50);
            MapleMap to = null;
            if (getMap().getForcedReturn() != 999999999) {
                to = getClient().getChannelServer().getMapFactory().getMap(getMap().getForcedReturn());
            } else {
                to = getMap().getReturnMap();
            }
            MaplePortal pto = to.getRandomPortal();
            setStance(0);
            changeMap(to, pto);
        }
    }

    public void changeJob(MapleJob newJob) {
        this.job = newJob;
        remainingSp++;
        updateSingleStat(MapleStat.AVAILABLESP, remainingSp);
        updateSingleStat(MapleStat.JOB, newJob.getId());
        getMap().broadcastMessage(this, MaplePacketCreator.showJobChange(getId()), false);
        silentPartyUpdate();
        guildUpdate();
    }

    public void gainAp(int ap) {
        this.remainingAp += ap;
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    public void changeSkillLevel(ISkill skill, int newLevel, int newMasterlevel) {
        skills.put(skill, new SkillEntry(newLevel, newMasterlevel));
        this.getClient().getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel));
    }

    public void addSkillLevel(ISkill skill, int amount) {
        int sLevel = this.getSkillLevel(skill);
        sLevel += amount;
        skills.put(skill, new SkillEntry(sLevel, getMasterLevel(skill)));
        this.getClient().getSession().write(MaplePacketCreator.updateSkill(skill.getId(), sLevel, getMasterLevel(skill)));
    }

    public void setHp(int newhp) {
        setHp(newhp, false);
    }

    private void setHp(int newhp, boolean silent) {
        int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = thp;

        if (!silent) {
            updatePartyMemberHP();
        }
        if (oldHp > hp && !isAlive()) {
            playerDead();
        }
    }

    public void setMaxHp(int newmaxhp) {
        this.maxhp = newmaxhp;
        updateSingleStat(MapleStat.MAXHP, newmaxhp);
    }

    public void setMaxMp(int newmaxmp) {
        this.maxmp = newmaxmp;
        updateSingleStat(MapleStat.MAXMP, newmaxmp);
    }

    private void playerDead() {
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }
        cancelAllBuffs();
        getClient().getSession().write(MaplePacketCreator.enableActions());
    }

    public void updatePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().getSession().write(
                                MaplePacketCreator.updatePartyMemberHP(getId(), this.hp, localmaxhp));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        getClient().getSession().write(
                                MaplePacketCreator.updatePartyMemberHP(other.getId(), other.getHp(), other.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void setMp(int newmp) {
        setMp(newmp, false);
    }

    public void setMp(int newmp, boolean now) {
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }
        this.mp = tmp;
        if (now) {
            updateSingleStat(MapleStat.MP, mp);
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setHp(int)
     * @param delta
     */
    public void addHP(int delta) {
        setHp(hp + delta);
        updateSingleStat(MapleStat.HP, hp);
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setMp(int)
     * @param delta
     */
    public void addMP(int delta) {
        setMp(mp + delta);
        updateSingleStat(MapleStat.MP, mp);
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        setHp(hp + hpDiff);
        setMp(mp + mpDiff);
        List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
        stats.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(hp)));
        stats.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(mp)));
        MaplePacket updatePacket = MaplePacketCreator.updatePlayerStats(stats);
        client.getSession().write(updatePacket);
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method only creates and sends an update packet,
     * it does not update the stat stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        Pair<MapleStat, Integer> statpair = new Pair<MapleStat, Integer>(stat, Integer.valueOf(newval));
        MaplePacket updatePacket = MaplePacketCreator.updatePlayerStats(Collections.singletonList(statpair),
                itemReaction);
        client.getSession().write(updatePacket);
    }

    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    public void gainExp(int gain, boolean show, boolean inChat, boolean white) {
        if (getLevel() < 200) { // lv200 is max and has 0 exp required to level
            int newexp = this.exp.addAndGet(gain);
            updateSingleStat(MapleStat.EXP, newexp);
        }
        if (show) { // still show the expgain even if it's not there
            client.getSession().write(MaplePacketCreator.getShowExpGain(gain, inChat, white));
        }
        if (getExp() < 0 || getExp() > Integer.MAX_VALUE) {
            exp.set(0);
        }

        if (getClient().getChannelServer().allowMultiLevel()) {
            while (level < 200 && exp.get() >= ExpTable.getExpNeededForLevel(level + 1)) //MultiLevel
            {
                levelUp();
            }
        } else if (level < 200 && exp.get() >= ExpTable.getExpNeededForLevel(level + 1)) {//SingleLevel
            levelUp();
        }
    }

    public void silentPartyUpdate() {
        if (party != null) {
            try {
                getClient().getChannelServer().getWorldInterface().updateParty(party.getId(),
                        PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(MapleCharacter.this));
            } catch (RemoteException e) {
                log.error("REMOTE THROW", e);
                getClient().getChannelServer().reconnectWorld();
            }
        }
    }

    public void gainExp(int gain, boolean show, boolean inChat) {
        gainExp(gain, show, inChat, true);
    }

    public boolean isGM() {
        return gmLevel > 0;
    }

    public int getGMLevel() {
        return gmLevel;
    }

    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    public MapleInventory getInventory(MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return meso.get();
    }

    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.ordinal()];
    }

    public void saveLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = getMapId();
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = -1;
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions) {
        gainMeso(gain, show, enableActions, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
        if (meso.get() + gain < 0) {
            client.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int newVal = meso.addAndGet(gain);
        updateSingleStat(MapleStat.MESO, newVal, enableActions);
        if (show) {
            client.getSession().write(MaplePacketCreator.getShowMesoGain(gain, inChat));
        }
    }

    /**
     * Adds this monster to the controlled list. The monster must exist on the Map.
     *
     * @param monster
     */
    public void controlMonster(MapleMonster monster, boolean aggro) {
        monster.setController(this);
        controlled.add(monster);
        client.getSession().write(MaplePacketCreator.controlMonster(monster, false, aggro));
    }

    public void stopControllingMonster(MapleMonster monster) {
        controlled.remove(monster);
    }

    public Collection<MapleMonster> getControlledMonsters() {
        return Collections.unmodifiableCollection(controlled);
    }

    public int getNumControlledMonsters() {
        return controlled.size();
    }

    @Override
    public String toString() {
        return "Character: " + this.name;
    }

    public int getAccountID() {
        return accountid;
    }

    public void mobKilled(int id) {
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == MapleQuestStatus.Status.COMPLETED || q.getQuest().canComplete(this, null)) {
                continue;
            }
            if (q.mobKilled(id) && !(q.getQuest() instanceof MapleCustomQuest)) {
                client.getSession().write(MaplePacketCreator.updateQuestMobKills(q));
                if (q.getQuest().canComplete(this, null)) {
                    client.getSession().write(MaplePacketCreator.getShowQuestCompletion(q.getQuest().getId()));
                }
            }
        }
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.STARTED) && !(q.getQuest() instanceof MapleCustomQuest)) {
                ret.add(q);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.COMPLETED) && !(q.getQuest() instanceof MapleCustomQuest)) {
                ret.add(q);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public MaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(MaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public Map<ISkill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public int getSkillLevel(int skill) {
        return getSkillLevel(SkillFactory.getSkill(skill));
    }

    public int getSkillLevel(ISkill skill) {
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.skillevel;
    }

    public int getMasterLevel(ISkill skill) {
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    // the equipped inventory only contains equip... I hope
    public int getTotalDex() {
        return localdex;
    }

    public int getTotalInt() {
        return localint_;
    }

    public int getTotalStr() {
        return localstr;
    }

    public int getTotalLuk() {
        return localluk;
    }

    public int getTotalMagic() {
        return magic;
    }

    public double getSpeedMod() {
        return speedMod;
    }

    public double getJumpMod() {
        return jumpMod;
    }

    public int getTotalWatk() {
        return watk;
    }

    public void sendToTown(MapleMonsterBanishInfo banishInfo) {
        if (banishInfo.getBanishMessage() != null) {
            dropMessage(banishInfo.getBanishMessage());
        }
        if (banishInfo.getMapId() != -1) {
            MapleMap townMap = getClient().getChannelServer().getMapFactory().getMap(banishInfo.getMapId());
            MaplePortal townPortal = townMap.getPortal(0);
            if (!banishInfo.getMapPortal().equals("sp")) {
                townPortal = townMap.getPortal(banishInfo.getMapPortal());
            }
            changeMap(townMap, townPortal);
        }
    }

    public int getEquippedId(byte equipSlot) {
        return getInventory(MapleInventoryType.EQUIPPED).getItem(equipSlot).getItemId();
    }

    private static int randomHp(int base, int bonus) {
        return Randomizer.randomInt(GameConstants.BaseHp.VARIATION) + base + bonus;
    }

    private static int randomMp(int base, int bonus) {
        return Randomizer.randomInt(GameConstants.BaseMp.VARIATION) + base + bonus;
    }

    private static int randomResetHp(boolean isReset, boolean isSubtract, int value, int valueS) {
        return (isReset ? (isSubtract ? -(valueS + value + GameConstants.BaseHp.VARIATION) : value) : randomHp(value, valueS));
    }

    private static int randomResetMp(boolean isReset, boolean isSubtract, int value, int valueS) {
        return (isReset ? (isSubtract ? -(valueS + value + GameConstants.BaseMp.VARIATION) : value) : randomMp(value, valueS));
    }

    public void levelUp() {
        ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
        ISkill improvingMaxMP = SkillFactory.getSkill(2000001);

        int improvingMaxHPLevel = getSkillLevel(improvingMaxHP);
        int improvingMaxMPLevel = getSkillLevel(improvingMaxMP);
        int intt = getTotalInt() / 10;
        int x = 0;
        remainingAp += GameConstants.Stats.AP_PER_LEVEL;
        if (job == MapleJob.BEGINNER) {
            maxhp += randomHp(GameConstants.BaseHp.BEGINNER, 0);
            maxmp += randomMp(GameConstants.BaseMp.BEGINNER, intt);
        } else if (job.isA(MapleJob.WARRIOR)) {
            if (improvingMaxHPLevel > 0) {
                x = improvingMaxHP.getEffect(improvingMaxHPLevel).getX();
            }
            maxhp += randomHp(GameConstants.BaseHp.WARRIOR, x);
            maxmp += randomMp(GameConstants.BaseMp.WARRIOR, intt);
        } else if (job.isA(MapleJob.MAGICIAN)) {
            if (improvingMaxMPLevel > 0) {
                x = improvingMaxMP.getEffect(improvingMaxMPLevel).getX();
            }
            maxhp += randomHp(GameConstants.BaseHp.MAGICIAN, 0);
            maxmp += randomMp(GameConstants.BaseMp.MAGICIAN, 2 * x + intt);
        } else if (job == MapleJob.BOWMAN) {
            maxhp += randomHp(GameConstants.BaseHp.BOWMAN, 0);
            maxmp += randomMp(GameConstants.BaseMp.BOWMAN, intt);
        } else if (job == MapleJob.THIEF) {
            maxhp += randomHp(GameConstants.BaseHp.THIEF, 0);
            maxmp += randomMp(GameConstants.BaseMp.THIEF, intt);
        } else if (job == MapleJob.PIRATE) {
            maxhp += randomHp(GameConstants.BaseHp.PIRATE, x);
            maxmp += randomHp(GameConstants.BaseMp.PIRATE, intt);
        } else if (job.isA(MapleJob.GM)) {
            maxhp += GameConstants.BaseHp.GM;
            maxmp += GameConstants.BaseMp.GM;
        } else {
            log.warn("No hp/mp add info for job:" + job.getId());
        }

        exp.addAndGet(-ExpTable.getExpNeededForLevel(level + 1));
        level += 1;
        if (level == 200) {
            exp.set(0);
        }

        if (level == job.getMaxLevel() && !isGM()) {
            String message = "[Congrats] " + getName() + " has reached Level " + job.getMaxLevel() + "! Congratulate " + getName() + " on such an amazing achievement!";
            getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, message));
        }

        maxhp = Math.min(GameConstants.Stats.MAX_MAX_HP, maxhp);
        maxmp = Math.min(GameConstants.Stats.MAX_MAX_MP, maxmp);

        List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(8);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, Integer.valueOf(remainingAp)));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, Integer.valueOf(maxhp)));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, Integer.valueOf(maxmp)));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(maxhp)));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(maxmp)));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.EXP, Integer.valueOf(exp.get())));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LEVEL, Integer.valueOf(level)));

        if (!job.isBeginnerJob()) {
            remainingSp += GameConstants.Stats.SP_PER_LEVEL;
            statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLESP, Integer.valueOf(remainingSp)));
        }

        setHp(maxhp);
        setMp(maxmp);
        getClient().getSession().write(MaplePacketCreator.updatePlayerStats(statup));
        getMap().broadcastMessage(this, MaplePacketCreator.showLevelup(getId()), false);
        recalcLocalStats();
        silentPartyUpdate();
        guildUpdate();
    }

    public void changeKeybinding(int key, MapleKeyBinding keybinding) {
        if (keybinding.getType() != 0) {
            keymap.put(Integer.valueOf(key), keybinding);
        } else {
            keymap.remove(Integer.valueOf(key));
        }
    }

    public void sendKeymap() {
        getClient().getSession().write(MaplePacketCreator.getKeymap(keymap));
    }

    public void tempban(String reason, Calendar duration, int greason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        tempban(reason, duration, greason, client.getAccID());
        client.getSession().close();
    }

    public static boolean tempban(String reason, Calendar duration, int greason, int accountid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            log.error("Error while tempbanning", ex);
        }
        return false;
    }

    public void ban(String reason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        try {
            getClient().banMacs();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, 1);
            ps.setString(2, reason);
            ps.setInt(3, accountid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            String[] ipSplit = client.getSession().getRemoteAddress().toString().split(":");
            ps.setString(1, ipSplit[0]);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error while banning", ex);
        }
        client.getSession().close();
    }

    public static boolean ban(String id, String reason, boolean accountId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.executeUpdate();
                ps.close();
                return true;
            }
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }
            boolean ret = false;
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
                psb.setString(1, reason);
                psb.setInt(2, rs.getInt(1));
                psb.executeUpdate();
                psb.close();
                ret = true;
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            log.error("Error while banning", ex);
        }
        return false;
    }

    /**
     * Oid of players is always = the cid
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return storage;
    }

    public int getCurrentMaxHp() {
        return localmaxhp;
    }

    public int getCurrentMaxMp() {
        return localmaxmp;
    }

    public int getCurrentMaxBaseDamage() {
        return localmaxbasedamage;
    }

    public int calculateMaxBaseDamage(int watk) {
        int maxbasedamage;
        if (watk == 0) {
            maxbasedamage = 1;
        } else {
            IItem weapon_item = getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            if (weapon_item != null) {
                MapleWeaponType weapon = MapleItemInformationProvider.getWeaponType(weapon_item.getItemId());
                int mainstat;
                int secondarystat;
                if (weapon == MapleWeaponType.BOW || weapon == MapleWeaponType.CROSSBOW) {
                    mainstat = localdex;
                    secondarystat = localstr;
                } else if (getJob().isA(MapleJob.THIEF) && (weapon == MapleWeaponType.CLAW || weapon == MapleWeaponType.DAGGER)) {
                    mainstat = localluk;
                    secondarystat = localdex + localstr;
                } else {
                    mainstat = localstr;
                    secondarystat = localdex;
                }
                maxbasedamage = (int) (((weapon.getMaxDamageMultiplier() * mainstat + secondarystat) / 100.0) * watk);
                //just some saveguard against rounding errors, we want to a/b for this
                maxbasedamage += 10;
            } else {
                maxbasedamage = 0;
            }
        }
        return maxbasedamage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        visibleMapObjects.add(mo);
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        visibleMapObjects.remove(mo);
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        return visibleMapObjects.contains(mo);
    }

    public Collection<MapleMapObject> getVisibleMapObjects() {
        return Collections.unmodifiableCollection(visibleMapObjects);
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    public int getBuffCount() {
        return buffCount;
    }

    public void setBuffCount(int buffCount) {
        this.buffCount = buffCount;
    }

    public void addBuffCount() {
        this.buffCount += 1;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removePlayerFromMap(this.getObjectId()));
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (!this.isHidden()) {
            client.getSession().write(MaplePacketCreator.spawnPlayerMapobject(this));
            for (int i = 0; i < 3; i++) {
                if (pets[i] != null) {
                    client.getSession().write(MaplePacketCreator.showPet(this, pets[i], false, false));
                }
            }
        }
    }

    private void recalcLocalStats() {
        int oldmaxhp = localmaxhp;
        localmaxhp = getMaxHp();
        localmaxmp = getMaxMp();
        localdex = getDex();
        localint_ = getInt();
        localstr = getStr();
        localluk = getLuk();
        int speed = 100;
        int jump = 100;
        magic = localint_;
        watk = 0;
        for (IItem item : getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            localmaxhp += equip.getHp();
            localmaxmp += equip.getMp();
            localdex += equip.getDex();
            localint_ += equip.getInt();
            localstr += equip.getStr();
            localluk += equip.getLuk();
            magic += equip.getMatk() + equip.getInt();
            watk += equip.getWatk();
            speed += equip.getSpeed();
            jump += equip.getJump();
        }
        magic = Math.min(magic, 2000);
        Integer hbhp = getBuffedValue(MapleBuffStat.HYPERBODYHP);
        if (hbhp != null) {
            localmaxhp += (hbhp.doubleValue() / 100) * localmaxhp;
        }
        Integer hbmp = getBuffedValue(MapleBuffStat.HYPERBODYMP);
        if (hbmp != null) {
            localmaxmp += (hbmp.doubleValue() / 100) * localmaxmp;
        }
        localmaxhp = Math.min(30000, localmaxhp);
        localmaxmp = Math.min(30000, localmaxmp);
        Integer watkbuff = getBuffedValue(MapleBuffStat.WATK);
        if (watkbuff != null) {
            watk += watkbuff.intValue();
        }
        if (job.isA(MapleJob.BOWMAN)) {
            ISkill expert = null;
            if (job.isA(MapleJob.CROSSBOWMASTER)) {
                expert = SkillFactory.getSkill(3220004);
            } else if (job.isA(MapleJob.BOWMASTER)) {
                expert = SkillFactory.getSkill(3120005);
            }
            if (expert != null) {
                int boostLevel = getSkillLevel(expert);
                if (boostLevel > 0) {
                    watk += expert.getEffect(boostLevel).getX();
                }
            }
        }
        Integer matkbuff = getBuffedValue(MapleBuffStat.MATK);
        if (matkbuff != null) {
            magic += matkbuff.intValue();
        }
        Integer speedbuff = getBuffedValue(MapleBuffStat.SPEED);
        if (speedbuff != null) {
            speed += speedbuff.intValue();
        }
        Integer jumpbuff = getBuffedValue(MapleBuffStat.JUMP);
        if (jumpbuff != null) {
            jump += jumpbuff.intValue();
        }
        speed = Math.min(speed, 140);
        jump = Math.min(jump, 123);

        speedMod = speed / 100.0;
        jumpMod = jump / 100.0;
        Integer mountId = getBuffedValue(MapleBuffStat.MONSTER_RIDING);
        if (mountId != null) {
            Pair<Double, Double> mountModData = MapleItemInformationProvider.getInstance().getMountModData(mountId);
            speedMod = mountModData.getLeft();
            jumpMod = mountModData.getRight();
        }
        localmaxbasedamage = calculateMaxBaseDamage(watk);
        if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
            updatePartyMemberHP();
        }
    }

    public void equipChanged() {
        getMap().broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
        recalcLocalStats();
        enforceMaxHpMp();
    }

    public MaplePet getPet(int index) {
        if (index + 1 > pets.length) {
            return null;
        }
        return pets[index];
    }

    public MaplePet getPetByUniqueId(int id) {
        for (MaplePet pet : pets) {
            if (pet == null) {
                continue;
            }
            if (pet.getUniqueId() == id) {
                return pet;
            }
        }
        return null;
    }

    public void addPet(MaplePet pet) {
        for (int i = 0; i < 3; i++) {
            if (pets[i] == null) {
                pets[i] = pet;
                return;
            }
        }
    }

    public void removePet(MaplePet pet, boolean shift_left) {
        int slot = -1;
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getUniqueId() == pet.getUniqueId()) {
                    pets[i] = null;
                    slot = i;
                    break;
                }
            }
        }
        if (shift_left) {
            if (slot > -1) {
                for (int i = slot; i < 3; i++) {
                    if (i != 2) {
                        pets[i] = pets[i + 1];
                    } else {
                        pets[i] = null;
                    }
                }
            }
        }
    }

    public int getPetsNumber() {
        int ret = 0;
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                ret++;
            }
        }
        return ret;
    }

    public int getPetIndex(MaplePet pet) {
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getUniqueId() == pet.getUniqueId()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getPetIndex(int petId) {
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getUniqueId() == petId) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getNextEmptyPetIndex() {
        if (pets[0] == null) {
            return 0;
        }
        if (pets[1] == null) {
            return 1;
        }
        if (pets[2] == null) {
            return 2;
        }
        return 3;
    }

    public MaplePet[] getAllPets() {
        return pets;
    }

    public List<MaplePet> getActivePets() {
        List<MaplePet> activePets = new ArrayList<MaplePet>();
        for (MaplePet pet : pets) {
            if (pet != null) {
                activePets.add(pet);
            }
        }
        return activePets;
    }

    public void unequipAllPets() {
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                unequipPet(pets[i], true);
            }
        }
    }

    public void unequipPet(MaplePet pet, boolean shift_left) {
        unequipPet(pet, shift_left, false);
    }

    public void unequipPet(MaplePet pet, boolean shift_left, boolean hunger) {
        cancelFullnessSchedule(getPetIndex(pet));

        pet.saveToDb();

        // Broadcast the packet to the map - with null instead of MaplePet
        getMap().broadcastMessage(this, MaplePacketCreator.showPet(this, pet, true, hunger), true);

        // Make a new list for the stat updates
        List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
        switch (getPetsNumber()) {
            case 1:
                stats.add(new Pair<MapleStat, Integer>(MapleStat.PET_1, Integer.valueOf(0)));
                break;
            case 2:
                stats.add(new Pair<MapleStat, Integer>(MapleStat.PET_1, Integer.valueOf(0)));
                stats.add(new Pair<MapleStat, Integer>(MapleStat.PET_2, Integer.valueOf(0)));
                break;
            case 3:
                stats.add(new Pair<MapleStat, Integer>(MapleStat.PET_1, Integer.valueOf(0)));
                stats.add(new Pair<MapleStat, Integer>(MapleStat.PET_2, Integer.valueOf(0)));
                stats.add(new Pair<MapleStat, Integer>(MapleStat.PET_3, Integer.valueOf(0)));
                break;
        }

        // Write the stat update to the player...
        getClient().getSession().write(MaplePacketCreator.petStatUpdate(this));
        getClient().getSession().write(MaplePacketCreator.enableActions());

        // Un-assign the pet set to the player
        removePet(pet, shift_left);
    }

    public void shiftPetsRight() {
        if (pets[2] == null) {
            pets[2] = pets[1];
            pets[1] = pets[0];
            pets[0] = null;
        }
    }

    public MapleMount getMount() {
        return mount;
    }

    public void setMount(MapleMount mount) {
        this.mount = mount;
    }

    public void mount(int id, int skillid) {
        mount = new MapleMount(id, skillid, this);
    }

    public void gainItem(int id, short quantity) {
        MapleClient c = new MapleClient(null, null, new MockIOSession());
        if (quantity >= 0) {
            StringBuilder logInfo = new StringBuilder(c.getPlayer().getName());
            logInfo.append(" received ");
            logInfo.append(quantity);
            logInfo.append(" from a NPC conversation with NPC id ");
            MapleInventoryManipulator.addById(c, id, quantity, logInfo.toString());
        } else {
            MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
        }
        c.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
    }

    public boolean isPartyLeader() {
        if (party != null && this.party.getLeader() == this.party.getMemberById(this.getId())) {
            return true;
        } else {
            return false;
        }
    }

    public FameStatus canGiveFame(MapleCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (lastmonthfameids.contains(Integer.valueOf(from.getId()))) {
            return FameStatus.NOT_THIS_MONTH;
        } else {
            return FameStatus.OK;
        }
    }

    public void hasGivenFame(MapleCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(Integer.valueOf(to.getId()));
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("ERROR writing famelog for char " + getName() + " to " + to.getName(), e);
        }
    }

    public MapleParty getParty() {
        return party;
    }

    public int getPartyId() {
        return (party != null ? party.getId() : -1);
    }

    public boolean isPartyInvited() {
        return partyInvited;
    }

    public void setPartyInvited(boolean partyInvited) {
        this.partyInvited = partyInvited;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public MapleMiniGame getMiniGame() {
        return miniGame;
    }

    public void setMiniGame(MapleMiniGame miniGame) {
        this.miniGame = miniGame;
    }

    public MapleMiniGameStats getMiniGameStats() {
        return miniGameStats;
    }

    public void setMiniGameStats(MapleMiniGameStats miniGameStats) {
        this.miniGameStats = miniGameStats;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    public void clearDoors() {
        doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList<MapleDoor>(doors);
    }

    public boolean canDoor() {
        return canDoor;
    }

    public void disableDoor() {
        canDoor = false;
        TimerManager tMan = TimerManager.getInstance();
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                canDoor = true;
            }
        }, 5000);
    }

    public List<MapleGenericPortal> getUsedPortals() {
        return usedPortals;

    }

    public void clearUsedPortals() {
        usedPortals.clear();
    }

    public boolean addUsedPortal(MapleGenericPortal mgp) {
        return usedPortals.add(mgp);
    }

    public boolean removeUsedPortal(MapleGenericPortal mgp) {
        return usedPortals.remove(mgp);
    }

    public boolean isPortalUsed(MapleGenericPortal mgp) {
        return usedPortals.contains(mgp);
    }

    public MapleMacro[] getSkillMacros() {
        return skillMacros;
    }

    public List<MapleMacro> getActiveMacros() {
        List<MapleMacro> ret = new ArrayList<MapleMacro>();
        for (MapleMacro macro : skillMacros) {
            if (macro != null) {
                ret.add(macro);
            }
        }
        return ret;
    }

    public void setSkillMacros(MapleMacro[] skillMacros) {
        this.skillMacros = skillMacros;
    }

    public void sendMacros() {
        List<MapleMacro> macros = getActiveMacros();
        if (macros.size() > 0) {
            getClient().getSession().write(MaplePacketCreator.showSkillMacros(macros));
        }
    }

    public void updateMacros(int position, MapleMacro updateMacro) {
        skillMacros[position] = updateMacro;
    }

    public Map<Integer, MapleSummon> getSummons() {
        return summons;
    }

    public int getChair() {
        return chair;
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
    }

    public int getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    public void setBuddyCapacity(int capacity) {
        buddylist.setCapacity(capacity);
        client.getSession().write(MaplePacketCreator.updateBuddyCapacity(capacity));
    }

    public void unequipEverything() {
        MapleInventory equipped = getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getInventory(MapleInventoryType.EQUIP);
        List<Byte> ids = new LinkedList<Byte>();
        for (IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (byte Id : ids) {
            MapleInventoryManipulator.unequip(getClient(), Id, equip.getNextFreeSlot());
        }
    }

    public void clearInv(int type) {
        clearInv(type, 1);
    }

    public void clearInv(int type, int quantity) {
        MapleInventory inv = getInventory(MapleInventoryType.getByType((byte) type));
        List<Byte> ids = new ArrayList<Byte>();
        for (IItem item : inv.list()) {
            ids.add(item.getPosition());
        }
        for (byte Id : ids) {
            MapleInventoryManipulator.removeFromSlot(getClient(), inv.getType(), Id, (short) quantity, false);
        }
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    @Override
    public Collection<MapleInventory> allInventories() {
        return Arrays.asList(inventory);
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public boolean isMarried() {
        return married;
    }

    public int getGuildId() {
        return guildid;
    }

    public int getGuildRank() {
        return guildrank;
    }

    public void setGuildId(int _id) {
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);
            } else {
                mgc.setGuildId(guildid);
            }
        } else {
            mgc = null;
        }
    }

    public void setGuildRank(int _rank) {
        guildrank = _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public MapleGuild getGuild() {
        MapleGuild retGuild;
        if (mgc == null) {
            retGuild = null;
        } else if (client.getChannelServer().getGuild(mgc) == null) {
            retGuild = null;
        } else {
            retGuild = client.getChannelServer().getGuild(mgc);
        }
        return retGuild;
    }

    public void guildUpdate() {
        if (this.guildid <= 0) {
            return;
        }

        mgc.setLevel(this.level);
        mgc.setJobId(this.job.getId());

        try {
            this.client.getChannelServer().getWorldInterface().memberLevelJobUpdate(this.mgc);
        } catch (RemoteException re) {
            log.error("RemoteExcept while trying to update level/job in guild.", re);
        }
    }

    public String guildCost() {
        return nf.format(MapleGuild.CREATE_GUILD_COST);
    }

    public String emblemCost() {
        return nf.format(MapleGuild.CHANGE_EMBLEM_COST);
    }

    public String capacityCost() {
        return nf.format(MapleGuild.INCREASE_CAPACITY_COST);
    }

    public void genericGuildMessage(int code) {
        this.client.getSession().write(MaplePacketCreator.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        if (guildid <= 0 || guildrank != 1) {
            log.warn(this.name + " tried to disband and s/he is either not in a guild or not leader.");
            return;
        }

        try {
            client.getChannelServer().getWorldInterface().disbandGuild(this.guildid);
        } catch (Exception e) {
            log.error("Error while disbanding guild.", e);
        }
    }

    public void increaseGuildCapacity() {
        if (this.getMeso() < MapleGuild.INCREASE_CAPACITY_COST) {
            client.getSession().write(MaplePacketCreator.serverNotice(1, "You do not have enough mesos."));
            return;
        }

        if (this.guildid <= 0) {
            log.info(this.name + " is trying to increase guild capacity without being in the guild.");
            return;
        }

        try {
            client.getChannelServer().getWorldInterface().increaseGuildCapacity(this.guildid);
        } catch (Exception e) {
            log.error("Error while increasing capacity.", e);
            return;
        }

        this.gainMeso(-MapleGuild.INCREASE_CAPACITY_COST, true, false, true);
    }

    public void saveGuildStatus() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?");
            ps.setInt(1, this.guildid);
            ps.setInt(2, this.guildrank);
            ps.setInt(3, this.id);
            ps.execute();
            ps.close();
        } catch (SQLException se) {
            log.error("SQL error: " + se.getLocalizedMessage(), se);
        }
    }

    /**
     * Allows you to change someone's NXCash, Maple Points, and Gift Tokens!
     *
     * types:<br>
     * 0 = NX<br>
     * 1 = MaplePoints<br>
     * 2 = GiftTokens<br>
     *
     * @param type Type of currency to add
     * @param quantity how much to modify it by. Negatives subtract points, Positives add points.
     */
    public void addCSPoints(int type, int quantity) {
        switch (type) {
            case 0:
                this.nxcash += quantity;
                break;
            case 1:
                this.maplepoints += quantity;
                break;
            case 2:
                this.gifttokens += quantity;
                break;
        }
    }

    public void setCSPoints(int type, int quantity) {
        switch (type) {
            case 0:
                this.nxcash = quantity;
                break;
            case 1:
                this.maplepoints = quantity;
                break;
            case 2:
                this.gifttokens = quantity;
                break;
        }
    }

    public int getCSPoints(int type) {
        switch (type) {
            case 0:
                return nxcash;
            case 1:
                return maplepoints;
            case 2:
                return gifttokens;
            default:
                return 0;
        }
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void checkMessenger() {
        if (messenger != null && messengerPosition < 4 && messengerPosition > -1) {
            try {
                WorldChannelInterface wci = ChannelServer.getInstance(client.getChannel()).getWorldInterface();
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(client.getPlayer(), messengerPosition);
                wci.silentJoinMessenger(messenger.getId(), messengerplayer, messengerPosition);
                wci.updateMessenger(getClient().getPlayer().getMessenger().getId(), getClient().getPlayer().getName(), getClient().getChannel());
            } catch (RemoteException e) {
                client.getChannelServer().reconnectWorld();
            }
        }
    }

    public int getMessengerPosition() {
        return messengerPosition;
    }

    public void setMessengerPosition(int position) {
        this.messengerPosition = position;
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
        MapleInventory iv = inventory[type.ordinal()];
        int possesed = iv.countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    public void setMuted(int muted) {
        this.muted = muted;
    }

    public void changeMuted() {
        if (muted == 0) {
            muted = 1;
        } else if (muted == 1) {
            muted = 0;
        }
    }

    public boolean isMuted() {
        return muted > 0;
    }

    public int getMuted() {
        return muted;
    }

    private static class MapleBuffStatValueHolder {

        public MapleStatEffect effect;
        public long startTime;
        public int value;
        public ScheduledFuture<?> schedule;

        public MapleBuffStatValueHolder(MapleStatEffect effect, long startTime, ScheduledFuture<?> schedule, int value) {
            super();
            this.effect = effect;
            this.startTime = startTime;
            this.schedule = schedule;
            this.value = value;
        }
    }

    public static class MapleCoolDownValueHolder {

        public int skillId;
        public long startTime;
        public long length;
        public ScheduledFuture<?> timer;

        public MapleCoolDownValueHolder(int skillId, long startTime, long length, ScheduledFuture<?> timer) {
            super();
            this.skillId = skillId;
            this.startTime = startTime;
            this.length = length;
            this.timer = timer;
        }
    }

    public static class SkillEntry {

        public int skillevel;
        public int masterlevel;

        public SkillEntry(int skillevel, int masterlevel) {
            this.skillevel = skillevel;
            this.masterlevel = masterlevel;
        }

        @Override
        public String toString() {
            return skillevel + ":" + masterlevel;
        }
    }

    public enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }

    public void addCooldown(int skillId, long startTime, long length, ScheduledFuture<?> timer) {
        this.cooldowns.put(Integer.valueOf(skillId), new MapleCoolDownValueHolder(skillId, startTime, length, timer));
    }

    public void addCooldownWithTimer(int skillId, long startTime, long length) {
        if (this.cooldowns.containsKey(Integer.valueOf(skillId))) {
            this.cooldowns.remove(skillId);
        }
        ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(this, skillId), length);
        this.cooldowns.put(Integer.valueOf(skillId), new MapleCoolDownValueHolder(skillId, startTime, length, timer));
    }

    public boolean containsCooldown(int skillId) {
        return this.cooldowns.containsKey(skillId);
    }

    public void removeCooldown(int skillId) {
        if (this.cooldowns.containsKey(Integer.valueOf(skillId))) {
            this.cooldowns.remove(Integer.valueOf(skillId));
        }
    }

    public Map<Integer, MapleCoolDownValueHolder> getCooldowns() {
        return cooldowns;
    }

    public int getCooldownTimeLeft(int skillid) {
        int cooltime = 0;
        if (cooldowns.containsKey(skillid)) {
            MapleCoolDownValueHolder cooldown = cooldowns.get(skillid);
        }
        return cooltime;
    }

    public static class CancelCooldownAction implements Runnable {

        private int skillId;
        private WeakReference<MapleCharacter> target;

        public CancelCooldownAction(MapleCharacter target, int skillId) {
            this.target = new WeakReference<MapleCharacter>(target);
            this.skillId = skillId;
        }

        @Override
        public void run() {
            MapleCharacter realTarget = target.get();
            if (realTarget != null) {
                realTarget.removeCooldown(skillId);
                realTarget.getClient().getSession().write(MaplePacketCreator.buffCooldown(skillId, 0));
            }
        }
    }

    public boolean isNXCodeValid(String code, boolean validcode) throws SQLException {

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `valid` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            validcode = rs.getInt("valid") == 0 ? false : true;
        }

        rs.close();
        ps.close();

        return validcode;
    }

    public int getNXCodeType(String code) throws SQLException {

        int type = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `type` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            type = rs.getInt("type");
        }

        rs.close();
        ps.close();

        return type;
    }

    public int getNXCodeItem(String code) throws SQLException {

        int item = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `item` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            item = rs.getInt("item");
        }

        rs.close();
        ps.close();

        return item;
    }

    public void setNXCodeUsed(String code) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `valid` = 0 WHERE code = ?");
        ps.setString(1, code);
        ps.executeUpdate();
        ps = con.prepareStatement("UPDATE nxcode SET `user` = ? WHERE code = ?");
        ps.setString(1, this.getName());
        ps.setString(2, code);
        ps.executeUpdate();
        ps.close();
    }

    public void setInCS(boolean yesno) {
        this.incs = yesno;
    }

    public boolean inCS() {
        return this.incs;
    }

    public int[] getWishList() {
        return wishList;
    }

    public List<Integer> getTrimmedWishList() {
        List<Integer> trimmedWishList = new ArrayList<Integer>();
        for (int wishListItem : wishList) {
            if (wishListItem > 0) {
                trimmedWishList.add(wishListItem);
            }
        }
        return trimmedWishList;
    }

    public void setWishList(int[] wishList) {
        this.wishList = wishList;
    }

    public int[] getTeleportMaps() {
        return teleportMaps;
    }

    public void setTeleportMaps(int[] teleportMaps) {
        this.teleportMaps = teleportMaps;
    }

    public boolean addTeleportMap(int map) {
        for (int i = 0; i < teleportMaps.length; i++) {
            if (teleportMaps[i] == 999999999) {
                teleportMaps[i] = map;
                return true;
            }
        }
        return false;
    }

    public boolean removeTeleportMap(int map) {
        for (int i = 0; i < teleportMaps.length; i++) {
            if (teleportMaps[i] == map) {
                teleportMaps[i] = 999999999;
                return true;
            }
        }
        return false;
    }

    public int[] getVipTeleportMaps() {
        return vipTeleportMaps;
    }

    public void setVipTeleportMaps(int[] vipTeleportMaps) {
        this.vipTeleportMaps = vipTeleportMaps;
    }

    public boolean addVipTeleportMap(int map) {
        for (int i = 0; i < vipTeleportMaps.length; i++) {
            if (vipTeleportMaps[i] == 999999999) {
                vipTeleportMaps[i] = map;
                return true;
            }
        }
        return false;
    }

    public boolean removeVipTeleportMap(int map) {
        for (int i = 0; i < vipTeleportMaps.length; i++) {
            if (vipTeleportMaps[i] == map) {
                vipTeleportMaps[i] = 999999999;
                return true;
            }
        }
        return false;
    }

    public NPCScriptInfo getNpcScriptInfo() {
        return npcScriptInfo;
    }

    public void setNpcScriptInfo(NPCScriptInfo npcScriptInfo) {
        this.npcScriptInfo = npcScriptInfo;
    }

    public MapleMonsterBook getMonsterbook() {
        return monsterbook;
    }

    public MapleRandom getRandom() {
        return random;
    }

    public MapleQuestRecord getQuestRecord() {
        return questRecord;
    }
}
