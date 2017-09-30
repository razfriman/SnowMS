/* 
	Treasure Chest (10652010)
	- Line 3 Construction Site: B3 <Subway Depot> (103000909)
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