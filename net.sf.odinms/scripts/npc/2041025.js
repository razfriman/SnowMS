/*
@	Author : Raz
@
@	NPC = Machine Apparatus(2041025)
@	Map =  Ludibirium <Origin of ClockTower>
@	NPC MapId = 220080001
@	Function = Warp out of Papulatus
@
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
//MODE = YES/NO
//TYPE = Last Message Type
//SELECTION = Selected Option
         
         if (mode == -1) {//ExitChat
		cm.dispose();
	
	} else if (mode == 0) {//No
		cm.dispose();

	} else {//Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		cm.sendYesNo("Beep... beep... you can make your escape to a safer place through me. Beep ... beep ... would you like to leave this place?");
		} else if (status == 1) {
		if(cm.getPlayerCount() == 1) {
		cm.resetReactors();
		cm.killAll();
		}
		cm.warp(220080000);
		cm.dispose();
	 	}            
          }
     }