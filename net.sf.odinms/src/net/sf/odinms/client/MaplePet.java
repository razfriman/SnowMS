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

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.movement.AbsoluteLifeMovement;
import net.sf.odinms.server.movement.LifeMovement;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.server.movement.MovementPath;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class MaplePet extends Item {

    private MapleClient client;
    private String name;
    private int uniqueid;
    private int closeness = 0;
    private int level = 1;
    private int fullness = 100;
    private int fh;
    private Point pos;
    private int stance;

    /** Creates a new instance of MaplePet */
    private MaplePet(int id, byte position, int uniqueid) {
	super(id, position, (short) 1);
	this.uniqueid = uniqueid;
    }

    /**
     * Loads a pet from the database
     * @param itemid
     * @param position
     * @param petid
     * @return The pet loaded from the database
     */
    public static MaplePet loadFromDb(int itemid, byte position, int petid) {
	try {
	    MaplePet ret = new MaplePet(itemid, position, petid);

	    Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
	    PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?"); // Get pet details..
	    ps.setInt(1, petid);

	    ResultSet rs = ps.executeQuery();
	    rs.next();

	    ret.setName(rs.getString("name"));
	    ret.setCloseness(rs.getInt("closeness"));
	    ret.setLevel(rs.getInt("level"));
	    ret.setFullness(rs.getInt("fullness"));

	    rs.close();
	    ps.close();

	    return ret;
	} catch (SQLException ex) {
	    Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    /**
     * Saves the pet to the database
     */
    public void saveToDb() {
	try {
	    Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
	    PreparedStatement ps = con.prepareStatement("UPDATE pets SET " + "name = ?, level = ?, " + "closeness = ?, fullness = ? " + "WHERE petid = ?"); // Prepare statement...
	    ps.setString(1, getName()); // Set name
	    ps.setInt(2, getLevel()); // Set Level
	    ps.setInt(3, getCloseness()); // Set Closeness
	    ps.setInt(4, getFullness()); // Set Fullness
	    ps.setInt(5, getUniqueId()); // Set ID
	    ps.executeUpdate(); // Execute statement
	    ps.close();
	} catch (SQLException ex) {
	    Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Creates a pet with itemid
     * @param itemid
     * @return the pet's unique id
     */
    public static int createPet(int itemid) {
	try {
	    MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();

	    Connection con = DatabaseConnection.getConnection();
	    PreparedStatement ps = con.prepareStatement("INSERT INTO pets (name, level, closeness, fullness) VALUES (?, ?, ?, ?)");
	    ps.setString(1, mii.getName(itemid));
	    ps.setInt(2, 1);
	    ps.setInt(3, 0);
	    ps.setInt(4, 100);
	    ps.executeUpdate();
	    ResultSet rs = ps.getGeneratedKeys();
	    rs.next();
	    int ret = rs.getInt(1);
	    rs.close();
	    ps.close();

	    return ret;
	} catch (SQLException ex) {
	    Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
	    return -1;
	}

    }

	@Override
	public byte getType() {
		return IItem.PET;
	}

    /**
     * Updates the pet and send a pet update packet
     */
    public void update() {
	if(getClient() != null) {
	    getClient().getSession().write(MaplePacketCreator.updatePet(this, true));
	}
    }

    /**
     * 
     * @return Pet's owner's client.
     */
    public MapleClient getClient() {
	return client;
    }

    public void setClient(MapleClient client) {
	this.client = client;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getUniqueId() {
	return uniqueid;
    }

    public void setUniqueId(int id) {
	this.uniqueid = id;
    }

    public int getCloseness() {
	return closeness;
    }
    
    public void setCloseness(int closeness) {
	closeness = Math.min(closeness, 30000);
	closeness = Math.max(closeness, 0);
	this.closeness = closeness;
    }

    public void handleCloseness(int closeness) {
	setCloseness(closeness);
	while (this.closeness >= ExpTable.getClosenessNeededForLevel(getLevel() + 1)) {
	    levelUp();
	}
	while (this.closeness < ExpTable.getClosenessNeededForLevel(this.level)) {
	    this.level -= 1;
	}
	update();
    }

    public void gainCloseness(int gain) {
	handleCloseness(this.closeness + gain);
    }

    public int getLevel() {
	return level;
    }

    public void setLevel(int level) {
	this.level = level;
    }
    
    public void levelUp() {
	setLevel(getLevel() + 1);
    	getClient().getSession().write(MaplePacketCreator.showOwnPetLevelUp(getClient().getPlayer().getPetIndex(this)));
	getClient().getPlayer().getMap().broadcastMessage(getClient().getPlayer(), MaplePacketCreator.showPetLevelUp(getClient().getPlayer(), getClient().getPlayer().getPetIndex(this)), false);
    }

    public int getFullness() {
	return fullness;
    }

    public void setFullness(int fullness) {
	fullness = Math.min(fullness, 100);
	this.fullness = fullness;
    }
    
    public void gainFullness(int fullness) {
	setFullness(this.fullness + fullness);
    }
    
    public boolean isFull() {
	return fullness >= 100;
    }

    public int getEquip(MapleCharacter chr, int i) {
	int slot = 0;
	switch (i) {
	    case 0:
		slot = 0;
		break;
	    case 1:
		slot = 16;
		break;
	    case 2:
		slot = 24;
		break;
	    }
	IItem item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-114 - slot));
	return item != null ? item.getItemId() : 0;
    }
    
    public int getIndex(MapleCharacter owner) {
	return owner.getPetIndex(this);
    }    

    public int getFh() {
	return fh;
    }

    public void setFh(int fh) {
	this.fh = fh;
    }

    public Point getPos() {
	return pos;
    }

    public void setPos(Point pos) {
	this.pos = pos;
    }

    public int getStance() {
	return stance;
    }

    public void setStance(int stance) {
	this.stance = stance;
    }
    
    public boolean canConsume(int itemId) {
	MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	for (int petId : ii.petsCanConsume(itemId)) {
	    if (petId == this.getItemId()) {
		return true;
	    }
	}
	return false;
    }
    
    public void updatePosition(MovementPath movementPath) {
	for (LifeMovementFragment move : movementPath.getRes()) {
	    if (move instanceof LifeMovement) {
		if (move instanceof AbsoluteLifeMovement) {
		    Point position = ((LifeMovement) move).getPosition();
		    this.setPos(position);
		}
		this.setStance(((LifeMovement) move).getNewstate());
	    }
	}
    }
}
