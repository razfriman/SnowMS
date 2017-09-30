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

package net.sf.odinms.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Danny
 */
public class MapleRing implements Comparable<MapleRing> {

	private int ringId;
	private int ringId2;
	private int partnerId;
	private int itemId;
	private String partnerName;
	private boolean equipped;
	
	private MapleRing(int id, int id2, int partnerId, int itemid, String partnerName) {
		this.ringId = id;
		this.ringId2 = id2;
		this.partnerId = partnerId;
		this.itemId = itemid;
		this.partnerName = partnerName;
	}

	public static MapleRing loadFromDb(int ringId) {		
		try {
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE id = ?");
			ps.setInt(1, ringId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			
			MapleRing ret = new MapleRing(ringId,
				rs.getInt("partnerRingId"),
				rs.getInt("partnerChrId"),
				rs.getInt("itemid"),
				rs.getString("partnerName"));

			rs.close();
			ps.close();
			
			return ret;
		} catch (SQLException ex) {
			Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
	
	public static int[] createRing(MapleClient c, int itemid, int chrId, String chrName, int partnerId, String partnerName) {
		try {
			MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(partnerId);
			if (chr == null) {
				int[] ret_ = new int[2];
				ret_[0] = -1;
				ret_[1] = -1;
				return ret_;
			}
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO rings (itemid, partnerChrId, partnerName) VALUES (?, ?, ?)");
			ps.setInt(1, itemid);
			ps.setInt(2, partnerId);
			ps.setString(3, partnerName);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			int[] ret = new int[2];
			ret[0] = rs.getInt(1);
			rs.close();
			ps.close();

			ps = con.prepareStatement("INSERT INTO rings (itemid, partnerRingId, partnerChrId, partnerName) VALUES (?, ?, ?, ?)");
			ps.setInt(1, itemid);
			ps.setInt(2, ret[0]);
			ps.setInt(3, chrId);
			ps.setString(4, chrName);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			ret[1] = rs.getInt(1);
			rs.close();
			ps.close();

			ps = con.prepareStatement("UPDATE rings SET partnerRingId = ? WHERE id = ?");
			ps.setInt(1, ret[1]);
			ps.setInt(2, ret[0]);
			ps.executeUpdate();
			ps.close();
			
			MapleCharacter player = c.getPlayer();
			
			MapleInventoryManipulator.addRing(player, itemid, ret[0]);
			
			MapleInventoryManipulator.addRing(chr, itemid, ret[1]);
			
			c.getSession().write(MaplePacketCreator.getCharInfo(player));
			player.getMap().removePlayer(player);
			player.getMap().addPlayer(player);
			
			chr.getClient().getSession().write(MaplePacketCreator.getCharInfo(chr));
			chr.getMap().removePlayer(chr);
			chr.getMap().addPlayer(chr);
			
			return ret;
		} catch (SQLException ex) {
			Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
			int[] ret = new int[2];
			ret[0] = -1;
			ret[1] = -1;
			return ret;
		}
		
	}
	
	public int getRingId() {
		return ringId;
	}
	
	public int getPartnerRingId() {
		return ringId2;
	}
	
	public int getPartnerChrId() {
		return partnerId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public String getPartnerName() {
		return partnerName;
	}
	
	public boolean isEquipped() {
		return equipped;
	}
	
	public void setEquipped(boolean equipped) {
		this.equipped = equipped;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MapleRing) {
			if (((MapleRing) o).getRingId() == getRingId()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 53 * hash + this.ringId;
		return hash;
	}
	
	@Override
	public int compareTo(MapleRing other) {
		if (ringId < other.getRingId())
			return -1;
		else if (ringId == other.getRingId())
			return 0;
		else
			return 1;
	}
}