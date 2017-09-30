/*
@	Author : Raz
@
@	NPC = Amos the Strong
@	Map = Hidden Street <BONUS STAGE> (Amos' Vault) [NOT COUPLES]
@	NPC MapId = 670010800
@	Function = Handle APQ
@
*/

/* 
	Amos the Strong		     (9201043)-(9201044)-(9201045)-(9201046)-(9201048)
	Amos' Training Ground	     (670010000)
	Entrance of Amorian Challenge(670010100)
	Stage 1 - Magik Mirror	     (670010100)
	Stage 2 - Heart Strings	     (670010100)
	Stage 3 - Twisted Switcher   (670010100)
	Stage 4 - Last Man Standing  (670010100)
	Stage 5 - Fluttering Hearts  (670010100)
	Stage 6 - Love Hurts	     (670010100)
	Stage 7 - Amos' Vault	     (670010100)
*/


//Stage 7 (BONUS)


var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();//ExitChat
	}else if (mode == 0){
		cm.sendOk("Wise choice, who wouldn't want free mesos and some items from the #bBonus-Stage#k.");
		cm.dispose();//No
	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		var eim = cm.getChar().getEventInstance();  
                 if (status == 0) {
		cm.sendYesNo("Would you like to leave the bonus?");
		}else if (status == 1) {
		if(isLeader()){
		cm.sendOk("Ok, Your loss");
		}else{
		cm.sendOk("Ask your #bParty-Leader#k to come talk to me.");
		cm.dispose();
		}
	 	}else if (status == 2) {
		var map = cm.getMap(670010100);
		var portal = map.getPortal("st00");
		var party = eim.getPlayers();
		cm.warpMembers(map, "st00", party);
		//STOP TIMER
		//WARP PLAYERS OUT OF BONUS
		cm.dispose();
		}            
          }
     }
     
function isLeader(){
if(cm.getParty() == null){
return false;
}else{
return cm.isLeader();
}
}