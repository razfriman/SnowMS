/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Raz
 */
public class MapleQuestRecord implements MapleCharacterObject {

    private Map<Integer, String> records = new HashMap<Integer, String>();

    public MapleQuestRecord() {

    }

    public MapleCharacterObjectType getType() {
        return MapleCharacterObjectType.QUEST_RECORD;
    }

    public void saveToDB(MapleCharacter chr) throws SQLException {
        PreparedStatement ps;
        Connection con = DatabaseConnection.getConnection();
        chr.deleteWhereCharacterId(con, "DELETE FROM questperform WHERE characterid = ?");
        ps = con.prepareStatement("INSERT INTO questperform (characterid, qrkey, queststate) VALUES (?, ?, ?)");
        ps.setInt(1, chr.getId());
        for (int key : records.keySet()) {
            ps.setInt(2, key);
            ps.setString(3, records.get(key));
            ps.executeUpdate();
        }
        ps.close();
    }

    public void loadFromDB(int characterId) throws SQLException {
        PreparedStatement ps;
        ResultSet rs;
        Connection con = DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT * FROM questperform WHERE characterid = ?");
        ps.setInt(1, characterId);
        rs = ps.executeQuery();
        while (rs.next()) {
            int key = rs.getInt("qrkey");
            String state = rs.getString("queststate");
            records.put(key, state);
        }
        ps.close();
        rs.close();
    }

    public String get(int key) {
        return records.get(key);
    }

    public String set(int key, String state) {
        return records.put(key, state);
    }

    public String remove(int key) {
        return records.remove(key);
    }
}
