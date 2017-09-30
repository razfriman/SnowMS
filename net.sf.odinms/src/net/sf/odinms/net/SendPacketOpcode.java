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

public enum SendPacketOpcode implements WritableIntValueHolder {
	// GENERAL
	PING,
	
	// LOGIN
	LOGIN_STATUS,
	SEND_LINK,
	PIN_OPERATION, 
	SERVERLIST,
	RECOMMENDED_SERVERLIST,
	SERVERSTATUS,
	SERVER_IP, 
	CHARLIST, 
	CHAR_NAME_RESPONSE, 
	RELOG_RESPONSE, 
	ADD_NEW_CHAR_ENTRY, 
	DELETE_CHAR_RESPONSE,
	GENDER_SET,
	PIN_ASSIGNED,
	VIEW_ALL_CHAR,
	CHANNEL_SELECTED,

	// CHANNEL
	CHANGE_CHANNEL, 
	UPDATE_STATS, 
	FAME_RESPONSE,
	UPDATE_SKILLS, 
	WARP_TO_MAP, 
	SERVERMESSAGE, 
	AVATAR_MEGA, 
	SPAWN_NPC, 
	SPAWN_NPC_REQUEST_CONTROLLER,
	SPAWN_MONSTER,
	SPAWN_MONSTER_CONTROL, 
	MOVE_MONSTER_RESPONSE, 
	CHATTEXT,
	SHOW_STATUS_INFO, 
	SHOW_QUEST_COMPLETION, 
	WHISPER,
	SPAWN_PLAYER, 
	SHOW_SCROLL_EFFECT,
	SHOW_ITEM_GAIN_INCHAT, 
	KILL_MONSTER, 
	DROP_ITEM_FROM_MAPOBJECT, 
	FACIAL_EXPRESSION, 
	MOVE_PLAYER, 
	MOVE_MONSTER, 
	CLOSE_RANGE_ATTACK, 
	RANGED_ATTACK, 
	MAGIC_ATTACK, 
	OPEN_NPC_SHOP, 
	CONFIRM_SHOP_TRANSACTION,
	OPEN_STORAGE, 
	MODIFY_INVENTORY_ITEM, 
	REMOVE_PLAYER_FROM_MAP, 
	REMOVE_ITEM_FROM_MAP, 
	UPDATE_CHAR_LOOK,
	SHOW_FOREIGN_EFFECT, 
	GIVE_FOREIGN_BUFF, 
	CANCEL_FOREIGN_BUFF,
	DAMAGE_PLAYER,
	CHAR_INFO, 
	UPDATE_QUEST_INFO,
	GIVE_BUFF, 
	CANCEL_BUFF, 
	PLAYER_INTERACTION, 
	UPDATE_CHAR_BOX,
	NPC_TALK, 
	NPC_CONFIRM,
	KEYMAP,
	NPC_ACTION,
	SHOW_MONSTER_HP,
	PARTY_OPERATION,
	UPDATE_PARTYMEMBER_HP,
	MULTICHAT,
	APPLY_MONSTER_STATUS,
	CANCEL_MONSTER_STATUS,
	CLOCK,
	SPAWN_PORTAL,
	SPAWN_DOOR,
	REMOVE_DOOR,
	SPAWN_SPECIAL_MAPOBJECT,
	REMOVE_SPECIAL_MAPOBJECT,
	SUMMON_ATTACK,
	MOVE_SUMMON,
	SPAWN_MIST,
	REMOVE_MIST,
	DAMAGE_SUMMON,
	DAMAGE_MONSTER,
	BUDDYLIST,
	SHOW_ITEM_EFFECT,
	SHOW_CHAIR,
	CANCEL_CHAIR,
	BLOCK_PORTAL,
	BLOCK_PORTAL_SHOP,
	REPORT_PLAYER,
	REPORT_PLAYER_MSG,
	BOSS_ENV,
	SHIP,
	SHIP_ENTER_MAP,
	NOTE_MSG,
	MONSTER_CARNIVAL_START,
	MONSTER_CARNIVAL_OBTAINED_CP,
	MONSTER_CARNIVAL_PARTY_CP,
	MONSTER_CARNIVAL_SUMMON,
	MONSTER_CARNIVAL_DIED,
	USE_SKILLBOOK,
	MAPLE_MESSENGER,
	MAP_EFFECT,
	REACTOR_SPAWN,
	REACTOR_HIT,
	REACTOR_DESTROY,
	PORT_TO_PORT,
	GUILD_OPERATION,
	BBS_OPERATION,
	SKILL_EFFECT,
	CANCEL_SKILL_EFFECT,
	PLAYER_HINT,
	TV_SMEGA,
	CANCEL_TV_SMEGA,
	LUCKSACK_FAIL,
	LUCKSACK_PASS,
	FORCED_MAP_EQUIP,
	SHOW_DRAGGED,
	BUFF_COOLDOWN,
	SKILL_MACRO,
	ITEM_MERGE_COMPLETE,
	ITEM_SORT_COMPLETE,
	SPAWN_PET,
	MOVE_PET,
	PET_CHAT,
	PET_NAMECHANGE,
	PET_SHOW,
	PET_COMMAND,
	UPDATE_ARIANT_PQ_POINTS,
	CHALKBOARD,
	CS_OPEN,
	CS_UPDATE,
	CS_OPERATION,
	EVENT_INSTRUCTION,
	ZAKUM_SHRINE,
	HORNTAIL_SHRINE,
	SPOUSE_CHAT,
	CATCH_MONSTER,
	SUMMON_SKILL,
	TELEPORT_ROCK,
    GM_COMMANDS,
    RECENTER_MAP,
    STOP_CLOCK,
    MONSTERBOOK_ADD,
    MONSTERBOOK_CHANGE_COVER,
    MAPLE_TIP,
    ALLIANCE_OPERATION,
    OX_QUIZ,
    ROLL_SNOWBALL,
    HIT_SNOWBALL,
    SNOWBALL_MESSAGE,
    KNOCKBACK_LEFT,
    //OPEN_CYGNUS_CREATE,
    CYGNUS_CREATE_RESPONSE,
    ENABLE_REPORT,

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
		FileInputStream fileInputStream = new FileInputStream(System.getProperty("net.sf.odinms.sendops"));
		props.load(fileInputStream);
		fileInputStream.close();
		return props;
	}
	
	public static SendPacketOpcode getByType(int type) {
		for (SendPacketOpcode l : SendPacketOpcode.values()) {
			if (l.getValue() == type) {
				return l;
			}
		}
		return UNKNOWN;
	}
	
	public static SendPacketOpcode getByName(String name) { 
	    for (SendPacketOpcode l : SendPacketOpcode.values()) {
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
			throw new RuntimeException("Failed to load sendops", e);
		}
	}
}
