/* 
	Treasure Chest (1052008)
	- Line 3 Construction Site: B1 <Subway Depot> (103000902)
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

//Adamantium
//Bronze
//Mithril
//Orihalcon
//Silver
//Steel