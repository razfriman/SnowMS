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

package net.sf.odinms.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;

/**
 *
 * @author Raz
 */
public class MapleShutdownHook extends Thread {

	@Override
	public void run() {
		for (ChannelServer cs : ChannelServer.getAllInstances()) {
			for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
				chr.saveToDB(true);
			}
		}
		try {
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0");
			ps.executeUpdate();
		} catch (SQLException ex) {
			System.out.println("ERROR: resetting loggedin");
			ex.printStackTrace();
		}
	}
}
