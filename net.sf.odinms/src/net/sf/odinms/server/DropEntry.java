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

package net.sf.odinms.server;

/**
 *
 * @author Raz
 */
public class DropEntry {

	private int itemId;
	private int questId;
	private int chance;
	private int amount;
	private int assignedRangeStart;
	private int assignedRangeLength;

	public DropEntry(int itemId, int questId, int chance, int amount) {
		this.itemId = itemId;
		this.questId = questId;
		this.chance = chance;
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAssignedRangeLength() {
		return assignedRangeLength;
	}

	public void setAssignedRangeLength(int assignedRangeLength) {
		this.assignedRangeLength = assignedRangeLength;
	}

	public int getAssignedRangeStart() {
		return assignedRangeStart;
	}

	public void setAssignedRangeStart(int assignedRangeStart) {
		this.assignedRangeStart = assignedRangeStart;
	}

	public int getChance() {
		return chance;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	@Override
	public String toString() {
		return itemId + " chance: " + chance;
	}
}
