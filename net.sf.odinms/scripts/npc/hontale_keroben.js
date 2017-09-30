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

//
//hontale_keroben
// Keroben, bouncer to Horntail's Cave

function start() {
    cm.sendNext("That's far enough, human! No one is allowed beyond this point. Get away from here!");
    cm.warp(240040600, "st00");
    cm.dispose();
}
/*
 if isActiveItem(2210003) then
	addText("Oh, my Brother! Don't worry about human's invasion. I'll protect you all. Come in.");
	sendNext();

	endMorph();
	setMap(240050000, "st00");
else
	if getHP() > 500 then
		setHP(getHP() - 500);
	elseif getHP() > 1 then
		setHP(1);
	end

	addText("That's far enough, human! No one is allowed beyond this point. Get away from here!");
	sendNext();

	setMap(240040600, "st00");
end
*/