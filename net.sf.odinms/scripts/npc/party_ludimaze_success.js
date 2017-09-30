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

function start() {
	if (cm.sendYesNo("Your party gave a stellar effort and gathered up at least 30 coupons. For that, I have a present for each and every one of you. After receiving the present, you will be sent back to Ludibrium. Now, would you like to receive the present right now?")) {
		cm.warp(809050017);
		var rewards = MaplePQRewards.LMPQrewards;
		MapleReward.giveReward(rewards, cm.getPlayer());
    } else {
     cm.sendOk("If you wish to receive your rewards and return to Ludibrium, please let me know!");
    }
}