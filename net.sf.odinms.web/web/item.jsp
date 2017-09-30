<%-- 
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
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="net.sf.odinms.client.Equip"%>
<%@page import="net.sf.odinms.client.IItem"%>
<%@page import="net.sf.odinms.client.MapleCharacter"%>
<%@page import="net.sf.odinms.client.MapleInventoryType"%>
<%@page import="net.sf.odinms.database.DatabaseConnection"%>
<%@page import="net.sf.odinms.server.life.MapleLifeFactory"%>
<%@page import="net.sf.odinms.server.life.MapleMonster"%>
<%@page import="net.sf.odinms.server.life.MapleNPC"%>
<%@page import="net.sf.odinms.server.MapleItemInformationProvider"%>
<%@page import="net.sf.odinms.server.TimerManager"%>
<%
int itemId = 0;
IItem inventoryItem = null;
String infoSuffix = "";
try {
	if (request.getParameter("char") != null &&
		request.getParameter("type") != null &&
		request.getParameter("slot") != null) {
		TimerManager.getInstance().start();
		MapleCharacter chr = MapleCharacter.loadCharFromDB(Integer.parseInt(request.getParameter("char")), null, false);
		MapleInventoryType type = MapleInventoryType.getByType(Byte.parseByte(request.getParameter("type")));
		inventoryItem = chr.getInventory(type).getItem(Byte.parseByte(request.getParameter("slot")));
		itemId = inventoryItem.getItemId();
		infoSuffix = "of " + chr.getName() + " in " + type + " slot " + inventoryItem.getPosition();
	} else {
		itemId = Integer.parseInt(request.getParameter("item"));
	}
} catch (Exception e) {
	return;
}
MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

Properties dbProps = new Properties();
dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
DatabaseConnection.setProps(dbProps);
Connection con = DatabaseConnection.getConnection();
String monsterimage = "IconExtractor?monster=";
String monsters = "";
String npcs = "";
int i = 0;
PreparedStatement ps = con.prepareStatement("SELECT monsterid FROM monsterdrops WHERE itemid = ? ORDER BY itemid");
ps.setInt(1, itemId);
ResultSet rs = ps.executeQuery();	
while(rs.next()) {
	int monster = rs.getInt("monsterid");
	MapleMonster monstername = MapleLifeFactory.getMonster(monster); 	
	monsters += "<b>" + monstername.getName() + "</b> (<b>ID:</b> <a href='monster.jsp?monster=" + monster + "'>" + monster + "</a>)<br>";
}
rs.close();
ps.close();
PreparedStatement shopps = con.prepareStatement("SELECT shopid FROM shopitems WHERE itemid = ? ORDER BY shopid");
shopps.setInt(1, itemId);
PreparedStatement idps = con.prepareStatement("SELECT npcid FROM shops WHERE shopid = ? ORDER BY npcid");
ResultSet shoprs = shopps.executeQuery();
while(shoprs.next()) {
	int npcshopid = shoprs.getInt("shopid");
	idps.setInt(1, npcshopid);
	ResultSet idrs = idps.executeQuery();
	while(idrs.next()) {
		int npc = idrs.getInt("npcid");
		MapleNPC npcname = MapleLifeFactory.getNPC(npc);
		npcs += "<td><center><img src='IconExtractor?npc=" + npc + "'><br><b>" + npcname.getName() + "</b> <br>(<b>ID:</b> <a href='npc.jsp?npc=" + npc + "'>" + npc + "</a>)<br></td>";		
		i++;
		if(i == 4) {
			npcs += "</tr><tr>";
			i = 0;
		}
	}
}

rs.close();
ps.close();

String itemName = "";
String itemDesc = "";
itemName = ii.getName(itemId);
itemDesc = ii.getDesc(itemId);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">

	<head>
		<title>Item Lookup: <%= itemName %> - WebWiz</title>
		<link rel="shortcut icon" href="images/favicon.ico" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="description" content="WebWiz." />
		
		<link rel="shortcut icon" href="required/favicon.ico" type="image/x-icon" />
		<link rel="stylesheet" href="required/stylesheet.css" type="text/css" media="screen" />
		<script language="javascript" src="/required/overlib.js" type="text/javascript"><!-- overLIB (c) Erik Bosrup --></script>

