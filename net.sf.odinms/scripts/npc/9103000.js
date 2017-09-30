/*
@	Author : Raz
@
@	NPC = 9103002 - Pietri
@	Map =  Ludibrium - <Ludibrium Maze 16>
@	NPC MapId = 809050015
@	Function = LMPQ - Finish NPC
@	Gives = Warp
@
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
		cm.dispose();
	
	} else if (mode == 0) {
		cm.dispose();

	} else {
		if (mode == 1)
			status++;
		else
			status--;
                 if (status == 0) {
		cm.sendNext("Great job escaping the maze! Did you collect the coupons from the monsters standing in your way at the maze?");

		} else if (status == 1) {
		if (cm.getParty() != null && isLeader()) {
		if (cm.itemQuantity(4001007) >= 30) {
		cm.sendOk("Good job! you collected #b" + cm.itemQuantity(4001007) + " #t4001007#'s\r\n#kYou may pass on to get your reward");
		} else {
		cm.sendOk("Please go collect more coupons.\r\nyou need at least #b30 Coupons");
		cm.dispose();
		}
		} else {
		cm.sendPrev("Please tell #byour party leader#k to speak to me after gathering all the coupons from the party members.");
		cm.dispose();
		}

	 	} else if (status == 2) {
		var eim = cm.getChar().getEventInstance();
		if (eim != null) {
		cm.givePartyExp((cm.itemQuantity(4001007)* 50), eim.getPlayers());
		eim.finishPQ();
		}
		cm.dispose();
		}
          }
     }
     
function isLeader(){
return cm.isLeader();
}     