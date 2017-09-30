/*
@	Author : Raz
@
@	NPC = Tommy
@	Map =  Hidden-Street <Shortcut>
@	NPC MapId = 910010100 | 910010200 | 910010300
@	Function = Warp out of PQ
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
		cm.sendOk("Ok, see you later.")
		cm.dispose();

	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		if(cm.getChar().getMap().getId() == 910010100){
		cm.sendYesNo("Would you like to enter the #bBonus-Stage#k?");;
		}else{
		cm.warpRandom(100000200);
		cm.dispose();
		}

		}else if (status == 1) {
		if(isLeader() == false){
		cm.sendOk("Please tell your #bParty-Leader#k to come talk to me.");
		cm.dispose();
		}else{
		cm.sendOk("BLAH BLAH I NEED TO WARP TO BONUS");
		cm.dispose();
	 	}}      
          }
     }

function isLeader(){
if(cm.getParty() == null){
return false;
}else{
return cm.isLeader();
}
}

function hasParty(){
if(cm.getPlayer().getParty() == null){
return false;
}else{
return true;
}
}