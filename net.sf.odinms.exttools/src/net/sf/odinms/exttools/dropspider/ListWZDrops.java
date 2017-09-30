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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;

public class ListWZDrops {

    private final static String STRING_WZ_NAME = "String.wz";
    private Map<Integer, Integer> itemChance = new HashMap<Integer, Integer>();
    private Map<Integer, Collection<Integer>> drops = new HashMap<Integer, Collection<Integer>>();
    private Collection<Integer> bosses = new HashSet<Integer>();
    private BufferedWriter logStream = null;

    public ListWZDrops() {

    }

    public void loadFromDatabase() throws SQLException {
	Connection con = DatabaseConnection.getConnection();
	PreparedStatement ps = con.prepareStatement("SELECT monsterid,itemid,chance FROM monsterdrops");
	ResultSet rs = ps.executeQuery();
	while (rs.next()) {
	    int monsterId = rs.getInt("monsterid");
	    int itemid = rs.getInt("itemid");
	    int chance = rs.getInt("chance");
	    MapleMonster monster = MapleLifeFactory.getMonster(monsterId);
	    if (monster == null) {
		return;
	    }
	    if (itemChance.containsKey(itemid) && itemChance.get(itemid) != chance && !monster.isBoss()) {
		itemChance.put(itemid, -1);
	    } else if (!monster.isBoss()) {
		itemChance.put(itemid, chance); // an item is dropped with a single chance from all monsters
	    }
	    if (monster.isBoss()) {
		bosses.add(monsterId);
	    }

	    Collection<Integer> monsterDrops = drops.get(monsterId);
	    if (monsterDrops == null) {
		monsterDrops = new LinkedList<Integer>();
		drops.put(monsterId, monsterDrops);
	    }
	    monsterDrops.add(itemid);
	}
    }

    public static String makeBasicMonsterInfo(MapleMonster monster) {
	return monster.getId() + "(" + monster.getName() + ")";
    }

