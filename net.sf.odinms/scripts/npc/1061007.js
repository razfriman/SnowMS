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

/*	  Crumbling Statue(1060007)
	- Deep Forest of Patience <Step 1> (105040310)
	- Deep Forest of Patience <Step 2> (105040311)
	- Deep Forest of Patience <Step 3> (105040312)
	- Deep Forest of Patience <Step 4> (105040313)
	- Deep Forest of Patience <Step 5> (105040314)
	- Deep Forest of Patience <Step 6> (105040315)
	- Deep Forest of Patience <Step 7> (105040316)
*/

importPackage(net.sf.odinms.client);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	}else if(mode == 0){
		cm.sendOk("Alright, see you next time.");
		cm.dispose();
	}else{
		if (mode == 1) {
			status++;
		}
		else {
			status--;
		}
		if (status == 0) {
			cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Will it be okay to go back to Sleepywood?");
		} 
		else if (status == 1) {
			cm.warp(105040300, 0);
			cm.dispose();
		}
	}
}	


