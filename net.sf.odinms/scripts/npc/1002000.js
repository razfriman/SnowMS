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

var status = 0;
var maps = Array(102000000, 101000000, 100000000, 103000000);
var cost = Array(1200, 1200, 800, 1000);
var costBeginner = Array(120, 120, 80, 100);
var selectedMap = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (status >= 24 && mode == 0) {
		cm.sendNext("There's a lot to see in this town, too. Let me know if you want to go somewhere else.");
		cm.dispose();
		return;
	} else if ((status <= 3 && mode == 0) || (status == 23 && mode == 0) || (status == 6 && mode == 1) || (status == 9 && mode == 1) || (status == 12 && mode == 1) || (status == 15 && mode == 1) || (status == 18 && mode == 1)) {
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		cm.sendNext("Do you wanna head over to some other town? With a little money involved, I can make it happen. It's a tad expensive, but I run a special 90% discount for beginners.");
	} else if (status == 1) {
		cm.sendSimple("It's understandable that you may be confused about this place if this is your first time around. If you got any questions about this place, fire away.\r\n#L0##bWhat kind of towns are here in Victoria Island?#l\r\n#L1#Please take me somewhere else.#k#l");
	} else if (status == 2) {
		if (selection == 0) {
			cm.sendSimple("There are 6 big towns here in Victoria Island. Which of those do you want to know more of?\r\n#L0##bLith Harbor#l\r\n#L1#Perion#l\r\n#L2#Ellinia#l\r\n#L3#Henesys#l\r\n#L4#Kerning City#l\r\n#L5#Sleepywood#k#l");
		} else if (selection == 1) {
			status = 23;
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				var selStr = "There's a special 90% discount for all beginners. Alright, where would you want to go?#b";
				for (var i = 0; i < maps.length; i++) {
					selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + costBeginner[i] + " mesos)#l";
				}
			} else {
				var selStr = "Oh you aren't a beginner, huh? Then I'm afraid I may have to charge you full price. Where would you like to go?#b";
				for (var i = 0; i < maps.length; i++) {
				selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + cost[i] + " mesos)#l";
				}
			}
		cm.sendSimple(selStr);
		}
	} else if (status == 3) {
		if (selection == 0) {
			status = 4;
			cm.sendNext("The town you are at is Lith Harbor! Alright I'll explain to you more about #bLith Harbor#k. It's the place you landed on Victoria Island by riding The Victoria. That's Lith Harbor. A lot of beginners who just got here from Maple Island start their journey here.");
		} else if (selection == 1) {
			status = 7;
			cm.sendNext("Alright I'll explain to you more about #bPerion#k. It's a warrior-town located at the northern-most part of Victoria Island, surrounded by rocky mountains. With an unfriendly atmosphere, only the strong survives there.");
		} else if (selection == 2) {
			status = 10;
			cm.sendNext("Alright I'll explain to you more about #bEllinia#k. It's a magician-town located at the far east of Victoria Island, and covered in tall, mystic trees. You'll find some fairies there, too. They don't like humans in general so it'll be best for you to be on their good side and stay quiet.");
		} else if (selection == 3) {
			status = 13;
			cm.sendNext("Alright I'll explain to you more about #bHenesys#k. It's a bowman-town located at the southernmost part of the island, made on a flatland in the midst of a deep forest and prairies. The weather's just right, and everything is plentiful around that town, perfect for living. Go check it out.");
		} else if (selection == 4) {
			status = 16;
			cm.sendNext("Alright I'll explain to you more about #bKerning City#k. It's a thief-town located at the northwest part of Victoria Island, and there are buildings up there that have just this strange feeling around them. It's mostly covered in black clouds, but if you can go up to a really high place, you'll be able to see a very beautiful sunset there.");
		} else if (selection == 5) {
			status = 19;
			cm.sendNext("Alright I'll explain to you more about #bSleepywood#k. It's a forest town located at the southeast side of Victoria Island. It's pretty much in between Henesys and the ant-tunnel dungeon. There's a hotel there, so you can rest up after a long day at the dungeon ... it's a quiet town in general.");
		}
	} else if (status == 4) {
		cm.sendNext("The town you are at is Lith Harbor! Alright I'll explain to you more about #bLith Harbor#k. It's the place you landed on Victoria Island by riding The Victoria. That's Lith Harbor. A lot of beginners who just got here from Maple Island start their journey here.");
	} else if (status == 5) {
		cm.sendNextPrev("It's a quiet town with the wide body of water on the back of it, thanks to the fact that the harbot is located at the west end of the island. Most of the people here are, or used to be fisherman, so they may look intimidating, but if you strike up a conversation with them, they'll be friendly to you.");
	} else if (status == 6) {
		cm.sendNextPrev("Around town lies a beautiful prairie. Most of the monsters there are small and gentle, perfect for beginners. If you haven't chosen your job yet, this is a good place to boost up your level.");
	} else if (status == 7) {
		cm.sendNext("Alright I'll explain to you more about #bPerion#k. It's a warrior-town located at the northern-most part of Victoria Island, surrounded by rocky mountains. With an unfriendly atmosphere, only the strong survives there.");
	} else if (status == 8) {
		cm.sendNextPrev("Around the highland you'll find a really skinny tree, a wild hog running around the place, and monkeys that live all over the island. There's also a deep valley, and when you go deep into it, you'll find a humongous dragon with the power to match his size. Better go in there very carefully, or don't go at all.");
	} else if (status == 9) {
		cm.sendNextPrev("If you want to be a #bWarrior#k then find #rDances with Balrog#k, the chief of Perion. If you're level 10 or higher, along with a good STR level, he may make you a warrior after all. If not, better keep training yourself until you reach that level.");
	} else if (status == 10) {
		cm.sendNext("Alright I'll explain to you more about #bEllinia#k. It's a magician-town located at the far east of Victoria Island, and covered in tall, mystic trees. You'll find some fairies there, too. They don't like humans in general so it'll be best for you to be on their good side and stay quiet.");
	} else if (status == 11) {
		cm.sendNextPrev("Near the forest you'll find green slimes, walking mushrooms, monkeys and zombie monkeys all residing there. Walk deeper into the forest and you'll find witches with the flying broomstick navigating the skies. A word of warning: Unless you are really strong, I recommend you don't go near them.");
	} else if (status == 12) {
		cm.sendNextPrev("If you want to be a #bMagician#k, search for #rGrendel the Really Old#k, the head wizard of Ellinia. He may make you a wizard if you're at or above level 8 with a decent amount of INT. If that's not the case, you may have to hunt more and train yourself to get there.");
	} else if (status == 13) {
		cm.sendNext("Alright I'll explain to you more about #bHenesys#k. It's a bowman-town located at the southernmost part of the island, made on a flatland in the midst of a deep forest and prairies. The weather's just right, and everything is plentiful around that town, perfect for living. Go check it out.");
	} else if (status == 14) {
		cm.sendNextPrev("Around the prairie you'll find weak monsters such as snails, mushrooms, and pigs. According to what I hear, though, in the deepest part of the Pig Park, which is connected to the town somewhere, you'll find a humongous, powerful mushroom called Mushmom every now and then.");
	} else if (status == 15) {
		cm.sendNextPrev("If you want to be a #bBowman#k, you need to go see #rAthena Pierce#k at Henesys. With a level at or above 10 and a decent amount of DEX, she may make you be one afterall. If not, go train yourself, make yourself stronger, then try again.");
	} else if (status == 16) {
		cm.sendNext("Alright I'll explain to you more about #bKerning City#k. It's a thief-town located at the northwest part of Victoria Island, and there are buildings up there that have just this strange feeling around them. It's mostly covered in black clouds, but if you can go up to a really high place, you'll be able to see a very beautiful sunset there.");
	} else if (status == 17) {
		cm.sendNextPrev("From Kerning City, you can go into several dungeons. You can go to a swamp where alligators and snakes are abound, or hit the subway full of ghosts and bats. At the deepest part of the underground, you'll find Lace, who is just as big and dangerous as a dragon.");
	} else if (status == 18) {
		cm.sendNextPrev("If you want to be a #bThief#k, seek #rDark Lord#k, the heart of darkness of Kerning City. He may well make you a thief if you're at or above level 10 with a good amount of DEX. If not, go hunt and train yourself to reach there.");
	} else if (status == 19) {
		cm.sendNext("Alright I'll explain to you more about #bSleepywood#k. It's a forest town located at the southeast side of Victoria Island. It's pretty much in between Henesys and the ant-tunnel dungeon. There's a hotel there, so you can rest up after a long day at the dungeon ... it's a quiet town in general.");
	} else if (status == 20) {
		cm.sendNextPrev("In front of the hotel there's an old buddhist monk by the name of #rChrishrama#k. Nobody knows a thing about that monk. Apparently he collects materials from the travelers and create something, but I am not too sure about the details. If you have any business going around that area, please check that out for me.");
	} else if (status == 21) {
		cm.sendNextPrev("From Sleepywood, head east and you'll find the ant tunnel connected to the deepest part of the Victoria Island. Lots of nasty, powerful monsters abound so if you walk in thinking it's a walk in the park, you'll be coming out as a corpse. You need to fully prepare yourself for a rough ride before going in.");
	} else if (status == 22) {
		cm.sendNextPrev("And this is what I hear ... apparently, at Sleepywood there's a secret entrance leading you to an unknown place. Apparently, once you move in deep, you'll find a stack of black rocks that actually move around. I want to see that for myself in the near future ...");
	} else if (status == 23) {
		cm.dispose();
	} else if (status == 24) {
		if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
			cm.sendYesNo("I guess you don't need to be here. Do you really want to move to #b#m" + maps[selection] + "##k? Well it'll cost you #b" + costBeginner[selection] + " mesos#k. What do you think?");
			selectedMap = selection;
		} else {
			cm.sendYesNo("I guess you don't need to be here. Do you really want to move to #b#m" + maps[selection] + "##k? Well it'll cost you #b" + cost[selection] + " mesos#k. What do you think?");
			selectedMap = selection;
		}
	} else if (status == 25) {
		if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
			if (cm.getMeso() < costBeginner[selectedMap]) {
				cm.sendNext("You don't have enough mesos. With your abilities, you should have more than that!");
				cm.dispose();
			} else {
				cm.gainMeso(-costBeginner[selectedMap]);
				cm.warp(maps[selectedMap], 0);
				cm.dispose();
			}
		} else {
			if (cm.getMeso() < cost[selectedMap]) {
				cm.sendNext("You don't have enough mesos. With your abilities, you should have more than that!");
				cm.dispose();
			} else {
				cm.gainMeso(-cost[selectedMap]);
				cm.warp(maps[selectedMap], 0);
				cm.dispose();
				}
			}
		}
	}
}