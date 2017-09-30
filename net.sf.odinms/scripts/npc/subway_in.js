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

/*	  The Ticket Gate(1052007)
        - Subway Ticketing Booth (103000100)
*/

importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

var status = 0;
var npcText = "";
var choice;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1){
		cm.dispose();
	}else if (mode == 0) {
		cm.sendOk("Alright, see you next time.");
		cm.dispose();
		}else{	
		
		if (mode == 1) {
			status++;
		}
		else {
			status--;
		}
		if (status == 0){
		if (cm.getLevel() < 15) {
		cm.sendOk("You must be a higher level to enter the construction sites.");
		cm.dispose();
		}else{
		npcText += "Please select a #bJumpQuest:\r\n";
		npcText += "#L0#Construction Site B1#l\r\n";
		npcText += "#L1#Construction Site B2#l\r\n";
		npcText += "#L2#Construction Site B3#l\r\n";
		cm.sendSimple(npcText);
		}
		
		}else if (status == 1){
		choice = selection;
		cm.sendYesNo("Are you sure you want to enter the construction sites?")

		}else if (status == 2) {
		if (choice == 0 && cm.haveItem(4031036)){
		cm.warp(103000900, 0);
		cm.gainItem(4031036, -1);
		}else if(choice == 1 && cm.haveItem(4031037)){
		cm.warp(103000903, 0);
		cm.gainItem(4031037, -1);
		}else if(choice == 2 && cm.haveItem(4031038)){
		cm.warp(103000906, 0);
		cm.gainItem(4031038, -1);
		}else{
		cm.sendOk("You need to buy a ticket before you can enter here!");
		}
		cm.dispose();
			
		}
	}
}
	


