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

import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.server.TimerManager;

/**
 *
 * @author Raz
 */
public class MapleMount {

	private int itemId;
	private int skillId;
	private int tiredness;
	private int exp;
	private int level;
	private MapleCharacter owner;
	private ScheduledFuture<?> tirednessSchedule;

	public MapleMount(int itemid, int skillid, MapleCharacter owner) {
		this.itemId = itemid;
		this.skillId = skillid;
		this.owner = owner;
		this.tiredness = 0;
		this.level = 1;
		this.exp = 0;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemid) {
		this.itemId = itemid;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public MapleCharacter getOwner() {
		return owner;
	}

	public void setOwner(MapleCharacter owner) {
		this.owner = owner;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public int getTiredness() {
		return tiredness;
	}

	public void setTiredness(int tiredness) {
		this.tiredness = tiredness;
	}

	public void increaseTiredness() {
		this.tiredness++;
	}

	public void update() {

	}

	public void startTirednessSchedule() {
		this.tirednessSchedule = TimerManager.getInstance().register(new Runnable() {
			public void run() {
				increaseTiredness();
				update();
			}
		}, 60000, 60000);
	}

	public void cancelTirednessSchedule() {
		if (this.tirednessSchedule != null) {
			this.tirednessSchedule.cancel(false);
		}
	}
}
