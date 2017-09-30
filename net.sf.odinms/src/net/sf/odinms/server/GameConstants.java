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

package net.sf.odinms.server;

import net.sf.odinms.net.IntValueHolder;

/**
 *
 * @author Raz
 */
public class GameConstants {

    public static class Stats {
        public static final int PLAYER_LEVELS = 200;
        public static final int  CYGNUS_LEVELS = 120;
        public static final int  PET_LEVELS = 30;
        public static final int  MAX_MAX_HP = 30000;
        public static final int  MIN_MAX_HP = 1;
        public static final int  MAX_MAX_MP = 30000;
        public static final int  MIN_MAX_MP = 1;
        public static final int  MAX_FAME = 30000;
        public static final int  MIN_FAME = -30000;
        public static final int  AP_PER_LEVEL = 5;
        public static final int  AP_PER_CYGNUS_LEVEL = 6;
        public static final int  CYGNUS_AP_CUTOFF  = 70;
        public static final int  SP_PER_LEVEL = 3;
        public static final int  MAX_FULLNESS = 100;
        public static final int  MIN_FULLNESS = 0;
        public static final int  PET_FFED_FULLNESS = 30;
        public static final int  MAX_DAMAGE = 199999;
    }

    public static class BaseHp {
        public static final short VARIATION = 4;
        public static final short BEGINNER = 12;
        public static final short WARRIOR = 24;
        public static final short MAGICIAN = 10;
        public static final short BOWMAN = 20;
        public static final short THIEF = 20;
        public static final short PIRATE = 22;
        public static final short GM = 150;

        public static final short BEGINNER_AP = 8;
        public static final short WARRIOR_AP = 20;
        public static final short MAGICIAN_AP = 8;
        public static final short BOWMAN_AP = 16;
        public static final short THIEF_AP = 16;
        public static final short PIRATE_AP = 18;
        public static final short GM_AP = 16;
    }

    public static class BaseMp {
        public static final short VARIATION = 2;
        public static final short BEGINNER = 10;
        public static final short WARRIOR = 4;
        public static final short MAGICIAN = 6;
        public static final short BOWMAN = 14;
        public static final short THIEF = 14;
        public static final short PIRATE = 18;
        public static final short GM = 150;

        public static final short BEGINNER_AP = 6;
        public static final short WARRIOR_AP = 2;
        public static final short MAGICIAN_AP = 18;
        public static final short BOWMAN_AP = 10;
        public static final short THIEF_AP = 10;
        public static final short PIRATE_AP = 14;
        public static final short GM_AP = 10;
    }

    public static enum MonsterStatus implements IntValueHolder {

        WEAPON_ATTACK(0x1),
        WEAPON_DEFENSE(0x2),
        MAGIC_ATTACK(0x4),
        MAGIC_DEFENSE(0x8),

        ACCURACY(0x10),
        AVOID(0x20),
        SPEED(0x40),
        STUN(0x80),

        FREEZE(0x100),
        POISON(0x200),
        SEAL(0x400),//or 0x800

        WEAPON_ATTACK_UP(0x1000),
        WEAPON_DEFENSE_UP(0x2000),
        MAGIC_ATTACK_UP(0x4000),
        MAGIC_DEFENSE_UP(0x8000),

        DOOM(0x10000),
        SHADOW_WEB(0x20000),
        WEAPON_IMMUNITY(0x40000),
        MAGIC_IMMUNITY(0x80000),

        NINJA_AMBUSH(0x400000),

        VENOMOUS_WEAPON(0x1000000),
        EMPTY(0x8000000),

        HYPNOTIZE(0x10000000),
        WEAPON_DAMAGE_REFLECT(0x20000000),
        MAGIC_DAMAGE_REFLECT(0x40000000);

        int i;

