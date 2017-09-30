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

/* Arwen
	Victoria Road: Ellinia (101000000)
	
	Refining NPC: 
	* Moon/Star Rocks
	* Special: Black Feather - Flaming Feather + Moon Rock + Black Crystal
	* Only available to Lv. 40+
*/

importPackage(net.sf.odinms.client);

var status = 0;
var selectedItem = -1;
var item;
var mats;
var matQty;
var cost;
var qty = 1;
var equip = false;

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
		if (cm.getLevel() >= 40) {
			var selStr = "I can... I usually don't provide these items to humans, but... I think you're strong enough to make good use of them..#b"
			var options = new Array("Refine a Moon Rock","Refine a Star Rock","Create a Black Feather");
			for (var i = 0; i < options.length; i++){
				selStr += "\r\n#L" + i + "# " + options[i] + "#l";
			}
				
			cm.sendSimple(selStr);
		}
		else
		{
			cm.sendOk("No, I don't know what you're talking about.");
			cm.dispose();
		}
	}
	else if (status == 1 && mode == 1) {
		selectedItem = selection;
		var itemIDs = new Array(4011007,4021009,4031042);
		var matIDs = new Array(new Array(4011000,4011001,4011002,4011003,4011004,4011005,4011006), new Array(4021000,4021001,4021002,4021003,4021004,4021005,4021006,4021007,4021008),
			new Array(4001006,4011007,4021008));
		var matQtySet = new Array(new Array(1,1,1,1,1,1,1),new Array(1,1,1,1,1,1,1,1,1),new Array(1,1,1));
		var costSet = new Array(10000,15000,30000);
		
		item = itemIDs[selection];
		mats = matIDs[selection];
		matQty = matQtySet[selection];
		cost = costSet[selection];
		
		if (selection == 2) {
			equip = true;
			cm.sendNext("...A #t" + item + "#? I haven't made one of those in a while... give me a second to refresh my memory...");
		}
		else {
			equip = false;
			cm.sendGetNumber("I'm familiar with #t" + item + "#, how many of those did you want me to make?",1,1,100);
		}
	}
	else if (status == 2 && mode == 1) {
		qty = selection;
		var prompt = "You want me to make "
		
		if (equip) {
			qty = 1;
		}
			
		if (qty == 1) {
			prompt += "a #t" + item + "#?"
		}
		else {
			prompt += qty + " #t" + item + "#s?";
		}
		 
		 prompt += " For that, I'll need certain items, and some money from you, but that'll be okay, right? Oh, and make sure you have enough room in your inventory, or I might not be able to give you the item.#b";
		
		if (mats instanceof Array){
			for(var i = 0; i < mats.length; i++){
				prompt += "\r\n#i"+mats[i]+"# " + matQty[i] * qty + " #t" + mats[i] + "#";
			}
		}
		else {
			prompt += "\r\n#i"+mats+"# " + matQty * qty + " #t" + mats + "#";
		}
		
		if (cost > 0)
			prompt += "\r\n#i4031138# " + cost * qty + " meso";
		
		cm.sendYesNo(prompt);
	}
	else if (status == 3 && mode == 1) {
		var complete = true;
		
		if (cm.getMeso() < cost * qty)
			{
				cm.sendOk("I'm sorry, but I cannot do this for free.")
			}
			else
			{
				if (mats instanceof Array) {
					for(var i = 0; complete && i < mats.length; i++)
					{
						if (matQty[i] * qty == 1){
							if (!cm.haveItem(mats[i]))
							{
								complete = false;
							}
						}
						else {
							var count = 0;
							var iter = cm.getChar().getInventory(MapleInventoryType.ETC).listById(mats[i]).iterator();
							while (iter.hasNext()) {
								count += iter.next().getQuantity();
							}
							if (count < matQty[i] * qty)
								complete = false;
						}					
					}
				}
				else {
					var count = 0;
					var iter = cm.getChar().getInventory(MapleInventoryType.ETC).listById(mats).iterator();
					while (iter.hasNext()) {
						count += iter.next().getQuantity();
					}
					if (count < matQty  * qty)
						complete = false;
				}
			}
			
			if (!complete) 
				cm.sendOk("I'm sorry, but I need those items to make the item you want.");
			else {
				if (mats instanceof Array) {
					for (var i = 0; i < mats.length; i++){
						cm.gainItem(mats[i], -matQty[i] * qty);
					}
				}
				else
					cm.gainItem(mats, -matQty * qty);
					
				if (cost > 0)
					cm.gainMeso(-cost * qty);
				
				cm.gainItem(item, qty);
				cm.sendOk("Use it wisely.");
			}
		cm.dispose();
	}
}