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

/* Spinel
	World Tour Bitch - Located everywhere
*/

importPackage(net.sf.odinms.server.maps);

var status = 0;
var cost;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if ((status <= 2 && mode == 0) || (status == 4 && mode == 1)){
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (cm.getChar().getMapId() != 800000000) {
		if (status == 0) {
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				cm.sendNext("If you're tired of the monotonous daily life, how about getting out for a change? There's nothing quite like soaking up a new culture, learning something new by the minute! It's time for you to get out and travel. We recommend a\r\n#bWorld Tour#k! Are you worried about the travel expense? No need to worry! The #bMaple Travel Agency#k offers first class travel accommodation for the low price of #b300 mesos#k.");
				cost = 300;
			} else {
				cm.sendNext("If you're tired of the monotonous daily life, how about getting out for a change? There's nothing quite like soaking up a new culture, learning something new by the minute! It's time for you to get out and travel. We, at the Maple Travel Agency recommend you going on a #bWorld Tour#k! Are you worried about the travel expense? You shouldn't be! We, the #bMaple Travel Agency#k, have carefully come up with a plan to let you travel for ONLY #b3,000 mesos!#k");
				cost = 3000;
			}
		} else if (status == 1) {
			cm.sendSimple("We currently offer this place for your traveling pleasure:\r\n#bMushroom Shrine of Japan#k. I'll be there serving you as the travel guide. Rest assured, the number of destinations will increase over time. Now, would you like to head over to the Mushroom Shrine?\r\n#L0##b Yes, take me to Mushroom Shrine (Japan)#k#l");
		} else if (status == 2) {
			cm.sendYesNo("Would you like to travel to the #bMushroom Shrine of Japan#k?\r\nIf you desire to feel the essence of Japan, there's nothing like visiting the Shrine, a Japanese cultural melting pot. Mushroom Shrine is a mythical place that serves the incomparable Mushroom God from ancient times.");
		} else if (status == 3) {
			cm.sendNext("Check out the female shaman serving the Mushroom God, and I strongly recommend trying Takoyaki, Yakisoba, and other delicious food sold in the streets of Japan. Now, let's head over to #bMushroom Shrine#k, a mythical place if there ever was one.");
		} else if (status == 4) {
			if (cm.getMeso() < cost) {
				cm.sendPrev("Please check and see if you have enough mesos to go.");
			} else {
				cm.gainMeso(-cost);
				cm.getChar().saveLocation(SavedLocationType.WORLDTOUR);
				cm.warp(800000000, 0);
				cm.dispose();
			}	
		}
	} else if (cm.getChar().getMapId() == 800000000) {
		if (status == 0) {
			cm.sendSimple ("How's the traveling? Are you enjoying it?\r\n#L0##bYes, I'm done with travelling. Can I go back to #m" + cm.getChar().getSavedLocation(SavedLocationType.WORLDTOUR) + "#?#k#l\r\n#L1##bNo, I'd like to continue exploring this place.#k#l");
		} else if (status == 1) {
			if (selection == 0) {
				cm.sendNext("Alright, I'll now take you back to where you were before the visit to Japan. If you ever feel like traveling again down the road, please let me know!");
			} else if (selection == 1) {
				cm.sendOk("OK. If you ever change your mind, please let me know.");
				cm.dispose();
			} 
		} else if (status == 2) {
			var map = cm.getChar().getSavedLocation(SavedLocationType.WORLDTOUR);
			if (map == -1) {
				map = 100000000;
			}
			cm.warp(map, 0);
			cm.dispose();
			}
		}
	}
}