        private MonsterStatus(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    public static enum PlayerStatus implements IntValueHolder {

        CURSE(0x1),
        WEAKNESS(0x2),
        DARKNESS(0x3),
        SEAL(0x8),

        POISON(0x10),
        STUN(0x20),
        SLOW(0x40),
        SEDUCE(0x80),

        ZOMBIFY(0x100),
        CRAZY_SKULL(0x200);

        int i;

        private PlayerStatus(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    public static enum MonsterSkills implements IntValueHolder {

        WEAPON_ATTACK_UP(100),
        WEAPON_ATTACK_UP_AOE(110),
        MAGIC_ATTACK_UP(101),
        MAGIC_ATTACK_UP_AOE(111),
        WEAPON_DEFENSE_UP(102),
        MAGIC_DEFENSE_UP(103),
        HEAL(114),
        SEAL(120),
        DARKNESS(121),
        WEAKNESS(122),
        STUN(123),
        CURSE(124),
        POISON(125),
        SLOW(126),
        DISPEL(127),
        SEDUCE(128),
        SEND_TO_TOWN(129),
        POISON_MIST(131),
        CRAZY_SKULL(132),
        ZOMBIFY(133),
        WEAPON_IMMUNITY(140),
        MAGIC_IMMUNITY(141),
        WEAPON_DAMAGE_REFLECT(143),
        MAGIC_DAMAGE_REFLECT(144),
        ANY_DAMAGE_REFLECT(145),
        MC_WEAPON_ATTACK_UP(150),
        MC_MAGIC_ATTACK_UP(151),
        MC_WEAPON_DEFENSE_UP(152),
        MC_MAGIC_DEFENSE_UP(153),
        MC_ACCURACY_UP(154),
        MC_AVOID_UP(155),
        MC_SPEED_UP(156),
        MC_SEAL(157),
        SUMMON(200);

        int i;

        private MonsterSkills(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    public static class EquipSlots {
 
        public static final byte Helm = 1;
        public static final byte Face = 2;
        public static final byte Eye = 3;
        public static final byte Earring = 4;
        public static final byte Top = 5;
        public static final byte Bottom = 6;
        public static final byte Shoe = 7;
        public static final byte Glove = 8;
        public static final byte Cape = 9;
        public static final byte Shield = 10;
        public static final byte Weapon = 11;
        public static final byte Ring1 = 12;
        public static final byte Ring2 = 13;
        public static final byte PetEquip1 = 14;
        public static final byte Ring3 = 15;
        public static final byte Ring4 = 16;
        public static final byte Pendant = 17;
        public static final byte Mount = 18;
        public static final byte Saddle = 19;
        public static final byte PetCollar = 20;
        public static final byte PetLabelRing1 = 21;
        public static final byte PetItemPouch1 = 22;
        public static final byte PetMesoMagnet1 = 23;
        public static final byte PetAutoHp = 24;
        public static final byte PetAutoMp = 25;
        public static final byte PetWingBoots1 = 26;
        public static final byte PetBinoculars1 = 27;
        public static final byte PetMagicScales1 = 28;
        public static final byte PetQuoteRing1 = 29;
        public static final byte PetEquip2 = 30;
        public static final byte PetLabelRing2 = 31;
        public static final byte PetQuoteRing2 = 32;
        public static final byte PetItemPouch2 = 33;
        public static final byte PetMesoMagnet2 = 34;
        public static final byte PetWingBoots2 = 35;
        public static final byte PetBinoculars2 = 36;
        public static final byte PetMagicScales2 = 37;
        public static final byte PetEquip3 = 38;
        public static final byte PetLabelRing3 = 39;
        public static final byte PetQuoteRing3 = 40;
        public static final byte PetItemPouch3 = 41;
        public static final byte PetMesoMagnet3 = 42;
        public static final byte PetWingBoots3 = 43;
        public static final byte PetBinoculars3 = 44;
        public static final byte PetMagicScales3 = 45;
        public static final byte PetItemIgnore1 = 46;
        public static final byte PetItemIgnore2 = 47;
        public static final byte PetItemIgnore3 = 48;
        public static final byte Medal = 49;
        public static final byte Belt = 50;
    }

    public static enum Items implements IntValueHolder {

        GM_HAT(1002140),
        GM_TOP(1042003),
        GM_BOTTOM(1062007),
        GM_WEAPON(1322013),

        BATTLESHIP_MOUNT(1932000),
        SHOE_SPIKES(204727),
        CAPE_COLD_PROTECTION(2041058),
        SPECIAL_TELEPORT_ROCK(2320000),
        WHITE_SCROLL(2340000),
        
        TELEPORT_ROCK(5040000),
        TELEPORT_COKE(5040001),
        VIP_TELEPORT_ROCK(5041000),
        AP_RESET(5050000),
        JOB_1_SP_RESET(5050001),
        JOB_2_SP_RESET(5050002),
        JOB_3_SP_RESET(5050003),
        JOB_4_SP_RESET(5050004),
        ITEM_NAME_TAG(5060000),
        ITEM_GUARD(5060001),
        MEGAPHONE(5070000),;
        int i;

        private Items(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    public static enum FieldLimitBits implements IntValueHolder {

        JUMP(0x1),
        MOVEMENT_SKILLS(0x2),
        SUMMONING_BAG(0x4),
        MYSTIC_DOOR(0x8),

        CHANNEL_SWITCH(0x10),
        REGULAR_EXP_LOSS(0x20),
        VIP_ROCK(0x40),
        MINIGAMES(0x80),

        UNKNOWN_1(0x100), // APQ and a couple quest maps have this
        MOUNT(0x200),
        UNKNOWN_2(0x400), // Monster carnival?
        UNKNOWN_3(0x800), // Monster carnival?

        POTION_USE(0x1000),
        UNKNOWN_4(0x2000), // No notes
        UNUSED(0x4000),
        UNKNOWN_5(0x8000),

        UNKNOWN_6(0x10000), // Ariant colosseum-related?
        DROP_DOWN(0x20000),
        UNKNOWN_7(0x40000),
        UNKNOWN_8(0x80000), // Seems to .. disable Rush if 0x2 is set
	  UNUSED_1(0x100000),
	  UNUSED_2(0x200000),
	  CHALKBOARD(0x400000),
	  UNUSED_3(0x800000);
        int i;

        private FieldLimitBits(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    public enum DueyAction implements IntValueHolder {

        SEND_ITEM(0x02),
        CLOSE_DUEY(0x07),
        RECEIVED_PACKAGE_MSG(0x1B),
        CLAIM_RECEIVED_PACKAGE(0x04),
        SUCCESSFULLY_RECEIVED(0x17),
        SUCCESSFULLY_SENT(0x18),
        ERROR_SENDING(0x12),
        OPEN_DUEY(0x08);
        int i;

        private DueyAction(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }

        public static DueyAction getById(int id) {
            for (DueyAction a : DueyAction.values()) {
                if (a.getValue() == id) {
                    return a;
                }
            }
            return null;
        }
    }

    public enum PlayerInteractionType implements IntValueHolder {

        CREATE(0),
        //1
        INVITE(2),
        DECLINE(3),
        VISIT(4),
        //5 - Enter Omok Game
        CHAT(6),
        //7
        //8
        //9
        EXIT(0xA),
        OPEN(0xB),
        //C
        //D
        SET_ITEMS(0xE),
        SET_MESO(0xF),
        CONFIRM(0x10),
        //11
        //12
        ADD_ITEM(0x13),
        BUY(0x14),
        //15
        //16
        //17
        REMOVE_ITEM(0x18), //slot(byte) bundlecount(short)

        REQUEST_TIE(0x2C),
        ANSWER_TIE(0x2D),//OnTieRequest
        GIVE_UP(0x2E),
        EXIT_AFTER_GAME(0x32),
        CANCEL_EXIT(0x33),
        READY(0x34),//OnUserReady
        NOT_READY(0x35),//OnUserCancelReady
        START(0x37),//OnUserStart
        SKIP(0x39),//On
        MOVE_OMOK(0x3A),
        SELECT_CARD(0x3E),
        UNDEFINED(-1);
        int i;

        private PlayerInteractionType(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }

        public static PlayerInteractionType getById(int id) {
            for (PlayerInteractionType act : PlayerInteractionType.values()) {
                if (act.getValue() == id) {
                    return act;
                }
            }
            return UNDEFINED;
        }
    }

    public enum StorageActionType implements IntValueHolder {

        C_TAKE_OUT(4),
        C_STORE(5),
        C_ARRANGE(6),
        C_MESO(7),
        C_CLOSE(8),
        S_TAKE_OUT(9),
        S_FULL_STORAGE(0x11),
        S_STORE_MESO(0x13),
        S_OPEN_STORAGE(0x16),
        S_STORE_ITEM(0x0D),
        UNDEFINED(-1);
        int i;

        private StorageActionType(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }

        public static StorageActionType getById(int id) {
            for (StorageActionType act : StorageActionType.values()) {
                if (act.getValue() == id) {
                    return act;
                }
            }
            return UNDEFINED;
        }
    }
}
