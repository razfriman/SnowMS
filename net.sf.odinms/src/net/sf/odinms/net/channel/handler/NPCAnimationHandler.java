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
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raz
 */
public class NPCAnimationHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(NPCAnimationHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	int length = (int) slea.available();

	if (length == 6) {//NPC TALK
	    mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
	    mplew.writeInt(slea.readInt());
	    mplew.write(slea.readByte());
            mplew.write(slea.readByte());
            //movement
	    c.getSession().write(mplew.getPacket());


	} else if (length > 6) {//NPC MOVE
	    byte[] bytes = slea.read(length - 9);// 8 9 10 or 11
	    mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
	    mplew.write(bytes);
	    c.getSession().write(mplew.getPacket());
	} else {
	    log.info("NPC-Animation Packet - " + c.getPlayer().getName() + "\r\nPACKET: " + slea.toString());
	}
    }
}
