/* 
	Pile of Blue Flowers (1063000)
	- The Deep Forest Of Patience <Step 4>(105040313)
	Jump Quest
*/
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

function start() {
    var rewards = MaplePQRewards.JQrewards1;
    MapleReward.giveReward(rewards, cm.getChar());
    cm.warp(105040300, 0);
    cm.dispose();
}
//Saphire
//Topaz
//Opal
//Amethyst
//Emerald
//Garnet
//Aquamarine