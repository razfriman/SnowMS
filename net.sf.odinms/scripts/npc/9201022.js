var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 1 && mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("Hi, I'm the Amoria Ambassador, your link between Amoria and Henesys.");
		}
		if (status == 1) {
			if(cm.getChar().getMapId() != 100000000) {
				cm.sendYesNo("Would you like to go back to Henesys?");
			}
			else if(cm.getChar().getMapId() != 680000000) {
				cm.sendYesNo("Would you like to go to Amoria?");
			}
		}
		if (status == 2) {
			if(cm.getChar().getMapId() != 100000000) {
				cm.warp(100000000, 0);
				cm.dispose();
			}
			else if(cm.getChar().getMapId() != 680000000) {
				cm.warp(680000000, 0);
				cm.dispose();
			}
		}
	}
}