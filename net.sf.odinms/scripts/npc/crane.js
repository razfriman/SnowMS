/*
 Hak - Warp Bird
*/

var status = 0;
var maps = new Array(251000000, 200000100);
var mapNames = new Array("Herb Town", "Orbis");

var maps2 = new Array(1);
maps2[0] = 250000100;
var mapNames2 = new Array(1);
mapNames2[0] = "Mu Lung";

var status = 0;
var selectedMap = -1;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (cm.getChar().getMapId() == 251000000) {
		maps = maps2;
		mapNames = mapNames2;
	}

	if (status == 0) {
		var where = "Where do you want to go today?";
		for (var i = 0; i < maps.length; i++) {
			where += "\r\n#L" + i + "# " + mapNames[i] + "#l";
		}
		cm.sendSimple(where);
		status++;
	} else {
		if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
			cm.dispose();
		} else {
			if (status == 1) {
					cm.sendNext ("Alright, see you next time. Take care.");
					selectedMap = selection;
					status++
			} else if (status == 2) {
					cm.warp(maps[selectedMap], 0);
					cm.dispose();
			}
		}
	}
}

