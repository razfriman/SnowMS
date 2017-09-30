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
Amoria PQ: 1st stage mirror portal
*/

function enter(pi) {
	var portal0;
	var portal1;
	var portal2;
	var portalO;
	var eim = pi.getPlayer().getEventInstance();
	var o = Math.floor(Math.random()*3) + 1;
	if(eim != null){
	portal0 = eim.getMapInstance(670010200).getPortal("go00");
	portal1 = eim.getMapInstance(670010200).getPortal("go01");
	portal2 = eim.getMapInstance(670010200).getPortal("go02");
	portalO = eim.getMapInstance(670010200).getPortal("go0" + (o-1));
	}else{
	portal0 = pi.getPlayer().getMap().getPortal("go00");
	portal1 = pi.getPlayer().getMap().getPortal("go01");
	portal2 = pi.getPlayer().getMap().getPortal("go02");
	portalO = pi.getPlayer().getMap().getPortal("go0" + (o-1));
	}
	portal0.setScriptName("apq1_C");
	portal1.setScriptName("apq1_C");
	portal2.setScriptName("apq1_C");
	portalO.setScriptName("apq1_O");
	pi.playerMessage("ASDFASDF");
	return false;
	
}