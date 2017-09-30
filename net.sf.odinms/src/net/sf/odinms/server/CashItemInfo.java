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
 * @author Lerk
 */
public class CashItemInfo {

	private int sn;
	private int itemId;
	private int count;
	private int price;
	private int period;
	private int priority;
	private int reqlevel;
	private int gender;
	private int onsale;

	public CashItemInfo(int sn, int itemId, int count, int price, int period, int priority, int reqlevel, int gender, int onsale) {
		this.sn = sn;
		this.itemId = itemId;
		this.count = count;
		this.price = price;
		this.period = period;
		this.priority = priority;
		this.reqlevel = reqlevel;
		this.gender = gender;
		this.onsale = onsale;
	}

	public int getSn() {
		return sn;
	}

	public int getId() {
		return itemId;
	}

	public int getCount() {
		return count;
	}

	public int getPrice() {
		return price;
	}

	public int getPeriod() {
		return period;
	}

	public int getPriority() {
		return priority;
	}

	public int getReqLevel() {
		return reqlevel;
	}

	public int getGender() {
		return gender;
	}

	public int getOnSale() {
		return onsale;
	}
}