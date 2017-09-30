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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Raz
 */
public class SetGenderHandler extends AbstractMaplePacketHandler {
    private static final Logger log = LoggerFactory.getLogger(MapleClient.class);
	
	@Override
        public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
	    int status = slea.readByte();
            switch(status) {
		case 1://SET GENDER
		int gender = slea.readByte();
		c.setGender(gender);
                Connection con = DatabaseConnection.getConnection();
		PreparedStatement ps;
                try {
                    ps = con.prepareStatement("UPDATE accounts SET gender = ? WHERE id = ?");
                    ps.setInt(1, gender);
                    ps.setInt(2, c.getAccID());
                    ps.executeUpdate();
                    ps.close();
                    if (c.getPin() == null)
                        c.setStatus(2);
                    c.getSession().write(MaplePacketCreator.setGender(gender));
		} catch (SQLException e) {
			log.error("ERROR", e);
		}
		break;
		case 8://WHO KNOWS
		    slea.readShort();
            }
        }
}
