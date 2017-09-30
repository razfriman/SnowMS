
function start() {		
	cm.sendNext("Hi, I'm the Amoria Ambassador, your link between Amoria and Henesys.");
	if(cm.getChar().getMapId() != 100000000) {
        if (cm.sendYesNo("Would you like to go back to Henesys?")) {
            cm.warp(100000000, 0);
			cm.dispose();
        }
	} else if(cm.getChar().getMapId() != 680000000) {
		if (cm.sendYesNo("Would you like to go to Amoria?")) {
            cm.warp(680000000, 0);
            cm.dispose();
        }
    }
}