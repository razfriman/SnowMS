/* 
	Pile of Flowers (1043000)
	- The Forest of Patience <Step 2> (101000101)
	Jump Quest
*/
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

function start() {
    var rewards = MaplePQRewards.JQrewards1;
    MapleReward.giveReward(rewards, cm.getPlayer());
    cm.warp(101000000, 0);
    cm.dispose();
}
//Saphire
//Topaz
//Opal
//Amethyst
//Emerald
//Garnet
//Aquamarine