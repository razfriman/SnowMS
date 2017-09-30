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

import java.util.HashMap;
import java.util.Map;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Lerk
 */
public class CashItemFactory {

	private static Map<Integer, Integer> snLookup = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> itemIdLookup = new HashMap<Integer, Integer>();
	private static Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
	private static MapleDataProvider data = MapleDataProviderFactory.getWzFile("Etc.wz");
	private static MapleData commodities = data.getData(StringUtil.getLeftPaddedStr("Commodity.img", '0', 11));

	public static CashItemInfo getItem(int sn) {
		CashItemInfo stats = itemStats.get(sn);
		if (stats == null) {
			int cid = getCommodityFromSN(sn);
			MapleData commodity = commodities.getChildByPath(Integer.toString(cid));
			int serialN = MapleDataTool.getIntConvert("SN", commodity);
			int itemId = MapleDataTool.getIntConvert("ItemId", commodity);
			int count = MapleDataTool.getIntConvert("Count", commodity, 1);
			int price = MapleDataTool.getIntConvert("Price", commodity, 0);
			int period = MapleDataTool.getIntConvert("Period", commodity, 0);
			int priority = MapleDataTool.getIntConvert("Priority", commodity, 0);
			int reqlevel = MapleDataTool.getIntConvert("reqLevel", commodity, 0);
			int gender = MapleDataTool.getIntConvert("Gender", commodity, 0);
			int onsale = MapleDataTool.getIntConvert("OnSale", commodity, 0);

			stats = new CashItemInfo(serialN, itemId, count, price, period, priority, reqlevel, gender, onsale);

			itemStats.put(sn, stats);
		}

		return stats;
	}

	public static CashItemInfo getSn(int itemID) {
		CashItemInfo stats = itemStats.get(itemID);
		if (stats == null) {
			int cid = getCommodityFromItemId(itemID);
			MapleData commodity = commodities.getChildByPath(Integer.toString(cid));
			int serialN = MapleDataTool.getIntConvert("SN", commodity);
			int itemId = MapleDataTool.getIntConvert("ItemId", commodity);
			int count = MapleDataTool.getIntConvert("Count", commodity, 1);
			int price = MapleDataTool.getIntConvert("Price", commodity, 0);
			int period = MapleDataTool.getIntConvert("Period", commodity, 0);
			int priority = MapleDataTool.getIntConvert("Priority", commodity, 0);
			int reqlevel = MapleDataTool.getIntConvert("reqLevel", commodity, 0);
			int gender = MapleDataTool.getIntConvert("Gender", commodity, 0);
			int onsale = MapleDataTool.getIntConvert("OnSale", commodity, 0);

			stats = new CashItemInfo(serialN, itemId, count, price, period, priority, reqlevel, gender, onsale);

			itemStats.put(itemID, stats);
		}

		return stats;
	}

	private static int getCommodityFromSN(int sn) {
		int cid;

		if (snLookup.get(sn) == null) {
			int curr = snLookup.size() - 1;
			int currSN = 0;
			if (curr == -1) {
				curr = 0;
				currSN = MapleDataTool.getIntConvert("0/SN", commodities);
				snLookup.put(currSN, curr);

			}
			for (int i = snLookup.size() - 1; currSN != sn; i++) {
				curr = i;
				currSN = MapleDataTool.getIntConvert(curr + "/SN", commodities);
				snLookup.put(currSN, curr);
			}
			cid = curr;
		} else {
			cid = snLookup.get(sn);
		}
		return cid;
	}

	private static int getCommodityFromItemId(int itemId) {
		int cid;

		if (itemIdLookup.get(itemId) == null) {
			int curr = itemIdLookup.size() - 1;
			int currSN = 0;
			if (curr == -1) {
				curr = 0;
				currSN = MapleDataTool.getIntConvert("0/ItemId", commodities);
				itemIdLookup.put(currSN, curr);

			}
			for (int i = itemIdLookup.size() - 1; currSN != itemId; i++) {
				curr = i;
				currSN = MapleDataTool.getIntConvert(curr + "/ItemId", commodities);
				itemIdLookup.put(currSN, curr);
			}
			cid = curr;
		} else {
			cid = itemIdLookup.get(itemId);
		}
		return cid;
	}

	public static int getSN(int itemid) {
		int ret = 0;
		for (MapleData comod : commodities.getChildren()) {
			int comodItemId = MapleDataTool.getIntConvert("ItemId", comod);
			if (comodItemId == itemid) {
				ret = comodItemId;
				break;
			}
		}
		return ret;
	}
}