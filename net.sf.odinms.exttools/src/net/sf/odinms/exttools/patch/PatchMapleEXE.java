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

package net.sf.odinms.exttools.patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.PropertyTool;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.meyling.console.Console;
import com.meyling.console.ConsoleFactory;
import com.meyling.console.ConsoleForegroundColor;

public class PatchMapleEXE {

    private static List<PatchThunk> patchThunks;
    private static Console console;
    private static Hashtable patchIPInfo;

    public static void main(String args[]) throws IOException {
	long startTime = System.currentTimeMillis();
	patchThunks = new ArrayList<PatchThunk>();
	console = ConsoleFactory.getConsole();
	patchIPInfo = new Hashtable(10);
	Properties settings = new Properties();
	boolean[] patchedIPs = new boolean[3];
	String[] oldIPs = new String[3];
	String[] newIPs = new String[3];

	try {
	    settings.load(new FileInputStream(new File("settings.properties")));
	} catch (Exception e) {
	    System.out.println("Error Reading settings.properties");
	    return;
	}
	PropertyTool propTool = new PropertyTool(settings);

	String inFileName = propTool.getSettingStr("CLIENT_IN");
	String patchDataName = propTool.getSettingStr("PATCH_INFO", "PatchInfo.xml");
	String outFileName = propTool.getSettingStr("CLIENT_OUT");
	boolean patchIPs = propTool.getSettingInt("PATCH_IPS", 1) > 0;
	boolean patchData = propTool.getSettingInt("PATCH_DATA", 1) > 0;

	parseXmlFile(new File(patchDataName));

	if (patchIPInfo == null) {
	    System.out.println("Error Reading Patch-IP-Info");
	    return;
	}
	File mapleExe = new File(inFileName);
	RandomAccessFile raf = new RandomAccessFile(mapleExe, "r");
	byte fileData[] = new byte[(int) raf.length()];
	raf.readFully(fileData);


	oldIPs[0] = patchIPInfo.get("oldip1").toString();
	oldIPs[1] = patchIPInfo.get("oldip2").toString();
	oldIPs[2] = patchIPInfo.get("oldip3").toString();
	newIPs[0] = patchIPInfo.get("newip").toString();
	newIPs[1] = newIPs[0];
	newIPs[2] = newIPs[0];

	for (int x = 0; x < fileData.length; x++) {

	    if (patchData) {
		for (int i = 0; i < patchThunks.size(); i++) {
		    PatchThunk currentThunk = patchThunks.get(i);
		    if ((x + currentThunk.getOldByte().length) < fileData.length) {
			if (compareByteArray(fileData, currentThunk.getOldByte(), x, 0, currentThunk.getOldByte().length) && (!currentThunk.isPatched()) && currentThunk.toApply()) {
			    System.out.print("\r");
			    System.out.print("[  ");
			    console.setForegroundColor(ConsoleForegroundColor.WHITE);
			    System.out.print("INFO");
			    console.resetColors();
			    //System.out.println("  ]   Found and Patched " + currentThunk.getName() + " ||  Offset: 0x" + Integer.toString(x, 16).toUpperCase());
			    System.out.println("  ]   Offset: 0x" + Integer.toString(x, 16).toUpperCase() + " || " + "Found and Patched " + currentThunk.getName());
			    System.arraycopy(currentThunk.getNewByte(), 0, fileData, x, currentThunk.getNewByte().length);
			    x += currentThunk.newByte.length;
			    currentThunk.setPatched(true);
			}
		    }
		}
	    }

	    if (patchIPs) {
		for (int y = 0; y < patchedIPs.length; y++) {
		    byte[] ipByte = padByteArray(getAsciiByteArray(oldIPs[y]), 16);
		    if (!patchedIPs[y] && compareByteArray(fileData, ipByte, x, 0, ipByte.length)) {
			System.out.print("\r[  ");
			console.setForegroundColor(ConsoleForegroundColor.WHITE);
			System.out.print("INFO");
			console.resetColors();
			int actualIP = y + 1;
			System.out.println("  ]   Found and Patched IP-" + actualIP);
			byte[] newIP = padByteArray(getAsciiByteArray(newIPs[y]), 16);
			System.arraycopy(newIP, 0, fileData, x, newIP.length);
			patchedIPs[y] = true;
			x += newIPs[y].length();
		    }
		}
	    }

	    if ((x % 500) == 0) {
		writeStatusOutput(fileData, x);
	    }
	}
	System.out.println();

	for (PatchThunk currentThunk : patchThunks) {
	    System.out.print("[ ");
	    console.setForegroundColor(ConsoleForegroundColor.LIGHT_BLUE);
	    System.out.print("RESULT");
	    console.resetColors();
	    System.out.print(" ]   ");
	    System.out.print("Patched " + currentThunk.getName() + ": ");
	    if (currentThunk.isPatched()) {
		console.setForegroundColor(ConsoleForegroundColor.LIGHT_GREEN);
		System.out.println("TRUE");
	    } else if (!currentThunk.toApply()) {
		console.setForegroundColor(ConsoleForegroundColor.WHITE);
		System.out.println("DISABLED");
	    } else {
		console.setForegroundColor(ConsoleForegroundColor.LIGHT_RED);
		System.out.println("FALSE");
	    }
	    console.resetColors();
	}

	for (int i = 0; i < oldIPs.length; i++) {
	    System.out.print("[ ");
	    console.setForegroundColor(ConsoleForegroundColor.LIGHT_BLUE);
	    System.out.print("RESULT");
	    console.resetColors();
	    System.out.print(" ]   ");
	    System.out.print("Patched IP [" + oldIPs[i] + "]: ");
	    if (patchedIPs[i]) {
		console.setForegroundColor(ConsoleForegroundColor.LIGHT_GREEN);
		System.out.println("TRUE");
	    } else {
		console.setForegroundColor(ConsoleForegroundColor.LIGHT_RED);
		System.out.println("FALSE");
	    }
	    console.resetColors();
	}

	File out = new File(outFileName);
	out.createNewFile();
	FileOutputStream fos = new FileOutputStream(out, false);
	fos.write(fileData);
	fos.close();
	long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
	System.out.println("Finished in " + timeTaken + " seconds");
    }

