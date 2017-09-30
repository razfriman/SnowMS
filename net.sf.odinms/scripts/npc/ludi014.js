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

/**
 First Eos Rock

	4001020 - Eos Rock Scroll
	221022900 - Ludibrium : Eos Tower 71st Floor
**/

function start() {
    if (cm.sendYesNo("Do you wish to go to the 2nd Eos Rock?")) {
        if(cm.haveItem(4001020)) {
            cm.gainItem(4001020, -1);
            cm.warp(221022900, 0);
            cm.dispose();
        } else {
            cm.sendOk("You need at least one Eos Rock Scroll.");
            cm.dispose();
        }
    }
}