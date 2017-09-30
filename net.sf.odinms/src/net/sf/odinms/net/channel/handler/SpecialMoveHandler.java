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

import java.awt.Point;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.StreamUtil;

public class SpecialMoveHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	c.getPlayer().getCheatTracker().inspectActionTime(slea.readInt(), 200);
	int skillid = slea.readInt();
	// seems to be skilllevel for movement skills and -32748 for buffs
	Point pos = null;
	short delay = 0;
	int __skillLevel = slea.readByte();
	if (slea.available() == 4) {
	    pos = StreamUtil.readShortPoint(slea);
	} else {
	    switch (skillid) {
		case 1121001: // Monster Magnet processing
		case 1221001:
		case 1321001:
		     int mobs = slea.readInt();
		    for ( int i = 0; i < mobs; i++) {
			int oid = slea.readInt();
			byte success = slea.readByte();
			c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showMagnetSuccess(oid, success));
		    }
		    break;

		default:
		     byte type = slea.readByte();
		    if (type == 0x80) {
			delay = slea.readShort();
		    }
		    break;
	    }
	}

	ISkill skill = SkillFactory.getSkill(skillid);
	int skillLevel = c.getPlayer().getSkillLevel(skill);

	if (skillLevel == 0 || skillLevel != __skillLevel) {
	    AutobanManager.getInstance().addPoints(c.getPlayer().getClient(), 1000, 0, "Using a move skill he doesn't have (" + skill.getId() + ")");
	} else {
	    if (c.getPlayer().isAlive()) {
		if (skill.getId() != 2311002 || c.getPlayer().canDoor()) {
		    MapleStatEffect effect = skill.getEffect(skillLevel);
		    effect.setDelay(delay);
		    effect.applyTo(c.getPlayer(), pos);
		} else {
		    new ServernoticeMapleClientMessageCallback(5, c).dropMessage("Please wait 5 seconds before casting Mystic Door again");
		    c.getSession().write(MaplePacketCreator.enableActions());
		}
	    } else {
		c.getSession().write(MaplePacketCreator.enableActions());
	    }
	}
    }
}
