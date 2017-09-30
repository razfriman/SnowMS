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
 * Ludi Maze PQ
 */
 
importPackage(net.sf.odinms.world);
importPackage(net.sf.odinms.server);
importPackage(net.sf.odinms.server.maps);

var exitMap;
var instanceId;

function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(922010000);//Exit
	var instanceName = "LudiPQ" + instanceId;
	var eim = em.newInstance(instanceName);
	instanceId++;
	

	eim.getMapInstance(922010200).getPortal("next00").setLocked(1);
	eim.getMapInstance(922010201).getPortal("out00").setLocked(1);
	eim.getMapInstance(922010300).getPortal("next00").setLocked(1);
	eim.getMapInstance(922010400).getPortal("next00").setLocked(1);
	eim.getMapInstance(922010401).getPortal("out00").setLocked(1);
	eim.getMapInstance(922010402).getPortal("out00").setLocked(1);
	eim.getMapInstance(922010403).getPortal("out00").setLocked(1);
	eim.getMapInstance(922010404).getPortal("out00").setLocked(1);
	eim.getMapInstance(922010405).getPortal("out00").setLocked(1);
	var map5 = eim.getMapInstance(922010500);
	map5.getPortal("next00").setLocked(1);
	map5.getPortal("in01").setLocked(1);
	eim.getMapInstance(922010501);
	eim.getMapInstance(922010502);
	eim.getMapInstance(922010503);
	eim.getMapInstance(922010504);
	eim.getMapInstance(922010505);
	eim.getMapInstance(922010506);
	eim.getMapInstance(922010600);
	eim.getMapInstance(922010700).getPortal("next00").setLocked(1);
	eim.getMapInstance(922010800).getPortal("next00").setLocked(1);
	eim.getMapInstance(922010900);
	eim.getMapInstance(922011000);
	eim.getMapInstance(922011100);

	var eventTime = 60 * (1000 * 60);
	em.schedule("timeOut", eventTime);
	eim.startEventTimer(eventTime);

	return eim;
}

function playerEntry(eim, player) {
	var map0 = eim.getMapInstance(922010100);
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
	var map = eim.getMapInstance(922011100);
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
	//var rewards = MaplePQRewards.LMPQrewards;
	for (var i = 0; i < party.size(); i++) {
		   playerFinish(eim, party.get(i));
		   //MapleReward.giveReward(rewards, party.get(i));
	}
	eim.dispose();
}

function allMonstersDead(eim) {
        //do nothing; LPQ has nothing to do with monster killing
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
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				if(pIter.next().getMap().getId() == 922011000){
				playerFinish(eim, pIter.next());
				}
			}
		}
	}

}

 function playerRevive(eim, player) {
     
}

/*
CHECKLIST 
1 [X]
2 [X]
3 [X]
4 [X]
5 [X]
6 [X]
7 [X]
8 [X]
9 [X]
B [X]
X [X]
*/