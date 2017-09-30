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
 NLC Taxi (Takes you to Haunted House)
**/

function start() {
    if (cm.getChar().getMapId() == 600000000) {
        if (cm.sendYesNo("Hello, I drive the NLC Taxi.\r\nDo you wish to visit Phantom Forest, wherein lies the Prendergast Mansion?")) {
            cm.sendNext("Alright, see you next time. Take care.");
            cm.warp(682000000, "st00");
        }
    } else {
        if (cm.sendYesNo("I drive the NLC Taxi.\r\nDo you wish to go to back to New Leaf City?")) {
            cm.sendNext("Alright, see you next time. Take care.");
            cm.warp(600000000, "bb14");
        }
    }
}