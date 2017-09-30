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

package net.sf.odinms.scripting.portal;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.tools.MaplePacketCreator;

public class PortalPlayerInteraction extends AbstractPlayerInteraction {

    private MaplePortal portal;

    public PortalPlayerInteraction(MapleClient c, MaplePortal portal) {
	  super(c);
	  this.portal = portal;
    }

    /**
     * Return the Portal Entered
     * @return [MaplePortal] Portal
     */
    public MaplePortal getPortal() {
	  return portal;
    }

    public void blockPortal() {
	  blockPortal(1);
    }

    public void blockPortal(int type) {
	  getPortal().setLocked(type);
    }

    public void playPortalSE() {
	  getClient().getSession().write(MaplePacketCreator.playPortalSE());
    }

    public boolean enterMiniDungeon(int baseId, int dungeonId, int dungeons) {
	if (getMapId() == baseId) {
	    for(int i = 0; i < dungeons; i++) {
		    if (getPlayer().getPartyId() != -1 && getPlayer().getPartyId() == getCurrentPartyId(dungeonId + i)) {
			warp(dungeonId + i, 0);
			return true;
		    } else if (getPlayerCount(dungeonId + i) == 0) {
			warp(dungeonId + i, 0);
			return true;
		    }
	    }
	    playerMessage(5, "All of the Mini-Dungeons are in use right now, please try again later.");
	} else {
	warp(baseId, "MD00");
	}
	return true;
    }
}
