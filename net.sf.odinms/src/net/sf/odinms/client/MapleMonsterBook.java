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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Raz
 */
public class MapleMonsterBook implements  MapleCharacterObject {

    private Map<Integer, MonsterCard> cardMap;
    private int specialCount;
    private int normalCount;
    private int level;
    private int cover;
    private MapleCharacter player;
    public static final int MAX_CARD_LEVEL = 5;
    public static final int MAX_PLAYER_LEVEL = 8;
    public static final int[] PLAYER_LEVELS = new int[]{10, 30, 60, 100, 150, 210, 280};
    protected static Map<Integer, Integer> monsterCardCache = new HashMap<Integer, Integer>();
    protected static Map<Integer, Integer> monsterCardCacheOpposite = new HashMap<Integer, Integer>();

    public MapleMonsterBook(MapleCharacter player) {
        cardMap = new HashMap<Integer, MonsterCard>();
        specialCount = 0;
        normalCount = 0;
        level = 1;
        cover = 0;
        this.player = player;
    }

    @Override
    public MapleCharacterObjectType getType() {
        return  MapleCharacterObjectType.MONSTERBOOK;
    }

    @Override
    public void saveToDB(MapleCharacter chr) throws SQLException {
        PreparedStatement ps;
        Connection con = DatabaseConnection.getConnection();
        chr.deleteWhereCharacterId(con, "DELETE FROM monsterbook WHERE characterid = ?");
        ps = con.prepareStatement("INSERT INTO monsterbook (characterid, cardid, level) VALUES (?, ?, ?)");
        ps.setInt(1, chr.getId());
        for (MonsterCard card : getAllCards()) {
            ps.setInt(2, card.getId());
            ps.setInt(3, card.getLevel());
            ps.executeUpdate();
        }
        ps.close();
    }

    @Override
    public void loadFromDB(int characterId) throws SQLException {
        PreparedStatement ps;
        ResultSet rs;
        Connection con = DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT * FROM monsterbook WHERE characterid = ?");
        ps.setInt(1, characterId);
        rs = ps.executeQuery();
        while (rs.next()) {
            int cardId = rs.getInt("cardid");
            int cardLevel = rs.getInt("level");
            addCard(cardId, cardLevel, true);
        }
        calculateLevel();
        ps.close();
        rs.close();
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(int normalCount) {
        this.normalCount = normalCount;
    }

    public int getSpecialCount() {
        return specialCount;
    }

    public void setSpecialCount(int specialCount) {
        this.specialCount = specialCount;
    }

    public int getTotalCount() {
	  return specialCount + normalCount;
    }

    public int getSize() {
        return cardMap.size();
    }

     public static int getMonsterCardMonsterId(int itemId) {
		if (monsterCardCache.containsKey(itemId)) {
			return monsterCardCache.get(itemId);
		}

		MapleData data = MapleItemInformationProvider.getInstance().getItemData(itemId);
        int monsterId = 0;
        if (MapleDataTool.getInt("info/monsterBook", data, 0) > 0) {
            monsterId = MapleDataTool.getInt("info/mob", data, 0);
        }

		monsterCardCache.put(itemId, monsterId);
		return monsterId;
	}

     public static int getMonsterCardId(int monsterId) {
		if (monsterCardCacheOpposite.containsKey(monsterId)) {
			return monsterCardCacheOpposite.get(monsterId);
		}

		MapleData dataDir = MapleDataProviderFactory.getWzFile("Item.wz").getData("Consume/0238.img");
        for(MapleData data : dataDir.getChildren()) {
            int itemId = Integer.parseInt(data.getName());
            if(!monsterCardCache.containsKey(itemId)) {
                int mobId = MapleDataTool.getInt("info/mob", data, 0);
                monsterCardCache.put(itemId, mobId);
                monsterCardCacheOpposite.put(mobId, itemId);
                if (mobId == monsterId) {
                    return itemId;
                }
            }
        }
        return 0;
	}

    public void calculateLevel() {
        int size = cardMap.size();
        level = MAX_PLAYER_LEVEL;
        for (int i = 1; i < MAX_PLAYER_LEVEL; i++) {
            // We don't calculate for the last level because that includes all values above the second to last level
            if (size < PLAYER_LEVELS[i - 1]) {
                level = i;
                break;
            }
        }
    }

    public List<MonsterCard> getAllCards() {
        return new ArrayList<MonsterCard>(cardMap.values());
    }

    public boolean addCard(int cardId) {
        return addCard(cardId, 1, false);
    }
    
    public boolean addCard(int cardId, int level, boolean initialLoad) {
        if (!cardMap.containsKey(cardId)) {
            if (MapleItemInformationProvider.isSpecialCard(cardId)) {
                specialCount++;
            } else {
                normalCount++;
            }
        }

        if (initialLoad) {
            cardMap.put(cardId, new MonsterCard(cardId, level));
        } else {
            
            MonsterCard card = cardMap.containsKey(cardId) ? cardMap.get(cardId) : new MonsterCard(cardId, 0);
            if (isFull(cardId)) {
                return true;
            }
            card.level++;
            cardMap.put(cardId, card);
            if (card.level == 1) {
                calculateLevel();
            }
            return false;
        }
        return false;
    }

    public void sendAddCardPackets(MonsterCard card, boolean updateCount) {
        player.getClient().getSession().write(MaplePacketCreator.addMonsterCard(card, updateCount));
        if (updateCount) {
            player.getClient().getSession().write(MaplePacketCreator.showOwnObtainedMonsterCard());
            player.getMap().broadcastMessage(player, MaplePacketCreator.showObtainedMonsterCard(player), false);
        }
    }

    public MonsterCard getCard(int cardId) {
        return cardMap.get(cardId);
    }

    public boolean isFull(int cardId) {
        MonsterCard card = cardMap.get(cardId);
        return card != null ? card.isFull() : false;
    }

    public static class MonsterCard {

        private int id;
        private int level;

        public MonsterCard(int id, int level) {
            this.id = id;
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public boolean isFull() {
            return level == MAX_CARD_LEVEL;
        }
    }
}
