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
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Raz
 */
public class WzInfo {

    private MapleDataProvider dataProvider = null;
    private MapleData data = null;
    private File outputFile = null;
    private PrintWriter logStream = null;
    private Scanner in = new Scanner(System.in);
    private PrintStream out = System.out;
    private WzInfoType infoType = WzInfoType.UNDEFINED;
    private static WzInfo instance = new WzInfo();

    public static WzInfo getInstance() {
	return instance;
    }

    private WzInfo() {

    }

    public static void main(String args[]) throws IOException {
	WzInfo session = getInstance();
	session.doMain();

    }

    public void doMain() {
	out.println("Snow's WzInfo Lister\r\n");

	for (WzInfoType singleInfo : WzInfoType.values()) {
	    if (singleInfo.getType() > -1) {
		out.println("[" + singleInfo.getType() + "] - " + singleInfo.name());
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
		out.println("Invalid Type");
		checkPassed = false;
	    }
	}
	try {
	    /*
	    String fileName = infoType.name() + ".txt";
	    outputFile = new File(fileName);
	    if(!outputFile.exists()){
	    outputFile.createNewFile();
	    out.println("Created File: " + outputFile.getName());
	    }
	    try {
	    logStream = new PrintWriter(outputFile);
	    } catch(IOException e) {
	    e.printStackTrace();
	    }*/

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
		default:
		    out.println("DEFAULT");
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
			out.println("[" + singleInfo.getType() + "] - " + singleInfo.name());
		    }
		}
	    } else {
		out.println("you typed: " + text);//UNKNOWN COMMAND
	    }
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
			out.println("Created File: " + outputFile.getName());
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

    public MapleDataProvider getDataProvider(String wzName) {
	MapleDataProvider ret = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + wzName));
	return ret;
    }

    public void outputWithLogging(String buff) {
	out.println(buff);
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
	for (Pair<Integer, String> outputPair : outputPairList) {
	    outputPair(outputPair);
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

	out.println("ITEMS");
	getItems();
	out.println("JOBS");
	getJobs();
	out.println("MAPS");
	getMaps();
	out.println("MOBS");
	getMobs();
	out.println("NPCS");
	getNpcs();
	out.println("PETS");
	getPets();
	out.println("PORTALSCRIPTS");
	getPortalScripts();
	out.println("SKILLS");
	getSkills();
    }

    public void getItems() {
	setFileStream("ITEM", true);
	dataProvider = getDataProvider("String.wz");
	List<Pair<Integer, String>> eqpPairList = new LinkedList<Pair<Integer, String>>();
	List<Pair<Integer, String>> usePairList = new LinkedList<Pair<Integer, String>>();
	List<Pair<Integer, String>> setupPairList = new LinkedList<Pair<Integer, String>>();
	List<Pair<Integer, String>> etcPairList = new LinkedList<Pair<Integer, String>>();
	List<Pair<Integer, String>> cashPairList = new LinkedList<Pair<Integer, String>>();
	int itemIdFromData;
	String itemNameFromData;
	String itemDescFromData;

	//EQUIP
	outputWithLogging("\r\n[EQUIP]");
	data = dataProvider.getData("Eqp.img").getChildByPath("Eqp");
	for (MapleData types : data.getChildren()) {
	    eqpPairList.add(new Pair<Integer, String>(null, types.getName()));
	    for (MapleData itemIdData : types.getChildren()) {
		itemIdFromData = Integer.parseInt(itemIdData.getName());
		itemNameFromData = MapleDataTool.getString(itemIdData.getChildByPath("name"), "NO-NAME");
		itemDescFromData = MapleDataTool.getString(itemIdData.getChildByPath("desc"), "");
		if (itemDescFromData.length() > 0) {
		    itemNameFromData += " - " + itemDescFromData;
		}
		eqpPairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
	    }
	}
	outputPairList(eqpPairList);

	//USE
	outputWithLogging("\r\n[USE]");
	data = dataProvider.getData("Consume.img");
	for (MapleData itemIdData : data.getChildren()) {
	    itemIdFromData = Integer.parseInt(itemIdData.getName());
	    itemNameFromData = MapleDataTool.getString(itemIdData.getChildByPath("name"), "NO-NAME");
	    itemDescFromData = MapleDataTool.getString(itemIdData.getChildByPath("desc"), "");
	    if (itemDescFromData.length() > 0) {
		itemNameFromData += " - " + itemDescFromData;
	    }
	    usePairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
	}
	outputPairList(usePairList);

	//SETUP
	outputWithLogging("\r\n[SETUP]");
	data = dataProvider.getData("Ins.img");
	for (MapleData itemIdData : data.getChildren()) {
	    itemIdFromData = Integer.parseInt(itemIdData.getName());
	    itemNameFromData = MapleDataTool.getString(itemIdData.getChildByPath("name"), "NO-NAME");
	    itemDescFromData = MapleDataTool.getString(itemIdData.getChildByPath("desc"), "");
	    if (itemDescFromData.length() > 0) {
		itemNameFromData += " - " + itemDescFromData;
	    }
	    setupPairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
	}
	outputPairList(setupPairList);

	//ETC
	outputWithLogging("\r\n[ETC]");
	data = dataProvider.getData("Etc.img").getChildByPath("Etc");
	for (MapleData itemIdData : data.getChildren()) {
	    itemIdFromData = Integer.parseInt(itemIdData.getName());
	    itemNameFromData = MapleDataTool.getString(itemIdData.getChildByPath("name"), "NO-NAME");
	    itemDescFromData = MapleDataTool.getString(itemIdData.getChildByPath("desc"), "");
	    if (itemDescFromData.length() > 0) {
		itemNameFromData += " - " + itemDescFromData;
	    }
	    etcPairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
	}
	outputPairList(etcPairList);

	//CASH
	outputWithLogging("\r\n[CASH]");
	data = dataProvider.getData("Cash.img");
	for (MapleData itemIdData : data.getChildren()) {
	    itemIdFromData = Integer.parseInt(itemIdData.getName());
	    itemNameFromData = MapleDataTool.getString(itemIdData.getChildByPath("name"), "NO-NAME");
	    itemDescFromData = MapleDataTool.getString(itemIdData.getChildByPath("desc"), "");
	    if (itemDescFromData.length() > 0) {
		itemNameFromData += " - " + itemDescFromData;
	    }
	    cashPairList.add(new Pair<Integer, String>(itemIdFromData, itemNameFromData));
	}
	outputPairList(cashPairList);


    }

    public void getJobs() {
	setFileStream("JOB", true);
	dataProvider = getDataProvider("String.wz");
	data = dataProvider.getData("Skill.img");
	List<Pair<Integer, String>> jobPairList = new LinkedList<Pair<Integer, String>>();
	for (MapleData jobIdData : data.getChildren()) {
	    int jobIdFromData = Integer.parseInt(jobIdData.getName());
	    if (jobIdData.getChildByPath("bookName") != null) {
		String jobNameFromData = MapleDataTool.getString(jobIdData.getChildByPath("bookName"), "NO-NAME");
		jobPairList.add(new Pair<Integer, String>(jobIdFromData, jobNameFromData));
	    }
	}
	outputPairList(jobPairList);

    }

    public void getMaps() {
	setFileStream("MAP", true);
	dataProvider = getDataProvider("String.wz");
	data = dataProvider.getData("Map.img");
	List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
	for (MapleData mapTypeData : data.getChildren()) {
	    for (MapleData mapIdData : mapTypeData.getChildren()) {
		int mapIdFromData = Integer.parseInt(mapIdData.getName());
		String mapNameFromData = MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME");
		mapPairList.add(new Pair<Integer, String>(mapIdFromData, mapNameFromData));
	    }
	}
	outputPairList(mapPairList);

    }

    public void getMobs() {
	setFileStream("MOB", true);
	dataProvider = getDataProvider("String.wz");
	data = dataProvider.getData("Mob.img");
	List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
	for (MapleData mobIdData : data.getChildren()) {
	    int mobIdFromData = Integer.parseInt(mobIdData.getName());
	    String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
	    mobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
	}
	outputPairList(mobPairList);

    }

    public void getNpcs() {
	setFileStream("NPC", true);
	dataProvider = getDataProvider("String.wz");
	data = dataProvider.getData("Npc.img");
	List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
	for (MapleData npcIdData : data.getChildren()) {
	    int npcIdFromData = Integer.parseInt(npcIdData.getName());
	    String npcNameFromData = MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME");
	    npcPairList.add(new Pair<Integer, String>(npcIdFromData, npcNameFromData));
	}
	outputPairList(npcPairList);

    }

    public void getPets() {
	setFileStream("PET", true);
	dataProvider = getDataProvider("String.wz");
	data = dataProvider.getData("Pet.img");
	List<Pair<Integer, String>> petPairList = new LinkedList<Pair<Integer, String>>();
	for (MapleData petIdData : data.getChildren()) {
	    int petIdFromData = Integer.parseInt(petIdData.getName());
	    String petNameFromData = MapleDataTool.getString(petIdData.getChildByPath("name"), "NO-NAME");
	    petPairList.add(new Pair<Integer, String>(petIdFromData, petNameFromData));
	}
	outputPairList(petPairList);

    }

    public void getPortalScripts() {
	setFileStream("PORTALSCRIPT", true);
	dataProvider = getDataProvider("Map.wz");
	List<Pair<Integer, String>> portalScriptPairList = new LinkedList<Pair<Integer, String>>();
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
		    if (portalScriptName != null && !portalScriptName.equals("") && !portalScriptPairList.contains(new Pair<Integer, String>(null, portalScriptName))) {
			portalScriptPairList.add(new Pair<Integer, String>(null, portalScriptName));
		    }
		}
	    } catch (Exception e) {
	    //NO PORTAL
	    }
	}
	outputPairList(portalScriptPairList);
    }

    public void getSkills() {
	setFileStream("SKILL", true);
	dataProvider = getDataProvider("String.wz");
	data = dataProvider.getData("Skill.img");
	List<Pair<Integer, String>> skillPairList = new LinkedList<Pair<Integer, String>>();
	for (MapleData skillIdData : data.getChildren()) {
	    int skillIdFromData = Integer.parseInt(skillIdData.getName());
	    if (skillIdData.getChildByPath("name") != null) {
		String skillNameFromData = MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME") + " - " + MapleDataTool.getString(skillIdData.getChildByPath("desc"), "NO-NAME");
		skillPairList.add(new Pair<Integer, String>(skillIdFromData, skillNameFromData));
	    }
	}
	outputPairList(skillPairList);

    }
}
