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

/* Alcaster
	El Nath: El Nath Market (211000100)
	
	Shop NPC: but must complete quest
	* 100 Summon Rocks - 500k
	* 100 Magic Rocks - 500k
	* 100 All-Cure Potions - 40k
	* 100 Holy Water - 30k
*/

importPackage(net.sf.odinms.client);

var status = 0;
var selectedItem = -1;
var item;
var cost;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else
		cm.dispose();
	if (status == 0 && mode == 1) {
		if (cm.getQuestStatus(3035).equals(MapleQuestStatus.Status.COMPLETED)) {
			var selStr = "Thank you for helping me find and destory the Book of the Ancients. As thanks for your help, I would like to put my alchemy skills to use. However, my special items are not free."
				
			cm.sendYesNo(selStr);
		}
		else
		{
			cm.sendOk("I was once a legendary alchemist, but those days are behind me.");
			cm.dispose();
		}
	}
	else if (status == 1 && mode == 1){
		var selStr = "Very well then, what item would you like? Just tell me, and I can get it to you straightaway!#b"
		var options = new Array("Holy Water #k(300 meso)#b","All-Cure Potion #k(400 meso)#b","The Magic Rock #k(5000 meso)#b","The Summoning Rock #k(5000 meso)#b");
		for (var i = 0; i < options.length; i++){
			selStr += "\r\n#L" + i + "# " + options[i] + "#l";
		}
		cm.sendSimple(selStr);
	}
	else if (status == 2 && mode == 1) {
		var itemSet = new Array(2050003,2050004,4006000,4006001);
		var costSet = new Array(300,400,5000,5000);
		var prompt;
		item = itemSet[selection];
		cost = costSet[selection];
		
		prompt = "So, you want me to make #t" + item + "# for you? In that case, it'll come to " + cost + " meso each. How many do you want?"
		cm.sendGetNumber(prompt, 1, 1, 100);
	}
	else if (status == 3 && mode == 1) {
		
		if (cm.getMeso() < cost * selection)
		{
			cm.sendOk("I'm sorry, but you do not have enough meso.")
		}
		else {	
			cm.gainMeso(-cost * selection);
			cm.gainItem(item, selection);
			cm.sendOk("I have faith that you will put these to great use.");
		}
		cm.dispose();
	}
}