    public static void writeStatusOutput(byte[] fileData, int pos) {
	System.out.print("\r[ ");
	console.setForegroundColor(ConsoleForegroundColor.LIGHT_GREEN);
	System.out.print("STATUS");
	console.resetColors();
	String curPos = "0x" + Integer.toString(pos, 16).toUpperCase();
	String totPos = "0x" + Integer.toString(fileData.length, 16).toUpperCase();
	System.out.print(" ]   At byte " + curPos + " of " + totPos);
    }

    private static boolean compareByteArray(byte[] a, byte[] b, int starta, int startb, int length) {
	int i = starta;
	int j = startb;
	for (int x = 0; x < length; x++) {
	    if (a[i] != b[j]) {
		return false;
	    }
	    i++;
	    j++;
	}
	return true;
    }

    private static boolean compareByteArray(byte[] a, MyByte[] b, int starta, int startb, int length) {
	int i = starta;
	int j = startb;
	for (int x = 0; x < length; x++) {
	    if ((a[i] != b[j].value) && (!b[j].matchall)) {
		return false;
	    }
	    i++;
	    j++;
	}
	return true;
    }

    private static byte[] padByteArray(byte[] in, int length) {
	byte[] ret = new byte[length];
	for (int x = 0; x < length; x++) {
	    if (x < in.length) {
		ret[x] = in[x];
	    } else {
		ret[x] = 0;
	    }
	}
	return ret;
    }

    private static byte[] getAsciiByteArray(String s) {
	byte[] ret = new byte[s.length()];
	for (int x = 0; x < s.length(); x++) {
	    ret[x] = (byte) s.charAt(x);
	}
	return ret;
    }

    private static MyByte[] getMyByteArrayFromHexString(String hex) {
	String[] bytes = hex.split(",");
	MyByte[] returnData = new MyByte[bytes.length];
	for (int j = 0; j < returnData.length; j++) {
	    MyByte newByte;
	    if (bytes[j].equals("*")) {
		newByte = new MyByte(true);
	    } else {
		newByte = new MyByte((byte) Integer.parseInt(bytes[j], 16));
	    }
	    returnData[j] = newByte;
	}
	return returnData;
    }

