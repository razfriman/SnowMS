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

package net.sf.odinms.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.IEquip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleKeyBinding;
import net.sf.odinms.client.MapleMacro;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.IEquip.ScrollResult;
import net.sf.odinms.client.MapleCharacter.MapleCoolDownValueHolder;
import net.sf.odinms.client.MapleMonsterBook;
import net.sf.odinms.client.MapleMonsterBook.MonsterCard;
import net.sf.odinms.client.MapleMount;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.net.ByteArrayMaplePacket;
import net.sf.odinms.net.LongValueHolder;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.MapleServer.MapleServerType;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.channel.handler.SummonDamageHandler.SummonAttackEntry;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.MapleWorld;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.net.world.guild.MapleGuildSummary;
import net.sf.odinms.scripting.npc.NPCConversationManager.NPCDialogType;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleMiniGame;
import net.sf.odinms.client.MapleMiniGameStats;
import net.sf.odinms.server.GameConstants.StorageActionType;
import net.sf.odinms.server.MaplePlayerShop;
import net.sf.odinms.server.MaplePlayerShopItem;
import net.sf.odinms.server.MapleShopItem;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.attack.AttackInfo;
import net.sf.odinms.server.attack.PGMRInfo;
import net.sf.odinms.server.attack.SpecialSkillInfo;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.life.MonsterSkill;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides all MapleStory packets needed in one place.
 * TODO: elminate references to big objects (EG: MapleCharacter) to obtain 1 variable
 * 
 * @author Frz
 */
public class MaplePacketCreator {

    private final static byte[] CHAR_INFO_MAGIC = new byte[]{(byte) 0xff, (byte) 0xc9, (byte) 0x9a, 0x3b};//int: 999999999
    private final static byte[] ITEM_MAGIC = new byte[]{(byte) 0x80, 5};
    private final static long NO_EXPIRATION = 150842304000000000L;
    public static final List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();
    private static Logger log = LoggerFactory.getLogger(MaplePacketCreator.class);

    /**
     * Sends a hello packet.
     *
     * @param mapleVersion The maple client version.
     * @param sendIv the IV used by the server for sending
     * @param recvIv the IV used by the server for receiving
     */
    public static MaplePacket getHello(short mapleVersion, byte[] sendIv, byte[] recvIv, MapleServerType serverType) {
        return getHello(mapleVersion, "", sendIv, recvIv, serverType);
    }

    public static MaplePacket getHello(short mapleVersion, String patchLoc, byte[] sendIv, byte[] recvIv, MapleServerType serverType) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(patchLoc.equals("") ? 0x0D : 0x0E);
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString(patchLoc);//login has '0' here, channel have ''
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(serverType.getType());

