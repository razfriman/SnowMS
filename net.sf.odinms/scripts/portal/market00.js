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

importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);

/*
Return from Free Market Script
*/

function enter(pi) {
    var qr = pi.getQuestRecord();
    var map = qr.get(7600);
    var portal = qr.get(7601);
    pi.playPortalSE();

    if (map != null && portal != null) {
	  pi.warp(map, portal);
    } else {
	  pi.warp(102000000, "market00");
    }
    qr.remove(7600);
    qr.remove(7601);
    return true;
}