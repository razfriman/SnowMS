/* 
	Treasure Chest (1052009)
	- Line 3 Construction Site: B2 <Subway Depot> (103000905)
	Jump Quest
*/
importPackage(net.sf.odinms.server.pq);
importPackage(net.sf.odinms.client);

function start() {
    var rewards = MaplePQRewards.JQrewards0;
    MapleReward.giveReward(rewards, cm.getChar());
    cm.warp(103000100, 0);
    cm.dispose();
}
//Saphire
//Topaz
//Opal
//Amethyst
//Emerald
//Garnet
//Aquamarine