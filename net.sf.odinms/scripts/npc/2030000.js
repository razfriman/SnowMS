/*
@	Author : Raz
@
@	NPC = Jeff
@	Map = El-Nath <Ice Valley II>
@	NPC MapId = 211040200
@	Function = Warp to Dungeon
@
*/

var status = 0;
var npcText = "";
function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {//ExitChat
		cm.dispose();
	
	}else if (mode == 0){//No
		cm.sendNext("Even if your level's high it's hard to actually go in there, but if you ever change your mind please find me. After all, my job is to protect this place.");
		cm.dispose();

	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		npcText = "Hey, you look like you want to go farther and deeper past this place. Over there, though, you'll find yourself surrounded by aggressive, dangerous monsters, so even if you feel that you're ready to go, please be careful. Long ago, a few brave men from our town went in wanting to eliminate anyone threatening the town, but never came back out..."; 
		if(cm.getChar().getLevel() < 50){
		npcText += " you haven't reached Level 50 yet. I can't let you in, then, so forget it."
		cm.sendPrev(npcText);
		cm.dispose();
		}else{
		npcText += "! You look pretty strong. All right, do you want to go in?";
		cm.sendYesNo(npcText);
		}
		}else if (status == 1) {
		if(cm.getChar().getLevel() < 50){
		cm.dispose();
		}else{
		cm.warp(211040300);
		cm.dispose();
		}
	 	}           
          }
     }