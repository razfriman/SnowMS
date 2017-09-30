/* 
	Pile of Pink Flowers (1063000)
	- The Deep Forest Of Patience <Step 2>(105040311)
	Jump Quest
*/
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

function start() {
    var rewards = MaplePQRewards.JQrewards0;
    MapleReward.giveReward(rewards, cm.getChar());
    cm.warp(105040300, 0);
    cm.dispose();
}

//Adamantium
//Bronze
//Mithril
//Orihalcon
//Silver
//Steel