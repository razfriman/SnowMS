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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleItemMask;
import net.sf.odinms.client.MapleWeaponType;
import net.sf.odinms.client.PetEvolutionInfo;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.Randomizer;
import net.sf.odinms.tools.StringUtil;

/**
 * 
 * @author Matze
 * 
 * TODO: make faster
 * 
 */
public class MapleItemInformationProvider {

	private static MapleItemInformationProvider instance = null;
	protected MapleDataProvider itemData;
	protected MapleDataProvider equipData;
	protected MapleDataProvider stringData;
	protected MapleDataProvider tamingMobData;
	protected Map<Integer, MapleInventoryType> inventoryTypeCache = new HashMap<Integer, MapleInventoryType>();
	protected Map<Integer, Short> slotMaxCache = new HashMap<Integer, Short>();
	protected Map<Integer, MapleStatEffect> itemEffects = new HashMap<Integer, MapleStatEffect>();
	protected Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<Integer, Map<String, Integer>>();
	protected Map<Integer, Equip> equipCache = new HashMap<Integer, Equip>();
	protected Map<Integer, Double> priceCache = new HashMap<Integer, Double>();
	protected Map<Integer, Integer> wholePriceCache = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> projectileWatkCache = new HashMap<Integer, Integer>();
	protected Map<Integer, String> nameCache = new HashMap<Integer, String>();
	protected Map<Integer, String> descCache = new HashMap<Integer, String>();
	protected Map<Integer, String> msgCache = new HashMap<Integer, String>();
	protected Map<Integer, Boolean> dropRestrictionCache = new HashMap<Integer, Boolean>();
	protected Map<Integer, Boolean> pickupRestrictionCache = new HashMap<Integer, Boolean>();
	protected Map<Integer, Boolean> expireOnLogoutCache = new HashMap<Integer, Boolean>();
	protected Map<Integer, Boolean> consumeOnPickupCache = new HashMap<Integer, Boolean>();
	protected Map<Integer, Boolean> runOnPickupCache = new HashMap<Integer, Boolean>();
	protected Map<Integer, Integer> giveMesoCache = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> mountIdCache = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> summonMobCache = new HashMap<Integer, Integer>();//Implement somehow?
	protected Map<Integer, Integer> createItemCache = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> mobActivateIdCache = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> mobActivateHPCache = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer> mcTypeCache = new HashMap<Integer, Integer>();
	protected List<Pair<Integer, String>> itemNameCache = new ArrayList<Pair<Integer, String>>();
	protected Map<Integer, PetEvolutionInfo> petEvolutionCache = new HashMap<Integer, PetEvolutionInfo>();
	protected Map<Integer, Pair<Double, Double>> mountModCache = new HashMap<Integer, Pair<Double, Double>>();
	protected Map<Integer, Integer> scriptedItemNpcCache = new HashMap<Integer, Integer>();
	protected Map<Integer, String> scriptedItemScriptCache = new HashMap<Integer, String>();

	protected MapleItemInformationProvider() {
		itemData = MapleDataProviderFactory.getWzFile("Item.wz");
		equipData = MapleDataProviderFactory.getWzFile("Character.wz");
		stringData = MapleDataProviderFactory.getWzFile("String.wz");
		tamingMobData = MapleDataProviderFactory.getWzFile("TamingMob.wz");
	}

	public static MapleItemInformationProvider getInstance() {
		if (instance == null) {
			instance = new MapleItemInformationProvider();
		}
		return instance;
	}

