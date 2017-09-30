/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
					   Matthias Butz <matze@odinms.de>
					   Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

function act(){
	//SCRIPT(sca_lich6)
    if (rm.getMonsterCount(6090000) > 0) {
         rm.mapMessage(6, "Once the stone began to disappear as it gave light, Richie's magic powers disappeared.");
         //TODO
         //nuffMob(6090000, 157, 1)
    }
    //SCRIPT(sca_lich7)
    if (rm.getMonsterCount(6090000) > 0) {
         rm.mapMessage(6, "Once the box, which is Richie's power source, was destroyed, the invisible power that protected him disappeared.");
         //TODO
         //nuffMob(6090000, 157, 1)
    }
    //TODO
}