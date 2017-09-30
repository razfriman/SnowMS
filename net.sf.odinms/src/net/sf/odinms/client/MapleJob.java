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

import net.sf.odinms.server.GameConstants;

public enum MapleJob {

    BEGINNER(0),

    WARRIOR(100),
    FIGHTER(110),
    CRUSADER(111),
    HERO(112),
    PAGE(120),
    WHITEKNIGHT(121),
    PALADIN(122),
    SPEARMAN(130),
    DRAGONKNIGHT(131),
    DARKKNIGHT(132),

    MAGICIAN(200),
    FP_WIZARD(210),
    FP_MAGE(211),
    FP_ARCHMAGE(212),
    IL_WIZARD(220),
    IL_MAGE(221),
    IL_ARCHMAGE(222),
    CLERIC(230),
    PRIEST(231),
    BISHOP(232),

    BOWMAN(300),
    HUNTER(310),
    RANGER(311),
    BOWMASTER(312),
    CROSSBOWMAN(320),
    SNIPER(321),
    CROSSBOWMASTER(322),

    THIEF(400),
    ASSASSIN(410),
    HERMIT(411),
    NIGHTLORD(412),
    BANDIT(420),
    CHIEFBANDIT(421),
    SHADOWER(422),

    PIRATE(500),
    INFIGHTER(510),
    MARAUDER(511),
    BUCCANEER(512),
    GUNSLINGER(520),
    OUTLAW(521),
    CORSAIR(522),

    PLAYER_WATCHER(800),

    GM(900),
    SUPERGM(910),

    NOBLESSE(1000),

    DAWNWARRIOR_1(1100),
    DAWNWARRIOR_2(1110),
    DAWNWARRIOR_3(1111),
    DAWNWARRIOR_4(1112),

    BLAZEWIZARD_1(1200),
    BLAZEWIZARD_2(1210),
    BLAZEWIZARD_3(1211),
    BLAZEWIZARD_4(1212),

    WINDARCHER_1(1300),
    WINDARCHER_2(1310),
    WINDARCHER_3(1311),
    WINDARCHER_4(1312),

    NIGHTWALKER_1(1400),
    NIGHTWALKER_2(1410),
    NIGHTWALKER_3(1411),
    NIGHTWALKER_4(1412),

    THUNDERBREAKER_1(1500),
    THUNDERBREAKER_2(1510),
    THUNDERBREAKER_3(1511),
    THUNDERBREAKER_4(1512),

    LEGEND(2000),

    ARAN_1(2100),
    ARAN_2(2110),
    ARAN_3(2111),
    ARAN_4(2112),

    EVAN_0(2001),
    
    EVAN_1(2200),
    EVAN_2(2210),
    EVAN_3(2211),
    EVAN_4(2212),
    EVAN_5(2213),
    EVAN_6(2214),
    EVAN_7(2215),
    EVAN_8(2216),
    EVAN_9(2217),
    EVAN_10(2218),

    UNK(9999);
    final int jobid;

    private MapleJob(int id) {
        jobid = id;
    }

    public int getId() {
        return jobid;
    }

    public static MapleJob getById(int id) {
        for (MapleJob l : MapleJob.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public static MapleJob getBy5ByteEncoding(int encoded) {
        switch (encoded) {
            case 2:
                return WARRIOR;
            case 4:
                return MAGICIAN;
            case 8:
                return BOWMAN;
            case 16:
                return THIEF;
            case 32:
                return PIRATE;
            default:
                return BEGINNER;
        }
    }

    public boolean isA(MapleJob basejob) {
        return getId() >= basejob.getId() && getId() / 100 == basejob.getId() / 100;
    }

    public MapleJob getBaseJob() {
        if (getId() == 0) {
            return MapleJob.BEGINNER;
        }
        switch (getId() / 100) {
            case 1:
                return WARRIOR;
            case 2:
                return MAGICIAN;
            case 3:
                return BOWMAN;
            case 4:
                return THIEF;
            case 5:
                return PIRATE;
            case 8:
                return PLAYER_WATCHER;
            case 9:
                return GM;
            case 10:
                return NOBLESSE;
            case 11:
                return LEGEND;
            case 12:
                return EVAN_0;
            default:
                return null;
        }
    }

    public boolean isRegularJob() {
        return (this == BEGINNER || (getId() >= 100 && getId() <= 910));
    }

    public boolean isCygnus() {
        return getBaseJob() == NOBLESSE;
    }

    public boolean isEvan() {
        return getBaseJob() == EVAN_0;
    }

    public boolean isNonBitJob() {
        return isRegularJob() || isCygnus();
    }

    public boolean isBeginnerJob() {
        switch(this) {
            case BEGINNER:
            case NOBLESSE:
            case LEGEND:
            case EVAN_0:
                return true;
        }
        return false;
    }

    public int getJobTrack(boolean flattenCygnus) {
        return (flattenCygnus && isCygnus() ? ((getId() / 100) % 10) : (getId() / 100));
    }

    public int getMaxLevel() {
        return isCygnus() ? GameConstants.Stats.CYGNUS_LEVELS : GameConstants.Stats.PLAYER_LEVELS;
    }
}