<script type="text/javascript" src="/required/sdmenu.js"></script>
<script type="text/javascript">
	// <![CDATA[
	var myMenu;
	window.onload = function() {
		myMenu = new SDMenu("my_menu");
		myMenu.init();
	};
	// ]]>
	</script>
    <style type="text/css">
		label { float: left; width: 150px; margin-top: 10px; }
		input { float: left; /*margin-left: 203px;*/ margin-top: 10px; }
		br { clear: left; }
	</style>
	</head>	
			<body>
                <div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
		<div id="content"> 
			<div id="header">

				<a href="index.jsp"></a>
			</div>
			<div id="nav">
            				<a href="index.jsp">Home</a></div>
			<div id="my_menu" class="sdmenu">
		<div><%@include file="links.jsp"%></div>
<br /><br />
</div>
			<div id="main">
            <table id="item" width="535" border="1" align="center" cellpadding="2">
  <tr>
    <td width="50" colspan="2"><div align="center"><img src="IconExtractor?id=<%= itemId %>" alt="Icon"/></div></td>
    <td width="135" colspan="2"><b><%= itemName %></b><br> (<b>ID:</b> <%= itemId %>)</td>
  </tr>
  <tr>
    <td colspan="4"><% if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
		Equip equip;
		if (inventoryItem != null) {
			equip = (Equip) inventoryItem;
		} else {
			equip = (Equip) ii.getEquipById(itemId);
		}
		%>
        <b>Type:<b> <%= ii.getInventoryType(itemId) %>
		<% if (inventoryItem != null) { %>Level: <%= equip.getLevel() %><br/><% } %>
		Upgrade slots: <%= equip.getUpgradeSlots() %><br/>
		<% if (equip.getHands() > 0) { %>Hands: <%= equip.getHands() %><br/><% } %>
		<% if (inventoryItem == null) { %>Average item stats (+/- 10%)<br/><% } %>
		<% if (equip.getStr() > 0) { %>STR: <%= equip.getStr() %><br/><% } %>
		<% if (equip.getDex() > 0) { %>DEX: <%= equip.getDex() %><br/><% } %>
		<% if (equip.getInt() > 0) { %>INT: <%= equip.getInt() %><br/><% } %>
		<% if (equip.getLuk() > 0) { %>LUK: <%= equip.getLuk() %><br/><% } %>
		<% if (equip.getWatk() > 0) { %>WATK: <%= equip.getWatk() %><br/><% } %>
		<% if (equip.getWdef() > 0) { %>WDEF: <%= equip.getWdef() %><br/><% } %>
		<% if (equip.getMatk() > 0) { %>MATK: <%= equip.getMatk() %><br/><% } %>
		<% if (equip.getMdef() > 0) { %>MDEF: <%= equip.getMdef() %><br/><% } %>
		<% if (equip.getAvoid() > 0) { %>Avoid: <%= equip.getAvoid() %><br/><% } %>
		<% if (equip.getAcc() > 0) { %>Accuracy: <%= equip.getAcc() %><br/><% } %>
		<% if (equip.getJump() > 0) { %>Jump: <%= equip.getJump() %><br/><% } %>
		<% if (equip.getSpeed() > 0) { %>Speed: <%= equip.getSpeed() %><br/><% } %>
		<% if (equip.getHp() > 0) { %>HP: <%= equip.getHp() %><br/><% } %>
		<% if (equip.getMp() > 0) { %>MP: <%= equip.getMp() %><br/><% } %>
		<%
	} else {
	%> 
    <b>Type:<b> <%= ii.getInventoryType(itemId) %>
    <% 
	}
	%>
  </tr>
  <tr>
    <td colspan="4"><strong>Description:</strong><br>
      <% if(itemDesc != null) { 
	  %>
	  <%= itemDesc %><%
	  } else {
	  %> 
      No Description Available.
	  <%
	  }
	  %>
      </td>
    </tr>
  <tr>
    <td colspan="4"><strong>NPC price:</strong> <%= ii.getWholePrice(itemId) %> </td>
  </tr>
    <tr>
    <td colspan="4"><center><strong>NPC Sale</strong></center>
    </td>
  </tr>
  <tr><% if(npcs != "") { %>
	<%= npcs %>
    <% } else { %>
    <td colspan="4">No NPC's Available.</td>
    <% } %>
  </tr>
  <tr>
    <td colspan="4"><center><strong>Monster Drops</strong></center>
    </td>
  </tr>
  <tr>
	<td colspan="4"><%= monsters %></td>
  </tr>
</table>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
		  <div id="footer">
			  Layout by Chip @ Hidden Street | Best viewed with Mozilla Firefox.</div>
		</div></div>		
	</body>
</html>
