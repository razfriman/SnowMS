/* 
	TombStone (2041024)
	Ludibrium
	Deep Inside the Clocktower
	220080000
	Sell Cracked Dimension
	
*/
importPackage(net.sf.odinms.client);

var status = 0;
var npcText = "";
var cost = 1000000;
function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();//ExitChat
	}else if (mode == 0){
		cm.sendOk("OK, see you later.");
		cm.dispose();//No
	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		npcText += "Would you like to buy a #i4031179# for #b" + cost + " mesos#k ?";
		cm.sendYesNo(npcText);
		npcText = "";
		
		}else if (status == 1) {
		if(cm.getPlayer().getMeso() >= cost){
		npcText += "Thank your for purchasing one:\r\n #i4031179# #b#t4031179#.";
		cm.gainItem(4031179, 1);
		}else{
		npcText += "You dont have enough mesos!";
		}
		cm.sendOk(npcText);
		npcText = "";		
	 	}else if (status == 2) {
		cm.dispose();
		}            
          }
     }