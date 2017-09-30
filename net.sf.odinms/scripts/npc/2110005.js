/*
@	Author : Raz
@
@	NPC = Camel-Cab
@	Map =  Magaita
@	NPC MapId = Magaita?
@	Function = Taxi
@
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
//MODE = YES/NO
//TYPE = Last Message Type
//SELECTION = Selected Option

    if (mode == -1) {//ExitChat
		cm.dispose();

    } else if (mode == 0) {//No
        cm.sendNex("Hmmm ... too busy to do it right now? If you feel like doing it, though, come back and find me.");
		cm.dispose();

	} else {//Regular Talk
		if (mode == 1)
			status++;
		else
			status--;

        if (status == 0) {
		cm.sendYesNo("Will you move to #b#m260000000##k now? The price is #b1500 mesos#k.");
		} else if (status == 1) {
            if (cm.getPlayer().getMesos() < 1500) {
                cm.sendNext("I am sorry, but I think you are short on mesos. I am afraid I can't let you ride this if you do not have enough money to do so. Please come back when you have enough money to use this.");
                cm.dispose();
            } else {
                cm.gainMeso(-1500);
                cm.warp(260000000, 0);
                cm.dispose();
            }
	 	}
    }
}