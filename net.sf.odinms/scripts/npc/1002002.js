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

/* Pison
	Warp NPC to Florina Beach (110000000)
	located in Lith Harbor (104000000)
*/

importPackage(net.sf.odinms.server.maps);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (status == 0 && mode == 0) {
		cm.dispose();
		return;
	} else if (status <= 2 && mode == 0) {
		cm.sendNext("You must have some business to take care of here. You must be tired from all that traveling and hunting. Go take some rest, and if you feel like changing your mind, then come talk to me.");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		cm.sendSimple("Have you heard of the beach with a spectacular view of the ocean called #bFlorina Beach#k, located near Lith Harbor? I can take you there right now for either #b1500 mesos#k, or if you have a #bVIP Ticket to Florina Beach#k with you, in which case you'll be there for free.\r\n\r\n#L0##b I'll pay 1500 mesos.#l\r\n#L1# I have a VIP Ticket to Florina Beach.#l\r\n#L2# What is a VIP Ticket to Florina Beach#k?#l");
	} else if (status == 1) {
		if (selection == 0) {
			cm.sendYesNo("So you want to pay #b1500 mesos#k and leave for Florina Beach? Alright, then, but just be aware that you may be running into some monsters around there, too. Okay, would you like to head over to Florina Beach right now?");
		} else if (selection == 1) {
			status = 2;
			cm.sendYesNo("So you have a #bVIP Ticket to Florina Beach#k? You can always head over to Florina Beach with that. Alright then, but just be aware that you may be running into some monsters there too. Okay, would you like to head over to Florina Beach right now?");
		} else if (selection == 2) {
			status = 4;
			cm.sendNext("You must be curious about a #bVIP Ticket to Florina Beach#k. Haha, that's very understandable. A VIP Ticket to Florina Beach is an item where as long as you have in possession, you may make your way to Florina Beach for free. It's such a rare item that even we had to buy those, but unfortunately I lost mine a few weeks ago during my precious summer break.");
		}
	} else if (status == 2) {
		if (cm.getMeso() < 1500) {
			cm.sendNext("I think you're lacking mesos. There are many ways to gather up some money, you know, like... selling your armor... defeating monsters... doing quests... you know what I'm talking about.");
			cm.dispose();
		} else {
			cm.gainMeso(-1500);
			cm.getChar().saveLocation(SavedLocationType.FLORINA);
			cm.warp(110000000, 0);
			cm.dispose();
		}
	} else if (status == 3) {
		if (cm.haveItem(4031134)) {
			cm.getChar().saveLocation(SavedLocationType.FLORINA);
			cm.warp(110000000, 0);
			cm.dispose();
		} else {
			cm.sendNext("Hmmm, so where exactly is your #bVIP Ticket to Florina\r\nBeach#k? Are you sure you have one? Please double-check.");
			cm.dispose();
		}
	} else if (status == 4) {
		cm.sendNext("You must be curious about a #bVIP Ticket to Florina Beach#k. Haha, that's very understandable. A VIP Ticket to Florina Beach is an item where as long as you have in possession, you may make your way to Florina Beach for free. It's such a rare item that even we had to buy those, but unfortunately I lost mine a few weeks ago during my precious summer break.");
	} else if (status == 5) {
		cm.sendNextPrev("I came back without it, and it just feels awful not having it. Hopefully someone picked it up and put it somewhere safe. Anyway, this is my story and who knows, you may be able to pick it up and put it to good use. If you have any questions, feel free to ask.");
	} else if (status == 6) {
		cm.dispose();
		}
	}
}