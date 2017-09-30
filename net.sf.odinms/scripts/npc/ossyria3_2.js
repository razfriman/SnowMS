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
 El Nath Magic Spot

	4001019 - Orbis Rock Scroll
	200080200 - Orbis : Orbis Tower <20th Floor>
**/

function start() {

    if(cm.haveItem(4001019)) {
        if (cm.sendYesNo("You can use #b#t4001019#k to activate #b#p2012014##k. Will you teleport to where #b#p2012015##k is?")) {
            cm.gainItem(4001019, -1);
            cm.warp(200080200);
            cm.dispose();
        }
    } else {
        cm.sendOk("There's a #b#p2012015##k that'll enable you to teleport to where #b#p2012014##k is, but you can't activate it without the scroll.");
        cm.dispose();
    }
}

