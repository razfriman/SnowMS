/*
@	Author : Snow
@
@	NPC = NAME
@	Map =  MAP
@	NPC MapId = MAPID
@	Function = Rebirth Player
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
		cm.sendOk("Ok, talk to me when your sure you want to #bRebirth#k.");
		cm.dispose();

	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		cm.sendYesNo("Welcome, great hero. You have been through a long and challenging road, and you have become immensely strong. If you bring me my favorite snack,I can use my magic to increase your power even further, and surpass your limits! You will become a level 1 Beginner again, but you will keep your stats the same. Do you wish to be reborn?" );
		}else if (status == 1) {
		if(cm.getChar().getLevel() == 200){
		cm.sendOk("Sorry, You have to be level 200 to rebirth.");
		cm.dispose();
		}else if (cm.itemQuantity(4140000) > 0){
		cm.sendOk("You did not bring me my Pocky D:");
		cm.dispose();
		}else{
		cm.sendOk("#bGood-Job#k, you have qualified for a #eRebirth#n.");
		}
	 	}else if (status == 2) {
		cm.getChar().setLevel(1);
		cm.getChar().setJob(0);
		cm.gainItem(4140000,-1);
		cm.sendNext("Enjoy your rebirth!");
		cm.dispose();
		}            
          }
     }