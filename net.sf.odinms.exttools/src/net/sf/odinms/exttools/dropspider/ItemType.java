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


public enum ItemType {
	WEAPON(7000),
	WEAPON_70(8000),
	WEAPON_90(9000),
	SHIELD(5000),
	UNIDENTIFIED_SCROLL(13000),
	SCROLL_10(8000),
	SCROLL_30(150000),
	SCROLL_60(12000),
	SCROLL_70(8000),
	SCROLL_100(4500),
	THROWING_STAR(10000),
	EQUIP(2400),
	EQUIP_70(3000),
	MONSTERSPOIL(5),
	UNCOMMON_MONSTERSPOIL(200),
	RARE_MONSTERSPOIL(4000),
	CRAFTING_MATERIAL(200),
	MAGIC_STONE(1500),
	ARROW(200),
	CRYSTAL_ORE(600),
	MINERAL_ORE(600),
	RARE_CRYSTAL_ORE(1500),
	POTION(400),
	FOOD(400),
	STIMULATOR(1000),
	QUEST_ITEM(300),
	CURING_POTION(600),
	MASTERY_BOOK(-1),
	STORY_BOOK(-1),
	RARE_QUESTITEM(-1),
	;

	private final int chance;
	
	private ItemType(int chance) {
		this.chance = chance;
	}
	
	public int getChance() {
		return chance;
	}
	
	@Override
	public String toString() {
		return name();
	}
}
