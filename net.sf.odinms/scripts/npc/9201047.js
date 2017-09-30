/*
@	Author : Raz
@
@	NPC = The Glimmer Man
@	Map =  Hidden Street <Exit>
@	NPC MapId = 670011000
@	NPC Exit-MapId = 670010000
@
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {//ExitChat
		cm.dispose();
	
	}else if (mode == 0){//No
		cm.sendOk("OK, Talk to me again if you want to leave here.");
		cm.dispose();

	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		if(cm.getChar().getMap().getId() == 670011000){
		if(status == 0){
		cm.sendNext("Goodbye! see you next time");
		}else if (status == 1){
		cm.warp(670010000);
		cm.dispose();
		}


		}else{
                 if (status == 0) {
		cm.sendYesNo("Are you sure you want to leave the Quest?");
		}else if (status == 1) {
		cm.sendNext("Ok, Bye!");
	 	}else if (status == 2) {
		var eim = cm.getChar().getEventInstance();  
		if(eim == null){
		cm.sendOk("Wait, Hey! how'd you get here?\r\nOh well you can leave anyways");
		}else{
		if(isLeader() == true){
		eim.disbandParty();
		cm.removeFromParty(4001008, eim.getPlayers());//CHANGE ITEM ID HERE
		}else{
		eim.leftParty(cm.getChar());
		cm.removeAll(4001022);//CHANGE ITEM ID HERE
		}
		cm.dispose();
		}
	    }else if (status == 3){
	    	cm.warp(670010000);
		cm.removeAll(4001022);//CHANGE ITEM ID HERE
		cm.dispose();
	    }
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