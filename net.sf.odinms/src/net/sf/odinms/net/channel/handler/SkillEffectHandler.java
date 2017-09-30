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

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.attack.SpecialSkillInfo;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class SkillEffectHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int skillId = slea.readInt();
		switch(skillId) {
		    case 1121001: // Monster Magnet
		    case 1221001: // Monster Magnet
		    case 1321001: // Monster Magnet
		    case 3221001: // Pierce
		    case 2121001: // Big Bang
		    case 2221001: // Big Bang
		    case 2321001: // Big Bang
			SpecialSkillInfo info = new SpecialSkillInfo();
			info.skillId = skillId;
			info.level = slea.readByte();
			info.direction = slea.readByte();
			info.wSpeed = slea.readByte();
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.specialSkillEffect(c.getPlayer(), info));
			break; 
		    case 4211001: // Chakra, unknown heal formula
			break;
		}
	}
}
