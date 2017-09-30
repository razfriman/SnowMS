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

package net.sf.odinms.exttools.dropspider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.odinms.database.DatabaseConnection;

public class ChanceRecalc {

    public static void main(String args[]) throws SQLException {
	Connection con = DatabaseConnection.getConnection();
	PreparedStatement ps = con.prepareStatement("UPDATE monsterdrops SET chance = ? WHERE monsterdropid = ?");
	PreparedStatement get = con.prepareStatement("SELECT * FROM monsterdrops");
	ResultSet rs = get.executeQuery();
	DropSpiderInformationProvider dsi = DropSpiderInformationProvider.getInstance();
	while (rs.next()) {
	    ps.setInt(2, rs.getInt("monsterdropid"));
	    ps.setInt(1, dsi.makeDropChance(rs.getInt("itemid")));
	    ps.executeUpdate();
	}
    }
}
