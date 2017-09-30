/*
	This file is part of the OdinMS Maple Story Server
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
Stage 5 - Fluttering Hearts - Hidden Street
*/
importPackage(net.sf.odinms.tools);
function enter(pi) {
	var pName = pi.getPortal().getName();
	var reactor;
	var map
	if(pi.getPlayer().getEventInstance() != null)
	    map = pi.getPlayer().getEventInstance().getMapInstance(670010600);
	else
	    map = pi.getPlayer().getMap();


	if(pName == "gt00PCS"){
	reactor = pi.getReactor(map, "gate00");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt00PIA").getId());
	return true;
	}else{
	return false;
	}
	
	}else if(pName == "gt01PCS"){
	reactor = pi.getReactor(map, "gate01");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt01PIA").getId());
	return true;
	}else{
	return false;
	}
	}else if(pName == "gt02PCS"){
	reactor = pi.getReactor(map, "gate02");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt02PIA").getId());
	return true;
	}else{
	return false;
	}
	}else if(pName == "gt03PCS"){
	reactor = pi.getReactor(map, "gate03");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt03PIA").getId());
	return true;
	}else{
	return false;
	}
	}else if(pName == "gt04PCS"){
	reactor = pi.getReactor(map, "gate04");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt04PIA").getId());
	return true;
	}else{
	return false;
	}
	}else if(pName == "gt05PCS"){
	reactor = pi.getReactor(map, "gate05");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt05PIA").getId());
	return true;
	}else{
	return false
	}
	}else if(pName == "gt06PCS"){
	reactor = pi.getReactor(map, "gate06");
	if(reactor.getState() == 4){
	pi.portToPort(pi.getPlayer().getMap().getPortal("gt06PIA").getId());
	return true;
	}else{
	return false;
	}
	
     
	}else{
	pi.playerMessage("IDK where else this is used to just messaign, this is the doorgo script");
	}
	
	return true;
}
