/**
 Hikari, Showa Townstreet, has a few quests and leads to the spa
**/

importPackage(net.sf.odinms.client);

function start() {
	if (cm.sendYesNo("You look stressed.\r\nDo you want to take a relaxing bath in the spa?")) {
        if (cm.getChar().getGender() == MapleGender.MALE) {
			cm.warp(801000100);
		} else {
			cm.warp(801000200);
		}
    }
    cm.dispose();
}