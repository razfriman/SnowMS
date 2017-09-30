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

package net.sf.odinms.net.login.handler;


import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.net.world.MapleWorld;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ViewAllCharHandler extends AbstractMaplePacketHandler {	
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	    List<MapleCharacter> chars = c.loadCharacters(1, true);//world doesnt matter with boolean
	    int unk = chars.size() + (3 - chars.size() % 3);
	    c.getSession().write(MaplePacketCreator.showAllCharactersInfo(chars.size(), unk));
	    for(MapleWorld world : LoginServer.getInstance().getWorlds()) {
		chars = c.loadCharacters(world.getId(), false);
		if (chars.size() > 0) {
		    c.getSession().write(MaplePacketCreator.showAllCharactersWorld(world.getId(), chars));
		}
	    }
	}
}
