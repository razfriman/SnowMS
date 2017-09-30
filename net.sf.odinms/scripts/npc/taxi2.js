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

/* Regular Cab
	Warp NPC
	- Henesys edition (100000000)
*/

var maps = Array(104000000, 102000000, 101000000, 103000000);
var cost = Array(800, 1000, 1000, 1200);
var costBeginner = Array(80, 100, 100, 120);
var selectedMap = -1;

importPackage(net.sf.odinms.client);

function start() {
    var isBeginner = cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER);
	cm.sendNext("Hello, I drive the #p" + cm.getNpc() + "#. If you want to go from town to town safely and fast, then ride our cab. We'll gladly take you to your destination with an affordable price.");
	cm.sendNextPrev("I can take you to various locations for just a small fee. Beginners will get a 90% discount on normal prices.")
	cm.addText("Select your destination.#b");
	if (isBeginner) {
        for (var i = 0; i < maps.length; i++) {
            cm.addText("\r\n#L" + i + "##m" + maps[i] + "# (" + costBeginner[i] + " meso)#l");
		}
	} else {
		for (var i = 0; i < maps.length; i++) {
            cm.addText("\r\n#L" + i + "##m" + maps[i] + "# (" + cost[i] + " meso)#l");
		}
	}
	var selection = cm.sendSimple();
	if (isBeginner) {
		if (cm.getMeso() < costBeginner[selection]) {
			cm.sendOk("You do not have enough mesos.")
			cm.dispose();
		} else {
            if (cm.sendYesNo("So you have nothing left to do here? Do you want to go to #m" + maps[selection] + "#?") == 0) {
                cm.sendOk("Alright, see you next time.");
                cm.dispose();
            }
			selectedMap = selection;
		}
	} else {
		if (cm.getMeso() < cost[selection]) {
			cm.sendOk("You do not have enough mesos.")
			cm.dispose();
		} else {
			if (cm.sendYesNo("So you have nothing left to do here? Do you want to go to #m" + maps[selection] + "#?") == 0) {
                cm.sendOk("Alright, see you next time.");
                cm.dispose();
            }
			selectedMap = selection;
		}
	}
	if (isBeginner) {
        cm.gainMeso(-costBeginner[selectedMap]);
	} else {
        cm.gainMeso(-cost[selectedMap]);
	}
	cm.warp(maps[selectedMap], 0);
	cm.dispose();
	}