/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
					   Matthias Butz <matze@odinms.de>
					   Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
*
* @Author Raz
*
* @Function Travel Orbis<->Ludibrium
*/

importPackage(net.sf.odinms.world);
importPackage(net.sf.odinms.tools);

var eim;

//Time Setting is in millisecond
var closeTime = 240000; //The time to close the gate
var beginTime = 300000; //The time to begin the ride
var rideTime = 600000; //The time that require move to destination
var Orbis_btf;
var Train_to_Orbis;
var Orbis_docked;
var Ludibrium_btf;
var Train_to_Ludibrium;
var Ludibrium_docked;

function init() {
	Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000122);
	Ludibrium_btf = em.getChannelServer().getMapFactory().getMap(220000111);
	Train_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090110);
	Train_to_Ludibrium = em.getChannelServer().getMapFactory().getMap(200090100);
	Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000100);
	Ludibrium_docked = em.getChannelServer().getMapFactory().getMap(220000100);
	scheduleNew();
}

function scheduleNew() {
	em.setProperty("docked", "true");
	em.setProperty("entry", "true");
	var lBoatMap = em.getChannelServer().getMapFactory().getMap(220000110);
	var oBoatMap = em.getChannelServer().getMapFactory().getMap(200000121);
	lBoatMap.setShip(true, false);
	oBoatMap.setShip(true, false);
	em.schedule("stopEntry", closeTime);
	em.schedule("takeoff", beginTime);
}

function stopEntry() {
	em.setProperty("entry","false");
}

function takeoff() {
	em.setProperty("docked","false");
	if(Orbis_btf.getPlayerCount() > 0) {
		while(Orbis_btf.getCharacters().iterator().hasNext()) {
			Orbis_btf.getCharacters().iterator().next().changeMap(Train_to_Ludibrium, Train_to_Ludibrium.getPortal(0));
		}
	}
	if(Ludibrium_btf.getPlayerCount() > 0) {
		while(Ludibrium_btf.getCharacters().iterator().hasNext()) {
			Ludibrium_btf.getCharacters().iterator().next().changeMap(Train_to_Orbis, Train_to_Orbis.getPortal(0));
		}
	}
	var lBoatMap = em.getChannelServer().getMapFactory().getMap(220000110);
	var oBoatMap = em.getChannelServer().getMapFactory().getMap(200000121);
	lBoatMap.setShip(false, false);
	oBoatMap.setShip(false, false);
	em.schedule("arrived", rideTime);
}

function arrived() {
	if(Train_to_Orbis.getPlayerCount() > 0) {
		while(Train_to_Orbis.getCharacters().iterator().hasNext()) {
			Train_to_Orbis.getCharacters().iterator().next().changeMap(Orbis_docked, Orbis_docked.getPortal(0));
		}
	}
	if(Train_to_Ludibrium.getPlayerCount() > 0) {
		while(Train_to_Ludibrium.getCharacters().iterator().hasNext()) {
			Train_to_Ludibrium.getCharacters().iterator().next().changeMap(Ludibrium_docked, Ludibrium_docked.getPortal(0));
		}
	}
	scheduleNew();
}

function playerEntry(eim, player) {
	var BFtakeoff;
	if(player.getMapId() == 200000121)
		BFtakeoff = em.getChannelServer().getMapFactory().getMap(200000122);
	else
		BFtakeoff = em.getChannelServer().getMapFactory().getMap(220000111);
	player.changeMap(BFtakeoff, BFtakeoff.getPortal(0));
}

function playerExit(eim, player) {
	var getOff;
	if(player.getMapId() == 200000122)
		getOff = em.getChannelServer().getMapFactory().getMap(200000121);
	else
		getOff = em.getChannelServer().getMapFactory().getMap(220000110);
	player.changeMap(getOff, getOff.getPortal(0));
}

function playerDisconnected(eim, player) {
	var playerMap = player.getMapId();
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	if(playerMap == 200000122)
		player.setMap(em.getChannelServer().getMapFactory().getMap(200000100));
	else
		player.setMap(em.getChannelServer().getMapFactory().getMap(220000100));
}

function cancelSchedule() {
//No clue really...
}