    public void compareToMonsterbook() {
	Map<Integer, Collection<Integer>> monsterBookDrops = new HashMap<Integer, Collection<Integer>>();
	MapleData monsterBook = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath"), STRING_WZ_NAME)).getData("MonsterBook.img");

	for (MapleData data : monsterBook) {
	    int monsterId = Integer.parseInt(data.getName());
	    List<Integer> dropsList = new LinkedList<Integer>();
	    for (MapleData drop : data.getChildByPath("reward")) {
		dropsList.add(MapleDataTool.getInt(drop));
	    }
	    monsterBookDrops.put(monsterId, dropsList);
	}

	int numAdd = 0;
	int numUpdate = 0;
	MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
	DropSpiderInformationProvider dsi = DropSpiderInformationProvider.getInstance();
	for (Entry<Integer, Collection<Integer>> dropEntry : monsterBookDrops.entrySet()) {
	    boolean nextMonster = true;
	    Integer monsterId = dropEntry.getKey();
	    Collection<Integer> bookDrops = dropEntry.getValue();
	    MapleMonster monster = MapleLifeFactory.getMonster(monsterId);
	    Collection<Integer> dbDrops = drops.get(monsterId);
	    if (bookDrops.size() > 0) {
		if (monster != null) {
		    String basicMonsterInfo = makeBasicMonsterInfo(monster);
		    if (dbDrops == null) {
			dbDrops = Collections.emptyList();
		    }
		    for (Integer itemDrop : bookDrops) {
			if (!dbDrops.contains(itemDrop)) {
			    String itemName = mii.getName(itemDrop);
			    if (nextMonster) {
				nextMonster = false;
				outputWithLogging("\n# Adding missing drops for Monster " + basicMonsterInfo + " Boss: " + monster.isBoss());
			    }
			    if (itemName != null) {
				ItemType itemClass = dsi.classifyItem(itemDrop);
				if (itemClass == null) {
				    continue;
				}
				if (itemClass.getChance() >= 0) {
				    Integer oldChance = itemChance.get(itemDrop);
				    int newChance = itemClass.getChance();
				    if (monster.isBoss()) {
					if (newChance > 100) {
					    newChance /= 10;
					}
					if (newChance < 100) {
					    newChance = 100;
					}
				    }
				    outputWithLogging("# Adding missing drop " + itemDrop + " (" + itemName + ") " +
					    itemClass + " newChance: " + newChance + " (old: " + oldChance + ")");
				    if (!monster.isBoss() && !shouldUpdate(itemClass) && oldChance != null &&
					    itemClass.getChance() != oldChance && oldChance != -1) {
					outputWithLogging("# WARNING Adding with a different chance than the old without updating");
				    }
				    if (monster.isBoss() && !bosses.contains(monster.getId())) {
					bosses.add(monster.getId());
				    }
				    outputWithLogging("INSERT INTO monsterdrops (monsterid,itemid,chance) VALUES (" +
					    monster.getId() + ", " + itemDrop + ", " + newChance + ");");
				    numAdd++;
				} else {
				    outputWithLogging("# Skipping  drop " + itemDrop + " (" + itemName + ") " +
					    itemClass + " (chance = -1)");
				}
			    } else {
				outputWithLogging("# Skipping Item " + itemDrop + " not in gms string.wz");
			    }
			}
		    }
		} else {
		    outputWithLogging("# Monster " + monsterId + " not in GMS data");
		}
	    }
	}
	System.out.println();
	StringBuilder notABossBuilder = new StringBuilder("");
	for (int bossId : bosses) {
	    notABossBuilder.append(" AND monsterId != ");
	    notABossBuilder.append(bossId);
	}
	String notABoss = notABossBuilder.toString();
	for (Entry<Integer, Integer> dropChance : itemChance.entrySet()) {
	    Integer itemId = dropChance.getKey();
	    ItemType itemClass = dsi.classifyItem(itemId);
	    if (itemClass != null) {
		if (shouldUpdate(itemClass)) {
		    if (dropChance.getValue() != itemClass.getChance()) {
			outputWithLogging("# Updating chance of " + itemId + "(" + mii.getName(itemId) + ") from " +
				dropChance.getValue() + " to " + itemClass.getChance() + " (" + itemClass + ")");
			outputWithLogging("UPDATE monsterdrops SET chance = " + itemClass.getChance() +
				" WHERE itemid = " + itemId + notABoss + ";");
			numUpdate++;
		    } else if (dropChance.getValue() == -1) {
			outputWithLogging("# Not updating chance of " + itemId + "(" + mii.getName(itemId) +
				") since it's old chance is disamgious");
		    }
		}
	    } else {
		outputWithLogging("# Unclassified item: " + itemId + " (" + mii.getName(itemId) + ") chance: " +
			dropChance.getValue());
	    }
	}
	outputWithLogging("# Totally adding " + numAdd + " drops and updating " + numUpdate + " dropchances");
    }

    private boolean shouldUpdate(ItemType itemClass) {
	switch (itemClass) {
	    case ARROW:
	    case POTION:
	    case FOOD:
	    case UNIDENTIFIED_SCROLL:
	    case SCROLL_10:
	    case SCROLL_30:
	    case SCROLL_60:
	    case SCROLL_70:
	    case SCROLL_100:
	    case WEAPON:
	    case WEAPON_70:
	    case WEAPON_90:
	    case THROWING_STAR:
	    case MAGIC_STONE:
	    case STIMULATOR:
	    case QUEST_ITEM:
	    case CRAFTING_MATERIAL:
	    case SHIELD:
		return true;
	}
	return false;
    }

    private void outputWithLogging(String buff) {
	System.out.println(buff);
	try {
	    logStream.write(buff + "\r\n");
	    logStream.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String args[]) throws SQLException, IOException {
	Properties dbProp = new Properties();
	InputStreamReader is = new FileReader("db.properties");
	dbProp.load(is);
	DatabaseConnection.setProps(dbProp);
	ListWZDrops lister = new ListWZDrops();
	File outputFile = new File("newDrops.sql");
	FileWriter outputFileWriter = new FileWriter(outputFile, !outputFile.createNewFile());
	lister.logStream = new BufferedWriter(outputFileWriter);
	lister.loadFromDatabase();
	lister.compareToMonsterbook();
    }
}
