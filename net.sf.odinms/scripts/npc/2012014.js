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
 Orbis Magic Spot

	4001019 - Orbis Rock Scroll
	200082100 - Orbis : Orbis Tower <1st Floor>
**/

var status = 0;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.dispose();
		return;
	}
	status++;
	if (status == 0) {
		if(cm.haveItem(4001019)) {
			cm.sendYesNo("You can use #b#t4001019#k to activate #b#p2012014##k. Will you teleport to where #b#p2012015##k is?");
		} else {
			cm.sendOk("There's a #b#p2012014##k that'll enable you to teleport to where #b#p2012015##k is, but you can't activate it without the scroll.");
			cm.dispose();
		}
	}
	if (status == 1) {
			cm.gainItem(4001019, -1);
			cm.warp(200082100);
			cm.dispose();
	}
}