        return mplew.getPacket();
    }

    /**
     * Sends a ping packet.
     *
     * @return The packet.
     */
    public static MaplePacket getPing() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
        mplew.writeHeader(SendPacketOpcode.PING);
        return mplew.getPacket();
    }

    /**
     * Gets a login failed packet.
     *
     * Possible values for <code>reason</code>:<br>
     * 3: ID deleted or blocked<br>
     * 4: Incorrect password<br>
     * 5: Not a registered id<br>
     * 6: System error<br>
     * 7: Already logged in<br>
     * 8: System error<br>
     * 9: System error<br>
     * 10: Cannot process so many connections<br>
     * 11: Only users older than 20 can use this channel<br>
     * 13: Unable to log on as master at this ip<br>
     * 14: Wrong gateway or personal info and weird korean button<br>
     * 15: Processing request with that korean button!<br>
     * 16: Please verify your account through email...<br>
     * 17: Wrong gateway or personal info<br>
     * 21: Please verify your account through email...<br>
     * 23: License agreement<br>
     * 25: Maple Europe notice =[<br>
     * 27: Some weird full client notice, probably for trial versions<br>
     *
     * @param reason The reason logging in failed.
     * @return The login failed packet.
     */
    public static MaplePacket getLoginFailed(int reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeHeader(SendPacketOpcode.LOGIN_STATUS);
        mplew.writeInt(reason);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static MaplePacket getPermBan(byte reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        // Response.WriteHexString("00 00 02 00 01 01 01 01 01 00");
        mplew.writeHeader(SendPacketOpcode.LOGIN_STATUS);
        mplew.write(2);
        mplew.write(0);
        mplew.write(0);//or 1??
        mplew.write(reason);
        mplew.writeHexString("01 01 01 01 00");
        return mplew.getPacket();
    }

    public static MaplePacket getTempBan(long timestampTill, byte reason) {

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);
        mplew.writeHeader(SendPacketOpcode.LOGIN_STATUS);
        mplew.write(2);
        mplew.write(0);//need verification
        mplew.writeInt(0);
        mplew.write(reason);
        mplew.writeLong(timestampTill);
        return mplew.getPacket();
    }

    /**
     * Gets a successful gender set Packet
     *
     * @param gender The account gender.
     * @return The gender set packet.
     */
    public static MaplePacket setGender(int gender) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeHeader(SendPacketOpcode.GENDER_SET);
        mplew.write(gender);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket getPinAssgined() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PIN_ASSIGNED);
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     * Gets a successful authentication and PIN Request packet.
     *
     * @param account The account name.
     * @return The PIN request packet.
     */
    public static MaplePacket getAuthSuccessRequestPin(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.LOGIN_STATUS);
        mplew.write(0); // type

        mplew.write(0);//needs verification?
        mplew.writeShort(0); // nUseDay?
        mplew.writeShort(0);
        mplew.writeInt(c.getAccID());
        switch (c.getStatus()) {
            case 1:
                mplew.write(0x0A);
                break; //Gender Select
            case 2:
                mplew.write(0x0B);
                break; //Pin Select
            default:
                mplew.write(c.getGender());
                break; //Gender
        }
        mplew.write(c.isGm() ? 1 : 0);//admin byte nGradeCode
        mplew.write(1);
        mplew.write(1);//account flags?
        mplew.writeMapleAsciiString(c.getAccountName());
        mplew.write(1); // purchaseExp
        mplew.write(c.getChatBlock());
        mplew.writeLong(DateUtil.getFileTimestamp(c.getChatBlockDate().getTimeInMillis()));
        mplew.writeLong(DateUtil.getFileTimestamp(c.getCreateDate().getTimeInMillis()));
        mplew.writeInt(0);//total # of characters
        mplew.write(0); //0=enabled 1=disabled    PIN
        mplew.write(2); // 2=disabled 1=ask 0=register PIC
        return mplew.getPacket();
    }

    /**
     * Gets a packet detailing a PIN operation.
     *
     * Possible values for <code>mode</code>:<br>
     * 0 - PIN was accepted<br>
     * 1 - Register a new PIN<br>
     * 2 - Invalid pin / Reenter<br>
     * 3 - Connection failed due to system error<br>
     * 4 - Enter the pin
     *
     * @param mode The mode.
     */
    public static MaplePacket pinOperation(byte mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeHeader(SendPacketOpcode.PIN_OPERATION);
        mplew.write(mode);
        return mplew.getPacket();
    }

    /**
     * Gets a packet requesting the client enter a PIN.
     *
     * @return The request PIN packet.
     */
    public static MaplePacket requestPin() {
        return pinOperation((byte) 4);
    }

    /**
     * Gets a packet requesting the PIN after a failed attempt.
     *
     * @return The failed PIN packet.
     */
    public static MaplePacket requestPinAfterFailure() {
        return pinOperation((byte) 2);
    }

    /**
     * Gets a packet saying the PIN has been accepted.
     *
     * @return The PIN accepted packet.
     */
    public static MaplePacket pinAccepted() {
        return pinOperation((byte) 0);
    }

    /**
     * Gets a packet detailing a server and its channels.
     *
     * @param world MapleWorld to get server info from.
     * @param channelLoad Load of the channel - 1200 seems to be max.
     * @return The server info packet.
     */
    public static MaplePacket getServerList(MapleWorld world, Map<Integer, Integer> channelLoad, String eventMessage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SERVERLIST);
        mplew.write(world.getId());
        mplew.writeMapleAsciiString(world.getName());
        mplew.write(world.getWorldStatusType().getValue());
        mplew.writeMapleAsciiString(eventMessage);//Event Message
        mplew.writeShort(100); // rate modifier
        mplew.writeShort(100); // rate modifier, don't ask O.O!
        mplew.write(0);

        int lastChannel = 1;
        Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(i)) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);

        int load;
        for (int i = 1; i <= lastChannel; i++) {
            if (channels.contains(i)) {
                load = channelLoad.get(i);
            } else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(world.getName() + "-" + i);
            mplew.writeInt(load);
            mplew.write(world.getId());
            mplew.write(i - 1);
            mplew.write(0);
        }
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    /**
     * Gets a packet saying that the server list is over.
     *
     * @return The end of server list packet.
     */
    public static MaplePacket getEndOfServerList() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SERVERLIST);
        mplew.write(0xFF);
        return mplew.getPacket();
    }

    public static MaplePacket getRecommendedServer(List<MapleWorld> servers) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.RECOMMENDED_SERVERLIST);
        mplew.write(servers.size());
        for (MapleWorld world : servers) {
            mplew.writeInt(world.getId());
            mplew.writeMapleAsciiString(world.getRecommendedMessage());
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet detailing a server status message.
     *
     * Possible values for <code>status</code>:<br>
     * 0 - Normal<br>
     * 1 - Highly populated<br>
     * 2 - Full
     *
     * @param status The server status.
     * @return The server status packet.
     */
    public static MaplePacket getServerStatus(int worldId, int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SERVERSTATUS);
        mplew.write(worldId);
        mplew.write(status);
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client the IP of the channel server.
     *
     * @param inetAddr The InetAddress of the requested channel server.
     * @param port The port the channel is on.
     * @param clientId The ID of the client.
     * @return The server IP packet.
     */
    public static MaplePacket getServerIP(InetAddress inetAddr, int port, int clientId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SERVER_IP);
        mplew.writeShort(0);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        mplew.writeInt(clientId); // this gets repeated to the channel server
        mplew.writeInt(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client the IP of the new channel.
     *
     * @param inetAddr The InetAddress of the requested channel server.
     * @param port The port the channel is on.
     * @return The server IP packet.
     */
    public static MaplePacket getChannelChange(InetAddress inetAddr, int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CHANGE_CHANNEL);
        mplew.write(1);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        return mplew.getPacket();
    }

    public static MaplePacket getChannelSelected() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CHANNEL_SELECTED);
        mplew.writeShort(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     * Gets a packet with a list of characters.
     *
     * @param c The MapleClient to load characters of.
     * @param world MapleWorld to load from.
     * @return The character list packet.
     */
    public static MaplePacket getCharList(MapleClient c, MapleWorld world) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.CHARLIST);
        mplew.write(0); // world?
        List<MapleCharacter> chars = c.loadCharacters(world.getId(), false);
        mplew.write(chars.size());

        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr);
        }
        mplew.write(2); //no pic
        mplew.writeInt(world.getMaxCharacters());
        return mplew.getPacket();
    }

    public static MaplePacket showAllCharactersInfo(int world, int unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.VIEW_ALL_CHAR);
        mplew.write(1);
        mplew.writeInt(world);
        mplew.writeInt(unk);

        return mplew.getPacket();
    }

    public static MaplePacket showAllCharactersWorld(int world, List<MapleCharacter> chars) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.VIEW_ALL_CHAR);
        mplew.write(0);
        mplew.write(world);
        mplew.write(chars.size());

        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr);
        }

        return mplew.getPacket();
    }

    /**
     * Adds character stats to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add the stats of.
     */
    private static void addCharStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getName(), '\0', 13));
        mplew.write(chr.getGender().getValue());
        mplew.write(chr.getSkinColor().getId());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob().getId());
        mplew.writeShort(chr.getStr());
        mplew.writeShort(chr.getDex());
        mplew.writeShort(chr.getInt());
        mplew.writeShort(chr.getLuk());
        mplew.writeShort(chr.getHp());
        mplew.writeShort(chr.getMaxHp());
        mplew.writeShort(chr.getMp());
        mplew.writeShort(chr.getMaxMp());
        mplew.writeShort(chr.getRemainingAp());
        if (!chr.getJob().isEvan()) {
        mplew.writeShort(chr.getRemainingSp());
        } else {
        mplew.write(chr.getRemainingSp());
        }
        mplew.writeInt(chr.getExp());
        mplew.writeShort(chr.getFame());
        mplew.writeInt(0); // gachapon exp
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getInitialSpawnpoint());
        mplew.writeInt(0);

    }

    /**
     * Adds the aesthetic aspects of a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add the looks of.
     * @param mega Unknown
     */
    private static void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega) {
        mplew.write(chr.getGender().getValue());
        mplew.write(chr.getSkinColor().getId()); // skin color
        mplew.writeInt(chr.getFace()); // face
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(chr.getHair()); // hair

        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
        Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (IItem item : equip.list()) {
            byte pos = (byte) (item.getPosition() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, item.getItemId());
            } else if (pos > 100 && pos != 111) { // don't ask. o.o

                pos -= 100;
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }
        for (Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF); // end of visible items
        // masked items

        for (Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        /*
         * for (IItem item : equip.list()) { byte pos = (byte)(item.getPosition() * -1); if (pos > 100) {
         * mplew.write(pos - 100); mplew.writeInt(item.getItemId()); } }
         */
        // ending markers
        mplew.write(0xFF);
        IItem cWeapon = equip.getItem((byte) -111);
        if (cWeapon != null) {
            mplew.writeInt(cWeapon.getItemId());
        } else {
            mplew.writeInt(0); // cashweapon

        }

        for (int i = 0; i < 3; i++) {
            if (chr.getPet(i) != null) {
                mplew.writeInt(chr.getPet(i).getItemId());
            } else {
                mplew.writeInt(0);
            }
        }
    }

    /**
     * Adds an entry for a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add.
     */
    private static void addCharEntry(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {//BOKRNE 62
        addCharStats(mplew, chr);
        addCharLook(mplew, chr, false);

        mplew.write(0);

        mplew.write(1); // world rank enabled (next 4 ints are not sent if disabled)
        mplew.writeInt(chr.getRank()); // world rank
        mplew.writeInt(chr.getRankMove()); // move (negative is downwards)
        mplew.writeInt(chr.getJobRank()); // job rank
        mplew.writeInt(chr.getJobRankMove()); // move (negative is downwards)

    }

    /**
     * Adds a quest info entry for a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add quest info about.
     */
    private static void addQuestRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeMapleAsciiString("");
            //mplew.writeMapleAsciiString(q.getQuest().getName());//Unk? some quest related string
        }
        List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeLong(DateUtil.getFileTimestamp(q.getCompletionTime()));
        }
    }

    private static void addMiniGameRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        //mplew.writeInt(0);
        //mplew.writeInt(0);
        //mplew.writeInt(0);
        //mplew.writeInt(0);
        //mplew.writeInt(0);
    }

    private static void addCoupleRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        //mplew.write(new byte[0x21]);
    }

    private static void addFriendRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        //mplew.write(new byte[0x25]);
    }

    private static void addMarriageRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        //mplew.write(new byte[0x30]);
    }

    private static void addBlessingOfFairyRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(0);//Has KOC Character
        //mplew.writeMapleAsciiString("NAME OF KOC CHAR");
    }

    /**
     * Adds the inventory item info to an existing MaplePacketLittleEndianWriter
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add inventory info about.
     */
    private static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {

        mplew.writeInt(chr.getMeso()); // mesos
        mplew.write(100); // equip slots
        mplew.write(100); // use slots
        mplew.write(100); // set-up slots
        mplew.write(100); // etc slots
        mplew.write(100); // cash slots

        mplew.writeHexString("00 40 E0 FD 3B 37 4F 01");

        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (IItem item : equippedC) {
            equipped.add((Item) item);
        }
        Collections.sort(equipped);

        for (Item item : equipped) {
            addItemInfo(mplew, item);
        }

        mplew.writeShort(0); // new?

        mplew.writeShort(0); // start of equip inventory

        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }

        mplew.writeShort(0); //new?
        mplew.write(0); //new?

        mplew.write(0); // start of use inventory

        iv = chr.getInventory(MapleInventoryType.USE);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of set-up inventory

        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of etc inventory

        iv = chr.getInventory(MapleInventoryType.ETC);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of cash inventory

        iv = chr.getInventory(MapleInventoryType.CASH);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // end of cash inventory / start of skills
    }

    /**
     * Adds the skill and cooldown info to an existing MaplePacketLittleEndianWriter
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add skill info about.
     */
    private static void addSkillRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {


        Map<ISkill, MapleCharacter.SkillEntry> skills = chr.getSkills();
        mplew.writeShort(skills.size());
        for (Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
            mplew.writeInt(skill.getKey().getId());
            mplew.writeInt(skill.getValue().skillevel);
            mplew.writeLong(NO_EXPIRATION);
            if (skill.getKey().isFourthJob()) {
                mplew.writeInt(skill.getValue().masterlevel);
            }

        }


        Map<Integer, MapleCoolDownValueHolder> coolDowns = chr.getCooldowns();
        mplew.writeShort(coolDowns.size());
        for (Entry<Integer, MapleCoolDownValueHolder> cooldown : coolDowns.entrySet()) {
            mplew.writeInt(cooldown.getKey());
            mplew.writeShort(0);//TODO: ADD COOLDOWN COOLTIME LENGTH HERE
        }
    }

    /**
     * Adds the teleport rock info to an existing MaplePacketLittleEndianWriter
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add skill info about.
     */
    private static void addTeleportRockRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(chr.getTeleportMaps()[i]);
        }
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(chr.getVipTeleportMaps()[i]);
        }
    }

    /**
     * Adds the monster book info to an existing MaplePacketLittleEndianWriter
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add skill info about.
     */
    private static void addMonsterBookRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getMonsterbook().getCover() != 0 ? MapleMonsterBook.getMonsterCardId(chr.getMonsterbook().getCover()) : 0);
        mplew.write(0);
        List<MonsterCard> allCards = chr.getMonsterbook().getAllCards();
        mplew.writeShort(allCards.size());
        for (MonsterCard card : allCards) {
            mplew.writeShort(MapleItemInformationProvider.getCardId(card.getId()));
            mplew.write(card.getLevel());
        }
    }

    private static void addPartyQuestRecord(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int quests = 0;
        mplew.writeShort(quests);
        for (int i = 0; i < quests; i++) {
            mplew.writeShort(0); // Quest ID (needs to be in quest record too)
            mplew.writeMapleAsciiString(""); // Party Quest data string
        }
    }

    /**
     * Gets character info for a character.
     *
     * @param chr The character to get info about.
     * @return The character info packet.
     */
    public static MaplePacket getCharInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.WARP_TO_MAP);

        mplew.writeInt(chr.getClient().getChannel() - 1);
        chr.incrementCurrentFieldKey();
        mplew.write(chr.getCurrentFieldKey());
        mplew.write(1); //bCharacterData
        mplew.writeShort(0); //amountof string

        mplew.writeInt(chr.getRandom().getX());
        mplew.writeInt(chr.getRandom().getY());
        mplew.writeInt(chr.getRandom().getZ());

        mplew.writeLong(-1);//Mask
        mplew.write(0);//??
        addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity());
        addBlessingOfFairyRecord(mplew, chr);
        addInventoryInfo(mplew, chr);
        addSkillRecord(mplew, chr);
        addQuestRecord(mplew, chr);
        addMiniGameRecord(mplew, chr);
        addCoupleRecord(mplew, chr);
        addFriendRecord(mplew, chr);
        addMarriageRecord(mplew, chr);
        addTeleportRockRecord(mplew, chr);
        addMonsterBookRecord(mplew, chr);
        mplew.writeShort(0);
        addPartyQuestRecord(mplew, chr);
        mplew.writeShort(0);
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        return mplew.getPacket();
    }

    private static void addPetShowInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        MaplePet[] pets = chr.getAllPets();
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                mplew.write(pets[i].getUniqueId());
                mplew.writeInt(pets[i].getItemId());
                mplew.writeMapleAsciiString(pets[i].getName());
                mplew.write(pets[i].getLevel());
                mplew.writeShort(pets[i].getCloseness());
                mplew.write(pets[i].getFullness());
                mplew.writeShort(0);//PET ITEMS
                mplew.writeInt(pets[i].getEquip(chr, i));
            }
        }
        mplew.write(0);//End of pets, Start of mount
    }

    private static void addMountShowInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (chr.getMount() != null) {
            mplew.write(1);
            mplew.writeInt(chr.getMount().getLevel());
            mplew.writeInt(chr.getMount().getExp());
            mplew.writeInt(chr.getMount().getTiredness());
        } else {
            mplew.write(0);
        }
    }

    private static void addWishlistShowInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(chr.getTrimmedWishList().size());
        for (int wishListItem : chr.getTrimmedWishList()) {
            mplew.writeInt(wishListItem);
        }
    }

    private static void addMonsterBookShowInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        MapleMonsterBook book = chr.getMonsterbook();
        mplew.writeInt(book.getLevel());
        mplew.writeInt(book.getNormalCount());
        mplew.writeInt(book.getSpecialCount());
        mplew.writeInt(book.getSize());
        mplew.writeInt(book.getCover());
    }

    private static void addMedalShowInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(0); // Current Medal
        mplew.writeShort(0); // # of Medals (ForEach: writeShort(id))
    }

    /**
     * Gets an empty stat update.
     *
     * @return The empy stat update packet.
     */
    public static MaplePacket enableActions() {
        return updatePlayerStats(EMPTY_STATUPDATE, true);
    }

    /**
     * Gets an update for specified stats.
     *
     * @param stats The stats to update.
     * @return The stat update packet.
     */
    public static MaplePacket updatePlayerStats(List<Pair<MapleStat, Integer>> stats) {
        return updatePlayerStats(stats, false);
    }

    /**
     * Gets an update for specified stats.
     *
     * @param stats The list of stats to update.
     * @param itemReaction Result of an item reaction(?)
     * @return The stat update packet.
     */
    public static MaplePacket updatePlayerStats(List<Pair<MapleStat, Integer>> stats, boolean itemReaction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.UPDATE_STATS);
        if (itemReaction) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        int updateMask = 0;
        for (Pair<MapleStat, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        List<Pair<MapleStat, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, new Comparator<Pair<MapleStat, Integer>>() {

                @Override
                public int compare(Pair<MapleStat, Integer> o1, Pair<MapleStat, Integer> o2) {
                    int val1 = o1.getLeft().getValue();
                    int val2 = o2.getLeft().getValue();
                    return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
                }
            });
        }
        mplew.writeInt(updateMask);
        for (Pair<MapleStat, Integer> statupdate : mystats) {
            if (statupdate.getLeft().getValue() >= 1) {
                if (statupdate.getLeft().getValue() == 0x1) {
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else if (statupdate.getLeft().getValue() <= 0x4) {
                    mplew.writeInt(statupdate.getRight());
                } else if (statupdate.getLeft().getValue() < 0x20) {
                    mplew.write(statupdate.getRight().shortValue());
                } else if (statupdate.getLeft().getValue() < 0xFFFF) {
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else {
                    mplew.writeInt(statupdate.getRight().intValue());
                }
            }
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to change maps.
     *
     * @param to The <code>MapleMap</code> to warp to.
     * @param spawnPoint The spawn portal number to spawn at.
     * @param chr The character warping to <code>to</code>
     * @return The map change packet.
     */
    public static MaplePacket getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.WARP_TO_MAP);

        mplew.writeInt(chr.getClient().getChannel() - 1);
        chr.incrementCurrentFieldKey();
        mplew.write(chr.getCurrentFieldKey());
        mplew.write(0); // bCharacterData
        mplew.writeShort(0); //# of strings to read?

        mplew.write(0); //boolean?
        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeShort(chr.getHp());
        mplew.write(0);
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));

        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a portal.
     *
     * @param townId The ID of the town the portal goes to.
     * @param targetId The ID of the target.
     * @param pos Where to put the portal.
     * @return The portal spawn packet.
     */
    public static MaplePacket spawnPortal(int townId, int targetId, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SPAWN_PORTAL);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if (pos != null) {
            mplew.writePoint(pos);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a door.
     *
     * @param oid The door's object ID.
     * @param pos The position of the door.
     * @param town
     * @return The remove door packet.
     */
    public static MaplePacket spawnDoor(int oid, Point pos, boolean town) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SPAWN_DOOR);
        // B9 00 00 47 1E 00 00
        mplew.write(town ? 1 : 0);
        mplew.writeInt(oid);
        mplew.writePoint(pos);
        return mplew.getPacket();
    }

    /**
     * Gets a packet to remove a door.
     *
     * @param oid The door's ID.
     * @param town
     * @return The remove door packet.
     */
    public static MaplePacket removeDoor(int oid, boolean town) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (town) {
            mplew.writeHeader(SendPacketOpcode.SPAWN_PORTAL);
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
        } else {
            mplew.writeHeader(SendPacketOpcode.REMOVE_DOOR);
            mplew.write(/*town ? 1 : */0);
            mplew.writeInt(oid);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a special map object.
     *
     * @param summon The MapleSummon to spawn.
     * @param skillLevel The level of the skill used.
     * @param animated Animated spawn?
     * @return The spawn packet for the map object.
     */
    public static MaplePacket spawnSpecialMapObject(MapleSummon summon, int skillLevel, boolean animated) {
        // [72 00] [29 1D 02 00] [FD FE 30 00] [19] [7D FF] [BA 00] [04] [01] [00] [03] [01] [00]
        // [85 00] [99 31 00 00] [a6 53 01 00] [2d 5d 20 00] [01] [2f fe] [d7 00] [04] [37] [00] [01] [01] [00]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SPAWN_SPECIAL_MAPOBJECT);

        mplew.writeInt(summon.getOwner().getId());
        mplew.writeInt(summon.getObjectId()); // Supposed to be Object ID, but this works too! <3
        mplew.writeInt(summon.getSkill());
        mplew.write(skillLevel);
        mplew.writePoint(summon.getPosition());
        mplew.write(4); // test
        mplew.write(0x53); // test
        mplew.write(1); // test
        mplew.write(summon.getMovementType().getValue()); // 0 = don't move, 1 = follow
        mplew.write(summon.isPuppet() ? 0 : 1); // 0 and the summon can't attack - but puppets don't
        mplew.write(animated ? 0 : 1);

        return mplew.getPacket();
    }

    /**
     * Gets a packet to remove a special map object.
     *
     * @param summon The MapleSummon to remove.
     * @param animated Animated removal
     * @return The packet removing the object.
     */
    public static MaplePacket removeSpecialMapObject(MapleSummon summon, boolean animated) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REMOVE_SPECIAL_MAPOBJECT);
        mplew.writeInt(summon.getOwner().getId());
        mplew.writeInt(summon.getObjectId());
        mplew.write(animated ? 4 : 1); // ?
        return mplew.getPacket();
    }

    /**
     * Adds info about an item to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to write info about.
     */
    protected static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item) {
        addItemInfo(mplew, item, false, false);
    }

    /**
     * Adds expiration time info to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param time The expiration time.
     * @param showexpirationtime Show the expiration time?
     */
    private static void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time, boolean showexpirationtime) {
        mplew.writeInt(DateUtil.getItemTimestamp(time));
        mplew.write(showexpirationtime ? 1 : 2);
    }

    /**
     * Adds item info to existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to add info about.
     * @param zeroPosition Is the position zero?
     * @param leaveOut Leave out the item if position is zero?
     */
    private static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, boolean zeroPosition, boolean leaveOut) {
        //TODO CLEAN ME
        boolean ring = false;
        IEquip equip = null;
        if (item.getType() == IItem.EQUIP) {
            equip = (IEquip) item;
            if (equip.getRingId() > -1) {
                ring = true;
            }
        }
        byte pos = item.getPosition();
        boolean masking = false;
        boolean equipped = false;
        if (zeroPosition) {
            if (!leaveOut) {
                mplew.write(0);
            }
        } else if (pos <= (byte) -1) {
            pos *= -1;
            if (pos > 100 || ring) {
                masking = true;
                mplew.writeShort(pos - 100);
            } else {
                mplew.writeShort(pos);
            }
            equipped = true;
        } else {
            mplew.write(item.getPosition());
        }
        if (item.getPetId() > -1) {
            mplew.write(3);
        } else {
            mplew.write(item.getType());
        }
        mplew.writeInt(item.getItemId());
        if (ring) {
            mplew.write(1);
            mplew.writeInt(equip.getRingId());
            mplew.writeInt(0);
        }
        if (item.getPetId() != -1) {
            MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getPosition(), item.getPetId());
            String petname = pet.getName();
            mplew.write(1);
            mplew.writeLong(item.getPetId());
            mplew.write(0);
            mplew.write(ITEM_MAGIC);
            addExpirationTime(mplew, item.getExpirationTime(System.currentTimeMillis()), true);
            if (petname.length() > 13) {
                petname = petname.substring(0, 13);
            }
            mplew.writeAsciiString(StringUtil.getLeftPaddedStr(petname, '\0', 13));
            mplew.write(pet.getLevel());
            mplew.writeShort(pet.getCloseness());
            mplew.write(pet.getFullness());
            mplew.writeLong(DateUtil.getFileTimestamp((long) (System.currentTimeMillis() * 1.5)));
            mplew.writeInt(1);
            mplew.writeInt(0);
            return;
        }
        if (!masking && ring) {
            mplew.write(0);
            mplew.write(ITEM_MAGIC);
            addExpirationTime(mplew, item.getExpirationTime(System.currentTimeMillis()), true);
        } else {
            mplew.write(MapleItemInformationProvider.getInventory(item.getItemId()) == 5 ? 1 : 0);
            if (MapleItemInformationProvider.getInventory(item.getItemId()) == 5) {
                mplew.writeLong(1000000);
            }
            mplew.write(0);
            mplew.write(ITEM_MAGIC);
            addExpirationTime(mplew, item.getExpirationTime(System.currentTimeMillis()), false);
        }
        if (item.getType() == IItem.EQUIP) {
            mplew.write(equip.getUpgradeSlots());
            mplew.write(equip.getLevel());
            mplew.writeShort(equip.getStr()); // str
            mplew.writeShort(equip.getDex()); // dex
            mplew.writeShort(equip.getInt()); // int
            mplew.writeShort(equip.getLuk()); // luk
            mplew.writeShort(equip.getHp()); // hp
            mplew.writeShort(equip.getMp()); // mp
            mplew.writeShort(equip.getWatk()); // watk
            mplew.writeShort(equip.getMatk()); // matk
            mplew.writeShort(equip.getWdef()); // wdef
            mplew.writeShort(equip.getMdef()); // mdef
            mplew.writeShort(equip.getAcc()); // accuracy
            mplew.writeShort(equip.getAvoid()); // avoid
            mplew.writeShort(equip.getHands()); // hands
            mplew.writeShort(equip.getSpeed()); // speed
            mplew.writeShort(equip.getJump()); // jump
            mplew.writeMapleAsciiString(equip.getOwner());
            mplew.write(equip.getMask());

            if (ring && !equipped) {
                mplew.write(0);
            }
            mplew.write(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.write(0);

            mplew.writeInt(0); // hammer?

            if (MapleItemInformationProvider.getInventory(item.getItemId()) != 5) {
                mplew.writeLong(-1);
            }
            mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
            mplew.writeInt(-1);
        } else {
            mplew.writeShort(item.getQuantity());
            mplew.writeMapleAsciiString(item.getOwner());
            mplew.writeShort(item.getMask());
            if (MapleItemInformationProvider.isRechargable(item.getItemId())) {
                mplew.writeInt(2);
                mplew.writeShort(0x54);
                mplew.write(0);
                mplew.write(0x34);
            }
        }
    }

    /**
     * Gets the response to a relog request.
     *
     * @return The relog response packet.
     */
    public static MaplePacket getRelogResponse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeHeader(SendPacketOpcode.RELOG_RESPONSE);
        mplew.write(1);
        return mplew.getPacket();
    }

    /**
     * Gets a server message packet.
     *
     * @param message The message to convey.
     * @return The server message packet.
     */
    public static MaplePacket serverMessage(String message) {
        return serverMessage(4, 0, message, true, false, -1);
    }

    /**
     * Gets a server notice packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Light blue background and lolwhut<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param message The message to convey.
     * @return The server notice packet.
     */
    public static MaplePacket serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false, false, -1);
    }

    /**
     * Gets a server notice packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Light blue background and lolwhut<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @return The server notice packet.
     */
    public static MaplePacket serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false, false, -1);
    }

    public static MaplePacket serverNotice(int type, int channel, String message, boolean ear) {
        return serverMessage(type, channel, message, false, ear, -1);
    }

    /**
     * Gets a server message packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Light blue background and lolwhut<br>
     * 3: Megaphone<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text<br>
     * 7: NPC Text<br>
     * 8: Item Expire(Use @sendItemExpired)
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @param servermessage Is this a scrolling ticker?
     * @return The server notice packet.
     */
    public static MaplePacket serverMessage(int type, int channel, String message, boolean servermessage, boolean ear, int npcId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SERVERMESSAGE);
        mplew.write(type);
        if (servermessage) {
            mplew.write(1);
        }
        mplew.writeMapleAsciiString(message);

        if (type == 3) {
            mplew.write(channel - 1); // channel
            mplew.write(ear ? 1 : 0); // Smega Ear
        }

        if (type == 6) {
            mplew.writeInt(0);
        }
        if (type == 7) {
            mplew.writeInt(npcId);
        }

        return mplew.getPacket();
    }

    public static MaplePacket sendItemExpired(List<IItem> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SERVERMESSAGE);
        mplew.write(8);
        mplew.write(items.size());
        for (IItem item : items) {
            mplew.writeInt(item.getItemId());
        }
        return mplew.getPacket();
    }

    /**
     * Gets an avatar megaphone packet.
     *
     * @param chr The character using the avatar megaphone.
     * @param channel The channel the character is on.
     * @param itemId The ID of the avatar-mega.
     * @param message The message that is sent.
     * @return The avatar mega packet.
     */
    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.AVATAR_MEGA);
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        for (String s : message) {
            mplew.writeMapleAsciiString(s);
        }
        mplew.writeInt(channel - 1); // channel

        mplew.write(0);
        addCharLook(mplew, chr, true);

        return mplew.getPacket();
    }

    /**
     * Gets a NPC spawn packet.
     *
     * @param life The NPC to spawn.
     * @param requestController Does the NPC want a controller?
     * @return The NPC spawn packet.
     */
    public static MaplePacket spawnNPC(MapleNPC life, boolean requestController, boolean show) {
        //A8 00 D6 22 00 00 92 71 0F 00 AD 01 5D 00 01 01 00 7B 01 DF 01 - spawn
        //AD 00 01 D6 22 00 00 92 71 0F 00 AD 01 5D 00 01 01 00 7B 01 DF 01 - request
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (requestController) {
            mplew.writeHeader(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER);
            mplew.write(1);

        } else {
            mplew.writeHeader(SendPacketOpcode.SPAWN_NPC);
        }
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write(life.getF() == 0 ? 1 : 0);//Opposite??
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(show ? 1 : 0);
        return mplew.getPacket();
    }

    /**
     * Gets a spawn monster packet.
     *
     * @param life The monster to spawn.
     * @param newSpawn Is it a new spawn?
     * @return The spawn monster packet.
     */
    public static MaplePacket spawnMonster(MapleMonster life, boolean newSpawn) {
        return spawnMonsterInternal(life, false, newSpawn, false);
    }

    /**
     * Gets a control monster packet.
     *
     * @param life The monster to give control to.
     * @param newSpawn Is it a new spawn?
     * @param aggro Aggressive monster?
     * @return The monster control packet.
     */
    public static MaplePacket controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        return spawnMonsterInternal(life, true, newSpawn, aggro);
    }

    /**
     * Handles monsters not being targettable, such as Zakum's first body.
     * @param life The mob to spawn as non-targettable.
     * @return The packet to spawn the mob as non-targettable.
     */
    public static MaplePacket spawnFakeMonster(MapleMonster life) {
        return spawnMonsterInternal(life, true, true, false);
    }

    /**
     * Makes a monster previously spawned as non-targettable, targettable.
     * @param life The mob to make targettable.
     * @return The packet to make the mob targettable.
     */
    public static MaplePacket makeMonsterReal(MapleMonster life) {
        return spawnMonsterInternal(life, false, false, false);
    }

    /**
     * Internal function to handler monster spawning and controlling.
     *
     * @param life The mob to perform operations with.
     * @param requestController Requesting control of mob?
     * @param newSpawn New spawn (fade in?)
     * @param aggro Aggressive mob?
     * @param effect The spawn effect to use.
     * @return The spawn/control packet.
     */
    private static MaplePacket spawnMonsterInternal(MapleMonster life, boolean requestController, boolean newSpawn, boolean aggro) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (requestController) {
            mplew.writeHeader(SendPacketOpcode.SPAWN_MONSTER_CONTROL);
            if (aggro) {
                mplew.write(2);
            } else {
                mplew.write(1);
            }

        } else {
            mplew.writeHeader(SendPacketOpcode.SPAWN_MONSTER);
        }
        //[AE 00] [1D 43 3C 00] [01] [21 B3 81 00] [[00 00 00 08] [00 00 00 00]] [63 FE] [7E FE] [02] [14 00] [14 00] [FD] [B3 42 3C 00] [FF] [00 00 00 00]
        //[AE 00] [DA 82 34 00] [01] [C6 E8 8D 00] [[00 00 00 08] [00 00 00 00]] [9C FC] [5F 00] [04] [12 00] [12 00] [0C 00] [00] [00 00] [FF] [00 00 00 00]
        //ACPQ BOMS EFFECT = 0x0C
        mplew.writeInt(life.getObjectId());
        mplew.write(life.getControlStatus());//5=none
        mplew.writeInt(life.getId());
        mplew.writeInt(0);//TODO: Status Mask

        mplew.writeLong(0); //more of status mask
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0x88);
        mplew.writeShort(0);
        mplew.writeInt(0);

        mplew.writePoint(life.getPosition());
        mplew.write(life.getStance()); // bitfield, // 0x08 - a summon, 0x02 - ???, 0x01 - faces right, or 5 or 2? o.O"
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getStartFh());

        if (life.getSummonEffect() > 0) {
            mplew.write(life.getSummonEffect());
            mplew.write(0);
            mplew.writeShort(0); // ?
        }

        mplew.write(newSpawn ? -2 : -1);
        mplew.write(-1);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    /**
     * Gets a stop control monster packet.
     *
     * @param oid The ObjectID of the monster to stop controlling.
     * @return The stop control monster packet.
     */
    public static MaplePacket stopControllingMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SPAWN_MONSTER_CONTROL);
        mplew.write(0);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    /**
     * Gets a response to a move monster packet.
     *
     * @param objectid The ObjectID of the monster being moved.
     * @param moveid The movement ID.
     * @param currentMp The current MP of the monster.
     * @param useSkills Can the monster use skills?
     * @return The move response packet.
     */
    public static MaplePacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills) {
        return moveMonsterResponse(objectid, moveid, currentMp, useSkills, 0, 0);
    }

    public static MaplePacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skill, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MOVE_MONSTER_RESPONSE);
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skill);
        mplew.write(level);

        return mplew.getPacket();
    }

    public static MaplePacket getChatText(int cidfrom, String text) {
        return getChatText(cidfrom, text, 0, 0);
    }

    /**
     * Gets a general chat packet.
     *
     * @param cidfrom The character ID who sent the chat.
     * @param text The text of the chat.
     * @param whiteBG use a white BG?
     * @param bubbleOnly only show text in bubble.
     * @return The general chat packet.
     */
    public static MaplePacket getChatText(int cidfrom, String text, int whiteBG, int bubbleOnly) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.CHATTEXT);
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG);//White BG Text
        mplew.writeMapleAsciiString(text);
        mplew.write(bubbleOnly);

        return mplew.getPacket();
    }

    /**
     * For testing only! Gets a packet from a hexadecimal string.
     *
     * @param hex The hexadecimal packet to create.
     * @return The MaplePacket representing the hex string.
     */
    public static MaplePacket getPacketFromHexString(String hex) {
        byte[] b = HexTool.getByteArrayFromHexString(hex);
        return new ByteArrayMaplePacket(b);
    }

    /**
     * Gets a packet telling the client to show an EXP increase.
     *
     * @param gain The amount of EXP gained.
     * @param inChat In the chat box?
     * @param white White text or yellow?
     * @return The exp gained packet.
     */
    public static MaplePacket getShowExpGain(int gain, boolean inChat, boolean white) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(3); // 3 = exp, 4 = fame, 5 = mesos, 6 = guildpoints
        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.writeInt(inChat ? 1 : 0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        if (inChat) {
            mplew.write(0);
        }
        mplew.writeInt(0); // new?

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a meso gain.
     *
     * @param gain How many mesos gained.
     * @return The meso gain packet.
     */
    public static MaplePacket getShowMesoGain(int gain) {
        return getShowMesoGain(gain, false);
    }

    /**
     * Gets a packet telling the client to show a meso gain.
     *
     * @param gain How many mesos gained.
     * @param inChat Show in the chat window?
     * @return The meso gain packet.
     */
    public static MaplePacket getShowMesoGain(int gain, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        if (!inChat) {
            mplew.write(0);
            mplew.write(1);
        } else {
            mplew.write(5);
        }
        mplew.write(0);
        mplew.writeInt(gain);
        mplew.writeShort(0); // inet cafe meso gain ?.o

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a item gain.
     *
     * @param itemId The ID of the item gained.
     * @param quantity How many items gained.
     * @return The item gain packet.
     */
    public static MaplePacket getShowItemGain(int itemId, short quantity) {
        return getShowItemGain(itemId, quantity, false);
    }

    /**
     * Gets a packet telling the client to show an item gain.
     *
     * @param itemId The ID of the item gained.
     * @param quantity The number of items gained.
     * @param inChat Show in the chat window?
     * @return The item gain packet.
     */
    public static MaplePacket getShowItemGain(int itemId, short quantity, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (inChat) {
            mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
            mplew.write(3);
            mplew.write(1);// Number of different items (itemid and amount gets repeated)
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        } else {
            mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.writeShort(0);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
            mplew.writeInt(0);//unneeded
            mplew.writeInt(0);//unneeded
        }
        return mplew.getPacket();
    }

    public static MaplePacket getShowItemGain(int type, List<Pair<Integer, Integer>> itemPairs) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        mplew.write(type);
        mplew.write(itemPairs.size());
        for (Pair<Integer, Integer> itemPair : itemPairs) {
            mplew.writeInt(itemPair.getLeft());
            mplew.writeInt(itemPair.getRight());
        }
        return mplew.getPacket();
    }

    public static MaplePacket getShowFameGain(int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(4);
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static MaplePacket getItemBuffGain(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(7);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client that a monster was killed.
     *
     * @param oid The objectID of the killed monster.
     * @param animation Show killed animation?
     * @return The kill monster packet.
     */
    public static MaplePacket killMonster(int oid, int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.KILL_MONSTER);
        mplew.writeInt(oid);
        mplew.write(animation);
        //3 = bomb
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show mesos coming out of a map
     * object.
     *
     * @param amount The amount of mesos.
     * @param itemoid The ObjectID of the dropped mesos.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @return The drop mesos packet.
     */
    public static MaplePacket dropMesoFromMapObject(int amount, int itemoid, int dropperoid, int ownerid, Point dropfrom, Point dropto, byte mod, boolean playerDrop) {
        return dropItemFromMapObjectInternal(amount, itemoid, dropperoid, ownerid, dropfrom, dropto, mod, true, playerDrop);
    }

    /**
     * Gets a packet telling the client to show an item coming out of a map
     * object.
     *
     * @param itemid The ID of the dropped item.
     * @param itemoid The ObjectID of the dropped item.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @return The drop mesos packet.
     */
    public static MaplePacket dropItemFromMapObject(int itemid, int itemoid, int dropperoid, int ownerid, Point dropfrom, Point dropto, byte mod, boolean playerDrop) {
        return dropItemFromMapObjectInternal(itemid, itemoid, dropperoid, ownerid, dropfrom, dropto, mod, false, playerDrop);
    }

    /**
     * Internal function to get a packet to tell the client to drop an item onto
     * the map.
     *
     * @param itemid The ID of the item to drop.
     * @param itemoid The ObjectID of the dropped item.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod drop packet modifier.
     * @param mesos Is the drop mesos?
     * @return The item drop packet.
     */
    public static MaplePacket dropItemFromMapObjectInternal(int itemid, int itemoid, int dropperoid, int ownerid, Point dropfrom, Point dropto, byte mod, boolean mesos, boolean playerDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 4000109 - flames
        mplew.writeHeader(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT);
        mplew.write(mod);// 1 with animation, 2 without o.o
        mplew.writeInt(itemoid);
        mplew.write(mesos ? 1 : 0);
        mplew.writeInt(itemid);
        mplew.writeInt(ownerid); // owner charid
        mplew.write(0);
        mplew.writePoint(dropto);
        if (mod != 2) {
            mplew.writeInt(ownerid);
            mplew.writePoint(dropfrom);
        } else {
            mplew.writeInt(dropperoid);
        }
        mplew.write(0);
        if (mod != 2) {
            mplew.write(0);
            mplew.write(playerDrop ? 0 : 1);
        }
        if (!mesos) {
            mplew.write(ITEM_MAGIC);
            // TODO getTheExpirationTimeFromSomewhere o.o
            addExpirationTime(mplew, System.currentTimeMillis(), false);
        }
        mplew.write(playerDrop ? 0 : 1);

        return mplew.getPacket();
    }

    /* (non-javadoc)
     * TODO: make MapleCharacter a mapobject, remove the need for passing oid
     * here.
     */
    /**
     * Gets a packet spawning a player as a mapobject to other clients.
     *
     * @param chr The character to spawn to other clients.
     * @return The spawn player packet.
     */
    public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SPAWN_PLAYER);
        //mplew.writeHexString("B5 1D 55 00 1F 05 00 43 61 70 7A 7A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FC 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 00 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 00 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 00 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 01 75 F2 CF 1F 00 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 00 00 00 00 00 00 00 00 00 00 00 00 01 46 37 0F 00 00 00 3E 08 00 00 84 4E 00 00 00 44 75 00 00 01 69 4A 0F 00 04 44 BF 0F 00 05 A9 DE 0F 00 06 BE 2C 10 00 07 AF 5B 10 00 0B 20 01 16 00 0C 5D F9 10 00 31 78 6D 11 00 FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 73 02 41 FF 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());

        if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("");
            mplew.write(new byte[6]);
        } else {
            MapleGuildSummary gs = chr.getClient().getChannelServer().getGuildSummary(
                    chr.getGuildId());

            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeMapleAsciiString("");
                mplew.write(new byte[6]);
            }
        }

        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0xFC);
        mplew.write(1); ///??
        //mplew.writeShort(0);

        long buffmask = 0;
        Integer buffvalue = null;

        if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
            buffmask |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
            buffmask |= MapleBuffStat.COMBO.getValue();
            buffvalue = Integer.valueOf(chr.getBuffedValue(MapleBuffStat.COMBO).intValue());
        }
        if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            buffmask |= MapleBuffStat.MONSTER_RIDING.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
            buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            buffvalue = Integer.valueOf(chr.getBuffedValue(MapleBuffStat.MORPH).intValue());
        }

        mplew.writeLong(buffmask);

        if (buffvalue != null) {
            if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
                mplew.writeShort(buffvalue);
            } else {
                mplew.write(buffvalue.byteValue());
            }
        }
        //int buffmask?

        int CHAR_MAGIC_SPAWN = new Random().nextInt();
        mplew.writeInt(0);
        mplew.writeInt(0);
        //mplew.writeShort(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        MapleMount mount = chr.getMount();
        if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null && mount != null) {
            //id
            mplew.writeInt(mount.getSkillId());
            mplew.writeInt(0x34F9D6ED);
        } else {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }

        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeShort(chr.getJob().getId());
        addCharLook(mplew, chr, false);
        mplew.writeInt(0);
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(chr.getChair());
        mplew.writePoint(chr.getPosition());
        mplew.write(chr.getStance());
        mplew.writeShort(0);//chr.getFoothold
        mplew.write(0);
        for (int i = 0; i < 3; i++) {
            MaplePet pet = chr.getPet(i);
            if (pet != null) {
                mplew.write(1);
                mplew.writeInt(pet.getUniqueId());//getType?
                mplew.writeMapleAsciiString(pet.getName());
                mplew.writeLong(pet.getItemId());//getId?
                mplew.writePoint(pet.getPos());
                mplew.write(pet.getStance());
                mplew.writeInt(pet.getFh());
            }
        }
        mplew.write(0);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    /**
     * Adds a announcement box to an existing MaplePacketLittleEndianWriter.
     * @param [MaplePacketLittleEndianWrite]mplew to add an announcement box to.
     * @param [MaplePlayerShop] shop The shop to announce.
     */
    private static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        MaplePlayerShop shop = chr.getPlayerShop();
        MapleMiniGame game = chr.getMiniGame();
        if (shop != null) {
            mplew.write(shop.getInteractionType().getValue());
            mplew.writeInt(shop.getObjectId()); // gameid/shopid
            mplew.writeMapleAsciiString(shop.getDescription()); // desc
            mplew.write(shop.isPublicGame() ? 0 : 1);
            mplew.write(0);
            // first slot: 1/2/3/4
            // second slot: 1/2/3/4
            mplew.write(1);
            mplew.write(4);
            mplew.write(shop.isInProgress() ? 1 : 0);
        } else if (game != null) {
            mplew.write(game.getGameType().getValue());
            mplew.writeInt(game.getObjectId());
            mplew.writeMapleAsciiString(game.getDescription());
            mplew.write(game.isWithPassword() ? 1 : 0);//withPassword
            if (game.isWithPassword()) {
                mplew.writeMapleAsciiString(game.getPassword());
            }
            mplew.write(game.getPieceType().getValue());//gameType
            mplew.write(game.getPlayerCount());
            mplew.write(2);//???
            mplew.write(game.isInProgress() ? 1 : 0);
        } else {
            mplew.write(0);
        }
    }

    /**
     *
     * @param from
     * @param expression
     * @return Returns a show face-expression packet
     */
    public static MaplePacket facialExpression(MapleCharacter from, int expression) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.FACIAL_EXPRESSION);
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        return mplew.getPacket();
    }

    /**
     * Serializes movement
     * @param lew
     * @param moves
     */
    private static void serializeMovementPath(LittleEndianWriter lew, MovementPath movementPath) {
        lew.writePoint(movementPath.getStartPos());
        
        lew.write(movementPath.getRes().size());
        for (LifeMovementFragment move : movementPath.getRes()) {
            move.serialize(lew);
        }
    }

    /**
     *
     * @param cid
     * @param moves
     * @return shows a player moving packet
     */
    public static MaplePacket movePlayer(int cid, MovementPath movementPath) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MOVE_PLAYER);
        mplew.writeInt(cid);
        serializeMovementPath(mplew, movementPath);
        return mplew.getPacket();
    }

    /**
     *
     * @param cid
     * @param oid
     * @param startPos
     * @param moves
     * @return a packet moving the summoned-monster
     */
    public static MaplePacket moveSummon(int cid, int oid, MovementPath movementPath) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.MOVE_SUMMON);
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        serializeMovementPath(mplew, movementPath);
        return mplew.getPacket();
    }

    /**
     * Shows a monster moving
     * @param useskill
     * @param skill_1
     * @param skillId
     * @param skillLevel
     * @param delay
     * @param oid
     * @param startPos
     * @param moves
     * @return Shows a mosnter moving packet
     */
    public static MaplePacket moveMonster(int useskill, int skill_1, int skillId, int skillLevel, int delay, int oid, MovementPath movementPath) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MOVE_MONSTER);
        mplew.writeInt(oid);
        mplew.write(useskill);
        mplew.write(skill_1);
        mplew.write(skillId);
        mplew.write(skillLevel);
        mplew.writeShort(delay);
        serializeMovementPath(mplew, movementPath);
        return mplew.getPacket();
    }

    public static MaplePacket moveMonster(int useskill, int skill, int oid, MovementPath movementPath) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MOVE_MONSTER);
        mplew.writeInt(oid);
        mplew.write(useskill);
        mplew.writeInt(skill);
        mplew.write(0);
        serializeMovementPath(mplew, movementPath);
        return mplew.getPacket();
    }

    /**
     * Shows a summon attack
     * @param cid
     * @param summonSkillId
     * @param newStance
     * @param allDamage
     * @return shows the attack of a summoned monster-packet
     */
    public static MaplePacket summonAttack(int cid, int summonSkillId, int newStance, List<SummonAttackEntry> allDamage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SUMMON_ATTACK);
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        mplew.write(allDamage.size());
        for (SummonAttackEntry attackEntry : allDamage) {
            mplew.writeInt(attackEntry.getMonsterOid()); // oid
            mplew.write(6); // who knows
            mplew.writeInt(attackEntry.getDamage()); // damage
        }

        return mplew.getPacket();
    }

    /**
     * Shows a close range attack
     * @param cid The CharacterID attacking
     * @param attack The AttackInfo to attack with
     * @return a packet for a close-range attack
     */
    public static MaplePacket closeRangeAttack(int cid, AttackInfo attack) {

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.CLOSE_RANGE_ATTACK);
        if (attack.getSkill() == 4211006) {// meso explosion
            addMesoExplosion(mplew, cid, attack);
        } else {
            addAttackBody(mplew, cid, attack, false);
        }
        return mplew.getPacket();
    }

    /**
     * Shows a Ranged Attack
     * @param cid The CharacterID attacking
     * @param attack The AttackInfo to attack with
     * @param useDirection Use the direction in the AttackInfo
     * @return A packet showing a ranged attack
     */
    public static MaplePacket rangedAttack(int cid, AttackInfo attack, boolean useDirection) {

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.RANGED_ATTACK);
        addAttackBody(mplew, cid, attack, useDirection);
        return mplew.getPacket();
    }

    /**
     * Shows a Magic-Attack
     * @param cid The CharacterID attacking
     * @param attack The AttackInfo to attack with
     * @return A packet showing a Magic-Attack
     */
    public static MaplePacket magicAttack(int cid, AttackInfo attack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAGIC_ATTACK);
        addAttackBody(mplew, cid, attack, false);
        if (attack.getCharge() > 0) {
            mplew.writeInt(attack.getCharge());
        }
        return mplew.getPacket();
    }

    /**
     * Add showing attacking a body
     * @param lew
     * @param cid The CharacterID attacking
     * @param attack The AttackInfo to attack with
     * @param useDirection Use the direction in the AttackInfo
     */
    private static void addAttackBody(LittleEndianWriter lew, int cid, AttackInfo attack, boolean useDirection) {
        lew.writeInt(cid);
        lew.write(attack.getNumAttackedAndDamage());
        lew.write(0);
        if (attack.getSkill() > 0) {
            lew.write(attack.getPlayer().getSkillLevel(SkillFactory.getSkill(attack.getSkill())));
            lew.writeInt(attack.getSkill());
        } else {
            lew.write(0);
        }
        lew.write(0);
        lew.write(attack.getProjectileDisplay());
        lew.write(useDirection ? attack.getDirection() : attack.getStance());
        lew.write(attack.getWSpeed());
        lew.write(10);//lew.write(attack.getMasteryId() > 0 ? ((attack.getPlayer().getSkillLevel(attack.getMasteryId()) + 1) / 2) : 0);//MasteryID - (0 for mages - no spells have swoosh
        lew.writeInt(attack.getProjectile());

        for (Pair<Integer, List<Integer>> oned : attack.getAllDamage()) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft().intValue());
                lew.write(0xFF);
                for (Integer eachd : oned.getRight()) {
                    //damage += 0x80000000; // Critical damage = 0x80000000 + damage || highest bit set = crit
                    lew.writeInt(eachd.intValue());
                }
            }
        }
    }

    /**
     * Shows a MesoExplosion
     * @param lew
     * @param cid
     * @param skill
     * @param stance
     * @param numAttackedAndDamage
     * @param projectile
     * @param damage
     */
    private static void addMesoExplosion(LittleEndianWriter lew, int cid, AttackInfo attack) {
        lew.writeInt(cid);
        lew.write(attack.getNumAttackedAndDamage());
        lew.write(attack.getPlayer().getSkillLevel(attack.getSkill()));
        lew.writeInt(attack.getSkill());
        lew.write(attack.getStance());
        lew.write(attack.getWSpeed());
        lew.write(10);//MasteryID - (0 for mages - no spells have swoosh
        lew.writeInt(attack.getProjectile());

        for (Pair<Integer, List<Integer>> oned : attack.getAllDamage()) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft().intValue());
                lew.write(0xFF);
                lew.write(oned.getRight().size());
                for (Integer eachd : oned.getRight()) {
                    lew.writeInt(eachd.intValue());
                }
            }
        }
    }

    /**
     * Shows an NPC-Shop
     * @param sid
     * @param items
     * @return A packet showing an NPC-Shop
     */
    public static MaplePacket getNPCShop(int sid, List<MapleShopItem> items) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OPEN_NPC_SHOP);
        mplew.writeInt(sid);
        mplew.writeShort(items.size()); // item count

        for (MapleShopItem item : items) {
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getPrice());
            mplew.writeInt(0); // Perfect pitches
            mplew.writeInt(0); // days useable
            mplew.writeInt(0); // unknown
            if (MapleItemInformationProvider.isRechargable(item.getItemId())) {
                mplew.writeShort(0);
                mplew.writeInt(0);
                mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
            } else {
                mplew.writeShort(1); // quantity
            }
            mplew.writeShort(ii.getSlotMax(item.getItemId()));
        }
        return mplew.getPacket();
    }

    /**
     * code (8 = sell, 0 = buy, 0x20 = due to an error the trade did not happen
     * o.o)
     *
     * @param code
     * @return A packet confirming the shop transaction
     */
    public static MaplePacket confirmShopTransaction(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION);
        mplew.write(code); // recharge == 8?

        return mplew.getPacket();
    }

    /**
     * Add Inventory Slots
     * @param type
     * @param item
     * @return a packet adding Inventory slots
     */
    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
        /*
         * 19 reference 00 01 00 = new while adding 01 01 00 = add from drop 00 01 01 = update count 00 01 03 = clear slot
         * 01 01 02 = move to empty slot 01 02 03 = move and merge 01 02 01 = move and merge with rest
         */
    }

    /**
     * Add Inventory Slots
     * @param type
     * @param item
     * @param fromDrop
     * @return a packet adding Inventory slots
     */
    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //1a 00 00 01 01 02 01 00 61 00
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        // mplew.writeShort(0x19);
        if (fromDrop) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.writeHexString("01 00"); // add mode

        mplew.write(type.getType()); // iv type

        mplew.write(item.getPosition()); // slot id

        addItemInfo(mplew, item, true, false);
        return mplew.getPacket();
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item) {
        return updateInventorySlot(type, item, false);
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        if (fromDrop) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.writeHexString("01 01");//Update
        // mode

        mplew.write(type.getType()); // iv type

        mplew.write(item.getPosition()); // slot id

        mplew.write(0); // ?

        mplew.writeShort(item.getQuantity());
        return mplew.getPacket();
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, byte src, byte dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, byte src, byte dst, byte equipIndicator) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.writeHexString("01 01 02");
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, byte src, byte dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.writeHexString("01 02 03");
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?

        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, byte src, byte dst,
            short srcQ, short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.writeHexString("01 02 01");
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(1);
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);
        return mplew.getPacket();
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, byte slot, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.write(fromDrop ? 1 : 0);
        mplew.writeHexString("01 03");
        mplew.write(type.getType());
        mplew.writeShort(slot);
        return mplew.getPacket();
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed) {
        // 18 00 01 02 03 02 08 00 03 01 F7 FF 01
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.write(1); // fromdrop always true

        if (destroyed) {
            mplew.write(2);
        } else {
            mplew.write(3);
        }
        if (scroll.getQuantity() > 0) {
            mplew.write(1);
        } else {
            mplew.write(3);
        }
        mplew.write(MapleInventoryType.USE.getType());
        mplew.writeShort(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            mplew.writeShort(scroll.getQuantity());
        }
        mplew.write(3);
        if (!destroyed) {
            mplew.write(MapleInventoryType.EQUIP.getType());
            mplew.writeShort(item.getPosition());
            mplew.write(0);
        }
        mplew.write(MapleInventoryType.EQUIP.getType());
        mplew.writeShort(item.getPosition());
        if (!destroyed) {
            addItemInfo(mplew, item, true, true);
        }
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_SCROLL_EFFECT);
        mplew.writeInt(chr);
        //TODO - add mask instead
        switch (scrollSuccess) {
            case SUCCESS:
                mplew.writeShort(1);//01 00
                break;
            case FAIL:
                mplew.writeShort(0);//00 00
                break;
            case CURSE:
                mplew.write(256);//00 01
                break;
            default:
                throw new IllegalArgumentException("effect in illegal range");
        }
        mplew.writeShort(legendarySpirit ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket removePlayerFromMap(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP);
        mplew.writeInt(cid);
        return mplew.getPacket();
    }

    public static MaplePacket removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, -1);
    }

    /**
     * animation:<br/>
     * 0 - expire<br/>
     * 1 - without animation<br/>
     * 2 - pickup<br/>
     * 4 - explode<br/>
     * 5 - petpickup<br/>
     * cid is ignored for 0 and 1
     *
     * @param oid
     * @param animation
     * @param cid
     * @param petIndex
     * @return packet removing an item from the map
     */
    public static MaplePacket removeItemFromMap(int oid, int animation, int cid, int petIndex) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REMOVE_ITEM_FROM_MAP);
        mplew.write(animation); // expire

        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (petIndex > -1) {
                mplew.write(petIndex);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateCharLook(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_CHAR_LOOK);
        mplew.writeInt(chr.getId());
        mplew.write(1);
        addCharLook(mplew, chr, false);
        mplew.writeShort(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.writeHexString("01 01 03");
        mplew.write(type.getType());
        mplew.writeShort(src);
        if (src < 0) {
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.writeHexString("01 01 01");
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());
        return mplew.getPacket();
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage) {
        return damagePlayer(skill, monsteridfrom, cid, damage, 0);
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage, int fake) {
        // 82 00 30 C0 23 00 FF 00 00 00 00 B4 34 03 00 01 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.DAMAGE_PLAYER);
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(0);
        mplew.writeInt(monsteridfrom);
        mplew.write(1);
        mplew.write(0);
        mplew.write(0); // > 0 = heros will effect

        mplew.writeInt(damage);
        if (fake > 0) {
            mplew.writeInt(fake);
        }
        return mplew.getPacket();
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage, int noDamageSkill, int direction, PGMRInfo pgmr) {
        // 82 00 30 C0 23 00 FF 00 00 00 00 B4 34 03 00 01 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.DAMAGE_PLAYER);
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(damage);
        mplew.writeInt(monsteridfrom);
        mplew.write(direction);
        if (pgmr != null && pgmr.getReduction() > 0) {
            mplew.write(pgmr.getReduction());
            mplew.write(pgmr.isPhysical() ? 1 : 0);
            mplew.writeInt(pgmr.getObjectId());
            mplew.write(6);//?
            mplew.writePoint(pgmr.getPosition());
        } else {
            mplew.write(0);
        }
        mplew.write(1);//stance
        mplew.writeInt(damage);
        if (noDamageSkill > 0) {
            mplew.writeInt(noDamageSkill);
        }

        return mplew.getPacket();
    }

    public static MaplePacket charNameResponse(String charname, boolean nameUsed) {
        // 0D 00 0C 00 42 6C 61 62 6C 75 62 62 31 32 33 34 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CHAR_NAME_RESPONSE);
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket cygnusCharNameResponse(String charname, int status) {
        // 0D 00 0C 00 42 6C 61 62 6C 75 62 62 31 32 33 34 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CYGNUS_CREATE_RESPONSE);
        mplew.writeMapleAsciiString(charname);
        mplew.writeInt(status);
        return mplew.getPacket();
    }

    public static MaplePacket addNewCharEntry(MapleCharacter chr, boolean worked) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.ADD_NEW_CHAR_ENTRY);

        mplew.write(worked ? 0 : 1);

        addCharEntry(mplew, chr);
        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @return a quest starting packet
     */
    public static MaplePacket startQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.writeShort(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    /**
     * state 0 = del ok state 12 = invalid bday
     *
     * @param cid
     * @param state
     * @return a delete character response packet
     */
    public static MaplePacket deleteCharResponse(int cid, int state) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.DELETE_CHAR_RESPONSE);
        mplew.writeInt(cid);
        mplew.write(state);
        return mplew.getPacket();
    }

    public static MaplePacket charInfo(MapleCharacter chr, boolean isSelf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CHAR_INFO);
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob().getId());
        mplew.writeShort(chr.getFame());
        mplew.write(chr.isMarried() ? 1 : 0);
        if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("-");
        } else {
            MapleGuildSummary gs = null;

            gs = chr.getClient().getChannelServer().getGuildSummary(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
            } else {
                mplew.writeMapleAsciiString("-");
            }
        }
        mplew.writeMapleAsciiString("");//Guild Aliance
        mplew.write(isSelf ? 1 : 0);
        addPetShowInfo(mplew, chr);
        addMountShowInfo(mplew, chr);
        addWishlistShowInfo(mplew, chr);
        addMonsterBookShowInfo(mplew, chr);
        addMedalShowInfo(mplew, chr);
        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @return a quest forfeit packet
     */
    public static MaplePacket forfeitQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @return a completed quest packet
     */
    public static MaplePacket completeQuest(MapleCharacter c, short quest, long completionTime) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.write(2);
        mplew.writeLong(DateUtil.getQuestTimestamp(completionTime));
        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @param npc
     * @param progress
     * @return update a quest info packet
     */
    // frz note, 0.52 transition: this is only used when starting a quest and
    // seems to have no effect, is it needed?
    public static MaplePacket updateQuestInfo(MapleCharacter c, short quest, int npc, byte progress) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_QUEST_INFO);
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    private static <E extends LongValueHolder> long getLongMask(List<Pair<E, Integer>> statups) {
        long mask = 0;
        for (Pair<E, Integer> statup : statups) {
            mask |= statup.getLeft().getValue();
        }
        return mask;
    }

    private static <E extends LongValueHolder> long getLongMaskFromList(List<E> statups) {
        long mask = 0;
        for (E statup : statups) {
            mask |= statup.getValue();
        }
        return mask;
    }

    public static MaplePacket giveDashBuff(MapleCharacter chr, MapleStatEffect stat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GIVE_BUFF);
        long mask = getLongMask(stat.getStatups());
        mplew.writeLong(0);
        mplew.writeLong(mask);
        mplew.writeShort(0);
        for (Pair<MapleBuffStat, Integer> statup : stat.getStatups()) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(0);
            mplew.writeInt(5001005);
            mplew.writeInt(880689251);//No Clue
            mplew.writeShort(stat.getDuration());
        }
        mplew.writeShort(0);
        mplew.write(0);//Buff Count (sometimes)
        return mplew.getPacket();
    }

    public static MaplePacket giveBuff(MapleStatEffect stat) {
        return giveBuff(stat, false, null);
    }

    /**
     * It is important that statups is in the correct order (see decleration
     * order in MapleBuffStat) since this method doesn't do automagical
     * reordering.
     *
     * @param stat The MapleStatEffect to buff with
     * @param  isMount if the stat is activating a mount
     * @param mount the mount if riding a mount otherwise null
     * @return a give buff packet
     */
    public static MaplePacket giveBuff(MapleStatEffect stat, boolean isMount, MapleMount mount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GIVE_BUFF);
        long mask = getLongMask(stat.getStatups());
        mplew.writeLong(0);
        mplew.writeLong(mask);
        if (!isMount) {

            for (Pair<MapleBuffStat, Integer> statup : stat.getStatups()) {
                mplew.writeShort(statup.getRight().shortValue());
                switch (stat.getStatType()) {
                    case PLAYER_SKILL:
                        mplew.writeInt(stat.getSourceId());
                        break;
                    case ITEM_BUFF:
                        mplew.writeInt(-stat.getSourceId());
                        break;
                    case MONSTER_SKILL:
                        mplew.writeShort(stat.getSourceId());
                        mplew.writeShort(stat.getSourceLevel());
                        break;
                }
                mplew.writeInt(stat.getDuration());
            }

            mplew.writeShort(0);
            switch (stat.getStatType()) {
                case PLAYER_SKILL:
                case MONSTER_SKILL:
                    mplew.writeShort(stat.getDelay());
                    mplew.write(0);//Buff Count (sometimes)
                    break;
                case ITEM_BUFF:
                    mplew.writeShort(0);
                    mplew.write(stat.isMorph() ? 1 : 0);
                    mplew.write(0);
                    if (stat.isMorph()) {
                        mplew.write(0);
                    }
                    break;
            }
        } else {
            mplew.writeShort(0);
            mplew.writeInt(mount.getItemId());
            mplew.writeInt(mount.getSkillId());
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.write(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups) {
        return giveForeignBuff(cid, statups, false);
    }

    public static MaplePacket giveForeignDashBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GIVE_FOREIGN_BUFF);
        mplew.writeInt(cid);
        long mask = getLongMask(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);
        mplew.writeShort(0);

        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(0);
            mplew.writeInt(5001005);
            mplew.writeInt(880689251);//No Clue
            mplew.writeShort(duration);//TIME
        }

        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, boolean morph) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GIVE_FOREIGN_BUFF);
        mplew.writeInt(cid);
        long mask = getLongMask(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);

        for (Pair<MapleBuffStat, Integer> statup : statups) {
            if (morph) {
                mplew.write(statup.getRight().byteValue());
            }
            mplew.writeShort(statup.getRight().shortValue());
        }

        mplew.writeShort(0);
        if (morph) {
            mplew.writeShort(0);
        }

        return mplew.getPacket();
    }

    public static MaplePacket giveForeignDebuff(int cid, MonsterSkill skill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GIVE_FOREIGN_BUFF);
        mplew.writeInt(cid);
        long mask = getLongMask(skill.statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);

        for (Pair<MapleBuffStat, Integer> statup : skill.statups) {
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillLevel());
        }
        mplew.writeShort(0); // same as give_buff
        mplew.writeShort(skill.getDelay());

        return mplew.getPacket();
    }

    public static MaplePacket cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CANCEL_FOREIGN_BUFF);
        mplew.writeInt(cid);
        long mask = getLongMaskFromList(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);
        return mplew.getPacket();
    }

    public static MaplePacket cancelBuff(List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CANCEL_BUFF);
        long mask = getLongMaskFromList(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket showMonsterRiding(int cid, List<Pair<MapleBuffStat, Integer>> statups, int itemId, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GIVE_FOREIGN_BUFF);
        mplew.writeInt(cid);
        long mask = getLongMask(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);
        mplew.writeShort(0);
        mplew.writeInt(itemId);
        mplew.writeInt(skillId);
        mplew.writeInt(0x2D4DFC2A);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopChat(MapleCharacter c, String chat, boolean owner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(6);
        mplew.write(8);
        mplew.write(owner ? 0 : 1);
        mplew.writeMapleAsciiString(c.getName() + " : " + chat);
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopNewVisitor(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(4);
        mplew.write(2);
        addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        return mplew.getPacket();
    }

    public static MaplePacket getTradePartnerAdd(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(4);
        mplew.write(1);
        addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        return mplew.getPacket();
    }

    public static MaplePacket getTradeInvite(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(2);
        mplew.write(3);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeHexString("B7 50 00 00");
        return mplew.getPacket();
    }

    public static MaplePacket getTradeMesoSet(byte number, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0xF);
        mplew.write(number);
        mplew.writeInt(meso);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeItemAdd(byte number, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0xE);
        mplew.write(number);
        // mplew.write(1);
        addItemInfo(mplew, item);
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopItemUpdate(MaplePlayerShop shop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0x16);
        mplew.write(shop.getItems().size());
        for (MaplePlayerShopItem item : shop.getItems()) {
            mplew.writeShort(item.getBundles());
            mplew.writeShort(item.getItem().getQuantity());
            mplew.writeInt(item.getPrice());
            addItemInfo(mplew, item.getItem(), true, true);
        }
        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param shop
     * @param owner
     * @return a get player shop packet
     */
    public static MaplePacket getPlayerShop(MapleClient c, MaplePlayerShop shop, boolean owner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.writeHexString("05 04 04");
        mplew.write(owner ? 0 : 1);
        mplew.write(0);
        addCharLook(mplew, shop.getOwner(), false);
        mplew.writeMapleAsciiString(shop.getOwner().getName());

        MapleCharacter[] visitors = shop.getVisitors();
        for (int i = 0; i < visitors.length; i++) {
            if (visitors[i] != null) {
                mplew.write(i + 1);
                addCharLook(mplew, visitors[i], false);
                mplew.writeMapleAsciiString(visitors[i].getName());
            }
        }
        mplew.write(0xFF);
        mplew.writeMapleAsciiString(shop.getDescription());
        List<MaplePlayerShopItem> items = shop.getItems();
        mplew.write(0x10);
        mplew.write(items.size());
        for (MaplePlayerShopItem item : items) {
            mplew.writeShort(item.getBundles());
            mplew.writeShort(item.getItem().getQuantity());
            mplew.writeInt(item.getPrice());
            addItemInfo(mplew, item.getItem(), true, true);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getTradeStart(MapleClient c, MapleTrade trade, byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.writeHexString("05 03 02");
        mplew.write(number);
        if (number == 1) {
            mplew.write(0);
            addCharLook(mplew, trade.getPartner().getChr(), false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
        }
        mplew.write(number);
        /*if (number == 1) {
        mplew.write(0);
        mplew.writeInt(c.getPlayer().getId());
        }*/
        addCharLook(mplew, c.getPlayer(), false);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.write(0xFF);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeConfirmation() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0x10);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeCompletion(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0xA);
        mplew.write(number);
        mplew.write(6);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeCancel(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0xA);
        mplew.write(number);
        mplew.write(2);
        /*Message:
        0x06 = success [tax is automated]
        0x07 = unsuccessful
        0x08 = "You cannot make the trade because there are some items which you cannot carry more than one."
        0x09 = "You cannot make the trade because the other person's on a different map."
         */
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameEntrance(MapleMiniGame miniGame, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);

        mplew.write(5);
        mplew.write(1);//Can Enter
        mplew.write(2);

        mplew.write(miniGame.isOwner(chr) ? 0 : 1);
        MapleCharacter visitor = miniGame.getVisitor();
        MapleCharacter owner = miniGame.getOwner();

        mplew.write(0);
        addCharLook(mplew, owner, false);
        mplew.writeMapleAsciiString(owner.getName());

        if (visitor != null) {
            mplew.write(1);
            addCharLook(mplew, visitor, false);
            mplew.writeMapleAsciiString(visitor.getName());
        }

        mplew.write(0xFF);
        for (int i = 0; i < miniGame.getPlayerCount(); i++) {//need a for statement for 1or2 players?
            MapleCharacter player = i == 0 ? owner : visitor;
            if (player == null) {
                continue;

            }
            MapleMiniGameStats gameStats = player.getMiniGameStats();
            mplew.write(i);
            mplew.writeInt(1);
            mplew.write(gameStats.getWins(miniGame.getGameType()));
            mplew.write(gameStats.getTies(miniGame.getGameType()));
            mplew.write(gameStats.getLosses(miniGame.getGameType()));
            mplew.write(gameStats.getPoints());
        }

        mplew.write(0xFF);
        mplew.writeMapleAsciiString(miniGame.getDescription());
        mplew.write(miniGame.getPieceType().getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameStatus(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(ready ? 0x34 : 0x35);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameStatus(int commandId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(commandId);//0x34 = Ready | 0x35 = Not_Ready
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameStart(boolean winner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0x37);
        mplew.write(winner ? 1 : 0);//TODO check
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameNewVisitor(MapleCharacter chr, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(4);
        mplew.write(slot);
        addCharLook(mplew, chr, false);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeInt(1);
        mplew.writeInt(chr.getMiniGameStats().getWins(chr.getMiniGame().getGameType()));
        mplew.writeInt(chr.getMiniGameStats().getTies(chr.getMiniGame().getGameType()));
        mplew.writeInt(chr.getMiniGameStats().getLosses(chr.getMiniGame().getGameType()));
        mplew.writeInt(chr.getMiniGameStats().getPoints());
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameRemoveVisitor(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(10);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameResult(MapleMiniGame game, boolean ownerWin, boolean tieGame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_INTERACTION);
        mplew.write(0x38);
        mplew.write(game.getResultType().getValue());//TIE
        mplew.write(ownerWin ? 1 : 0);
        mplew.writeInt(1);
        mplew.writeInt(game.getOwner().getMiniGameStats().getWins(game.getGameType()));
        mplew.writeInt(game.getOwner().getMiniGameStats().getTies(game.getGameType()));
        mplew.writeInt(game.getOwner().getMiniGameStats().getLosses(game.getGameType()));
        mplew.writeInt(game.getOwner().getMiniGameStats().getPoints());
        mplew.writeInt(1);
        mplew.writeInt(game.getVisitor().getMiniGameStats().getWins(game.getGameType()));
        mplew.writeInt(game.getVisitor().getMiniGameStats().getTies(game.getGameType()));
        mplew.writeInt(game.getVisitor().getMiniGameStats().getLosses(game.getGameType()));
        mplew.writeInt(game.getVisitor().getMiniGameStats().getPoints());
        return mplew.getPacket();
    }

    public static MaplePacket updateCharBox(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_CHAR_BOX);
        mplew.writeInt(chr.getId());
        addAnnounceBox(mplew, chr);
        return mplew.getPacket();
    }

    public static MaplePacket addMiniGameBox(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_CHAR_BOX);
        mplew.writeInt(chr.getId());
        addAnnounceBox(mplew, chr);
        return mplew.getPacket();
    }

    public static MaplePacket removeMiniGameBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_CHAR_BOX);
        mplew.writeInt(c.getId());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacketLittleEndianWriter getNPCTalk(int npc, NPCDialogType dialog, String talk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.NPC_TALK);
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(dialog.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(talk);
        return mplew;
    }

    public static MaplePacket showLevelup(int cid) {
        return showForeignEffect(cid, 0);
    }

    public static MaplePacket showJobChange(int cid) {
        return showForeignEffect(cid, 8);
    }

    public static MaplePacket showEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        mplew.write(effect);
        return mplew.getPacket();
    }

    public static MaplePacket showForeignEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
        mplew.writeInt(cid);
        mplew.write(effect);
        return mplew.getPacket();
    }

    public static MaplePacket showOwnBuffEffectAuto(int skillid) {
        return showOwnBuffEffectAuto(skillid, 0);
    }

    public static MaplePacket showOwnBuffEffectAuto(int skillid, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        //Heal 2 - skillid - 1
        switch (skillid) {
            case 2100000: // MP Eater
            case 2200000: // MP Eater
            case 2300000: // MP Eater
                mplew.write(1);
                mplew.writeInt(skillid);
                mplew.write(1);
                break;
            case 2301002: // Heal
            case 2311001: // Dispel
            case 9101000: // Dispel + Heal(GM)
                mplew.write(2);
                mplew.writeInt(skillid);
                mplew.write(level);
                break;
            case 4211005: // Meso Guard
            case 1311008: // Dragon Blood
                mplew.write(5);
                mplew.writeInt(skillid);
                break;
            case 1121002: // Power Stance
            case 1221002: // Power Stance
            case 1321002: // Power Stance
                mplew.write(1);
                mplew.writeInt(skillid);
                mplew.write(level);
                break;
        }
        return mplew.getPacket();
    }

    public static MaplePacket showBuffEffectAuto(int cid, int skillid) {
        return showBuffEffectAuto(cid, skillid, 0, true);
    }

    public static MaplePacket showBuffEffectAuto(int cid, int skillid, int level) {
        return showBuffEffectAuto(cid, skillid, level, true);
    }

    public static MaplePacket showBuffEffectAuto(int cid, int skillid, int level, boolean caster) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
        mplew.writeInt(cid);
        switch (skillid) {
            case 1004: //Mount-Rider
            case 2100000: // MP Eater
            case 2200000: // MP Eater
            case 2300000: // MP Eater
                mplew.write(1);
                mplew.writeInt(skillid);
                mplew.write(1);
                break;
            case 2301002: // Heal
            case 2311001: // Dispel
            case 9101000: // Dispel + Heal(GM)
                mplew.write(caster ? 1 : 2);
                mplew.writeInt(skillid);
                mplew.write(level);
                break;
            case 1311008: // Dragon Blood
            case 4211005: // Meso Guard
                mplew.write(5);
                mplew.writeInt(skillid);
                break;
            case 1121002: // Power Stance
            case 1221002: // Power Stance
            case 1321002: // Power Stance
                mplew.write(1);
                mplew.writeInt(skillid);
                mplew.write(level);
                break;
        }
        return mplew.getPacket();
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket showBuffEffect(int cid, int skillid, int effectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
        mplew.writeInt(cid);
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket updateSkill(int skillid, int level, int masterlevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_SKILLS);
        mplew.write(1);
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);
        mplew.writeLong(NO_EXPIRATION);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket updateQuestMobKills(MapleQuestStatus status) {
        // 21 00 01 FB 03 01 03 00 30 30 31
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(1);
        mplew.writeShort(status.getQuest().getId());
        mplew.write(1);
        String killStr = "";
        for (int kills : status.getMobKills().values()) {
            killStr += StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3);
        }
        mplew.writeMapleAsciiString(killStr);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_QUEST_COMPLETION);
        mplew.writeShort(id);
        return mplew.getPacket();
    }

    public static MaplePacket getKeymap(Map<Integer, MapleKeyBinding> keybindings) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.KEYMAP);
        mplew.write(0);

        for (int x = 0; x < 90; x++) {
            MapleKeyBinding binding = keybindings.get(Integer.valueOf(x));
            if (binding != null) {
                mplew.write(binding.getType());
                mplew.writeInt(binding.getAction());
            } else {
                mplew.write(0);
                mplew.writeInt(0);
            }
        }

        return mplew.getPacket();
    }

    public static MaplePacket getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.WHISPER);
        mplew.write(18);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1); // I guess this is the channel

        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    /**
     *
     * @param target name of the target character
     * @param reply error code: 0x0 = cannot find char, 0x1 = success
     * @return the MaplePacket
     */
    public static MaplePacket getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.WHISPER);
        mplew.write(10); // whisper?

        mplew.writeMapleAsciiString(target);
        mplew.write(reply);
        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithCS(String target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.WHISPER);
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);
        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithMap(String target, int mapid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.WHISPER);
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        // ?? official doesn't send zeros here but whatever
        mplew.write(new byte[8]);
        return mplew.getPacket();
    }

    public static MaplePacket getFindReply(String target, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.WHISPER);
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);
        return mplew.getPacket();
    }

    public static MaplePacket getInventoryFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.writeHexString("01 00");
        return mplew.getPacket();
    }

    public static MaplePacket getShowInventoryFull() {
        return getShowInventoryStatus(0xFF);
    }

    public static MaplePacket showItemUnavailable() {
        return getShowInventoryStatus(0xFE);
    }

    public static MaplePacket getShowInventoryStatus(int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_STATUS_INFO);
        mplew.write(0);
        mplew.write(mode);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getStorage(int npcId, byte slots, Collection<IItem> items, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OPEN_STORAGE);
        mplew.write(StorageActionType.S_OPEN_STORAGE.getValue());
        mplew.writeInt(npcId);
        mplew.write(slots);
        mplew.writeShort(0x7E);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(meso);
        mplew.writeShort(0);
        mplew.write((byte) items.size());
        for (IItem item : items) {
            addItemInfo(mplew, item, true, true);
        }
        mplew.writeShort(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getStorageFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OPEN_STORAGE);
        mplew.write(StorageActionType.S_FULL_STORAGE.getValue());
        return mplew.getPacket();
    }

    public static MaplePacket mesoStorage(byte slots, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OPEN_STORAGE);
        mplew.write(StorageActionType.S_STORE_MESO.getValue());
        mplew.write(slots);
        mplew.writeShort(2);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(meso);
        return mplew.getPacket();
    }

    public static MaplePacket storeStorage(byte slots, MapleInventoryType type, Collection<IItem> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OPEN_STORAGE);
        mplew.write(StorageActionType.S_STORE_ITEM.getValue());
        mplew.write(slots);
        mplew.writeShort(type.getBitfieldEncoding());
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(items.size());
        for (IItem item : items) {
            addItemInfo(mplew, item, true, true);
        }

        return mplew.getPacket();
    }

    public static MaplePacket takeOutStorage(byte slots, MapleInventoryType type, Collection<IItem> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OPEN_STORAGE);
        mplew.write(StorageActionType.S_TAKE_OUT.getValue());
        mplew.write(slots);
        mplew.writeShort(type.getBitfieldEncoding());
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(items.size());
        for (IItem item : items) {
            addItemInfo(mplew, item, true, true);
        }

        return mplew.getPacket();
    }

    /**
     *
     * @param oid
     * @param remhppercentage in %
     * @return a monster hp packet
     */
    public static MaplePacket showMonsterHP(int oid, int remhppercentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_MONSTER_HP);
        mplew.writeInt(oid);
        mplew.write(remhppercentage);

        return mplew.getPacket();
    }

    public static MaplePacket showBossHP(int oid, int currHP, int maxHP, byte tagColor, byte tagBgColor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        //53 00 05 21 B3 81 00 46 F2 5E 01 C0 F3 5E 01 04 01
        //00 81 B3 21 = 8500001 = Pap monster ID
        //01 5E F3 C0 = 23,000,000 = Pap max HP
        //04, 01 - boss bar color/background color as provided in WZ

        mplew.writeHeader(SendPacketOpcode.BOSS_ENV);
        mplew.write(5);
        mplew.writeInt(oid);
        mplew.writeInt(currHP);
        mplew.writeInt(maxHP);
        mplew.write(tagColor);
        mplew.write(tagBgColor);

        return mplew.getPacket();
    }

    public static MaplePacket giveFameResponse(int mode, String charname, int newfame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.FAME_RESPONSE);

        mplew.write(0);
        mplew.writeMapleAsciiString(charname);
        mplew.write(mode);
        mplew.writeShort(newfame);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    /**
     * status can be: <br>
     * 0: ok, use giveFameResponse<br>
     * 1: the username is incorrectly entered<br>
     * 2: users under level 15 are unable to toggle with fame.<br>
     * 3: can't raise or drop fame anymore today.<br>
     * 4: can't raise or drop fame for this character for this month anymore.<br>
     * 5: received fame, use receiveFame()<br>
     * 6: level of fame neither has been raised nor dropped due to an unexpected
     * error
     *
     * @param status
     * @return a give fame error response packet
     */
    public static MaplePacket giveFameErrorResponse(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.FAME_RESPONSE);
        mplew.write(status);
        return mplew.getPacket();
    }

    public static MaplePacket receiveFame(int mode, String charnameFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.FAME_RESPONSE);
        mplew.write(5);
        mplew.writeMapleAsciiString(charnameFrom);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static MaplePacket partyCreated(int partyId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PARTY_OPERATION);
        mplew.write(8);
        mplew.writeInt(partyId);
        mplew.write(CHAR_INFO_MAGIC);
        mplew.write(CHAR_INFO_MAGIC);
        mplew.writeInt(0);//TODO CHECK

        return mplew.getPacket();
    }

    public static MaplePacket partyInvite(MapleCharacter from) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PARTY_OPERATION);
        mplew.write(4);
        mplew.writeInt(from.getParty().getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.write(0);//V59

        return mplew.getPacket();
    }

    /**
     * 10: a beginner can't create a party<br>
     * 11/14/19: your request for a party didn't work due to an unexpected error<br>
     * 13: you have yet to join a party<br>
     * 16: already have joined a party<br>
     * 17: the party you are trying to join is already at full capacity<br>
     * 18: unable to find the requested character in this channel<br>
     *
     * @param message
     * @return a party status message packet
     */
    public static MaplePacket partyStatusMessage(int message) {
        // 32 00 08 DA 14 00 00 FF C9 9A 3B FF C9 9A 3B 22 03 6E 67
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PARTY_OPERATION);
        mplew.write(message);

        return mplew.getPacket();
    }

    /**
     * 22: is taking care of another invitation<br>
     * 23: has denied the invitation<br>
     *
     * @param message
     *
     * @param charname
     * @return a party status message packet
     */
    public static MaplePacket partyStatusMessage(int message, String charname) {
        // 3b 00 08 DA 14 00 00 FF C9 9A 3B FF C9 9A 3B 22 03 6E 67
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PARTY_OPERATION);
        mplew.write(message);
        mplew.writeMapleAsciiString(charname);

        return mplew.getPacket();
    }

    private static void addPartyStatus(int forchannel, MapleParty party, LittleEndianWriter lew, boolean leaving) {
        List<MaplePartyCharacter> partymembers = new ArrayList<MaplePartyCharacter>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(StringUtil.getRightPaddedStr(partychar.getName(), '\0', 13));
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            } else {
                lew.writeInt(-2);
            }
        }
        lew.writeInt(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            } else {
                lew.writeInt(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(0);
                lew.writeInt(0);
                lew.writeInt(0);
                lew.writeInt(0);
            }
        }
    }

    public static MaplePacket updateParty(int forChannel, MapleParty party, PartyOperation op,
            MaplePartyCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.PARTY_OPERATION);
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                mplew.write(0xC);
                mplew.writeInt(party.getId());
                mplew.writeInt(target.getId());

                if (op == PartyOperation.DISBAND) {
                    mplew.write(0);
                    mplew.writeInt(party.getId());
                } else {
                    mplew.write(1);
                    if (op == PartyOperation.EXPEL) {
                        mplew.write(1);
                    } else {
                        mplew.write(0);
                    }
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, false);
                    // addLeavePartyTail(mplew);
                }

                break;
            case JOIN:
                mplew.write(0xF);
                mplew.writeInt(party.getId());
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                // addJoinPartyTail(mplew);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                if (op == PartyOperation.LOG_ONOFF) {
                    mplew.write(0x1F); // actually this is silent too

                } else {
                    mplew.write(0x7);
                }
                mplew.writeInt(party.getId());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            case LEADER_CHANGE:
                mplew.write(0x1A);
                mplew.writeInt(target.getId());
                boolean is = false;
                mplew.write(is ? 1 : 0);
                break;

        }
        return mplew.getPacket();
    }

    public static MaplePacket partyPortal(int townId, int targetId, Point position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.PARTY_OPERATION);
        mplew.writeShort(0x22);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writePoint(position);
        return mplew.getPacket();
    }

    public static MaplePacket updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.UPDATE_PARTYMEMBER_HP);
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);
        return mplew.getPacket();
    }

    /**
     * Modes:<br>
     * 0: buddy-chat<br>
     * 1: party-chat<br>
     * 2: guild-chat<br>
     * 3: alliance-chat<br>
     *
     * @param name
     * @param chattext
     * @param mode
     * @return a multichat packet.
     */
    public static MaplePacket multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.MULTICHAT);
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);
        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stats, int skill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.APPLY_MONSTER_STATUS);
        mplew.writeInt(oid);

        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        int mask = 0;
        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }

        mplew.writeInt(mask);

        for (Integer val : stats.values()) {
            mplew.writeShort(val);
            mplew.writeInt(skill);
            mplew.writeShort(0); //Buff Length - Seconds * 2?
        }
        mplew.writeInt(0); // delay in ms
        mplew.write(2);//Buff Position???
        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(int oid, MonsterSkill skill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // [B3 00] [7B 59 5A 00] [00 40 00 00] [55 00] [66 00] [01 00] [3C 00] [84 03] [02] //Monster Buffs Self
        // [B3 00] [C8 67 57 00] [00 40 00 00] [55 00] [66 00] [01 00] [3C 00] [84 03] [02] //Monster Buffs Self
        // [B3 00] [C8 67 57 00] [00 10 00 00] [73 00] [64 00] [01 00] [3C 00] [84 03] [02] //Monster Buffs Self
        // [B3 00] [F2 D4 E4 00] [00 10 00 00] [73 00] [64 00] [01 00] [3C 00] [84 03] [03] //Monster Buffs Self
        // [B3 00] [F2 D4 E4 00] [00 80 00 00] [55 00] [71 00] [01 00] [3C 00] [84 03] [03] //Monster Buffs Self
        // [B3 00] [32 D3 E4 00] [00 80 00 00] [55 00] [71 00] [01 00] [3C 00] [84 03] [02] //Monster Buffs Self
        // [B3 00] [F2 D4 E4 00] [00 40 00 00] [55 00] [66 00] [01 00] [3C 00] [84 03] [02] //Monster Buffs Self
        // [B3 00] [AD 73 6D 00] [00 80 00 00] [55 00] [71 00] [01 00] [3C 00] [84 03] [02] //Monster Debuffs Me
        // Header(2) MonsterOid(4) BuffTypes(4) BuffValue(2) Buff(2) Level(2) BuffLength(2) Delay(2) BuffType(1)
        // header | oid | bufftpyes | skill.x | skill | level | WTF | WTF | pos
        mplew.writeHeader(SendPacketOpcode.APPLY_MONSTER_STATUS);
        mplew.writeInt(oid);

        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        int mask = 0;
        for (MonsterStatus stat : skill.getMonsterStatus().keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeInt(mask);
        for (Integer val : skill.getMonsterStatus().values()) {
            mplew.writeShort(val);
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillLevel());
            mplew.writeShort((int) (skill.getDuration() * 2) / 1000); //Buff Length - Seconds * 2?
        }

        //for int loop, writeint(reflection)

        mplew.writeInt(skill.getDelay());
        mplew.write(skill.getMonsterStatus().size());
        return mplew.getPacket();
    }

    public static MaplePacket cancelMonsterStatus(int oid, Map<MonsterStatus, Integer> stats) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // [B4 00] [12 0D 4E 00] [00 01 00 00] [01]
        // [B4 00] [F2 D4 E4 00] [00 10 00 00] [05]
        // [B4 00] [F2 D4 E4 00] [00 80 00 00] [05]
        // [B4 00] [F2 D4 E4 00] [00 40 00 00] [04]
        mplew.writeHeader(SendPacketOpcode.CANCEL_MONSTER_STATUS);

        mplew.writeInt(oid);

        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        int mask = 0;

        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }

        mplew.writeInt(mask);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket getClock(int time) { // time in seconds
        // 01 = Time
        // 02 = Timer
        // 03  = ???
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (time >= 0) {
            mplew.writeHeader(SendPacketOpcode.CLOCK);
            mplew.write(2);
            mplew.writeInt(time);
        } else {
            mplew.writeHeader(SendPacketOpcode.STOP_CLOCK);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getClockTime(int hour, int min, int sec) { // Current Time

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CLOCK);
        mplew.write(1); //Clock-Type
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);
        return mplew.getPacket();
    }

    public static MaplePacket spawnMist(int oid, int ownerCid, int skillId, int level, Rectangle mistPosition) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SPAWN_MIST);
        mplew.writeInt(oid); // maybe this should actually be the "mistid" -
        mplew.writeInt(oid);//0, 1, or 2
        mplew.writeInt(ownerCid); // probably only intresting for smokescreen
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.writeShort(0);

        mplew.writeInt(mistPosition.x); // left position
        mplew.writeInt(mistPosition.y); // bottom position
        mplew.writeInt(mistPosition.x + mistPosition.width); // left position
        mplew.writeInt(mistPosition.y + mistPosition.height); // upper
        // position
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket removeMist(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.REMOVE_MIST);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static MaplePacket damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.DAMAGE_SUMMON);
        //[77 00] [29 1D 02 00] [FA FE 30 00] [00] [10 00 00 00] [BF 70 8F 00] [00]
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(unkByte);
        mplew.writeInt(damage);
        mplew.writeInt(monsterIdFrom);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket damageMonster(int oid, int damage) {
        return damageMonster(oid, damage, false, 0, 0);
    }

    public static MaplePacket damageMonster(int oid, int damage, boolean mobHitMob, int hp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.DAMAGE_MONSTER);
        mplew.writeInt(oid);
        mplew.write(1);
        mplew.writeInt(damage);
        if (mobHitMob) {
            mplew.writeInt(hp);
            mplew.writeInt(maxhp);
        }
        return mplew.getPacket();
    }

    public static MaplePacket healMonster(int oid, int heal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.DAMAGE_MONSTER);
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);

        return mplew.getPacket();
    }

    public static MaplePacket updateBuddylist(Collection<BuddylistEntry> buddylist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.BUDDYLIST);
        mplew.write(7);
        mplew.write(buddylist.size());
        for (BuddylistEntry buddy : buddylist) {
            if (buddy.isVisible()) {
                mplew.writeInt(buddy.getCharacterId()); // cid

                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getName(), '\0', 13));
                mplew.write(0);
                mplew.writeInt(buddy.getChannel() - 1);
            }
        }
        for (int x = 0; x < buddylist.size(); x++) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket requestBuddylistAdd(int cidFrom, String nameFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.BUDDYLIST);
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(nameFrom, '\0', 13));
        mplew.write(1);
        mplew.write(31);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyChannel(int characterid, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.BUDDYLIST);
        mplew.write(0x14);
        mplew.writeInt(characterid);
        mplew.write(0);
        mplew.writeInt(channel);
        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyCapacity(int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.BUDDYLIST);
        mplew.write(0x15);
        mplew.write(capacity);

        return mplew.getPacket();

    }

    public static MaplePacket buddyListMeesage(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.BUDDYLIST);
        mplew.write(type);
        return mplew.getPacket();

    }

    public static MaplePacket itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_EFFECT);

        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket startMapEffect(String msg, int itemid, boolean active) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //55 00 01 05 20 4E 00-Chocolate
        mplew.writeHeader(SendPacketOpcode.MAP_EFFECT);
        mplew.write(active ? 0 : 1);
        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket removeMapEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //55 00 00 00 00 00 00-Remove effect
        mplew.writeHeader(SendPacketOpcode.MAP_EFFECT);
        mplew.write(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    /**
     * Shows a chair
     * @param characterid
     * @param itemid
     * @return a packet showing a chair
     */
    public static MaplePacket showChair(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SHOW_CHAIR);

        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    /**
     * Cancels a chair
     * @return a packet canceling a char
     */
    public static MaplePacket cancelChair() {
        return cancelChair(-1);
    }

    /**
     * Cancels a chair
     * @return a packet canceling a char
     */
    public static MaplePacket cancelChair(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CANCEL_CHAIR);
        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }
        return mplew.getPacket();
    }

    /**
     * Mode:<br>
     * 0: ?? byte|int|int<br>
     * 1. Tremble Effect(Incompatible Here) (byte|int)<br>
     * 2. Door Animation<br>
     * 3. Clear/Fail Sign<br>
     * 4. Sound<br>
     * 5. BossHP(Incompatible, See ShowBossHP)<br>
     * 6. Music<br>
     * @param env The environment string variable
     * @param mode The environment change mode
     * @return an environment change packet
     */
    public static MaplePacket effectEnvironment(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.BOSS_ENV);
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);

        return mplew.getPacket();
    }

    /**
     *
     * @param type - (0:Light&Long 1:Heavy&Short)
     * @param delay - seconds
     * @return
     */
    public static MaplePacket effectTremble(int type, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.BOSS_ENV);
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        return mplew.getPacket();
    }

    public static MaplePacket npcConfirm(int oid) {//Apparently this deletes the npc from the map o.O
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.NPC_CONFIRM);
        mplew.writeInt(oid);
        return mplew.getPacket();
    }

    public static MaplePacket npcTalkBubble(int npcid, byte action, byte action2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.NPC_ACTION);
        mplew.writeInt(npcid);
        mplew.write(action);
        mplew.write(action2);
        return mplew.getPacket();
    }

    /**
     * Block a portal<br>
     * Types:<br>
     * 00 = NULL<br>
     * -----<br>
     * shopPortal<br>
     * 01 = You cannot move that channel. Please try again later.<br>
     * 02 = You cannot go into the cash shop. Please try again later.<br>
     * 03 = The Item-Trading Shop is currently unavailable. Please try again later.<br>
     * 04 = You cannot go into the trade shop, due to the limitation of user count.<br>
     * 05 = You do not meet the minimum level requirement to access the Trade Shop.<br>
     * -----<br>
     * mapPortal<br>
     * 01 = The portal is closed for now.<br>
     * 02 = You cannot go to that place.<br>
     * 03 = Unable to approach due to the force of the ground.<br>
     * 04 = (POPUP) You cannot teleport to or on this map.
     * 05 = Unable to approach due to the force of the ground.<br>
     * 06 = (POPUP) The cash shop is currently \r\n not available. \r\n Stay tuned...
     * @param type
     * @return a packet blocking a portal
     */
    public static MaplePacket blockPortal(int type, boolean mapPortal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (mapPortal) {
            mplew.writeHeader(SendPacketOpcode.BLOCK_PORTAL);
        } else {
            mplew.writeHeader(SendPacketOpcode.BLOCK_PORTAL_SHOP);
        }
        mplew.write(type);
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     * Report a player (notice)<br>
     * Types<br>
     * 00 = You have successfully reported the user.<br>
     * 01 = Unable to locate the user.<br>
     * 02 = You may only report users 10 times a day.<br>
     * 03 = You have been reported to the GM's by a user.<br>
     * 04 = Your request did not go through for unknown reasons. Please try again later.<br>
     * @param type
     * @return a packet reporting a player
     */
    public static MaplePacket reportPlayerMsg(int type) {//NOTICE
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REPORT_PLAYER_MSG);
        mplew.write(type);
        return mplew.getPacket();
    }

    public static MaplePacket reportPlayer(int type, int number) {//POPUP
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REPORT_PLAYER);
        mplew.write(type);
        if (type == 2) {
            mplew.write(1);
            mplew.writeInt(number);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateMount(int cid, MapleMount mount, boolean levelup) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UNKNOWN);//TODO
        mplew.writeInt(cid);
        mplew.writeInt(mount.getLevel());
        mplew.writeInt(mount.getExp());
        mplew.writeInt(0);
        mplew.write(levelup ? 1 : 0);
        return mplew.getPacket();
    }

    /**
     * Shows a boat
     * @return shows the boat-packet
     */
    public static MaplePacket showShip(boolean enterMap, boolean docked, boolean isBalrog) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader((enterMap ? SendPacketOpcode.SHIP_ENTER_MAP : SendPacketOpcode.SHIP));
        if (isBalrog) {
            if (enterMap) {
                mplew.write(docked ? 0x03 : 0x04);
                mplew.write(1);
            } else {
                mplew.write(0x0A);
                mplew.write(docked ? 0x04 : 0x05);
            }
        } else {
            if (enterMap) {
                mplew.write(docked ? 0x01 : 0x02);
                mplew.write(0x00);
            } else {
                mplew.write(docked ? 0x0C : 0x08);
                mplew.write(docked ? 0x06 : 0x02);
            }
        }
        return mplew.getPacket();
    }

    /**
     * Spawn a reactor
     * @param reactor
     * @return spawn a reactor-packet
     */
    public static MaplePacket spawnReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REACTOR_SPAWN);
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getId());
        mplew.write(reactor.getState());
        mplew.writePoint(reactor.getPosition());
        mplew.write(reactor.getF());
        mplew.writeMapleAsciiString("");
        return mplew.getPacket();
    }

    /**
     * Trigger a reactor
     * @param reactor
     * @return trigger a reactor-packet
     */
    public static MaplePacket triggerReactor(MapleReactor reactor, int stance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REACTOR_HIT);
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePoint(reactor.getPosition());
        mplew.writeShort(stance);
        mplew.write(0);

        //frame delay, set to 5 since there doesn't appear to be a fixed formula for it
        mplew.write(5);

        return mplew.getPacket();
    }

    /**
     * Destroy a reactor
     * @param reactor
     * @return destroy a reactor-packet
     */
    public static MaplePacket destroyReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.REACTOR_DESTROY);
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePoint(reactor.getPosition());
        return mplew.getPacket();
    }

    /**
     * Send a note
     * @param chr
     * @param message
     * @return a note-packet,explled from a guild
     */
    public static MaplePacket noteSend(MapleCharacter chr, String message, long timestamp) {
        //03 = the note has been successfully sent
        //04 + extra bytes = the character is now online, please use the whisper function
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.NOTE_MSG);
        mplew.write(2);//SendNote->Player
        mplew.write(1);//Count

        mplew.writeInt(chr.getId());
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(message);
        mplew.writeLong(DateUtil.getFileTimestamp(timestamp));
        mplew.write(0);//Prize/No-Prize

        return mplew.getPacket();
    }

    public static MaplePacket useSkillBook(MapleCharacter chr, List<Integer> skillIds, int newMaxLevel, boolean canUse, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.USE_SKILLBOOK);
        mplew.writeInt(chr.getId());
        mplew.write(skillIds.size());
        for (int skillId : skillIds) {
            mplew.writeInt(skillId);
        }
        mplew.writeInt(newMaxLevel);
        mplew.write(canUse ? 1 : 0);
        mplew.write(success ? 1 : 0);
        return mplew.getPacket();
    }

    /**
     * Shows the MonsterCarnival Panel<br>
     * Teams:<br>
     * 0 = Red<br>
     * 1 = Blue<br>
     *
     * @return startMonsterCarnival Packet
     */
    public static MaplePacket startMonsterCarnival(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MONSTER_CARNIVAL_START);
        mplew.write(1);//Team
        mplew.writeShort(1);//ObtianedCP [Numerator]
        mplew.writeShort(2);//ObtainedCP [Denominator]
        mplew.writeShort(3);//PartyCP [Num]
        mplew.writeShort(4);//PartyCP [Den]
        mplew.writeShort(5);//OtherCP [Num]
        mplew.writeShort(6);//OtherCP [Den]
        mplew.writeShort(7);//SummonList [1]
        mplew.writeShort(8);//SummonList [2]
        mplew.writeShort(9);//SummonList [3]
        mplew.writeShort(10);//SummonList [4]
        mplew.writeShort(11);//SummonList [5]
        return mplew.getPacket();
    }

    public static MaplePacket playerDiedMessage(String name, int lostCP, int team) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MONSTER_CARNIVAL_DIED);
        mplew.write(team);
        mplew.writeMapleAsciiString(name);
        mplew.write(lostCP);
        return mplew.getPacket();
    }

    public static MaplePacket CPUpdate(boolean party, int curCP, int totalCP, int team) { //CPQ
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (!party) {
            mplew.writeHeader(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP);
        } else {
            mplew.writeHeader(SendPacketOpcode.MONSTER_CARNIVAL_PARTY_CP);
            mplew.write(team);//??
        }
        mplew.writeShort(curCP);
        mplew.writeShort(totalCP);
        return mplew.getPacket();
    }

    public static MaplePacket playerSummoned(String name, int tab, int number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON);
        mplew.write(tab);
        mplew.write(number);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket portToPort(int end) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PORT_TO_PORT);
        mplew.write(1);
        mplew.write(end);
        return mplew.getPacket();
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        //whatever functions calling this better make sure
        //that the character actually HAS a guild
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x1A); //signature for showing guild info

        if (c == null) {//show empty guild (used for leaving, expelled)
            mplew.write(0);
            return mplew.getPacket();
        }

        MapleGuildCharacter initiator = c.getMGC();
        MapleGuild g = c.getClient().getChannelServer().getGuild(initiator);

        if (g == null) {//failed to read from DB - don't show a guild
            mplew.write(0);
            log.warn(MapleClient.getLogMessage(c, "Couldn't load a guild"));
            return mplew.getPacket();
        } else {
            //MapleGuild holds the absolute correct value of guild rank
            //after it is initiated
            MapleGuildCharacter mgc = g.getMGC(c.getId());
            c.setGuildRank(mgc.getGuildRank());
        }

        mplew.write(1); //bInGuild
        mplew.writeInt(c.getGuildId()); //not entirely sure about this one

        mplew.writeMapleAsciiString(g.getName());

        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(g.getRankTitle(i));
        }

        Collection<MapleGuildCharacter> members = g.getMembers();

        mplew.write(members.size());
        //then it is the size of all the members

        for (MapleGuildCharacter mgc : members) //and each of their character ids o_O
        {
            mplew.writeInt(mgc.getId());
        }

        for (MapleGuildCharacter mgc : members) {
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(g.getSignature());
            mplew.writeInt(3);//V58
        }

        mplew.writeInt(g.getCapacity());
        mplew.writeShort(g.getLogoBG());
        mplew.write(g.getLogoBGColor());
        mplew.writeShort(g.getLogo());
        mplew.write(g.getLogoColor());
        mplew.writeMapleAsciiString(g.getNotice());
        mplew.writeInt(g.getGP());
        mplew.writeInt(0);//V58

        return mplew.getPacket();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x3d);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x05);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);

        return mplew.getPacket();
    }

    public static MaplePacket denyGuildInvitation(String charName) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x37);
        mplew.writeMapleAsciiString(charName);

        return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(int code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(code);

        return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x27);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
        mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
        mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
        mplew.writeInt(3);//Alliance Junk? //V59
        return mplew.getPacket();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(bExpelled ? 0x2f : 0x2c);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());

        return mplew.getPacket();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x40);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());

        return mplew.getPacket();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x44);

        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);

        return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x3C);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());

        return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x3e);
        mplew.writeInt(gid);

        for (int i = 0; i < 5; i++) {
            mplew.writeMapleAsciiString(ranks[i]);
        }

        return mplew.getPacket();
    }

    public static MaplePacket guildDisband(int gid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x32);
        mplew.writeInt(gid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x42);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);

        return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x3a);
        mplew.writeInt(gid);
        mplew.write(capacity);

        return mplew.getPacket();
    }

    public static void addThread(MaplePacketLittleEndianWriter mplew, ResultSet rs) throws SQLException {
        mplew.writeInt(rs.getInt("localthreadid"));
        mplew.writeInt(rs.getInt("postercid"));
        mplew.writeMapleAsciiString(rs.getString("name"));
        mplew.writeLong(DateUtil.getFileTimestamp(rs.getLong("timestamp")));
        mplew.writeInt(rs.getInt("icon"));
        mplew.writeInt(rs.getInt("replycount"));
    }

    public static MaplePacket BBSThreadList(ResultSet rs, int start) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.BBS_OPERATION);
        mplew.write(0x06);

        if (!rs.last()) //no result at all
        {
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        int threadCount = rs.getRow();
        if (rs.getInt("localthreadid") == 0) //has a notice
        {
            mplew.write(1);
            addThread(mplew, rs);
            threadCount--; //one thread didn't count (because it's a notice)
        } else {
            mplew.write(0);
        }

        if (!rs.absolute(start + 1)) //seek to the thread before where we start
        {
            rs.first(); //uh, we're trying to start at a place past possible
            start = 0;
        }

        mplew.writeInt(threadCount);
        mplew.writeInt(Math.min(10, threadCount - start));

        for (int i = 0; i < Math.min(10, threadCount - start); i++) {
            addThread(mplew, rs);
            rs.next();
        }

        return mplew.getPacket();
    }

    public static MaplePacket showThread(int localthreadid, ResultSet threadRS, ResultSet repliesRS) throws SQLException, RuntimeException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.BBS_OPERATION);
        mplew.write(0x07);

        mplew.writeInt(localthreadid);
        mplew.writeInt(threadRS.getInt("postercid"));
        mplew.writeLong(DateUtil.getFileTimestamp(threadRS.getLong("timestamp")));
        mplew.writeMapleAsciiString(threadRS.getString("name"));
        mplew.writeMapleAsciiString(threadRS.getString("startpost"));
        mplew.writeInt(threadRS.getInt("icon"));

        if (repliesRS != null) {
            int replyCount = threadRS.getInt("replycount");
            mplew.writeInt(replyCount);

            int i;
            for (i = 0; i < replyCount && repliesRS.next(); i++) {
                mplew.writeInt(repliesRS.getInt("replyid"));
                mplew.writeInt(repliesRS.getInt("postercid"));
                mplew.writeLong(DateUtil.getFileTimestamp(repliesRS.getLong("timestamp")));
                mplew.writeMapleAsciiString(repliesRS.getString("content"));
            }

            if (i != replyCount || repliesRS.next()) {
                //in the unlikely event that we lost count of replyid
                throw new RuntimeException(String.valueOf(threadRS.getInt("threadid")));
                //we need to fix the database and stop the packet sending
                //or else it'll probably error 38 whoever tries to read it

                //there is ONE case not checked, and that's when the thread
                //has a replycount of 0 and there is one or more replies to the
                //thread in bbs_replies
            }
        } else {
            mplew.writeInt(0);
        } //0 replies

        return mplew.getPacket();
    }

    public static MaplePacket showGuildRanks(int npcid, ResultSet rs) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x49);
        mplew.writeInt(npcid);

        if (!rs.last()) {//no guilds o.o
            mplew.writeInt(0);
            return mplew.getPacket();
        } else {
            mplew.writeInt(rs.getRow());//number of entries
            rs.beforeFirst();
            while (rs.next()) {
                mplew.writeMapleAsciiString(rs.getString("name"));
                mplew.writeInt(rs.getInt("GP"));
                mplew.writeInt(rs.getInt("logo"));
                mplew.writeInt(rs.getInt("logoColor"));
                mplew.writeInt(rs.getInt("logoBG"));
                mplew.writeInt(rs.getInt("logoBGColor"));
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.GUILD_OPERATION);
        mplew.write(0x48);
        mplew.writeInt(gid);
        mplew.writeInt(GP);

        return mplew.getPacket();
    }

    public static MaplePacket warpCS(MapleClient c, int date) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        MapleCharacter chr = c.getPlayer();
        mplew.writeHeader(SendPacketOpcode.CS_OPEN);

        mplew.writeLong(-1);//mask
        addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity());
        addInventoryInfo(mplew, chr);
        addSkillRecord(mplew, chr);
        addQuestRecord(mplew, chr);
        addMiniGameRecord(mplew, chr);
        addCoupleRecord(mplew, chr);
        addFriendRecord(mplew, chr);
        addMarriageRecord(mplew, chr);
        addTeleportRockRecord(mplew, chr);
        addMonsterBookRecord(mplew, chr);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);

        mplew.write(c.getAccountName() != null ? 1 : 0);
        if (c.getAccountName() != null) {
            mplew.writeMapleAsciiString(c.getAccountName());
        }


        //EVENT ITEMS
        List<Integer> eventItems = new ArrayList<Integer>();
        mplew.writeInt(eventItems.size());
        for (int eventItem : eventItems) {
            mplew.writeInt(eventItem);
        }

        //MODIFIED COMMODITY ITEMS
        List<Integer> commodityItems = new ArrayList<Integer>();
        mplew.writeShort(commodityItems.size());
        for (int i = 0; i < commodityItems.size(); i++) {
            mplew.writeInt(commodityItems.get(i));//SN
            int mask = 0;
            mplew.writeInt(mask);
            if ((mask & 1) != 0) {
                mplew.writeInt(0);
            }
            if ((mask & 2) != 0) {
                mplew.writeShort(0);
            }
            if ((mask & 4) != 0) {
                mplew.writeInt(0);
            }
            if ((mask & 8) != 0) {
                mplew.write(0);
            }
            if ((mask & 0x10) != 0) {
                mplew.write(0);//checked
            }
            if ((mask & 0x20) != 0) {
                mplew.writeInt(0);
            }
            if ((mask & 0x40) != 0) {
                mplew.writeInt(0);
            }
            if ((mask & 0x80) != 0) {
                mplew.write(0);
            }
            if ((mask & 0x100) != 0) {
                mplew.write(0);
            }
            if ((mask & 0x200) != 0) {
                mplew.write(0);
            }
            if ((mask & 0x400) != 0) {
                mplew.write(0);//checked
            }
            if ((mask & 0x800) != 0) {
                mplew.write(0);//checked
            }
            if ((mask & 0x1000) != 0) {
                mplew.writeShort(0);
            }
            if ((mask & 0x2000) != 0) {
                mplew.writeShort(0);
            }
            if ((mask & 0x4000) != 0) {
                mplew.writeShort(0);
            }
            if ((mask & 0x8000) != 0) {//checked
                List<Integer> itemSubInfo = new ArrayList<Integer>();
                mplew.write(itemSubInfo.size());
                for (int i2 = 0; i2 < itemSubInfo.size(); i2++) {
                    mplew.writeInt(0);//bundle sn id
                }
            }
        }

        //CATEGORY DISCOUNT RATE
        int rate = 0;
        mplew.write(rate);
        for (int i = 0; i < rate; i++) {
            mplew.write(0);//idunno
            mplew.write(0);//idunno
            mplew.write(0);//idunno
        }

        mplew.writeNullData(120);//some unicode string?!?!

        //TOP ITEMS
        int[] topItems = new int[5];
        topItems[0] = 30000032;
        topItems[1] = 30000035;
        topItems[2] = 50000284;
        topItems[3] = 70000326;
        topItems[4] = 10002131;

        for (int i = 1; i <= 8; i++) {
            for (int topItem : topItems) {
                mplew.writeInt(i);
                mplew.writeInt(0);
                mplew.writeInt(topItem);
            }
            for (int topItem : topItems) {
                mplew.writeInt(i);
                mplew.writeInt(1);
                mplew.writeInt(topItem);
            }
        }

        //STOCK
        int stockAmount = 0;
        mplew.writeShort(stockAmount);
        for (int i = 0; i < stockAmount; i++) {
            mplew.writeLong(0);//idunno
        }

        mplew.writeShort(0);

        //LIMITED GOODS
        List<Integer> limitedGoodsItems = new ArrayList<Integer>();
        mplew.writeShort(limitedGoodsItems.size());
        for (int i = 0; i < limitedGoodsItems.size(); i++) {
            //idunno, 104 bytes
            mplew.writeInt(5000009);//DinoBoy - Item-ID
            mplew.writeInt(10100994);//DinoBoy - Sn-ID
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeInt(0);
            mplew.writeInt(1);
            mplew.writeInt(16);//Or 30?
            mplew.writeInt(0);//stock amount
            mplew.writeInt(16);
            mplew.writeInt(date);
            mplew.writeInt(date);
            mplew.writeInt(11);
            mplew.writeInt(12);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeInt(1);
        }
        mplew.write(0);//isEventOn

        mplew.writeInt(1);
        return mplew.getPacket();
    }

    public static MaplePacket showNXMapleTokens(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.CS_UPDATE);
        mplew.writeInt(chr.getCSPoints(0)); // NX
        mplew.writeInt(chr.getCSPoints(1)); // Maple Points
        mplew.writeInt(chr.getCSPoints(2)); // Gift Tokens

        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.writeInt(15741499);//3b 32 f0 00
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(4305024);
        mplew.writeInt(0);
        mplew.writeInt(itemid);
        mplew.writeHexString("CD D0 CC 01 01 00 00 0A F1 4B 40 00 80 00 00 00 01 00 00 10 CB E9 4A BD 53 C9 01");
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSQuestItem(byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.write(114);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(position);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse0() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(0x12);
        mplew.write(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse1(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x37);//CommandID
        mplew.write(0);//amount of items in CS invent
        mplew.write(0);//end
        mplew.writeShort(chr.getStorage().getSlots());
        mplew.writeShort(3);//characters

        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse2() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x39);//CommandID
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static MaplePacket getWishList(MapleCharacter chr, boolean update) {//WishList
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.write(update ? 0x41 : 0x3B);//CommandID
        for (int wishListItem : chr.getWishList()) {
            mplew.writeInt(wishListItem);
        }
        return mplew.getPacket();
    }

    public static MaplePacket wrongCouponCode() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x48);//CommandID
        mplew.write(0x98);//Type of error wrong/used
        return mplew.getPacket();
    }

    public static MaplePacket rewardCouponCode(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CS_OPERATION);
        mplew.write(0x3A);//CommandID
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(0x1A);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket messengerInvite(String from, int messengerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(0x03);
        mplew.writeMapleAsciiString(from);
        mplew.write(0x00);
        mplew.writeInt(messengerid);
        mplew.write(0x00);
        return mplew.getPacket();
    }

    public static MaplePacket addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(0x00);
        mplew.write(position);
        addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0x00);
        return mplew.getPacket();
    }

    public static MaplePacket removeMessengerPlayer(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(0x02);
        mplew.write(position);
        return mplew.getPacket();
    }

    public static MaplePacket updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(0x07);
        mplew.write(position);
        addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0x00);
        return mplew.getPacket();
    }

    public static MaplePacket joinMessenger(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(0x01);
        mplew.write(position);
        return mplew.getPacket();
    }

    public static MaplePacket messengerChat(String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(0x06);
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    public static MaplePacket messengerNote(String text, int mode, int mode2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_MESSENGER);
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);
        return mplew.getPacket();
    }

    public static MaplePacket specialSkillEffect(MapleCharacter chr, SpecialSkillInfo info) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SKILL_EFFECT);
        mplew.writeInt(chr.getId());
        mplew.writeInt(info.getSkillId());
        mplew.write(info.getLevel());
        mplew.write(info.getDirection());
        mplew.write(info.getWSpeed());
        return mplew.getPacket();
    }

    public static MaplePacket specialSkillCancel(MapleCharacter from, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CANCEL_SKILL_EFFECT);
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        return mplew.getPacket();
    }

    public static MaplePacket sendHint(String hint) {
        return sendHint(hint, -1, 5, false, 0, 0);
    }

    public static MaplePacket sendHint(String hint, int width, int time, boolean isStatic, int x, int y) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PLAYER_HINT);
        mplew.writeMapleAsciiString(hint);
        if (width == -1) {
            width = hint.length() * 10;
        }
        if (width < 40) {
            width = 40;// Anything lower crashes client/doesn't look good
        }
        mplew.writeShort(width);
        mplew.writeShort(time);
        mplew.write(isStatic ? 0 : 1);

        if (isStatic) {
            mplew.writeInt(x);
            mplew.writeInt(y);
        }
        return mplew.getPacket();
    }

    public static MaplePacket sendTV(MapleCharacter chr, List<String> messages, int type, MapleCharacter partner, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.TV_SMEGA);
        mplew.write(partner != null ? 2 : 0);
        mplew.write(type);
        addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(partner != null ? partner.getName() : "");
        for (String s : messages) {//Messages
            mplew.writeMapleAsciiString(s);
        }
        mplew.writeInt(time);//Length
        if (partner != null) {
            addCharLook(mplew, partner, false);
        }
        return mplew.getPacket();
    }

    public static MaplePacket cancelTvSmega() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CANCEL_TV_SMEGA);
        return mplew.getPacket();
    }

    public static MaplePacket luckSackPass(int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.LUCKSACK_PASS);
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static MaplePacket luckSackFail() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.LUCKSACK_FAIL);
        return mplew.getPacket();
    }

    public static MaplePacket showForcedMapEquip() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.FORCED_MAP_EQUIP);
        return mplew.getPacket();
    }

    public static MaplePacket showMagnetSuccess(int mobid, int success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_DRAGGED);
        mplew.writeInt(mobid);
        mplew.write(success);
        return mplew.getPacket();
    }

    public static MaplePacket buffCooldown(int skillId, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.BUFF_COOLDOWN);
        mplew.writeInt(skillId);
        mplew.writeShort(time);
        return mplew.getPacket();
    }

    public static MaplePacket showSkillMacros(List<MapleMacro> macros) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SKILL_MACRO);
        mplew.write(macros.size());
        for (MapleMacro macro : macros) {
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket updatePet(MaplePet pet, boolean alive) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MODIFY_INVENTORY_ITEM);
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(5);
        mplew.write(pet.getPosition());
        mplew.writeShort(0);
        mplew.write(5);
        mplew.write(pet.getPosition());
        mplew.write(0);
        mplew.write(3);
        mplew.writeInt(pet.getItemId());
        mplew.write(1);
        mplew.writeInt(pet.getUniqueId());
        mplew.writeInt(0);
        mplew.write(HexTool.getByteArrayFromHexString("00 40 6f e5 0f e7 17 02"));
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(pet.getName(), '\0', 13));
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (alive) {
            mplew.writeLong(DateUtil.getFileTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            mplew.write(0);
            mplew.write(ITEM_MAGIC);
            mplew.writeHexString("bb 46 e6 17");
            mplew.write(2);
        }
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket showPet(MapleCharacter chr, MaplePet pet, boolean remove) {
        return showPet(chr, pet, remove, false);
    }

    public static MaplePacket showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SPAWN_PET);

        mplew.writeInt(chr.getId());
        mplew.write(chr.getPetIndex(pet));
        if (remove) {
            mplew.write(0);
            mplew.write(hunger ? 1 : 0);//should be die/alive
        } else {
            mplew.write(1);
            mplew.write(0);
            mplew.writeInt(pet.getItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeInt(pet.getUniqueId());
            mplew.writeInt(0);
            mplew.writePoint(pet.getPos());
            mplew.write(pet.getStance());
            mplew.writeInt(pet.getFh());
        }
        return mplew.getPacket();
    }

    public static MaplePacket showSummonedPet(MapleCharacter chr, MaplePet pet) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.PET_SHOW);
        mplew.writeInt(chr.getId());
        mplew.write(chr.getPetIndex(pet));
        mplew.writeInt(pet.getUniqueId());
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket movePet(int cid, int pid, int slot, MovementPath movementPath) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.MOVE_PET);
        mplew.writeInt(cid);
        mplew.write(slot);
        serializeMovementPath(mplew, movementPath);

        return mplew.getPacket();
    }

    public static MaplePacket petChat(int cid, int act, String text, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PET_CHAT);
        mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeShort(act);
        mplew.writeMapleAsciiString(text);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket commandResponse(int cid, int command, int slot, boolean success, boolean food) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.PET_COMMAND);
        mplew.writeInt(cid);
        mplew.write(slot);
        if (!food) {
            mplew.write(0);
        }
        mplew.write(command);
        mplew.write(success ? 1 : 0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket showOwnPetLevelUp(int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        mplew.write(4);
        mplew.write(0);
        mplew.write(index);

        return mplew.getPacket();
    }

    public static MaplePacket showPetLevelUp(MapleCharacter chr, int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
        mplew.writeInt(chr.getId());
        mplew.write(4);
        mplew.write(0);
        mplew.write(index);

        return mplew.getPacket();
    }

    public static MaplePacket changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.PET_NAMECHANGE);
        mplew.writeInt(chr.getId());
        mplew.write(slot);
        mplew.writeMapleAsciiString(newname);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket petStatUpdate(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeHeader(SendPacketOpcode.UPDATE_STATS);

        int mask = 0;
        switch (chr.getPetsNumber()) {
            case 1:
                mask |= MapleStat.PET_1.getValue();
                break;
            case 2:
                mask |= MapleStat.PET_1.getValue();
                mask |= MapleStat.PET_2.getValue();
                break;
            case 3:
                mask |= MapleStat.PET_1.getValue();
                mask |= MapleStat.PET_2.getValue();
                mask |= MapleStat.PET_3.getValue();
                break;
        }

        mplew.write(0);
        mplew.writeInt(mask);

        for (MaplePet pet : chr.getAllPets()) {
            if (pet != null) {
                mplew.writeInt(pet.getUniqueId());
            } else {
                mplew.writeInt(0);
            }
            mplew.writeInt(0);
        }
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket itemMergeComplete(boolean changed, byte inv) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.ITEM_MERGE_COMPLETE);
        mplew.write(changed ? 1 : 0);
        mplew.write(inv);
        return mplew.getPacket();
    }

    public static MaplePacket itemSortComplete(boolean changed, byte inv) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.ITEM_MERGE_COMPLETE);
        mplew.write(changed ? 1 : 0);
        mplew.write(inv);
        return mplew.getPacket();
    }

    public static MaplePacket summonSkill(int cid, int summonSkillId, int newStance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SUMMON_SKILL);
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        return mplew.getPacket();
    }

    public static MaplePacket ariantPqUpdate(String name, int points, boolean empty) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UPDATE_ARIANT_PQ_POINTS);
        mplew.write(empty ? 0 : 1);
        if (!empty) {
            mplew.writeMapleAsciiString(name);
            mplew.writeInt(points);
        }
        return mplew.getPacket();
    }

    public static MaplePacket catchMonster(int mobid, int itemid, boolean success) {
        //[4C 00] [00] [32 A3 22 00] [BD E8 8D 00]
        //[4C 00] [01] [32 A3 22 00] [BD E8 8D 00]
        //[BE 00] [8B 70 34 00] [32 A3 22 00] [01]
        //[BE 00] [49 6C 34 00] [32 A3 22 00] [01]
        //[BE 00] [0A 71 34 00] [32 A3 22 00] [00]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CATCH_MONSTER);
        mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket eventInstruction() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.EVENT_INSTRUCTION);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket useChalkBoard(int cid, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.CHALKBOARD);
        mplew.writeInt(cid);
        boolean open = text != null;
        mplew.write(open ? 1 : 0);
        if (open) {
            mplew.writeMapleAsciiString(text);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showBossShrineTime(int time, boolean zakum) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(zakum ? SendPacketOpcode.ZAKUM_SHRINE : SendPacketOpcode.HORNTAIL_SHRINE);
        mplew.write(0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket sendSpouseChat(String spouse, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SPOUSE_CHAT);
        mplew.writeMapleAsciiString(spouse);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static MaplePacket updateTeleportMaps(MapleCharacter chr, boolean vip) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.TELEPORT_ROCK);
        mplew.write(3);
        mplew.write(vip ? 1 : 0);
        for (int teleportMap : (vip ? chr.getVipTeleportMaps() : chr.getTeleportMaps())) {
            mplew.writeInt(teleportMap);
        }
        return mplew.getPacket();
    }

    public static MaplePacket sendTeleportRockError(int code, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.TELEPORT_ROCK);
        mplew.write(code);
        mplew.write(type);
        return mplew.getPacket();
    }

    public static MaplePacket sendGMBlock() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GM_COMMANDS);
        mplew.write(0x04);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket sendGMInvalidCharacterName() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GM_COMMANDS);
        mplew.write(0x06);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket sendGMSetGetVarResult(String name, String variable, String value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GM_COMMANDS);
        mplew.write(0x09);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(variable);
        mplew.writeMapleAsciiString(value);
        return mplew.getPacket();
    }

    public static MaplePacket sendGMHide(boolean hidden) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GM_COMMANDS);
        mplew.write(0x10);
        mplew.write(hidden ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket sendGMhiredMerchantPlace(boolean isChannel, int mapOrChannel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GM_COMMANDS);
        mplew.write(0x13);
        mplew.write(isChannel ? 1 : 0);
        mplew.write(mapOrChannel);
        return mplew.getPacket();
    }

    public static MaplePacket sendGMWarning(boolean succeed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.GM_COMMANDS);
        mplew.write(0x1D);
        mplew.write(succeed ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket sendRecenterMap() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.RECENTER_MAP);
        return mplew.getPacket();
    }

    public static MaplePacket showOwnObtainedMonsterCard() {
        return showEffect(0x0D);
    }

    public static MaplePacket showObtainedMonsterCard(MapleCharacter chr) {
        return showForeignEffect(chr.getId(), 0x0D);
    }

    public static MaplePacket addMonsterCard(MonsterCard card, boolean updateCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MONSTERBOOK_ADD);
        mplew.write(updateCount ? 1 : 0);
        mplew.writeInt(card.getId());
        mplew.writeInt(card.getLevel());
        return mplew.getPacket();
    }

    public static MaplePacket changeMonsterBookCover(int cardId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MONSTERBOOK_CHANGE_COVER);
        mplew.writeInt(cardId);
        return mplew.getPacket();
    }

    public static MaplePacket showOwnFinishQuest() {
        return showEffect(9);
    }

    public static MaplePacket showFinishQuest(MapleCharacter player) {
        return showForeignEffect(player.getId(), 0x09);
    }

    public static MaplePacket playPortalSE() {
        return showEffect(7);
    }

    public static MaplePacket sendMapleTip(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.MAPLE_TIP);
        mplew.write(1);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static MaplePacket showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.OX_QUIZ);
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);
        return mplew.getPacket();
    }

    public static MaplePacket sendKnockbackLeft() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.KNOCKBACK_LEFT);
        return mplew.getPacket();
    }

    public static MaplePacket rollSnowball(int roll0, int roll1) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.ROLL_SNOWBALL);
        //!packet fb00000000000000000000 0000 00 0000 00000000000000000000
        //!packet fb00000000000000000000b00000 b000 00
        //!packet fb00000000000000000000 0000 00 0000 00 MOVE TO START
        //!packet fb 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 reset snowball
        //!packet fb00 0001010101000000 0000 01 0000 0101010101010101010000000 posiiton #2
        //!packet fb00 0002020202000000 0000 02 0000 0101010101010101020000000
        //!packet fb 00 03 00 01 01 01 01 01 01 02 0000 00 0000 00 hide top
        //!packet fb 00 02 00 01 01 01 01 01 01 02 0000 00 0000 00 hide bottom
        mplew.write(0); //command i
        mplew.writeLong(0);
        mplew.writeShort(roll0);
        mplew.write(0);
        mplew.writeShort(roll1);
        mplew.writeLong(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket hitSnowBall(int team, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.HIT_SNOWBALL);
        mplew.write(team); // 0=down, 1=up
        mplew.writeInt(damage);
        return mplew.getPacket();
    }

    public static MaplePacket snowballMessage(int team, int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SNOWBALL_MESSAGE);
        mplew.write(team); // 0=down, 1=up
        mplew.writeInt(message);
        return mplew.getPacket();
    }

    public static MaplePacket showMovieEffect(String scene) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT);
        mplew.write(18);
        mplew.writeMapleAsciiString(scene);
        return mplew.getPacket();
    }

    public static MaplePacket enableReports(boolean allow) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.ENABLE_REPORT);
        mplew.write(allow ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket DEFAULT_PACKET() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeHeader(SendPacketOpcode.UNKNOWN);
        return mplew.getPacket();
    }
}
