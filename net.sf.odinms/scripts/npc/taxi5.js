/*
@	Author : Raz
@
@	NPC = Nautilus' Mid-Size Taxi
@	Map = Nautilus Port
@	NPC MapId = 120000000
@	Function = Taxi
@
*/

var status = 0;
var maps = Array(104000000, 102000000, 100000000, 103000000, 101000000);
var cost = Array(1200, 1000, 1000, 1200, 1000);
var costBeginner = Array(120, 120, 80, 100);
var selectedMap = -1;
var job;

importPackage(net.sf.odinms.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 2 && mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("Hello, I drive the #p" + cm.getNpc() + "#. If you want to go from town to town safely and fast, then ride our cab. We'll gladly take you to your destination with an affordable price.");
		} else if (status == 1) {
			cm.sendNextPrev("I can take you to various locations for just a small fee. Beginners will get a 90% discount on normal prices.")
		} else if (status == 2) {
			var selStr = "Select your destination.#b";
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				for (var i = 0; i < maps.length; i++) {
					selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + costBeginner[i] + " meso)#l";
				}
			} else {
				for (var i = 0; i < maps.length; i++) {
					selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + cost[i] + " meso)#l";
				}
			}
			cm.sendSimple(selStr);
		} else if (status == 3) {
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				if (cm.getMeso() < costBeginner[selection]) {
					cm.sendOk("You do not have enough mesos.")
					cm.dispose();
				} else {
					cm.sendYesNo("So you have nothing left to do here? Do you want to go to #m" + maps[selection] + "#?");
					selectedMap = selection;
				}
			}
			else {
				if (cm.getMeso() < cost[selection]) {
					cm.sendOk("You do not have enough mesos.")
					cm.dispose();
				} else {
					cm.sendYesNo("So you have nothing left to do here? Do you want to go to #m" + maps[selection] + "#?");
					selectedMap = selection;
				}
			}
		} else if (status == 4) {
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				cm.gainMeso(-costBeginner[selectedMap]);
			}
			else {
				cm.gainMeso(-cost[selectedMap]);
			}
			cm.warp(maps[selectedMap], 0);
			cm.dispose();
		}
	}
}