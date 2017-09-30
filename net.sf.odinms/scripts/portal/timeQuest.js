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

function enter(pi) {
    var map = pi.getMapId();
    var toMapIfDone = 0;
    var toMap = 0;
    var quest = 0;

    if (map == 270010100 ) {
        quest = 3501;
        toMapIfDone = 270010110;
        toMap = 270010000;
    } else if ( map == 270010200 ) {
        quest = 3502;
        toMapIfDone = 270010210;
        toMap = 270010110;
    } else if ( map == 270010300 ) {
        quest = 3503;
        toMapIfDone = 270010310;
        toMap = 270010210;
    } else if ( map == 270010400 ) {
        quest = 3504;
        toMapIfDone = 270010410;
        toMap = 270010310;
    } else if ( map == 270010500 ) {
        quest = 3507;
        toMapIfDone = 270020000;
        toMap = 270010410;

    // Regrets/blue area
    } else if ( map == 270020100 ) {
        quest = 3508;
        toMapIfDone = 270020110;
        toMap = 270020000;
    } else if ( map == 270020200 ) {
        quest = 3509;
        toMapIfDone = 270020210;
        toMap = 270020110;
    } else if ( map == 270020300 ) {
        quest = 3510;
        toMapIfDone = 270020310;
        toMap = 270020210;
    } else if ( map == 270020400 ) {
        quest = 3511;
        toMapIfDone = 270020410;
        toMap = 270020310;
    } else if ( map == 270020500 ) {
        quest = 3514;
        toMapIfDone = 270030000;
        toMap = 270020410;

    // Oblivion/red area
    } else if ( map == 270030100 ) {
        quest = 3515;
        toMapIfDone = 270030110;
        toMap = 270030100;
    } else if ( map == 270030200 ) {
        quest = 3516;
        toMapIfDone = 270030210;
        toMap = 270030210;
    } else if ( map == 270030300 ) {
        quest = 3517;
        toMapIfDone = 270030310;
        toMap = 270030310;
    } else if ( map == 270030400 ) {
        quest = 3518;
        toMapIfDone = 270030410;
        toMap = 270030410;
    } else if ( map == 270030500 ) {
        quest = 3521;
        toMapIfDone = 270040000;
        toMap = 270030410;

    // Deep palace
    } else if ( map == 270040000 ) {
        quest = 3522;
        toMapIfDone = 270040100;
        toMap = 270040000;
    }

    if (isQuestCompleted(quest)) {
        pi.playPortalSE();
        pi.warp(toMapIfDone, "out00");
    } else {
        pi.playerMessage("Those who have not received permission cannot walk against the flow of the temple and will return to the previous place.");
        pi.playPortalSE();
        pi.warp(toMap);
    }
    return true;
	
}
