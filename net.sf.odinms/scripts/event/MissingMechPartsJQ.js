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
 * Missing Mechanical Parts JQ
 */
 
importPackage(net.sf.odinms.world);

var exitMap;
var instanceId;
var time = 20;

function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(220020000);
	var instanceName = "MissingMechPartsJQ" + instanceId;
	var eim = em.newInstance(instanceName);
	instanceId++;
	
	eim.getMapInstance(922000000);

	var eventTime = time * (1000 * 60);
	em.schedule("timeOut", eventTime);
	eim.startEventTimer(eventTime);
	
	return eim;
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(922000000);
	player.changeMap(map, map.getPortal(0));	
	player.getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.getClock(time * 60));
}

function playerDead(eim, player) {
	playerExit(eim, player);
	eim.dispose();
}

function playerDisconnected(eim, player) {
	removePlayer(eim, player);
	eim.dispose();
}

function leftParty(eim, player) {
 //Nothing :D   
}

function disbandParty(eim) {
//Nothing :D
}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}

//for offline players
function removePlayer(eim, player) {
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
}

function clearPQ(eim) {
//Nothing :D
}

function allMonstersDead(eim) {
 //Nothing :D       
}

function cancelSchedule() {
//Nothing :D
}

function timeOut() {
	var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				if(pIter.next().getMap().getId() == 922000000){
				playerExit(eim, pIter.next());
				}
			}
		}
		eim.dispose();
	}
}

 function playerRevive(eim, player) {
     playerExit(eim, player);
}