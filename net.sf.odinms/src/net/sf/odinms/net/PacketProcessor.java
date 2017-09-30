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

import net.sf.odinms.net.channel.handler.*;
import net.sf.odinms.net.handler.*;
import net.sf.odinms.net.login.handler.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PacketProcessor {

    private static Logger log = LoggerFactory.getLogger(PacketProcessor.class);

    public enum Mode {

        LOGINSERVER,
        CHANNELSERVER
    };
    private static PacketProcessor instance;
    private MaplePacketHandler[] handlers;

    private PacketProcessor() {
        int maxRecvOp = 0;
        for (RecvPacketOpcode op : RecvPacketOpcode.values()) {
            if (op.getValue() > maxRecvOp) {
                maxRecvOp = op.getValue();
            }
        }
        handlers = new MaplePacketHandler[maxRecvOp + 1];
    }

    public MaplePacketHandler getHandler(short packetId) {
        if (packetId > handlers.length) {
            return null;
        }
        MaplePacketHandler handler = handlers[packetId];
        if (handler != null) {
            return handler;
        }
        return null;
    }

    public void registerHandler(RecvPacketOpcode code, MaplePacketHandler handler) {
        try {
            handlers[code.getValue()] = handler;
        } catch (ArrayIndexOutOfBoundsException ae) {
            log.error("Check your Recv Packet Opcodes for - " + code.name(), ae);
        }
    }

    public synchronized static PacketProcessor getProcessor(Mode mode) {
        if (instance == null) {
            instance = new PacketProcessor();
            instance.reset(mode);
        }
        return instance;
    }

    public void reset(Mode mode) {
        handlers = new MaplePacketHandler[handlers.length];

        //Both Modes
        registerHandler(RecvPacketOpcode.PONG, new KeepAliveHandler());

        switch (mode) {
            case LOGINSERVER:
                registerHandler(RecvPacketOpcode.AFTER_LOGIN, new AfterLoginHandler());
                registerHandler(RecvPacketOpcode.SERVERLIST_REREQUEST, new ServerlistRequestHandler());
                registerHandler(RecvPacketOpcode.CHARLIST_REQUEST, new CharlistRequestHandler());
                registerHandler(RecvPacketOpcode.CHAR_SELECT, new CharSelectedHandler());
                registerHandler(RecvPacketOpcode.LOGIN_PASSWORD, new LoginPasswordHandler());
                registerHandler(RecvPacketOpcode.RELOG, new RelogRequestHandler());
                registerHandler(RecvPacketOpcode.SERVERLIST_REQUEST, new ServerlistRequestHandler());
                registerHandler(RecvPacketOpcode.SERVERSTATUS_REQUEST, new ServerStatusRequestHandler());
                registerHandler(RecvPacketOpcode.CHECK_CHAR_NAME, new CheckCharNameHandler());
                registerHandler(RecvPacketOpcode.CREATE_CHAR, new CreateCharHandler());
                registerHandler(RecvPacketOpcode.DELETE_CHAR, new DeleteCharHandler());
                registerHandler(RecvPacketOpcode.SET_GENDER, new SetGenderHandler());
                registerHandler(RecvPacketOpcode.REGISTER_PIN, new RegisterPinHandler());
                registerHandler(RecvPacketOpcode.CLIENT_ERROR, new ClientErrorHandler());
                registerHandler(RecvPacketOpcode.GUEST_LOGIN, new GuestLoginHandler());
		registerHandler(RecvPacketOpcode.REGISTER_PIC, new RegisterPicHandler());
                break;
            case CHANNELSERVER:
                registerHandler(RecvPacketOpcode.CHANGE_CHANNEL, new ChangeChannelHandler());
                registerHandler(RecvPacketOpcode.STRANGE_DATA, LoginRequiringNoOpHandler.getInstance());
                registerHandler(RecvPacketOpcode.GENERAL_CHAT, new GeneralchatHandler());
                registerHandler(RecvPacketOpcode.WHISPER, new WhisperHandler());
                registerHandler(RecvPacketOpcode.NPC_TALK, new NPCTalkHandler());
                registerHandler(RecvPacketOpcode.NPC_TALK_MORE, new NPCMoreTalkHandler());
                registerHandler(RecvPacketOpcode.QUEST_ACTION, new QuestActionHandler());
                registerHandler(RecvPacketOpcode.NPC_SHOP, new NPCShopHandler());
                registerHandler(RecvPacketOpcode.ITEM_MOVE, new ItemMoveHandler());
                registerHandler(RecvPacketOpcode.MESO_DROP, new MesoDropHandler());
                registerHandler(RecvPacketOpcode.PLAYER_LOGGEDIN, new PlayerLoggedinHandler());
                registerHandler(RecvPacketOpcode.CHANGE_MAP, new ChangeMapHandler());
                registerHandler(RecvPacketOpcode.MOVE_LIFE, new MoveLifeHandler());
                registerHandler(RecvPacketOpcode.CLOSE_RANGE_ATTACK, new CloseRangeDamageHandler());
                registerHandler(RecvPacketOpcode.RANGED_ATTACK, new RangedAttackHandler());
                registerHandler(RecvPacketOpcode.MAGIC_ATTACK, new MagicDamageHandler());
                registerHandler(RecvPacketOpcode.TAKE_DAMAGE, new TakeDamageHandler());
                registerHandler(RecvPacketOpcode.MOVE_PLAYER, new MovePlayerHandler());
                registerHandler(RecvPacketOpcode.USE_CASH_ITEM, new UseCashItemHandler());
                registerHandler(RecvPacketOpcode.USE_ITEM, new UseItemHandler());
                registerHandler(RecvPacketOpcode.USE_RETURN_SCROLL, new UseItemHandler());
                registerHandler(RecvPacketOpcode.USE_UPGRADE_SCROLL, new ScrollHandler());
                registerHandler(RecvPacketOpcode.FACE_EXPRESSION, new FaceExpressionHandler());
                registerHandler(RecvPacketOpcode.HEAL_OVER_TIME, new HealOvertimeHandler());
                registerHandler(RecvPacketOpcode.ITEM_PICKUP, new ItemPickupHandler());
                registerHandler(RecvPacketOpcode.CHAR_INFO_REQUEST, new CharInfoRequestHandler());
                registerHandler(RecvPacketOpcode.SPECIAL_MOVE, new SpecialMoveHandler());
                registerHandler(RecvPacketOpcode.CANCEL_BUFF, new CancelBuffHandler());
                registerHandler(RecvPacketOpcode.CANCEL_ITEM_EFFECT, new CancelItemEffectHandler());
                registerHandler(RecvPacketOpcode.PLAYER_INTERACTION, new PlayerInteractionHandler());
                registerHandler(RecvPacketOpcode.DISTRIBUTE_AP, new DistributeAPHandler());
                registerHandler(RecvPacketOpcode.DISTRIBUTE_SP, new DistributeSPHandler());
                registerHandler(RecvPacketOpcode.CHANGE_KEYMAP, new KeymapChangeHandler());
                registerHandler(RecvPacketOpcode.CHANGE_MAP_SPECIAL, new ChangeMapSpecialHandler());
                registerHandler(RecvPacketOpcode.STORAGE, new StorageHandler());
                registerHandler(RecvPacketOpcode.GIVE_FAME, new GiveFameHandler());
                registerHandler(RecvPacketOpcode.PARTY_OPERATION, new PartyOperationHandler());
                registerHandler(RecvPacketOpcode.DENY_PARTY_REQUEST, new DenyPartyRequestHandler());
                registerHandler(RecvPacketOpcode.PARTYCHAT, new PartychatHandler());
                registerHandler(RecvPacketOpcode.USE_DOOR, new DoorHandler());
                registerHandler(RecvPacketOpcode.ENTER_MTS, new EnterMTSHandler());
                registerHandler(RecvPacketOpcode.ENTER_CASH_SHOP, new EnterCashShopHandler());
                registerHandler(RecvPacketOpcode.DAMAGE_SUMMON, new DamageSummonHandler());
                registerHandler(RecvPacketOpcode.MOVE_SUMMON, new MoveSummonHandler());
                registerHandler(RecvPacketOpcode.SUMMON_ATTACK, new SummonDamageHandler());
                registerHandler(RecvPacketOpcode.BUDDYLIST_MODIFY, new BuddylistModifyHandler());
                registerHandler(RecvPacketOpcode.USE_ITEMEFFECT, new UseItemEffectHandler());
                registerHandler(RecvPacketOpcode.USE_CHAIR, new UseChairHandler());
                registerHandler(RecvPacketOpcode.CANCEL_CHAIR, new CancelChairHandler());
                registerHandler(RecvPacketOpcode.NPC_ACTION, new NPCAnimationHandler());
                registerHandler(RecvPacketOpcode.REPORT_PLAYER, new ReportPlayerHandler());
                registerHandler(RecvPacketOpcode.DAMAGE_REACTOR, new ReactorHitHandler());
                registerHandler(RecvPacketOpcode.POST_PORTTOPORT, new PortToPortHandler());
                registerHandler(RecvPacketOpcode.USE_SKILLBOOK, new UseSkillBookHandler());
                registerHandler(RecvPacketOpcode.GUILD_OPERATION, new GuildOperationHandler());
                registerHandler(RecvPacketOpcode.BBS_OPERATION, new BBSOperationHandler());
                registerHandler(RecvPacketOpcode.CHANGE_CHANNEL, new ChangeChannelHandler());
                registerHandler(RecvPacketOpcode.TOUCHING_CS, new TouchingCashShopHandler());
                registerHandler(RecvPacketOpcode.CASHSHOP_OPERATION, new CashShopOperationHandler());
                registerHandler(RecvPacketOpcode.COUPON_CODE, new CouponCodeHandler());
                registerHandler(RecvPacketOpcode.SKILL_EFFECT, new SkillEffectHandler());
                registerHandler(RecvPacketOpcode.USE_SUMMON_BAG, new UseSummonBagHandler());
                registerHandler(RecvPacketOpcode.MAPLE_MESSENGER, new MessengerHandler());
                registerHandler(RecvPacketOpcode.ITEM_MERGE, new ItemMergeHandler());
                registerHandler(RecvPacketOpcode.ITEM_SORT, new ItemSortHandler());
                registerHandler(RecvPacketOpcode.MACRO_MODIFIER, new SkillMacroHandler());
                registerHandler(RecvPacketOpcode.AUTO_AGGRO, new AutoAggroHandler());
                registerHandler(RecvPacketOpcode.CANCEL_DEBUFF, new CancelDebuffHandler());
                registerHandler(RecvPacketOpcode.MONSTER_BOMB, new MonsterBombHandler());
                registerHandler(RecvPacketOpcode.SPAWN_PET, new SpawnPetHandler());
                registerHandler(RecvPacketOpcode.MOVE_PET, new MovePetHandler());
                registerHandler(RecvPacketOpcode.PET_CHAT, new PetChatHandler());
                registerHandler(RecvPacketOpcode.PET_COMMAND, new PetCommandHandler());
                registerHandler(RecvPacketOpcode.USE_PET_FOOD, new UsePetFoodHandler());
                registerHandler(RecvPacketOpcode.PET_LOOT, new PetLootHandler());
                registerHandler(RecvPacketOpcode.OBJECT_REQUEST, new ObjectRequestHandler());
                registerHandler(RecvPacketOpcode.NOTE_ACTION, new NoteActionHandler());
                registerHandler(RecvPacketOpcode.PLAYER_UPDATE, new PlayerUpdateHandler());
                registerHandler(RecvPacketOpcode.DENY_GUILD_REQUEST, new DenyGuildRequestHandler());
                registerHandler(RecvPacketOpcode.CLOSE_CHALKBOARD, new CloseChalkboardHandler());
                registerHandler(RecvPacketOpcode.USE_CATCH_ITEM, new UseCatchItemHandler());
                registerHandler(RecvPacketOpcode.USE_MOUNT_FOOD, new UseMountFoodHandler());
                registerHandler(RecvPacketOpcode.DUEY_ACTION, new DueyActionHandler());
                registerHandler(RecvPacketOpcode.SPOUSE_CHAT, new SpouseChatHandler());
                registerHandler(RecvPacketOpcode.MOB_TO_MOB_HIT, new MobToMobHitHandler());
                registerHandler(RecvPacketOpcode.TELEPORT_ROCK, new TeleportRockHandler());
                registerHandler(RecvPacketOpcode.USE_TELEPORT_ROCK, new UseTeleportRockHandler());
                registerHandler(RecvPacketOpcode.USE_MINERVA_SEARCH, new UseMinervaSearchHandler());
                registerHandler(RecvPacketOpcode.PET_AUTO_POT, new PetAutoPotHandler());
                registerHandler(RecvPacketOpcode.CLIENT_INFO, new ClientInfoHandler());
                registerHandler(RecvPacketOpcode.AUTO_DISTRIBUTE_AP, new AutoDistributeAPHandler());
                registerHandler(RecvPacketOpcode.MONSTERBOOK, new MonsterBookHandler());
                registerHandler(RecvPacketOpcode.USE_EXP_ITEM, new UseExpItemHandler());
                registerHandler(RecvPacketOpcode.HYPNOTIZE_MOB_TO_MOB_HIT, new MobTurncoatHandler());
                registerHandler(RecvPacketOpcode.MONSTER_BANISH, new MonsterBanishHandler());
                registerHandler(RecvPacketOpcode.TOUCH_SNOWBALL, new TouchSnowballHandler());
                registerHandler(RecvPacketOpcode.MAKE_ITEM, new MakeItemHandler());
                registerHandler(RecvPacketOpcode.ADMIN_COMMAND, new AdminCommandHandler());
                registerHandler(RecvPacketOpcode.ADMIN_LOG, new AdminLogHandler());
                registerHandler(RecvPacketOpcode.USE_SCRIPTED_ITEM, new UseScriptedItemHandler());
                break;
            default:
                throw new RuntimeException("Unknown packet processor mode");
        }
    }
}