    private static void parseXmlFile(File xmlFile) {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(xmlFile);
	    PatchThunk currentThunk = new PatchThunk();
	    doc.getDocumentElement().normalize();
	    NodeList listOfThunks = doc.getElementsByTagName("thunk");
	    NodeList listOfIPs = doc.getElementsByTagName("ip");

	    Node firstIPNode = listOfIPs.item(0);
	    if (firstIPNode.getNodeType() == Node.ELEMENT_NODE) {
		Element firstIPElement = (Element) firstIPNode;

		NodeList oldIP1List = firstIPElement.getElementsByTagName("oldip1");
		Element oldIP1Element = (Element) oldIP1List.item(0);
		NodeList textOldIP1List = oldIP1Element.getChildNodes();
		patchIPInfo.put("oldip1", textOldIP1List.item(0).getNodeValue().trim());

		NodeList oldIP2List = firstIPElement.getElementsByTagName("oldip2");
		Element oldIP2Element = (Element) oldIP2List.item(0);
		NodeList textOldIP2List = oldIP2Element.getChildNodes();
		patchIPInfo.put("oldip2", textOldIP2List.item(0).getNodeValue().trim());

		NodeList oldIP3List = firstIPElement.getElementsByTagName("oldip3");
		Element oldIP3Element = (Element) oldIP3List.item(0);
		NodeList textOldIP3List = oldIP3Element.getChildNodes();
		patchIPInfo.put("oldip3", textOldIP3List.item(0).getNodeValue().trim());

		NodeList newIPList = firstIPElement.getElementsByTagName("newip");
		Element newIPElement = (Element) newIPList.item(0);
		NodeList textNewIPList = newIPElement.getChildNodes();
		patchIPInfo.put("newip", textNewIPList.item(0).getNodeValue().trim());

	    }


	    //PATCH THUNK LOADING\\
	    for (int s = 0; s < listOfThunks.getLength(); s++) {
		Node firstThunkNode = listOfThunks.item(s);
		if (firstThunkNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element firstThunkElement = (Element) firstThunkNode;
		    //-------
		    NodeList oldDataList = firstThunkElement.getElementsByTagName("olddata");
		    Element oldDataElement = (Element) oldDataList.item(0);
		    NodeList textOldDataList = oldDataElement.getChildNodes();
		    currentThunk.setOldByte(getMyByteArrayFromHexString(textOldDataList.item(0).getNodeValue().trim()));
		    //-------
		    NodeList newDataList = firstThunkElement.getElementsByTagName("newdata");
		    Element newDataElement = (Element) newDataList.item(0);
		    NodeList textNewDataList = newDataElement.getChildNodes();
		    currentThunk.setNewByte(HexTool.getByteArrayFromHexString(textNewDataList.item(0).getNodeValue().trim()));
		    //----
		    NodeList nameList = firstThunkElement.getElementsByTagName("name");
		    Element nameElement = (Element) nameList.item(0);
		    NodeList textNameList = nameElement.getChildNodes();
		    currentThunk.setName(textNameList.item(0).getNodeValue().trim());
		    //------
		    NodeList applyList = firstThunkElement.getElementsByTagName("apply");
		    Element applyElement = (Element) applyList.item(0);
		    NodeList textApplyList = applyElement.getChildNodes();
		    currentThunk.setApplied(Boolean.parseBoolean(textApplyList.item(0).getNodeValue().trim()));
		//------

		}
		//End of clause of if
		patchThunks.add(currentThunk);
		currentThunk = new PatchThunk();

	    }//end of for loop with s var


	} catch (SAXParseException err) {
	    System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
	    System.out.println(" " + err.getMessage());

	} catch (SAXException e) {
	    Exception x = e.getException();
	    ((x == null) ? e : x).printStackTrace();

	} catch (Throwable t) {
	    t.printStackTrace();
	}

    }

    private static class PatchThunk {

	private String name;
	private MyByte[] oldByte;
	private byte[] newByte;
	private boolean patched;
	private boolean applied;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public MyByte[] getOldByte() {
	    return oldByte;
	}

	public void setOldByte(MyByte[] oldByte) {
	    this.oldByte = oldByte;
	}

	public byte[] getNewByte() {
	    return newByte;
	}

	public void setNewByte(byte[] newByte) {
	    this.newByte = newByte;
	}

	public boolean isPatched() {
	    return patched;
	}

	public void setPatched(boolean patched) {
	    this.patched = patched;
	}

	public boolean toApply() {
	    return applied;
	}

	public void setApplied(boolean applied) {
	    this.applied = applied;
	}
	}

    private static class MyByte {

	private byte value;
	private boolean matchall;

	public MyByte(byte primByte) {
	    this(primByte, false);
	}

	public MyByte(boolean wildcard) {
	    this((byte) 0x00, wildcard);
	}

	public MyByte(byte primByte, boolean wildcard) {
	    this.value = primByte;
	    this.matchall = wildcard;
	}

	public void setValue(byte value) {
	    this.value = value;
	}

	public byte getValue() {
	    return value;
	}

	public void setMatchall(boolean matchall) {
	    this.matchall = matchall;
	}

	public boolean isMatchall() {
	    return matchall;
	}
	}
}
