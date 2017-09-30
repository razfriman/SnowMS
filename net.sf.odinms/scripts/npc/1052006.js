/*
	Jake (1052006)
	- Subway Ticketing Booth (103000100)
	Subway Worker
*/
importPackage(net.sf.odinms.client);

var status = 0;
var npcText = "";
var itemid = Array(4031036, 4031037, 4031038);
var name = Array("Construction Site B1", "Construction Site B2", "Construction Site B3");
var cost = Array(50000, 50000, 50000);


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();//ExitChat
	}else if (mode == 0){
		cm.sendOk("Alright, see you next time.");
		cm.dispose();//No
	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		if (cm.getLevel() < 15) {
		npcText += "You must be a higher level to enter the construction sites.";
		cm.sendOk(npcText);
		cm.dispose();
		npcText = "";
		}else{
		npcText += "You must purchase a ticket to enter. Once you have made the purchase, you may enter via #bThe Ticket Gate#k on the right. Which ticket would you like to buy?";
		npcText += "\r\n#L0##b" + name[0] + " (" + cost[0] + " meso)#l";
		npcText += "\r\n#L1##b" + name[1] + " (" + cost[1] + " meso)#l";
		npcText += "\r\n#L2##b" + name[2] + " (" + cost[2] + " meso)#l";

		cm.sendSimple(npcText);
		npcText = "";
		}
		}else if (status == 1) {
		if (cm.getMeso() < cost[selection]) {
		npcText += "You do not have enough mesos to buy a ticket!";
		cm.sendOk(npcText);
		npcText = "";
		}else{
		npcText += "You can now insert your ticket into #bThe Ticket Gate#k on the right.";
		cm.sendOk(npcText);
		cm.gainItem(itemid[selection], 1);
		cm.gainMeso(-cost[selection]);
		npcText = "";
		}
		cm.dispose();
	 	

		}else if (status == 2) {
		cm.dispose();
		}            
          }
     }