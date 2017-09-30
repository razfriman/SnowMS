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

/* Konpei (9120015)
   * Bain Armory Version
*/

function start() {
    if (cm.haveItem(4000138)) {
        if (cm.sendYesNo("You've taken out the Female Boss, good job! And I see you've got her comb as a trophy, too. Excellent. So, are you ready to take on The Boss? If you're not 100% ready, I highly suggest you prepare. He's not one to be taken lightly!")) {
            cm.warp(4000138, 0);
        }
        cm.dispose();
     } else {
        cm.sendOk("So you've made it here. Not bad. You'll be taking on the boss now! I'm concerned as to whether you're able to take on the mighty boss with your abilities or not... don't get me wrong, our boss couldn't handle her either. If you, by any chance, take down the boss and bring back her comb with you, then I'll take you to the next stage.");
        cm.dispose();
     }
}