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
package net.sf.odinms.scripting.reactor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.scripting.AbstractScriptManager;
import net.sf.odinms.server.DropEntry;
import net.sf.odinms.server.maps.MapleReactor;

/**
 * @author Lerk
 */
public class ReactorScriptManager extends AbstractScriptManager {

	private static ReactorScriptManager instance = new ReactorScriptManager();
	private Map<Integer, List<DropEntry>> drops = new HashMap<Integer, List<DropEntry>>();

	public synchronized static ReactorScriptManager getInstance() {
		return instance;
	}

	public void act(MapleClient c, MapleReactor reactor) {
		try {
			ReactorActionManager rm = new ReactorActionManager(c, reactor);

            Invocable iv = null;

            if (reactor.getStats().getAction() != null) {
                iv = getInvocable("reactor/" + reactor.getStats().getAction() + ".js", c);
            }
			if (iv == null) {
                iv = getInvocable("reactor/" + reactor.getId() + ".js", c);
                if (iv == null) {
				return;
                }
			}
            
			engine.put("rm", rm);
			ReactorScript rs = iv.getInterface(ReactorScript.class);
			rs.act();
		} catch (Exception e) {
			log.error("Error executing reactor script.", e);
		}
	}

	public List<DropEntry> getDrops(int rid) {
		List<DropEntry> ret = drops.get(rid);
		if (ret == null) {
			ret = new LinkedList<DropEntry>();
			try {
				Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM reactordrops WHERE reactorid = ? AND chance >= 0");
				ps.setInt(1, rid);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					ret.add(new DropEntry(rs.getInt("itemid"), rs.getInt("questid"), rs.getInt("chance"), rs.getInt("amount")));
				}
				rs.close();
				ps.close();
			} catch (SQLException sqle) {
				log.error(sqle.getMessage());
			} catch (Exception e) {
				log.error("Could not retrieve drops for reactor " + rid, e);
			}
			drops.put(rid, ret);
		}
		return ret;
	}

	public void clearDrops() {
		drops.clear();
	}
}
