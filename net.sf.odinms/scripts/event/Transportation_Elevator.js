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
 * Ludibrium Elevator
 */

importPackage(net.sf.odinms.scripting.reactor);
var elevator_s;
var elevator_m;
var returnMap;
var arrive;
var mf;

function init() {
	mf = em.getChannelServer().getMapFactory();
	elevator_m = mf.getMap(222020211);
	em.setProperty("isUp", "false");
	em.setProperty("isDown", "false");
    var elevator_r_map = mf.getMap(222020200);
	var elevator_r = elevator_r_map.getReactorByName("elevator");
	elevator_r.setState(1);
	elevator_r.update();
	onDown();
}

function onDown() {
	mf.getMap(222020100).resetReactors();
	arrive = mf.getMap(222020100);
	returnMap = mf.getMap(222020100);
	warpToDest();
	elevator_s = mf.getMap(222020110);
	elevator_m = mf.getMap(222020111);
	em.setProperty("isDown","true");
	em.schedule("goingUp", 60000);
}

function goingUp() {
	warpToM();
	em.setProperty("isDown","false");
	em.schedule("onUp", 50000);
	var elevator_r = mf.getMap(222020100).getReactorByName("elevator");
	elevator_r.setState(1);
	elevator_r.update();
}

function onUp() {
	mf.getMap(222020200).resetReactors();
	arrive = mf.getMap(222020200);
	returnMap = mf.getMap(222020200);
	elevator_s = mf.getMap(222020210);
	elevator_m = mf.getMap(222020211);
	warpToDest();
	em.setProperty("isUp", "true");
	em.schedule("goingDown", 60000);
}

function goingDown() {
	warpToM();
	em.setProperty("isUp","false");
	em.schedule("onDown", 50000);
	var elevator_r = mf.getMap(222020200).getReactorByName("elevator");
	elevator_r.setState(1);
	elevator_r.update();
}

function warpToDest() {
	if(elevator_m.getPlayerCount() > 0) {
		var iter = elevator_m.getCharacters().iterator();
		while(iter.hasNext()) {
			var player = iter.next();
			player.changeMap(arrive, arrive.getPortal(0));
		}
	}
}

function warpToM() {
	if(elevator_s.getPlayerCount() > 0) {
		var iter = elevator_s.getCharacters().iterator();
		while(iter.hasNext()) {
			var player = iter.next();
			player.changeMap(elevator_m, elevator_m.getPortal(0));
		}
	}
}

function playerDisconnected(eim, player) {
	player.getMap().removePlayer(player);
	player.setMap(returnMap);
}

function monsterValue(eim, mobId) {
	return 1;
}

function allMonstersDead(eim) {
        //do nothing; APQ has nothing to do with monster killing
}

function cancelSchedule() {
    //No clue really...
}

function playerRevive(eim, player) {
     
}
