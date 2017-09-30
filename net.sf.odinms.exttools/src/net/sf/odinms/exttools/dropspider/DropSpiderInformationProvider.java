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

import java.util.Map;

import net.sf.odinms.client.MapleWeaponType;
import net.sf.odinms.server.MapleItemInformationProvider;

/**
 *
 * @author Matze
 */
public class DropSpiderInformationProvider extends MapleItemInformationProvider {

    private static DropSpiderInformationProvider instance = new DropSpiderInformationProvider();

    private DropSpiderInformationProvider() {

    }

    public static DropSpiderInformationProvider getInstance() {
	return instance;
    }

    public int makeDropChance(int itemId) {
	return classifyItem(itemId).getChance();
    }

    private ItemType classifyMonsterSpoil(int itemId) {
	switch (itemId) {
	    case 4000021: // leather
		return ItemType.UNCOMMON_MONSTERSPOIL;
	    case 4000245:
	    case 4000244:
		return ItemType.RARE_MONSTERSPOIL;

	}
	return ItemType.MONSTERSPOIL;
    }

    public ItemType classifyItem(int itemId) {
	switch (itemId / 10000) {
	    case 400:
		int subCat = (itemId % 1000000 / 1000);
		switch (subCat) {
		    case 1:
			return ItemType.RARE_QUESTITEM;
		    case 3:
			return ItemType.CRAFTING_MATERIAL;
		    case 4:
			return ItemType.RARE_CRYSTAL_ORE;
		    case 6:
			return ItemType.MAGIC_STONE;
		}
		return classifyMonsterSpoil(itemId);
	    case 401:
		return ItemType.MINERAL_ORE;
	    case 402:
		return ItemType.CRYSTAL_ORE;
	    case 403:
		return ItemType.QUEST_ITEM;
	    case 413:
		return ItemType.STIMULATOR;
	    case 416:
		return ItemType.STORY_BOOK;
	    case 200:
		return ItemType.POTION;
	    case 201:
	    case 202:
		return ItemType.FOOD;
	    case 204: // scroll
		Map<String, Integer> equipStats = getEquipStats(itemId);
		switch (equipStats.get("success")) {
		    case 10:
			return ItemType.SCROLL_10;
		    case 30:
			return ItemType.SCROLL_30;
		    case 60:
			return ItemType.SCROLL_60;
		    case 70:
			return ItemType.SCROLL_70;
		    case 100:
			return ItemType.SCROLL_100;
		}
		return ItemType.UNIDENTIFIED_SCROLL;
	    case 205:
		return ItemType.CURING_POTION;
	    case 206:
		return ItemType.ARROW;
	    case 207:
		return ItemType.THROWING_STAR;
	    case 229:
		return ItemType.MASTERY_BOOK;
	}
	if (isEquip(itemId)) {
	    if (isShield(itemId)) {
		return ItemType.SHIELD;
	    }
	    int reqLevel = getEquipStats(itemId).get("reqLevel");
	    if (getWeaponType(itemId) != MapleWeaponType.NOT_A_WEAPON) {
		if (reqLevel >= 90) {
		    return ItemType.WEAPON_90;
		} else if (reqLevel >= 70) {
		    return ItemType.WEAPON_70;
		} else {
		    return ItemType.WEAPON;
		}
	    } else {
		if (reqLevel >= 70) {
		    return ItemType.EQUIP_70;
		} else {
		    return ItemType.EQUIP;
		}
	    }
	}
	//throw new RuntimeException("Encountered unclassified item: " + itemId);
	return null;
    }
}
