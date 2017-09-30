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
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.tools.MaplePacketCreator;

public class BuddyList implements MapleCharacterObject {

    public enum BuddyOperation {

        ADDED, DELETED;
    }

    public enum BuddyAddResult {

        BUDDYLIST_FULL, ALREADY_ON_LIST, OK;
    }
    private Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<Integer, BuddylistEntry>();
    private int capacity;
    private Deque<CharacterNameAndId> pendingRequests = new LinkedList<CharacterNameAndId>();

    /**
     * Creates a new buddylist
     * @param capacity - size of buddylist
     */
    public BuddyList(int capacity) {
        super();
        this.capacity = capacity;
    }

    /**
     *
     * @param characterId
     * @return the buddylist contains the characterId
     */
    public boolean contains(int characterId) {
        return buddies.containsKey(Integer.valueOf(characterId));
    }

    /**
     *
     * @param characterId
     * @return the buddylist contains the characterId AND the character is visible
     */
    public boolean containsVisible(int characterId) {
        BuddylistEntry ble = buddies.get(characterId);
        if (ble == null) {
            return false;
        }
        return ble.isVisible();
    }

    /**
     *
     * @return the capacity of the buddylist
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity of the buddylist
     * @param capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     *
     * @param characterId
     * @return characterId's buddylistEntry
     */
    public BuddylistEntry get(int characterId) {
        return buddies.get(Integer.valueOf(characterId));
    }

    /**
     *
     * @param characterName
     * @return characterName's buddylistEntry
     */
    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : buddies.values()) {
            if (ble.getName().toLowerCase().equals(lowerCaseName)) {
                return ble;
            }
        }
        return null;
    }

    /**
     * Adds a new buddy
     * @param entry
     */
    public void put(BuddylistEntry entry) {
        buddies.put(Integer.valueOf(entry.getCharacterId()), entry);
    }

    /**
     * Remove a buddy
     * @param characterId
     */
    public void remove(int characterId) {
        buddies.remove(Integer.valueOf(characterId));
    }

    /**
     *
     * @return Collection of the buddylistEntries
     */
    public Collection<BuddylistEntry> getBuddies() {
        return buddies.values();
    }

    /**
     *
     * @return is the buddylist full
     */
    public boolean isFull() {
        return buddies.size() >= capacity;
    }

    /**
     *
     * @return all the buddies characterId
     */
    public int[] getBuddyIds() {
        int buddyIds[] = new int[buddies.size()];
        int i = 0;
        for (BuddylistEntry ble : buddies.values()) {
            buddyIds[i++] = ble.getCharacterId();
        }
        return buddyIds;
    }

    @Override
    public MapleCharacterObjectType getType() {
        return MapleCharacterObjectType.BUDDY_LIST;
    }

    /**
     * Loads a buddylist from the database
     * @param characterId
     * @throws java.sql.SQLException
     */
    @Override
    public void loadFromDB(int characterId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ?");
        ps.setInt(1, characterId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            if (rs.getInt("pending") == 1) {
                pendingRequests.push(new CharacterNameAndId(rs.getInt("buddyid"), rs.getString("buddyname")));
            } else {
                put(new BuddylistEntry(rs.getString("buddyname"), rs.getInt("buddyid"), -1, true));
            }
        }
        rs.close();
        ps.close();

        ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ?");
        ps.setInt(1, characterId);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void saveToDB(MapleCharacter chr) throws SQLException {
        PreparedStatement ps;
        Connection con = DatabaseConnection.getConnection();
        chr.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ? AND pending = 0");
        ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, 0)");
        ps.setInt(1, chr.getId());
        for (BuddylistEntry entry : getBuddies()) {
            if (entry.isVisible()) {
                ps.setInt(2, entry.getCharacterId());
                ps.executeUpdate();
            }
        }
        ps.close();
    }

    /**
     *
     * @return CharacterNameAndId of the next pending request
     */
    public CharacterNameAndId pollPendingRequest() {
        return pendingRequests.pollLast();
    }

    /**
     * Adds a new buddy request to [C] from [cidFrom, nameFrom] from channel [channelFrom]
     * @param c
     * @param cidFrom
     * @param nameFrom
     * @param channelFrom
     */
    public void addBuddyRequest(MapleClient c, int cidFrom, String nameFrom, int channelFrom) {
        put(new BuddylistEntry(nameFrom, cidFrom, channelFrom, false));
        if (pendingRequests.isEmpty()) {
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(cidFrom, nameFrom));
        } else {
            pendingRequests.push(new CharacterNameAndId(cidFrom, nameFrom));
        }
    }
}