	/* returns the inventory type for the specified item id */
	public MapleInventoryType getInventoryType(int itemId) {
		if (inventoryTypeCache.containsKey(itemId)) {
			return inventoryTypeCache.get(itemId);
		}
		MapleInventoryType ret;
		String idStr = "0" + String.valueOf(itemId);
		// first look in items...
		MapleDataDirectoryEntry root = itemData.getRoot();
		for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
			// we should have .img files here beginning with the first 4 IID
			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
					ret = MapleInventoryType.getByWZName(topDir.getName());
					inventoryTypeCache.put(itemId, ret);
					return ret;
				} else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
					ret = MapleInventoryType.getByWZName(topDir.getName());
					inventoryTypeCache.put(itemId, ret);
					return ret;
				}
			}
		}
		// not found? maybe its equip...
		root = equipData.getRoot();
		for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				if (iFile.getName().equals(idStr + ".img")) {
					ret = MapleInventoryType.EQUIP;
					inventoryTypeCache.put(itemId, ret);
					return ret;
				}
			}
		}
		ret = MapleInventoryType.UNDEFINED;
		inventoryTypeCache.put(itemId, ret);
		return ret;
	}

	public List<Pair<Integer, String>> getAllItems() {
		if (itemNameCache.size() != 0) {
			return itemNameCache;
		}
		List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
		MapleData itemsData;

		itemsData = stringData.getData("Cash.img");
		for (MapleData itemFolder : itemsData.getChildren()) {
			int itemId = Integer.parseInt(itemFolder.getName());
			String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
			itemPairs.add(new Pair<Integer, String>(itemId, itemName));
		}

		itemsData = stringData.getData("Consume.img");
		for (MapleData itemFolder : itemsData.getChildren()) {
			int itemId = Integer.parseInt(itemFolder.getName());
			String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
			itemPairs.add(new Pair<Integer, String>(itemId, itemName));
		}

		itemsData = stringData.getData("Eqp.img").getChildByPath("Eqp");
		for (MapleData eqpType : itemsData.getChildren()) {
			for (MapleData itemFolder : eqpType.getChildren()) {
				int itemId = Integer.parseInt(itemFolder.getName());
				String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
				itemPairs.add(new Pair<Integer, String>(itemId, itemName));
			}
		}

		itemsData = stringData.getData("Etc.img").getChildByPath("Etc");
		for (MapleData itemFolder : itemsData.getChildren()) {
			int itemId = Integer.parseInt(itemFolder.getName());
			String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
			itemPairs.add(new Pair<Integer, String>(itemId, itemName));
		}

		itemsData = stringData.getData("Ins.img");
		for (MapleData itemFolder : itemsData.getChildren()) {
			int itemId = Integer.parseInt(itemFolder.getName());
			String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
			itemPairs.add(new Pair<Integer, String>(itemId, itemName));
		}

		itemsData = stringData.getData("Pet.img");
		for (MapleData itemFolder : itemsData.getChildren()) {
			int itemId = Integer.parseInt(itemFolder.getName());
			String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
			itemPairs.add(new Pair<Integer, String>(itemId, itemName));
		}
		return itemPairs;
	}

	public MapleData getStringData(int itemId) {
		String type;
		String cat;
		if (itemId >= 5010000) {
			type = "Cash.img";
			cat = "";
		} else if (itemId >= 2000000 && itemId < 3000000) {
			type = "Consume.img";
			cat = "";
		} else if (itemId >= 1010000 && itemId < 1040000 || itemId >= 1122000 && itemId < 1123000) {
			type = "Eqp.img";
			cat = "Eqp/Accessory";
		} else if (itemId >= 1000000 && itemId < 1010000) {
			type = "Eqp.img";
			cat = "Eqp/Cap";
		} else if (itemId >= 1102000 && itemId < 1103000) {
			type = "Eqp.img";
			cat = "Eqp/Cape";
		} else if (itemId >= 1040000 && itemId < 1050000) {
			type = "Eqp.img";
			cat = "Eqp/Coat";
		} else if (itemId >= 20000 && itemId < 22000) {
			type = "Eqp.img";
			cat = "Eqp/Face";
		} else if (itemId >= 1080000 && itemId < 1090000) {
			type = "Eqp.img";
			cat = "Eqp/Glove";
		} else if (itemId >= 30000 && itemId < 32000) {
			type = "Eqp.img";
			cat = "Eqp/Hair";
		} else if (itemId >= 1050000 && itemId < 1060000) {
			type = "Eqp.img";
			cat = "Eqp/Longcoat";
		} else if (itemId >= 1060000 && itemId < 1070000) {
			type = "Eqp.img";
			cat = "Eqp/Pants";
		} else if (itemId >= 1802000 && itemId < 1810000) {
			type = "Eqp.img";
			cat = "Eqp/PetEquip";
		} else if (itemId >= 1112000 && itemId < 1120000) {
			type = "Eqp.img";
			cat = "Eqp/Ring";
		} else if (itemId >= 1092000 && itemId < 1100000) {
			type = "Eqp.img";
			cat = "Eqp/Shield";
		} else if (itemId >= 1070000 && itemId < 1080000) {
			type = "Eqp.img";
			cat = "Eqp/Shoes";
		} else if (itemId >= 1900000 && itemId < 2000000) {
			type = "Eqp.img";
			cat = "Eqp/Taming";
		} else if (itemId >= 1300000 && itemId < 1800000) {
			type = "Eqp.img";
			cat = "Eqp/Weapon";
		} else if (itemId >= 4000000 && itemId < 5000000) {
			type = "Etc.img";
			cat = "Etc";
		} else if (itemId >= 3000000 && itemId < 4000000) {
			type = "Ins.img";
			cat = "";
		} else if (itemId >= 5000000 && itemId < 5010000) {
			type = "Pet.img";
			cat = "";
		} else {
			return null;
		}
		if (!cat.equals("")) {
			cat += "/";
		}
		return stringData.getData(type).getChildByPath(cat + itemId);
	}

	public MapleData getItemData(int itemId) {
		MapleData ret = null;
		String idStr = "0" + String.valueOf(itemId);
		MapleDataDirectoryEntry root = itemData.getRoot();
		for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
			// we should have .img files here beginning with the first 4 IID
			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
					ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
					if (ret == null) {
						return null;
					}
					ret = ret.getChildByPath(idStr);
					return ret;
				} else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
					return itemData.getData(topDir.getName() + "/" + iFile.getName());
				}
			}
		}
		root = equipData.getRoot();
		for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
			for (MapleDataFileEntry iFile : topDir.getFiles()) {
				if (iFile.getName().equals(idStr + ".img")) {
					return equipData.getData(topDir.getName() + "/" + iFile.getName());
				}
			}
		}
		return ret;
	}

	/** returns the maximum of items in one slot */
	public short getSlotMax(int itemId) {
		if (slotMaxCache.containsKey(itemId)) {
			return slotMaxCache.get(itemId);
		}
		short ret = 0;
		MapleData item = getItemData(itemId);
		if (item != null) {
			MapleData smEntry = item.getChildByPath("info/slotMax");
			if (smEntry == null) {
				if (getInventoryType(itemId).getType() == MapleInventoryType.EQUIP.getType()) {
					ret = 1;
				} else {
					ret = 100;
				}
			} else {
				if (isRechargable(itemId)) {
					ret = 1;
				} else if (MapleDataTool.getInt(smEntry) == 0) {
					ret = 1;
				}
				ret = (short) MapleDataTool.getInt(smEntry);
			}
		}
		slotMaxCache.put(itemId, ret);
		return ret;
	}

	public int getWholePrice(int itemId) {
		if (wholePriceCache.containsKey(itemId)) {
			return wholePriceCache.get(itemId);
		}
		MapleData item = getItemData(itemId);
		if (item == null) {
			return -1;
		}

		int pEntry = 0;
		MapleData pData = item.getChildByPath("info/price");
		if (pData == null) {
			return -1;
		}
		pEntry = MapleDataTool.getInt(pData);

		wholePriceCache.put(itemId, pEntry);
		return pEntry;
	}

	public double getPrice(int itemId) {
		if (priceCache.containsKey(itemId)) {
			return priceCache.get(itemId);
		}
		MapleData item = getItemData(itemId);
		if (item == null) {
			return -1;
		}

		//TODO ULTRAHACK - prevent players gaining miriads of mesars with orbis/eos scrolls
		if (itemId == 4001019 || itemId == 4001020) {
			return 0;
		}

		double pEntry = 0.0;
		MapleData pData = item.getChildByPath("info/unitPrice");
		if (pData != null) {
			try {
				pEntry = MapleDataTool.getDouble(pData);
			} catch (Exception e) {
				pEntry = (double) MapleDataTool.getInt(pData);
			}
		} else {
			pData = item.getChildByPath("info/price");
			if (pData == null) {
				return -1;
			}
			pEntry = (double) MapleDataTool.getInt(pData);
		}

		priceCache.put(itemId, pEntry);
		return pEntry;
	}

	protected Map<String, Integer> getEquipStats(int itemId) {
		if (equipStatsCache.containsKey(itemId)) {
			return equipStatsCache.get(itemId);
		}
		Map<String, Integer> ret = new LinkedHashMap<String, Integer>();
		MapleData item = getItemData(itemId);
		if (item == null) {
			return null;
		}
		MapleData info = item.getChildByPath("info");
		if (info == null) {
			return null;
		}
		for (MapleData data : info.getChildren()) {
			if (data.getName().startsWith("inc")) {
				ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
			}
		}
		ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
		ret.put("reqLevel", MapleDataTool.getIntConvert("reqLevel", info, 0));
		ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
		ret.put("success", MapleDataTool.getInt("success", info, 0));
		equipStatsCache.put(itemId, ret);
		return ret;
	}

	public int getReqLevel(int itemId) {
		final Integer req = getEquipStats(itemId).get("reqLevel");
		return req == null ? 0 : req.intValue();
	}

	public List<Integer> getScrollReqs(int itemId) {
		List<Integer> ret = new ArrayList<Integer>();
		MapleData data = getItemData(itemId);
		data = data.getChildByPath("req");
		if (data == null) {
			return ret;
		}
		for (MapleData req : data.getChildren()) {
			ret.add(MapleDataTool.getInt(req));
		}
		return ret;
	}

    public static boolean isMasterLevelSkill(int skillId) {
        int root = skillId % 100;
        return root % 10 == 2;
    }

	public static MapleWeaponType getWeaponType(int itemId) {
		int cat = itemId / 10000;
		cat = cat % 100;
		switch (cat) {
			case 30:
				return MapleWeaponType.SWORD1H;
			case 31:
				return MapleWeaponType.AXE1H;
			case 32:
				return MapleWeaponType.BLUNT1H;
			case 33:
				return MapleWeaponType.DAGGER;
			case 37:
				return MapleWeaponType.WAND;
			case 38:
				return MapleWeaponType.STAFF;
			case 40:
				return MapleWeaponType.SWORD2H;
			case 41:
				return MapleWeaponType.AXE2H;
			case 42:
				return MapleWeaponType.BLUNT2H;
			case 43:
				return MapleWeaponType.SPEAR;
			case 44:
				return MapleWeaponType.POLE_ARM;
			case 45:
				return MapleWeaponType.BOW;
			case 46:
				return MapleWeaponType.CROSSBOW;
			case 47:
				return MapleWeaponType.CLAW;
			case 48:
				return MapleWeaponType.KNUCKLE;
			case 49:
				return MapleWeaponType.GUN;

		}
		return MapleWeaponType.NOT_A_WEAPON;
	}

	public static boolean isShield(int itemId) {
		int cat = itemId / 10000;
		cat = cat % 100;
		return cat == 9;
	}

	public static int getInventory(int itemId) {
		return itemId / 1000000;
	}

	public static boolean isEquip(int itemId) {
		return getInventory(itemId) == 1;
	}

	public IItem scrollEquipWithId(IItem equip, int scrollId, boolean usingWhiteScroll) {
		if (equip instanceof Equip) {
			Equip nEquip = (Equip) equip;
			Map<String, Integer> stats = this.getEquipStats(scrollId);
			Map<String, Integer> eqstats = this.getEquipStats(equip.getItemId());
			boolean success = Math.ceil(Math.random() * 100.0) <= stats.get("success");
			if ((nEquip.getUpgradeSlots() > 0 || isCleanSlate(scrollId) || isEquipMask(scrollId)) && success) {
				switch (scrollId) {
					case 2049000:
					case 2049001:
					case 2049002:
					case 2049003:
						if (nEquip.getUpgradeSlots() <= eqstats.get("tuc") && nEquip.getLevel() != eqstats.get("tuc")) {
							byte newSlots = (byte) (nEquip.getUpgradeSlots() + 1);
							nEquip.setUpgradeSlots(newSlots);
							return equip;
						}
						break;
					case 2040727://Shoe Spikes
						nEquip.addMask(MapleItemMask.SPIKES);
						break;
					case 2041058://Cold Protection
						nEquip.addMask(MapleItemMask.COLD);
						break;
					case 2049100:
						int increase = 1;
						if (Math.ceil(Math.random() * 100.0) <= 50) {
							increase = increase * -1;
						}
						if (nEquip.getStr() > 0) {
							short newStat = (short) (nEquip.getStr() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setStr(newStat);
						}
						if (nEquip.getDex() > 0) {
							short newStat = (short) (nEquip.getDex() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setDex(newStat);
						}
						if (nEquip.getInt() > 0) {
							short newStat = (short) (nEquip.getInt() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setInt(newStat);
						}
						if (nEquip.getLuk() > 0) {
							short newStat = (short) (nEquip.getLuk() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setLuk(newStat);
						}
						if (nEquip.getWatk() > 0) {
							short newStat = (short) (nEquip.getWatk() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setWatk(newStat);
						}
						if (nEquip.getWdef() > 0) {
							short newStat = (short) (nEquip.getWdef() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setWdef(newStat);
						}
						if (nEquip.getMatk() > 0) {
							short newStat = (short) (nEquip.getMatk() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setMatk(newStat);
						}
						if (nEquip.getMdef() > 0) {
							short newStat = (short) (nEquip.getMdef() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setMdef(newStat);
						}
						if (nEquip.getAcc() > 0) {
							short newStat = (short) (nEquip.getAcc() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setAcc(newStat);
						}
						if (nEquip.getAvoid() > 0) {
							short newStat = (short) (nEquip.getAvoid() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setAvoid(newStat);
						}
						if (nEquip.getSpeed() > 0) {
							short newStat = (short) (nEquip.getSpeed() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setSpeed(newStat);
						}
						if (nEquip.getJump() > 0) {
							short newStat = (short) (nEquip.getJump() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setJump(newStat);
						}
						if (nEquip.getHp() > 0) {
							short newStat = (short) (nEquip.getHp() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setHp(newStat);
						}
						if (nEquip.getMp() > 0) {
							short newStat = (short) (nEquip.getMp() + Math.ceil(Math.random() * 5.0) * increase);
							nEquip.setStr(newStat);
						}
						break;
					default:
						for (Entry<String, Integer> stat : stats.entrySet()) {
							if (stat.getKey().equals("STR")) {
								nEquip.setStr((short) (nEquip.getStr() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("DEX")) {
								nEquip.setDex((short) (nEquip.getDex() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("INT")) {
								nEquip.setInt((short) (nEquip.getInt() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("LUK")) {
								nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("PAD")) {
								nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("PDD")) {
								nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("MAD")) {
								nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("MDD")) {
								nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("ACC")) {
								nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("EVA")) {
								nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("Speed")) {
								nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("Jump")) {
								nEquip.setJump((short) (nEquip.getJump() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("MHP")) {
								nEquip.setHp((short) (nEquip.getHp() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("MMP")) {
								nEquip.setMp((short) (nEquip.getMp() + stat.getValue().intValue()));
							} else if (stat.getKey().equals("afterImage")) {
							}
						}
						break;
				}
				nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
				nEquip.setLevel((byte) (nEquip.getLevel() + 1));
			} else {
				if (!usingWhiteScroll) {
					nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
				}
				if (Math.ceil(1.0 + Math.random() * 100.0) < stats.get("cursed")) {
					// DESTROY :) (O.O!)
					return null;
				}
			}
		}
		return equip;
	}

	public IItem getEquipById(int equipId) {
		return getEquipById(equipId, -1);
	}

	public IItem getEquipById(int equipId, int ringId) {
		if (equipCache.containsKey(equipId)) {
			return equipCache.get(equipId).copy();
		}
		Equip nEquip = new Equip(equipId, (byte) 0, ringId);
		nEquip.setQuantity((short) 1);
		Map<String, Integer> stats = this.getEquipStats(equipId);
		if (stats != null) {
			for (Entry<String, Integer> stat : stats.entrySet()) {
				if (stat.getKey().equals("STR")) {
					nEquip.setStr((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("DEX")) {
					nEquip.setDex((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("INT")) {
					nEquip.setInt((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("LUK")) {
					nEquip.setLuk((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("PAD")) {
					nEquip.setWatk((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("PDD")) {
					nEquip.setWdef((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("MAD")) {
					nEquip.setMatk((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("MDD")) {
					nEquip.setMdef((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("ACC")) {
					nEquip.setAcc((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("EVA")) {
					nEquip.setAvoid((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("Speed")) {
					nEquip.setSpeed((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("Jump")) {
					nEquip.setJump((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("MHP")) {
					nEquip.setHp((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("MMP")) {
					nEquip.setMp((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("tuc")) {
					nEquip.setUpgradeSlots((byte) stat.getValue().intValue());
				} else if (stat.getKey().equals("attackSpeed")) {
					nEquip.setAttackSpeed((short) stat.getValue().intValue());
				} else if (stat.getKey().equals("afterImage")) {
				}
			}
		}
		equipCache.put(equipId, nEquip);
		return nEquip.copy();
	}

	private short getRandStat(short defaultValue, int maxRange) {
		if (defaultValue == 0) {
			return 0;
		}

		// vary no more than ceil of 10% of stat
		int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);
		return (short) ((defaultValue - lMaxRange) + Math.floor(Randomizer.randomDouble() * (lMaxRange * 2 + 1)));
	}

	public Equip randomizeStats(Equip equip) {
		equip.setStr(getRandStat(equip.getStr(), 5));
		equip.setDex(getRandStat(equip.getDex(), 5));
		equip.setInt(getRandStat(equip.getInt(), 5));
		equip.setLuk(getRandStat(equip.getLuk(), 5));
		equip.setMatk(getRandStat(equip.getMatk(), 5));
		equip.setWatk(getRandStat(equip.getWatk(), 5));
		equip.setAcc(getRandStat(equip.getAcc(), 5));
		equip.setAvoid(getRandStat(equip.getAvoid(), 5));
		equip.setJump(getRandStat(equip.getJump(), 5));
		equip.setSpeed(getRandStat(equip.getSpeed(), 5));
		equip.setWdef(getRandStat(equip.getWdef(), 10));
		equip.setMdef(getRandStat(equip.getMdef(), 10));
		equip.setHp(getRandStat(equip.getHp(), 10));
		equip.setMp(getRandStat(equip.getMp(), 10));
		return equip;
	}

	public MapleStatEffect getItemEffect(int itemId) {
		MapleStatEffect ret = itemEffects.get(Integer.valueOf(itemId));
		if (ret == null) {
			MapleData item = getItemData(itemId);
			if (item == null) {
				return null;
			}
			MapleData spec = item.getChildByPath("spec");
			ret = MapleStatEffect.loadItemEffectFromData(spec, itemId);
			itemEffects.put(Integer.valueOf(itemId), ret);
		}
		return ret;
	}

	public static boolean isThrowingStar(int itemId) {
		return itemId >= 2070000 && itemId < 2080000;
	}

	public static boolean isBullet(int itemId) {
		return itemId >= 2330000 && itemId < 2340000;
	}

	public static boolean isRechargable(int itemId) {
		return isBullet(itemId) || isThrowingStar(itemId);
	}

	public static boolean isOverall(int itemId) {
		return itemId >= 1050000 && itemId < 1060000;
	}

	public static boolean isArrowForCrossBow(int itemId) {
		return itemId >= 2061000 && itemId < 2062000;
	}

	public static boolean isArrowForBow(int itemId) {
		return itemId >= 2060000 && itemId < 2061000;
	}

	public static boolean isPet(int itemId) {
		return itemId >= 5000000 && itemId <= 5000100;
	}

	public static boolean isExpCard(int itemId) {
		return itemId >= 5210000 && itemId <= 5211002;
	}

    public static int getItemType(int itemId) {
        return itemId / 10000;
    }

    public static boolean isMonsterCard(int itemId) {
        return getItemType(itemId) == 238;
    }

    public static int getCardId(int itemId) {
        return itemId % 10000;
    }

    public static boolean isSpecialCard(int itemId) {
        return getCardId(itemId) >= 8000;
    }

	public static boolean isTwoHanded(int itemId) {
		switch (getWeaponType(itemId)) {
			case AXE2H:
				return true;
			case BLUNT2H:
				return true;
			case BOW:
				return true;
			case CLAW:
				return true;
			case CROSSBOW:
				return true;
			case POLE_ARM:
				return true;
			case SPEAR:
				return true;
			case SWORD2H:
				return true;
			case GUN:
                return true;
            case KNUCKLE:
                return true;
			default:
				return false;
		}
	}

	public static boolean isCleanSlate(int scrollId) {
		switch (scrollId) {
			case 2049000:
			case 2049001:
			case 2049002:
			case 2049003:
				return true;
			default:
				return false;
		}
	}

	public static boolean isEquipMask(int scrollId) {
		switch (scrollId) {
			case 2040727:
			case 2041058:
				return true;
			default:
				return false;
		}
	}

	public List<Pair<Integer, Integer>> getSummonMobs(int itemId) {
		MapleData data = getItemData(itemId);
		List<Pair<Integer, Integer>> summonMobs = new ArrayList<Pair<Integer, Integer>>();
		if (data.getChildByPath("mob") != null) {
			for (MapleData spawnD : data.getChildByPath("mob").getChildren()) {
				int id = MapleDataTool.getIntConvert("id", spawnD, -1);
				int prob = MapleDataTool.getIntConvert("prob", spawnD, -1);
				summonMobs.add(new Pair<Integer, Integer>(id, prob));
			}
		}
		return summonMobs;
	}

	public List<Integer> petsCanConsume(int itemId) {
		List<Integer> ret = new ArrayList<Integer>();
		MapleData data = getItemData(itemId);
		int curPetId = 0;
		for (int i = 0; i < 100; i++) {
			curPetId = MapleDataTool.getInt("spec/" + Integer.toString(i), data, 0);
			if (curPetId == 0) {
				break;
			}
			ret.add(Integer.valueOf(curPetId));
		}
		return ret;
	}

	public int getWatkForProjectile(int itemId) {
		Integer atk = projectileWatkCache.get(itemId);
		if (atk != null) {
			return atk.intValue();
		}
		MapleData data = getItemData(itemId);
		atk = Integer.valueOf(MapleDataTool.getInt("info/incPAD", data, 0));
		projectileWatkCache.put(itemId, atk);
		return atk.intValue();
	}

	public boolean canScroll(int scrollid, int itemid) {
		int scrollCategoryQualifier = (scrollid / 100) % 100;
		int itemCategoryQualifier = (itemid / 10000) % 100;
		return scrollCategoryQualifier == itemCategoryQualifier;
	}

	public String getName(int itemId) {
		if (nameCache.containsKey(itemId)) {
			return nameCache.get(itemId);
		}
		MapleData strings = getStringData(itemId);
		if (strings == null) {
			return null;
		}
		String ret = MapleDataTool.getString("name", strings, null);
		nameCache.put(itemId, ret);
		return ret;
	}

	public String getDesc(int itemId) {
		if (descCache.containsKey(itemId)) {
			return descCache.get(itemId);
		}
		MapleData strings = getStringData(itemId);
		if (strings == null) {
			return null;
		}
		String ret = MapleDataTool.getString("desc", strings, null);
		descCache.put(itemId, ret);
		return ret;
	}

	public String getMsg(int itemId) {
		if (msgCache.containsKey(itemId)) {
			return msgCache.get(itemId);
		}
		MapleData strings = getStringData(itemId);
		if (strings == null) {
			return null;
		}
		String ret = MapleDataTool.getString("msg", strings, null);
		msgCache.put(itemId, ret);
		return ret;
	}

	public boolean isDropRestricted(int itemId) {
		if (dropRestrictionCache.containsKey(itemId)) {
			return dropRestrictionCache.get(itemId);
		}

		MapleData data = getItemData(itemId);

		boolean bRestricted = MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1;
		if (!bRestricted) {
			bRestricted = MapleDataTool.getIntConvert("info/quest", data, 0) == 1;
		}
		dropRestrictionCache.put(itemId, bRestricted);

		return bRestricted;
	}

	public boolean isPickupRestricted(int itemId) {
		if (pickupRestrictionCache.containsKey(itemId)) {
			return pickupRestrictionCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		boolean bRestricted = MapleDataTool.getIntConvert("info/only", data, 0) == 1;

		pickupRestrictionCache.put(itemId, bRestricted);
		return bRestricted;
	}

	public boolean isExpireOnLogout(int itemId) {
		if (expireOnLogoutCache.containsKey(itemId)) {
			return expireOnLogoutCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		boolean expire = MapleDataTool.getIntConvert("info/expireOnLogout", data, 0) == 1;

		expireOnLogoutCache.put(itemId, expire);
		return expire;
	}

	public boolean isConsumeOnPickup(int itemId) {
		if (consumeOnPickupCache.containsKey(itemId)) {
			return consumeOnPickupCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		boolean consume = MapleDataTool.getIntConvert("spec/consumeOnPickup", data, 0) == 1;
		if (!consume && data.getChildByPath("specEx") != null) {
			consume = MapleDataTool.getIntConvert("specEx/consumeOnPickup", data, 0) == 1;
		}

		consumeOnPickupCache.put(itemId, consume);
		return consume;
	}

	public boolean isRunOnPickup(int itemId) {
		if (runOnPickupCache.containsKey(itemId)) {
			return runOnPickupCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		boolean consume = MapleDataTool.getIntConvert("spec/runOnPickup", data, 0) == 1;
		if (!consume && data.getChildByPath("specEx") != null) {
			consume = MapleDataTool.getIntConvert("specEx/runOnPickup", data, 0) == 1;
		}

		runOnPickupCache.put(itemId, consume);
		return consume;
	}

	public int getGiveMesos(int itemId) {
		if (giveMesoCache.containsKey(itemId)) {
			return giveMesoCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		int mesos = MapleDataTool.getIntConvert("info/meso", data, 0);

		giveMesoCache.put(itemId, mesos);
		return mesos;
	}

	public int getMountId(int itemId) {
		if (mountIdCache.containsKey(itemId)) {
			return mountIdCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		int id = MapleDataTool.getInt("info/tamingMob", data, -1);

		mountIdCache.put(itemId, id);
		return id;
	}

	public int getCreateItem(int itemId) {
		if (createItemCache.containsKey(itemId)) {
			return createItemCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		int id = MapleDataTool.getInt("info/create", data, -1);

		createItemCache.put(itemId, id);
		return id;
	}

	public int getMobActivateId(int itemId) {
		if (mobActivateIdCache.containsKey(itemId)) {
			return mobActivateIdCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		int id = MapleDataTool.getInt("info/mob", data, -1);

		mobActivateIdCache.put(itemId, id);
		return id;
	}

	public int getMobActivateHP(int itemId) {
		if (mobActivateHPCache.containsKey(itemId)) {
			return mobActivateHPCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		int id = MapleDataTool.getInt("info/mobHP", data, 0);

		mobActivateHPCache.put(itemId, id);
		return id;
	}

	public int getMcType(int itemId) {
		if (mcTypeCache.containsKey(itemId)) {
			return mcTypeCache.get(itemId);
		}

		MapleData data = getItemData(itemId);
		int type = MapleDataTool.getInt("info/mcType", data, 0);

		mcTypeCache.put(itemId, type);
		return type;
	}

	public Pair<Double, Double> getMountModData(int mountId) {
		if (mountModCache.containsKey(mountId)) {
			return mountModCache.get(mountId);
		}

		MapleData data = tamingMobData.getData(StringUtil.getLeftPaddedStr(Integer.toString(mountId), '0', 4) + ".img").getChildByPath("info");
		int speed = MapleDataTool.getInt("speed", data);
		int jump = MapleDataTool.getInt("jump", data);

		Pair<Double, Double> mountModData = new Pair<Double, Double>((double) speed / 100.0, (double) jump / 100.0);
		mountModCache.put(mountId, mountModData);
		return mountModData;
	}

	public Pair<Boolean, Integer> getExpCardTime(int itemId) {
		if (!isExpCard(itemId)) {
			return null;
		}

		int rate = 1;
		boolean active = false;
		List<String> times = new ArrayList<String>();
		Calendar today = Calendar.getInstance();
		MapleData data = getItemData(itemId);

		rate = MapleDataTool.getIntConvert("rate", data, 1);
		for (MapleData days : data.getChildByPath("time").getChildren()) {
			times.add(MapleDataTool.getString(days));
		}
		//TODO take 'HOL' into account. whens HOL? christmas?
		for (String time : times) {
			time.replace(':', '-');
			String[] timeSet = time.split("-");
			String day = today.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.ALL_STYLES, Locale.ENGLISH);
			day = day.toUpperCase();
			day = day.substring(0, 3);
			int start = Integer.parseInt(timeSet[1]);
			int end = Integer.parseInt(timeSet[2]);
			if (day.equals(timeSet[0]) && today.get(Calendar.HOUR_OF_DAY) >= start && today.get(Calendar.HOUR_OF_DAY) <= end) {
				active = true;
			}
		}
		return new Pair<Boolean, Integer>(active, rate);
	}

	public PetEvolutionInfo getPetEvolutionInfo(int itemId) {

		if (this.petEvolutionCache.containsKey(itemId)) {
			return petEvolutionCache.get(itemId);
		}
		PetEvolutionInfo evoInfo = new PetEvolutionInfo();
		MapleData data = getItemData(itemId);

		if (MapleDataTool.getInt("evol", data, 0) < 1) {
			return null;
		}
		evoInfo.setEvolveNo(MapleDataTool.getInt("evolveNo", data, 0));
		evoInfo.setReqItemId(MapleDataTool.getInt("evolReqItemID", data, 0));
		for (int i = 1; i <= evoInfo.getEvolveNo(); i++) {
			int evolve = MapleDataTool.getInt("evol" + i, data, 0);
			int prob = MapleDataTool.getInt("evolProb" + i, data, 0);
			evoInfo.addEvolve(new Pair<Integer, Integer>(evolve, prob));
		}

		petEvolutionCache.put(itemId, evoInfo);
		return evoInfo;
	}

    public void spawnMonsterBag(MapleCharacter chr, int itemId, Point position) {
         for (Pair<Integer, Integer> mobPair : getSummonMobs(itemId)) {
                int rand = Randomizer.randomInt(100);
                int prob = mobPair.getRight();
                if (rand <= prob) {//PASS
                    MapleMonster mob = MapleLifeFactory.getMonster(mobPair.getLeft());
                    chr.getMap().spawnMonsterOnGroundBelow(mob, position);
                }
            }
    }

    public int getScriptedItemNpc(int itemId) {
	  if (scriptedItemNpcCache.containsKey(itemId)) {
		return scriptedItemNpcCache.get(itemId);
	  }
	  MapleData data = getItemData(itemId);
	  int npcId = MapleDataTool.getInt("spec/npc", data, 0);
	  scriptedItemNpcCache.put(itemId, npcId);
	  return scriptedItemNpcCache.get(itemId);
    }

    public String getScriptedItemScript(int itemId) {
	  if (scriptedItemScriptCache.containsKey(itemId)) {
		return scriptedItemScriptCache.get(itemId);
	  }
	  MapleData data = getItemData(itemId);
	  String script = MapleDataTool.getString("spec/script", data);
	  scriptedItemScriptCache.put(itemId, script);
	  return scriptedItemScriptCache.get(itemId);
    }
}
