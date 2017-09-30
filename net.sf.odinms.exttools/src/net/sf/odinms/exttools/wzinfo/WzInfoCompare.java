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

package net.sf.odinms.exttools.wzinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.tools.MapRender;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Raz
 */
public class WzInfoCompare {

	private MapleDataProvider dataProvider = null;
	private MapleData data = null;
	private int oldVer = -1;
	private int newVer = -1;
	private String oldVerPath;
	private String newVerPath;
	private File outputFile = null;
	private PrintWriter logStream = null;
	private WzInfoType infoType = WzInfoType.UNDEFINED;
	private static WzInfoCompare instance = new WzInfoCompare();
	private Properties settings = new Properties();

	public static WzInfoCompare getInstance() {
		return instance;
	}

	private WzInfoCompare() {
	}

	public static void main(String args[]) throws IOException {
		System.out.println("Snow's WzInfo Comparer\r\n");
		WzInfoCompare session = getInstance();
		session.doMain();

	}

	public void doMain() {

		if (!loadProps()) {
			System.out.println("Cannot find settings file.");
			return;
		}
		defineSettings();


		for (WzInfoType singleInfo : WzInfoType.values()) {
			if (singleInfo.getType() > -1) {
				System.out.println("[" + singleInfo.getType() + "] - " + singleInfo.name());
			}
		}
		boolean checkPassed = false;
		byte choice = -1;
		while (!checkPassed) {
			String input = System.console().readLine();
			try {
				//choice = in.nextByte();
				choice = Byte.parseByte(input);
			} catch (Exception e) {
				checkPassed = false;
			}

			if (WzInfoType.getByType(choice) != null && WzInfoType.getByType(choice) != WzInfoType.UNDEFINED) {
				infoType = WzInfoType.getByType(choice);
				checkPassed = true;
			} else {
				System.out.println("Invalid Type");
				checkPassed = false;
			}
		}
		try {

			switch (infoType) {
				case UNDEFINED:
					break;
				case ALL:
					getAll();
					break;
				case ITEM:
					getItems();
					break;
				case JOB:
					getJobs();
					break;
				case MAP:
					getMaps();
					break;
				case MOB:
					getMobs();
					break;
				case NPC:
					getNpcs();
					break;
				case PET:
					getPets();
					break;
				case PORTAL_SCRIPT:
					getPortalScripts();
					break;
				case SKILL:
					getSkills();
					break;
				case COMPARE_ALL:
					getCompareAll();
					break;
				default:
					System.out.println("DEFAULT");
					break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			String text = System.console().readLine();
			String[] splitted = text.split(" ");
			if (splitted[0].equals("restart")) {
				doMain();
			} else if (splitted[0].equals("exit")) {
				System.exit(0);
			} else if (splitted[0].equals("types")) {
				for (WzInfoType singleInfo : WzInfoType.values()) {
					if (singleInfo.getType() > -1) {
						System.out.println("[" + singleInfo.getType() + "] - " + singleInfo.name());
					}
				}
			} else {
				System.out.println("you typed: " + text);//UNKNOWN COMMAND
			}
		}
	}

	public boolean loadProps() {
		try {
			settings.load(new FileInputStream("settings.properties"));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean defineSettings() {
		this.oldVer = getSettingInt("OLD_VER", "-1");
		this.newVer = getSettingInt("NEW_VER", "-1");
		this.oldVerPath = getSettingStr("OLD_VER_PATH", null);
		this.newVerPath = getSettingStr("NEW_VER_PATH", null);

		return true;
	}

	public int getSettingInt(String key, String def) {
		String resStr = settings.getProperty(key, def);
		int resInt = Integer.parseInt(resStr);
		return resInt;
	}

	public String getSettingStr(String key, String def) {
		String resStr = settings.getProperty(key, def);
		return resStr;
	}

	public boolean setProperty(String key, String value, boolean toFile) {
		if (settings != null) {
			settings.setProperty(key, value);
			if (toFile) {
				return updateFile("settings.properties");
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean updateFile(String fileName) {
		try {
			settings.store(new FileOutputStream(fileName), null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void setFileStream(String name, boolean showMessage) {//DO NOT INCLUDE .txt EXTENSION
		if (name != null) {
			try {
				String fileName = name + ".txt";

				outputFile = new File(fileName);
				if (!outputFile.exists()) {
					outputFile.createNewFile();
					if (showMessage) {
						System.out.println("Created File: " + outputFile.getName());
					}
				}
				try {
					logStream = new PrintWriter(outputFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public MapleDataProvider getDataProvider(String wzName, boolean newVer) {
		MapleDataProvider ret = null;
		if (newVer) {
			System.setProperty("net.sf.odinms.wzpath", newVerPath);
			ret = MapleDataProviderFactory.getDataProvider(new File(newVerPath + "/" + wzName));
		} else {
			System.setProperty("net.sf.odinms.wzpath", oldVerPath);
			ret = MapleDataProviderFactory.getDataProvider(new File(oldVerPath + "/" + wzName));
		}
		return ret;
	}

	public void outputWithLogging(String buff) {
		System.out.println(buff);
		if (logStream != null) {
			try {
				logStream.write(buff + "\r\n");
				logStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void outputPairList(List<Pair<Integer, String>> outputPairList) {
		outputPairList(outputPairList, 0);
	}

	public void outputPairList(List<Pair<Integer, String>> outputPairList, int size) {

		for (Pair<Integer, String> outputPair : outputPairList) {
			outputPair(outputPair);
		}
		if (outputPairList.size() == size) {
			outputWithLogging("No changes from Version(" + oldVer + ") to Version(" + newVer + ")");
		}
	}

	public void outputPair(Pair<Integer, String> outputPair) {
		String toOutput = null;
		if (outputPair.getLeft() == null && outputPair.getRight() == null) {
			toOutput = "";
		} else if (outputPair.getLeft() == null) {
			toOutput = outputPair.getRight();
		} else if (outputPair.getRight() == null) {
			toOutput = Integer.toString(outputPair.getLeft());
		} else {
			toOutput = outputPair.getLeft() + " - " + outputPair.getRight();
		}
		outputWithLogging(toOutput);
	}

	public void getAll() {

		System.out.println("ITEMS");
		getItems();
		System.out.println("JOBS");
		getJobs();
		System.out.println("MAPS");
		getMaps();
		System.out.println("MOBS");
		getMobs();
		System.out.println("NPCS");
		getNpcs();
		System.out.println("PETS");
		getPets();
		System.out.println("PORTALSCRIPTS");
		getPortalScripts();
		System.out.println("SKILLS");
		getSkills();
	}

	public List<Pair<Integer, String>> comparePairs(List<Pair<Integer, String>> a, List<Pair<Integer, String>> b) {
		List<Pair<Integer, String>> ret = new ArrayList<Pair<Integer, String>>();
		for (Pair<Integer, String> singlePair : a) {
			if (!b.contains(singlePair)) {
				ret.add(singlePair);
			}
		}

		return ret;
	}

	public void getItems() {
		setFileStream("ITEM", true);
		List<MapleData> dataList = new ArrayList<MapleData>();
		List<Pair<Integer, String>> itemPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldItemPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newItemPairList = new ArrayList<Pair<Integer, String>>();
		boolean reloop = false;
		boolean firstLoop = true;
		int itemIdFromData;
		String itemNameFromData;

		List<MapleInventoryType> invTypes = new ArrayList<MapleInventoryType>();
		invTypes.add(MapleInventoryType.EQUIP);
		invTypes.add(MapleInventoryType.USE);
		invTypes.add(MapleInventoryType.SETUP);
		invTypes.add(MapleInventoryType.ETC);
		invTypes.add(MapleInventoryType.CASH);

		for (MapleInventoryType singleInv : invTypes) {
			for (int i = 0; i < 2; i++) {
				dataProvider = getDataProvider("String.wz", !firstLoop);
				if (firstLoop) {
					newItemPairList.add(new Pair<Integer, String>(null, "[" + singleInv.name() + "]"));
				}
				switch (singleInv) {
					case EQUIP:
						data = dataProvider.getData("Eqp.img").getChildByPath("Eqp");
						reloop = true;
						break;
					case USE:
						data = dataProvider.getData("Consume.img");
						reloop = false;
						break;
					case SETUP:
						data = dataProvider.getData("Ins.img");
						reloop = false;
						break;
					case ETC:
						data = dataProvider.getData("Etc.img").getChildByPath("Etc");
						reloop = false;
						break;
					case CASH:
						data = dataProvider.getData("Cash.img");
						reloop = false;
						break;
				}
				if (reloop) {//GET DATA
					for (MapleData firstRow : data.getChildren()) {
						for (MapleData secondRow : firstRow.getChildren()) {
							dataList.add(secondRow);
						}
					}
				} else {//GET DATA
					dataList = data.getChildren();
				}

				for (MapleData itemIdData : dataList) {
					itemIdFromData = Integer.parseInt(itemIdData.getName());
					itemNameFromData = MapleDataTool.getString(itemIdData.getChildByPath("name"), "NO-NAME");
					if (firstLoop) {
						oldItemPairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
					} else {
						newItemPairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
					}
				}
				firstLoop = !firstLoop;
				dataList = new ArrayList<MapleData>();
			}
			itemPairList = comparePairs(newItemPairList, oldItemPairList);
			outputPairList(itemPairList, invTypes.size());
			oldItemPairList = new ArrayList<Pair<Integer, String>>();
			newItemPairList = new ArrayList<Pair<Integer, String>>();
		}


	}

	public void getJobs() {
		setFileStream("JOB", true);
		List<Pair<Integer, String>> jobPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldJobPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newJobPairList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("String.wz", i == 1);
			data = dataProvider.getData("Skill.img");
			for (MapleData jobIdData : data.getChildren()) {
				int jobIdFromData = Integer.parseInt(jobIdData.getName());
				if (jobIdData.getChildByPath("bookName") != null) {
					String jobNameFromData = MapleDataTool.getString(jobIdData.getChildByPath("bookName"), "NO-NAME");
					if (i == 1) {
						newJobPairList.add(new Pair<Integer, String>(jobIdFromData, jobNameFromData));
					} else {
						oldJobPairList.add(new Pair<Integer, String>(jobIdFromData, jobNameFromData));
					}
				}
			}
		}
		jobPairList = comparePairs(newJobPairList, oldJobPairList);
		outputPairList(jobPairList);
	}

	public void getMaps() {
		setFileStream("MAP", true);
		List<Pair<Integer, String>> mapPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldMapPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newMapPairList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("String.wz", i == 1);
			data = dataProvider.getData("Map.img");
			for (MapleData mapTypeData : data.getChildren()) {
				for (MapleData mapIdData : mapTypeData.getChildren()) {
					int mapIdFromData = Integer.parseInt(mapIdData.getName());
					String mapNameFromData = MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME");
					if (i == 1) {
						newMapPairList.add(new Pair<Integer, String>(mapIdFromData, mapNameFromData));
					} else {
						oldMapPairList.add(new Pair<Integer, String>(mapIdFromData, mapNameFromData));
					}
				}
			}
		}
		mapPairList = comparePairs(newMapPairList, oldMapPairList);
		MapRender render = MapRender.getInstance();
		for(Pair<Integer, String> mapP : mapPairList) {
                    try {
			render.renderAndSaveMap(mapP.getLeft());
                    } catch (Exception e) {
                        System.out.println("Error Rendering Map: " + mapP.getLeft());
                    }
		}
		outputPairList(mapPairList);
	}

	public void getTests() {
	}

	public void getMobs() {
		setFileStream("MOB", true);
		List<Pair<Integer, String>> mobPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldMobPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newMobPairList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("String.wz", i == 1);
			data = dataProvider.getData("Mob.img");
			for (MapleData mobIdData : data.getChildren()) {
				int mobIdFromData = Integer.parseInt(mobIdData.getName());
				String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
				if (i == 1) {
					newMobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
				} else {
					oldMobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
				}
			}
		}
		mobPairList = comparePairs(newMobPairList, oldMobPairList);
		outputPairList(mobPairList);
	}

	public void getNpcs() {
		setFileStream("NPC", true);
		List<Pair<Integer, String>> npcPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldNpcPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newNpcPairList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("String.wz", i == 1);
			data = dataProvider.getData("Npc.img");
			for (MapleData npcIdData : data.getChildren()) {
				int npcIdFromData = Integer.parseInt(npcIdData.getName());
				String npcNameFromData = MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME");
				if (i == 1) {
					newNpcPairList.add(new Pair<Integer, String>(npcIdFromData, npcNameFromData));
				} else {
					oldNpcPairList.add(new Pair<Integer, String>(npcIdFromData, npcNameFromData));
				}
			}
		}
		npcPairList = comparePairs(newNpcPairList, oldNpcPairList);
		outputPairList(npcPairList);
	}

	public void getPets() {
		setFileStream("PET", true);
		List<Pair<Integer, String>> petPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldPetPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newPetPairList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("String.wz", i == 1);
			data = dataProvider.getData("Pet.img");

			for (MapleData petIdData : data.getChildren()) {
				int petIdFromData = Integer.parseInt(petIdData.getName());
				String petNameFromData = MapleDataTool.getString(petIdData.getChildByPath("name"), "NO-NAME");
				if (i == 1) {
					newPetPairList.add(new Pair<Integer, String>(petIdFromData, petNameFromData));
				} else {
					oldPetPairList.add(new Pair<Integer, String>(petIdFromData, petNameFromData));
				}
			}
		}
		petPairList = comparePairs(newPetPairList, oldPetPairList);
		outputPairList(petPairList);
	}

	public void getPortalScripts() {
		setFileStream("PORTALSCRIPT", true);
		List<Pair<Integer, String>> portalScriptList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldPortalScriptList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newPortalScriptList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("Map.wz", i == 1);
			MapleMapFactory mapFactory = new MapleMapFactory(MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Map.wz")), MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")));
			for (int mapId : mapFactory.getAllMapIds()) {
				String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapId), '0', 9);
				StringBuilder builder = new StringBuilder("Map/Map");
				int area = mapId / 100000000;
				builder.append(area);
				builder.append("/");
				builder.append(mapName);
				builder.append(".img");
				mapName = builder.toString();
				data = dataProvider.getData(mapName);
				try {
					for (MapleData portalData : data.getChildByPath("portal")) {
						String portalScriptName = MapleDataTool.getString("script", portalData, null);
						if (portalScriptName != null && !portalScriptName.equals("") && !portalScriptList.contains(portalScriptName)) {
							if (i == 1) {
								newPortalScriptList.add(new Pair<Integer, String>(null, portalScriptName));
							} else {
								oldPortalScriptList.add(new Pair<Integer, String>(null, portalScriptName));
							}

						}
					}
				} catch (Exception e) {
					//NO PORTAL
				}
			}
		}
		portalScriptList = comparePairs(newPortalScriptList, oldPortalScriptList);
		outputPairList(portalScriptList);
	}

	public void getSkills() {
		setFileStream("SKILL", true);
		List<Pair<Integer, String>> skillPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> oldSkillPairList = new ArrayList<Pair<Integer, String>>();
		List<Pair<Integer, String>> newSkillPairList = new ArrayList<Pair<Integer, String>>();
		for (int i = 0; i < 2; i++) {
			dataProvider = getDataProvider("String.wz", i == 1);
			data = dataProvider.getData("Skill.img");

			for (MapleData skillIdData : data.getChildren()) {
				int skillIdFromData = Integer.parseInt(skillIdData.getName());
				if (skillIdData.getChildByPath("name") != null) {
					String skillNameFromData = MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME") + " - " + MapleDataTool.getString(skillIdData.getChildByPath("desc"), "NO-NAME");
					if (i == 1) {
						newSkillPairList.add(new Pair<Integer, String>(skillIdFromData, skillNameFromData));
					} else {
						oldSkillPairList.add(new Pair<Integer, String>(skillIdFromData, skillNameFromData));
					}
				}
			}
		}
		skillPairList = comparePairs(newSkillPairList, oldSkillPairList);
		outputPairList(skillPairList);
	}

	public void getCompareAll() {
		setFileStream("COMPARE_ALL", true);
		Map<String, MapleDataFileEntry> oldImgs = new HashMap<String, MapleDataFileEntry>();
		Map<String, MapleDataFileEntry> newImgs = new HashMap<String, MapleDataFileEntry>();
		dataProvider = getDataProvider("Base.wz", false);
		for (MapleDataDirectoryEntry dirEntry : dataProvider.getRoot().getSubdirectories()) {
			for (int i = 0; i < 2; i++) {
				dataProvider = getDataProvider(dirEntry.getName() + ".wz", i == 1);
				for (MapleDataFileEntry fileEntry : dataProvider.getRoot().getFiles()) {
					if (i == 1) {
						newImgs.put(fileEntry.getName(), fileEntry);
					} else {
						oldImgs.put(fileEntry.getName(), fileEntry);
					}
				}
			}
			for (MapleDataFileEntry a : newImgs.values()) {
				if (!oldImgs.containsKey(a.getName())) {
					outputWithLogging("New Img File: " + a.getParent().getName() + "/" + a.getName());
				} else if (oldImgs.get(a.getName()).getChecksum() != a.getChecksum()) {
					//diff checksum (stuff added)
					outputWithLogging("Checksum Changed: " + a.getParent().getName() + "/" + a.getName());
				}
			}

			for (MapleDataFileEntry b : oldImgs.values()) {
				if (!newImgs.containsKey(b.getName())) {
					outputWithLogging("Removed Img File: " + b.getParent().getName() + "/" + b.getName());
				}
			}
			oldImgs.clear();
			newImgs.clear();
		}
	}
}
