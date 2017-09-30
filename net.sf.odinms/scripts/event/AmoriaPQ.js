/* 
 * This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * @Author Raz
 * 
 * Amoria PQ
 */
 
importPackage(net.sf.odinms.world);

var exitMap;
var instanceId;

function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(670011000);//Exit
	var instanceName = "AmoriaPQ" + instanceId;
	var eim = em.newInstance(instanceName);
	instanceId++;
	
	var map1 = eim.getMapInstance(670010200);
	var map2 = eim.getMapInstance(670010300);
	var map2_1 = eim.getMapInstance(670010301);
	var map2_2 = eim.getMapInstance(670010302);
	var map3 = eim.getMapInstance(670010400);
	var map4 = eim.getMapInstance(670010500);
	var map5 = eim.getMapInstance(670010600);
	var map6 = eim.getMapInstance(670010700);
	var map6_1 =eim.getMapInstance(670010750);
	var map7 = eim.getMapInstance(670010800);

	var o = Math.floor(Math.random() * 3) + 1;

	var stage1Portal0 = eim.getMapInstance(670010200).getPortal("go00");
	stage1Portal0.setScriptName("apq1_C");

	var stage1Portal1 = eim.getMapInstance(670010200).getPortal("go01");
	stage1Portal1.setScriptName("apq1_C");

	var stage1Portal2 = eim.getMapInstance(670010200).getPortal("go02");
	stage1Portal2.setScriptName("apq1_C");

	var stage1PortalO = eim.getMapInstance(670010200).getPortal("go0" + (o-1));
	stage1PortalO.setScriptName("apq1_O");

	var stage1PortalM = eim.getMapInstance(670010200).getPortal("gom00");
	stage1PortalM.setScriptName("apq1_M");

	var stage2PortalA = eim.getMapInstance(670010300).getPortal("next00");
	stage2PortalA.setScriptName("apq2");

	var stage2PortalB = eim.getMapInstance(670010301).getPortal("next00");
	stage2PortalB.setScriptName("apq2");

	var stage2PortalC = eim.getMapInstance(670010302).getPortal("next00");
	stage2PortalC.setScriptName("apq2");

	var stage3Portal = eim.getMapInstance(670010400).getPortal("next00");
	stage3Portal.setScriptName("apq3");

	var stage4Portal = eim.getMapInstance(670010500).getPortal("next00");
	stage4Portal.setScriptName("apq4");

	var eventTime = 70 * (1000 * 60);
	em.schedule("timeOut", eventTime);
	eim.startEventTimer(eventTime);

	return eim;
}

function playerEntry(eim, player) {
	var map0 = eim.getMapInstance(670010200);
	player.changeMap(map0, map0.getPortal(0));
	

}

function playerDead(eim, player) {
	if (player.isAlive()) { //don't trigger on death, trigger on manual revive
		if (eim.isLeader(player)) { //check for party leader
			//boot whole party and end
			var party = eim.getPlayers();
			for (var i = 0; i < party.size(); i++) {
				playerExit(eim, party.get(i));
			}
			eim.dispose();
		}
		else { //boot dead player
			playerExit(eim, player);
		}
	}
}

function playerDisconnected(eim, player) {
	if (eim.isLeader(player)) { //check for party leader
		//boot whole party and end
		var party = eim.getPlayers();
		for (var i = 0; i < party.size(); i++) {
			if (party.get(i).equals(player)) {
				removePlayer(eim, player);
			}			
			else {
				playerExit(eim, party.get(i));
			}
		}
		eim.dispose();
	}
	else { //boot d/ced player
		removePlayer(eim, player);
	}
}

function leftParty(eim, player) {
	playerExit(eim, player);
}

function disbandParty(eim) {
	//boot whole party and end
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
	eim.dispose();
}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}


function playerFinish(eim, player) {
	var map = exitMap;
	player.changeMap(map, map.getPortal(0));
}

//for offline players
function removePlayer(eim, player) {
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
}

function clearPQ(eim) {
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		   playerFinish(eim, party.get(i));
	}
	eim.dispose();
}

function allMonstersDead(eim) {
        //do nothing; APQ has nothing to do with monster killing
}

function cancelSchedule() {
    //No clue really...
}

function timeOut() {
	var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				playerExit(eim, pIter.next());
			}
		}
		eim.dispose();
	}
}

function startBonus() {
var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				if(pIter.next().getMap().getId() == 670010800){
				playerExit(eim, pIter.next());
			}
		}
	}

}

function playerRevive(eim, player) {
     
}

/*
CHECKLIST 
1 [X]//Do Stuff?
2 [X]//3 Ropes
3 [X]//8 Platforms
4 [X]//Kill Mobs
5 [X]//Gates
6 [X]//Balrog
7 [ ]//Bonus
B [ ]//Bonus1
X [ ]//Exit
*/