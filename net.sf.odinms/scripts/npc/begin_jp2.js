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

/* 
	Peter
	Maple Road: Entrance - Mushroom Town Training Camp (3)
	Takes you out of Entrace of Mushroom Town Training Camp
*/

function start() {
    cm.sendNext("You have finished all your trainings. Good job. You seem to be ready to start with the journey right away! Good, I will let you move on to the next place.");
    cm.sendNextPrev("But remember, once you get out of here, you will enter a village full with monsters. Well them, good bye!");
    cm.warp(40000, 0);
}            