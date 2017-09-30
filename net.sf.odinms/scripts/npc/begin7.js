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

/* Shanks
	Brings you to Victoria Island(60000)
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (status >= 0 && mode == 0) {
		cm.sendOk("Hmm... I guess you still have things to do here?");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		cm.sendYesNo("Take this ship and you'll head off to a bigger continent. For #e150 mesos#n, I'll take you to #bVictoria Island#k. The thing is, once you leave this place, you can't ever come back. What do you think? Do you want to go to Victoria Island?");
	} else if (status == 1) {
		if (cm.haveItem(4031801)) {
			cm.sendNext("Okay, now give me 150 mesos... Hey, what's that? Is that the recommendation letter from Lucas, the chief of Amherst? Hey, you should have told me you had this. I, Shanks, recognize greatness when I see one, and since you have been recommended by Lucas, I see that you have a great, great potential as an adventurer. No way would I charge you for this trip!");
		} else {
			cm.sendNext("Bored of this place? Here... Give me #e150 mesos#n first...");
		}
	} else if (status == 2) {
		if (cm.haveItem(4031801)) {
			cm.sendNextPrev("Since you have the recommendation letter, I won't charge you for this. Alright, buckle up, because we're going to head to Victoria Island right now, and it might get a bit turbulent!!");
		} else {
			if (cm.getLevel() >= 7) {
				if (cm.getMeso() < 150) {
					cm.sendOk("What? You're telling me you wanted to go without any money? You're one weirdo...");
					cm.dispose();
				} else {
					cm.sendNext("Awesome! #e150#n mesos accepted! Alright, off to Victoria Island!");
				}
			} else {
				cm.sendOk("Let's see... I don't think you are strong enough. You'll have to be at least Level 7 to go to Victoria Island.");
				cm.dispose();
			}
		}
	} else if (status == 3) {
		if (cm.haveItem(4031801)) {
			cm.gainItem(4031801, -1);
			cm.warp(104000000);
			cm.dispose();
		} else {
			cm.gainMeso(-150);
			cm.warp(104000000);
			cm.dispose();
			}
		}
	}
}