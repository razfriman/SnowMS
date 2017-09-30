/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.exttools.wzinfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.tools.PropertyTool;

/**
 *
 * @author Raz
 */
public class NXWatch {

	private Map<Integer, NXItem> oldItems = new HashMap<Integer, NXItem>();
	private Map<Integer, NXItem> newItems = new HashMap<Integer, NXItem>();
	List<Integer> indexSN = new ArrayList<Integer>();
	private Properties settings = new Properties();
	PropertyTool propTool;
	private String oldFilePath;
	private String newFilePath;

	public NXWatch() {
		try {
			settings.load(new FileInputStream(new File("settings.properties")));
		} catch (Exception e) {
			System.out.println("Unable to find settings.properties");
			return;
		}
		propTool = new PropertyTool(settings);
		loadItems(false);
		loadItems(true);
		compareItems();
	}

	public boolean loadItems(boolean newItem) {
		try {
			System.setProperty("net.sf.odinms.wzpath", newItem ? newFilePath : oldFilePath);
			Map<Integer, NXItem> items = newItem ? newItems : oldItems;
			for(MapleData nxItem : MapleDataProviderFactory.getWzFile("Etc.wz").getData("Commodity.img").getChildren()) {
				int sn = MapleDataTool.getInt("SN", nxItem);
				int itemId = MapleDataTool.getInt("ItemId", nxItem);
				int onSale = MapleDataTool.getInt("OnSale", nxItem, 0);
				int price = MapleDataTool.getInt("Price", nxItem, 0);
				int period = MapleDataTool.getInt("Period", nxItem, 0);
				int count = MapleDataTool.getInt("Count", nxItem, 1);
				if (!indexSN.contains(sn)) {
					indexSN.add(sn);
				}
				items.put(sn, new NXItem(sn, itemId, onSale, price, period, count));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void compareItems() {
	Collections.sort(indexSN);
	NXItem newItem;
	NXItem oldItem;
	for(int sn : indexSN) {
		newItem = newItems.get(sn);
		oldItem = oldItems.get(sn);
		if (newItem == null) {
			//Item Removed
		} else if (oldItem == null) {
			//Item Added
		} else if (!newItem.equals(oldItem)) {
			//Item Changed
			if (newItem.getOnSale() != oldItem.getOnSale()) {
				if (newItem.isOnSale()) {
					//Item is now on sale
				} else {
					//Removed from this section
				}
			}
			if (newItem.getPrice() != oldItem.getPrice()) {
				//Price changed from oldItem.getPrice() to newItem.getPrice()
			}
			if (newItem.getPeriod() != oldItem.getPeriod()) {
				//Duration changed from oldItem.getPeriod() to newItem.getPeriod()
			}
			if (newItem.getCount() != oldItem.getCount()) {
				//Quantity changed from oldItem.getCount() to newItem.getCount();
			}
		}
	}
	}

	public static void main(String args[]) {
		new NXWatch();
	}

	public class NXItem {
		private int sn;
		private int itemId;
		private int onSale;
		private int price;
		private int period;
		private int count;

		public NXItem(int sn, int itemId, int onSale, int price, int period, int count) {
			this.sn = sn;
			this.itemId = itemId;
			this.onSale = onSale;
			this.price = price;
			this.period = period;
			this.count = count;
		}

		public int getCount() {
			return count;
		}

		public int getItemId() {
			return itemId;
		}

		public int getOnSale() {
			return onSale;
		}

		public boolean isOnSale() {
			return onSale > 0;
		}

		public int getPeriod() {
			return period;
		}

		public int getPrice() {
			return price;
		}

		public int getSn() {
			return sn;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final NXItem other = (NXItem) obj;
			if (this.sn != other.sn) {
				return false;
			}
			if (this.itemId != other.itemId) {
				return false;
			}
			if (this.onSale != other.onSale) {
				return false;
			}
			if (this.price != other.price) {
				return false;
			}
			if (this.period != other.period) {
				return false;
			}
			if (this.count != other.count) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + this.sn;
			hash = 97 * hash + this.itemId;
			hash = 97 * hash + this.onSale;
			hash = 97 * hash + this.price;
			hash = 97 * hash + this.period;
			hash = 97 * hash + this.count;
			return hash;
		}

	}

}
