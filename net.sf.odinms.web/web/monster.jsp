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
<%@page import="java.io.File"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="net.sf.odinms.client.IItem"%>
<%@page import="net.sf.odinms.client.MapleInventoryType"%>
<%@page import="net.sf.odinms.database.DatabaseConnection"%>
<%@page import="net.sf.odinms.provider.MapleData"%>
<%@page import="net.sf.odinms.provider.MapleDataTool"%>
<%@page import="net.sf.odinms.provider.MapleDataProviderFactory"%>
<%@page import="net.sf.odinms.server.MapleMonsterBook"%>
<%@page import="net.sf.odinms.server.MapleMonsterBook.MonsterBookEntry"%>
<%@page import="net.sf.odinms.server.maps.MapleMap"%>
<%@page import="net.sf.odinms.server.life.MapleLifeFactory"%>
<%@page import="net.sf.odinms.server.life.MapleMonster"%>
<%@page import="net.sf.odinms.server.MapleItemInformationProvider"%>
<%@page import="net.sf.odinms.tools.Pair"%>
<%@page import="net.sf.odinms.web.MapleMapProvider"%>
<%
MapleMonster monster = MapleLifeFactory.getMonster(Integer.parseInt(request.getParameter("monster")));
MapleMapProvider mp = MapleMapProvider.getInstance();
MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
Properties dbProps = new Properties();
dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
DatabaseConnection.setProps(dbProps);
Connection con = DatabaseConnection.getConnection();
String itemimage = "IconExtractor?id=";
String equips = "";
String use = "";
String setup = "";
String etc = "";
String cash = "";
String maps = "";
String skills = "";

String query = "SELECT COUNT(monsterid) AS numrows FROM monsterdrops WHERE monsterid = " + monster.getId();
PreparedStatement ps = con.prepareStatement("SELECT itemid, chance FROM monsterdrops WHERE monsterid = ? ORDER BY itemid");
ps.setInt(1, monster.getId());
ResultSet rs = ps.executeQuery();		
while(rs.next()) {
	int item = rs.getInt("itemid");
	if(ii.getInventoryType(item) == MapleInventoryType.EQUIP)
		equips += "<img src='" + itemimage + item + "'> <b>" + ii.getName(item) + "</b> (<b>ID:</b> <a href='item.jsp?item=" + item + "'>" + item + "</a>)<br>";
	if(ii.getInventoryType(item) == MapleInventoryType.USE)
		use += "<img src='" + itemimage + item + "'> <b>" + ii.getName(item) + "</b> (<b>ID:</b> <a href='item.jsp?item=" + item + "'>" + item + "</a>)<br>";
	if(ii.getInventoryType(item) == MapleInventoryType.SETUP)
		setup += "<img src='" + itemimage + item + "'> <b>" + ii.getName(item) + "</b> (<b>ID:</b> <a href='item.jsp?item=" + item + "'>" + item + "</a>)<br>";
	if(ii.getInventoryType(item) == MapleInventoryType.ETC)
		etc += "<img src='" + itemimage + item + "'> <b>" + ii.getName(item) + "</b> (<b>ID:</b> <a href='item.jsp?item=" + item + "'>" + item + "</a>)<br>";
	if(ii.getInventoryType(item) == MapleInventoryType.CASH)
		cash += "<img src='" + itemimage + item + "'> <b>" + ii.getName(item) + "</b> (<b>ID:</b> <a href='item.jsp?item=" + item + "'>" + item + "</a>)<br>";
}
rs.close();
ps.close();	


for(Pair<Integer, Integer> skillEntry : monster.getSkillEntries()) {
	skills += "<tr><td colspan=\"6\"><center><b>ID:</b> " + skillEntry.getLeft() + " <b>|||</b> <b>LEVEL:</b> " + skillEntry.getRight() + "<br></td><tr>";
}

int k = 0;
int totalMaps = 0;
MonsterBookEntry bookEntry = MapleMonsterBook.getInstance().getEntry(monster.getId());
if (bookEntry != null) {
	for (int mapid : bookEntry.getMaps()) {
		MapleMap map = mp.getMap(mapid);
		maps += "<td><center><b>" + map.getStreetName() + "</b><br>" + map.getMapName() + "<br>(<b>ID:</b> 	<a href='map.jsp?map=" + map.getId() + "'>" + map.getId() + "</a>)<br></td>";
		k++;
		totalMaps++;
		if (k == 5) {
			maps += "</tr><tr>";
			k = 0;
		}
	}
}
	

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Monster Lookup: <%= monster.getName() %> - WebWiz</title>
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
		<table id="monster" width="99%" border="1" align="center" cellpadding="3">
          <tr>
            <td colspan = "5" style="overflow:scroll"><div align="center"><img src="IconExtractor?monster=<%= monster.getId() %>" alt=<%= monster.getName() %> /></div></td></tr><tr>
            <td width="75"><div align="center"><strong><%= monster.getName() %></strong></div></td>
            <td width="75"><strong>Monster ID:</strong> <%= monster.getId() %></td>
            <td width="75"><strong>Level:</strong> <%= monster.getLevel() %></td>
            <td width="75"><strong>HP/MP:</strong> <%= monster.getHp() %>/<%= monster.getMp() %></td>
            <td width="75"><strong>EXP:</strong> <%= monster.getExp() %></td>
          </tr>
          <tr>
            <td colspan="6"><center><strong>Item Drops</strong><br></center>
			</td>
          </tr>
          <tr>
            <td colspan="6" valign="top"><strong>EQUIP</strong><br>
				<%= equips %>
            </td></tr>
          <tr>
            <td colspan="6" valign="top"><strong>SETUP</strong><br>
				<%= setup %>
            </td>
          </tr>
          <tr>
            <td colspan="6" valign="top"><strong>ETC</strong><br>
				<%= etc %>
            </td>
		</tr>

          <tr>
          <td colspan="6" valign="top"><strong>CASH</strong><br>
	  <%= cash %>
          </td>
          </tr>

          <tr>
          <td colspan="6" valign="top"><strong>USE</strong><br>
	  <%= use %>
          </td>
          </tr>

	  <tr>
          <td colspan="6"><center><strong>Locations (<%= totalMaps %>)</strong><br></center></td>
          </tr>

	  <tr>
	  <% if(maps != "") {%>
	  <%= maps %>
    	  <%} else { %>
    	  <td colspan="6"><center>No Maps Available.</center></td>
    	  <% } %>
	  </tr>

	  <tr>
          <td colspan="6"><center><strong>Monster Skills(<%= monster.getSkillEntrySize() %>)</strong><br></center></td>
          </tr>

	  <tr>	
	  <% if(skills != "") {%>
	  <%= skills %>
    	  <%} else { %>
    	  <td colspan="6"><center>No Skills Available.</center></td>
    	  <% } %>
	  </tr>




            </td>
          </tr>
        </table>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer">
				Layout by Chip @ Hidden Street | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>