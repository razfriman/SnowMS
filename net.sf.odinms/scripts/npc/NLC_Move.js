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

/* NLC ticket gate
	Warp NPC to Subway Ticketing Booth (103000100)
	located in NLC Subway Station (600010001)
*/

function start() {
    cm.sendNext("Hi, I'm a ticket gate.");
    cm.sendNextPrev("I can take you back to Kerning City for just a small fee.")
    if (cm.getMeso() < 5000) {
        cm.sendOk("You do not have enough mesos.")
        cm.dispose();
    } else {
        if (cm.sendYesNo("The ride to Kerning City of Victoria Island will cost you #b5000 mesos#k. Are you sure you want to return to #bKerning City#k?")) {
            cm.gainMeso(-5000);
            cm.warp(103000100, 0);
            cm.dispose();
        }
    }
}