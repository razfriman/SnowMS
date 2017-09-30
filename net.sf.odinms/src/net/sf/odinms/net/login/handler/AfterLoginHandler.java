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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;


public class AfterLoginHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		byte c2 = slea.readByte();
		byte c3 = slea.readByte();
		if (c2 == 1 && c3 == 1) {
			if(LoginServer.getInstance().allowPins()) {
			c.getSession().write(MaplePacketCreator.requestPin());
			} else {
			c.getSession().write(MaplePacketCreator.pinAccepted());
			}
		} else if (c2 == 1 && c3 == 0) {
		    //[09 00] [01] [00] F1 B5 2E 00 [(04 00) 36 30 31 33]
			slea.readInt();//Unknown
			String pin = slea.readMapleAsciiString();
			if (pin.equals(c.getPin())) {
 				c.getSession().write(MaplePacketCreator.pinAccepted());
			} else {
				c.getSession().write(MaplePacketCreator.requestPinAfterFailure());
			}
		} else {
			// abort login attempt
		}
	}
}
