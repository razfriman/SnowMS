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

package net.sf.odinms.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum RecvPacketOpcode implements WritableIntValueHolder {
    // GENERIC

    PONG,
    // LOGIN
    AFTER_LOGIN,
    SERVERLIST_REQUEST,
    SERVERLIST_REREQUEST,
    CHARLIST_REQUEST,
    CHAR_SELECT,
    CHECK_CHAR_NAME,
    CREATE_CHAR,
    DELETE_CHAR,
    LOGIN_PASSWORD,
    RELOG,
    SERVERSTATUS_REQUEST,
    SET_GENDER,
    REGISTER_PIN,
    CLIENT_ERROR,
    GUEST_LOGIN,
    VIEW_ALL_CHAR,
    VIEW_ALL_CHAR_CONNECT,
	REGISTER_PIC,
	CHAR_SELECT_WITH_PIC,
    // CHANNEL
    CHANGE_CHANNEL,
    CHAR_INFO_REQUEST,
    CLOSE_RANGE_ATTACK,
    RANGED_ATTACK,
    MAGIC_ATTACK,
    FACE_EXPRESSION,
    HEAL_OVER_TIME,
    ITEM_MOVE,
    ITEM_PICKUP,
    CHANGE_MAP,
    MESO_DROP,
    MOVE_LIFE,
    MOVE_PLAYER,
    NPC_SHOP,
    NPC_TALK,
    NPC_TALK_MORE,
    PLAYER_LOGGEDIN,
    QUEST_ACTION,
    TAKE_DAMAGE,
    USE_CASH_ITEM,
    USE_ITEM,
    USE_RETURN_SCROLL,
    USE_UPGRADE_SCROLL,
    USE_SUMMON_BAG,
    NPC_ACTION,
    REPORT_PLAYER,
    TELEPORT_ROCK,
    MAPLE_MESSENGER,
    USE_SKILLBOOK,
    GENERAL_CHAT,
    WHISPER,
    SPECIAL_MOVE,
    CANCEL_BUFF,
    PLAYER_INTERACTION,
    CANCEL_ITEM_EFFECT,
    DISTRIBUTE_AP,
    DISTRIBUTE_SP,
    CHANGE_KEYMAP,
    CHANGE_MAP_SPECIAL,
    STORAGE,
    STRANGE_DATA,
    GIVE_FAME,
    PARTY_OPERATION,
    DENY_PARTY_REQUEST, //probably something else too..
    PARTYCHAT,
    USE_DOOR,
    ENTER_MTS,
    ENTER_CASH_SHOP,
    DAMAGE_SUMMON,
    MOVE_SUMMON,
    SUMMON_ATTACK,
    BUDDYLIST_MODIFY,
    USE_ITEMEFFECT,
    USE_CHAIR,
    MONSTER_CARNIVAL,
    DAMAGE_REACTOR,
    CANCEL_CHAIR,
    POST_PORTTOPORT,
    GUILD_OPERATION,
    BBS_OPERATION,
    MOB_TO_MOB_HIT,
    TOUCHING_CS,
    CASHSHOP_OPERATION,
    COUPON_CODE,
    SKILL_EFFECT,
    MACRO_MODIFIER,
    AUTO_AGGRO,
    USE_PET_FOOD,
    SPAWN_PET,
    MOVE_PET,
    PET_CHAT,
    PET_COMMAND,
    PET_LOOT,
    CANCEL_DEBUFF,
    MONSTER_BOMB,
    OBJECT_REQUEST,
    NOTE_ACTION,
    PLAYER_UPDATE,
    CLOSE_CHALKBOARD,
    DENY_GUILD_REQUEST,
    USE_CATCH_ITEM,
    USE_MOUNT_FOOD,
    DUEY_ACTION,
    SPOUSE_CHAT,
    USE_MINERVA_SEARCH,
    USE_TELEPORT_ROCK,
    LIE_DETECTOR,
    RING_ACTION,
    PET_AUTO_POT,
    MTS_OPERATION,
    CLIENT_INFO,
    AUTO_DISTRIBUTE_AP,
    MONSTERBOOK,
    USE_EXP_ITEM,
    ITEM_MERGE,
    ITEM_SORT,
    HYPNOTIZE_MOB_TO_MOB_HIT,
    MONSTER_BANISH,
    TOUCH_SNOWBALL,
    MAKE_ITEM,
    ADMIN_COMMAND,
    ADMIN_LOG,
    USE_SCRIPTED_ITEM,
    UNKNOWN;
    private int code = -2;

    public void setValue(int code) {
        this.code = code;
    }

    @Override
    public int getValue() {
        return code;
    }

    public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("net.sf.odinms.recvops"));
        props.load(fis);
        fis.close();
        return props;
    }

    public static RecvPacketOpcode getByType(int type) {
        for (RecvPacketOpcode l : RecvPacketOpcode.values()) {
            if (l.getValue() == type) {
                return l;
            }
        }
        return UNKNOWN;
    }

    public static RecvPacketOpcode getByName(String name) {
        for (RecvPacketOpcode l : RecvPacketOpcode.values()) {
            if (l.name().equalsIgnoreCase(name)) {
                return l;
            }
        }
        return UNKNOWN;
    }

    static {
        try {
            ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load recvops", e);
        }
    }
}
