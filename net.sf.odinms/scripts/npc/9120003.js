/**
 Hikari, Showa Townstreet, has a few quests and leads to the spa
**/

importPackage(net.sf.odinms.client);

var status = 0;

function start() {
	cm.sendYesNo("You look stressed.\r\nDo you want to take a relaxing bath in the spa?");
}

function action(mode, type, selection) {
	if (mode == 1) {
		if (cm.getChar().getGender() == MapleGender.MALE) {
			cm.warp(801000100);
		} else {
			cm.warp(801000200);
		}
	}
	cm.dispose();
}
