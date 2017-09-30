/* 
	Pile of Herbs (1063002)
	- The Forest of Patience <Step 5> (101000104)
	Jump Quest
*/
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

function start() {
    var rewards = MaplePQRewards.JQrewards2;
    MapleReward.giveReward(rewards, cm.getChar());
    cm.warp(105040300, 0);
    cm.dispose();
}

//Diamon Ore
//Black Crystal Ore
//Gold Ore