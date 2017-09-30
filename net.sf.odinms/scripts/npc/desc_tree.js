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
    cm.sendNext("Do you see the group of snowman standing over there? Go talk to one of them, and it'll take you to the famous christmas tree--it's just humongous! While there, you can decorate the tree using various kinds of ornaments. What do you think? Sounds fun, right?");
    cm.sendNextPrev("Only 6 can be in the map at one time, and you can't #btrade or open a store#k there. Also, only you can pick up the ornaments that you have dropped, so don't worry about losing your ornaments here.");
    cm.sendNextPrev("Of course, the items you drop in the map will never disappear. When you are ready, you can leave through the snowman that is inside. You will recover all the items that you dropped inside the map when you leave, so don't worry about picking up all your items. Isn't that sweet?");
    cm.sendPrev("Well then, go see #p2002001#, buy some christmas ornaments there, and then decorate the tree with those~ Oh yeah! The biggest and most beautiful ornament can't be bought from him. It's probably been... taken by a monster! Dum dum dum~");
}