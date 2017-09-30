/*
@	Author : Raz
@
@	NPC = 9103002 - Rolly
@	Map =  Ludibrium - <Exit of the Maze> [RIGHT SIDE]
@	NPC MapId = 809050016
@	Function = LMPQ - Quit NPC
@	Gives = Reward + XP
@
*/

importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);


var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {//ExitChat
		cm.dispose();
	
	}else if (mode == 0){//No
		cm.sendOk("If you wish to receive your rewards and return to Ludibrium, please let me know!");
		cm.dispose();

	}else{		    //Regular Talk
		if (mode == 1)
			status++;
		else
			status--;
		
                 if (status == 0) {
		cm.sendYesNo("Your party gave a stellar effort and gathered up at least 30 coupons. For that, i have a present for each and every one of you. After receiving the present, you will be sent back to Ludibrium. Now, would you like to receive the present right now?");

		}else if (status == 1) {
		cm.warp(809050017);
		var rewards = MaplePQRewards.LMPQrewards;
		MapleReward.giveReward(rewards, cm.getChar());
		cm.dispose();
		
	 	}           
          }
     }