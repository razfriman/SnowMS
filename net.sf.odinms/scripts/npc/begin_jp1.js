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

function start() {
    if (cm.getMapId() == 0) {
	  if (cm.sendYesNo("Welcome to the world of MapleStory. The purpose of this training camp is to help beginners. Would you like to enter this training camp? Some people start their journey without taking the training program. But I strongly recommend you take the training program first.")) {
		cm.sendNext("Ok then, I will let you enter the training camp. Please follow your instructor's lead.");
		cm.warp(1);
	  } else {
		if (cm.sendYesNo("Do you really wanted to start your journey right away?")) {
		    cm.sendNext("It seems like you want to start your journey without taking the training program. Then, I will let you move on the training ground. Be careful~");
		    cm.warp(40000);
		} else {
		    cm.sendNext("Please talk to me again when you finally made your decision.");
		}
	  }
    } else if (cm.getMapId() == 1) {
	cm.sendNext("This is the image room where your first training program begins. In this room, you will have an advance look into the job of your choice.");
	cm.sendPrev("Once you train hard enough, you will be entitled to occupy a job. You can become a Bowman in Henesys, a Magician in Ellinia, a Warrior in Perion, and a Thief in Kerning City..");

    }
}