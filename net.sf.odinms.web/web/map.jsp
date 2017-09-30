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
<%@page import="java.io.FileReader"%>
<%@page import="java.util.Properties"%>
<%@page import="net.sf.odinms.database.DatabaseConnection"%>
<%@page import="net.sf.odinms.server.life.MapleNPC"%>
<%@page import="net.sf.odinms.server.life.MapleMonster"%>
<%@page import="net.sf.odinms.server.life.MapleLifeFactory"%>
<%@page import="net.sf.odinms.server.maps.MapleMap"%>
<%@page import="net.sf.odinms.server.maps.MapleGenericPortal"%>
<%@page import="net.sf.odinms.server.MaplePortal"%>
<%@page import="net.sf.odinms.server.maps.MapleMapObject"%>
<%@page import="net.sf.odinms.web.MapleMapProvider"%>

<%
Properties dbProps = new Properties();
		dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
		DatabaseConnection.setProps(dbProps);
		MapleMapProvider mp = MapleMapProvider.getInstance();
		String npcs = "";
		String monsters = "";
		String portals = "";
		int i = 0;
		int j = 0;
		int k = 0;
		MapleMap map;
		if (request.getParameter("map") != null) {
			map = mp.getMap(Integer.parseInt(request.getParameter("map")));
		} else {
			map = mp.getMap(0);
		}

		if (map == null) {
			map = mp.getMap(0);
		}



		for (MapleMapObject mo : map.getMapObjects()) {
			if (mo instanceof MapleNPC) {
				MapleNPC npc = (MapleNPC) mo;
				if (npc.getId() == 9201066 || npc.getId() == 9250023 || npc.getId() == 9250024 || npc.getId() == 9250025 || npc.getId() == 9250026 || npc.getId() == 9250042 || npc.getId() == 9250043 || npc.getId() == 9250044 || npc.getId() == 9250045 || npc.getId() == 9250046 || npc.getId() == 9270000 || npc.getId() == 9270001 || npc.getId() == 9270002 || npc.getId() == 9270003 || npc.getId() == 9270004 || npc.getId() == 9270005 || npc.getId() == 9270006 || npc.getId() == 9270007 || npc.getId() == 9270008 || npc.getId() == 9270009 || npc.getId() == 9270010 || npc.getId() == 9270011 || npc.getId() == 9270012 || npc.getId() == 9270013 || npc.getId() == 9270014 || npc.getId() == 9270015 || npc.getId() == 9270016) {
					npcs += "<td><center><br><b>" + npc.getName() + "</b> <br>(<b>ID:</b> <a href='npc.jsp?npc=" + npc.getId() + "'>" + npc.getId() + "</a>)<br></td>";
				} else {
					npcs += "<td><center><img src='IconExtractor?npc=" + npc.getId() + "'><br><b>" + npc.getName() + "</b> <br>(<b>ID:</b> <a href='npc.jsp?npc=" + npc.getId() + "'>" + npc.getId() + "</a>)<br></td>";
				}
				i++;
				if (i == 4) {
					npcs += "</tr><tr>";
					i = 0;
				}
			}
		}

		for (MapleMapObject mon : map.getMapObjects()) {
			if (mon instanceof MapleMonster) {
				MapleMonster mons = (MapleMonster) mon;
				if (!monsters.contains("monster.jsp?monster=" + mons.getId())) {
					if (mons.getId() == 9400569) {
						monsters += "<td><center><b>" + mons.getName() + "</b> (<b>ID:</b> <a href='monster.jsp?monster=" + mons.getId() + "'>" + mons.getId() + "</a>)<br></td>";
					} else {
						monsters += "<td><center><img src='IconExtractor?monster=" + mons.getId() + "'> <br/><b>" + mons.getName() + "</b><br>(<b>ID:</b> <a href='monster.jsp?monster=" + mons.getId() + "'>" + mons.getId() + "</a>)<br></td>";
					}
					j++;
				}
				if (j == 4) {
					monsters += "</tr><tr>";
					j = 0;

				}
			}
		}


		for (MaplePortal p : map.getPortals()) {
			MaplePortal portal = p;
			MapleGenericPortal gportal = (MapleGenericPortal) p;
			if (gportal.getTargetMapId() != 999999999) {
				MapleMap to = mp.getMap(gportal.getTargetMapId());
				if (to.getId() != map.getId()) {
					portals += "<td><center><b>" + to.getStreetName() + "</b><br>" + to.getMapName() + "<br>(<b>ID:</b> 	<a href='map.jsp?map=" + to.getId() + "'>" + to.getId() + "</a>)<br></td>";
					k++;
					if (k == 4) {
						portals += "</tr><tr>";
						k = 0;
					}
				}
			}
		}




%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Map Lookup: <%= map.getMapName() %> - WebWiz</title>
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
		<table id="map" width="99%" border="1" align="center" cellpadding="3">

	<tr>
		<td colspan="5" style="overflow:scroll"><div align="center">
		<a href="IconExtractor?maprender=<%= map.getId() %>">
		<img src="IconExtractor?map=<%= map.getId() %>" alt="<%= map.getMapName() %>"></div></td>
		</a>


	</tr>

	<tr>
		<td colspan="2"><strong><%= map.getStreetName() %>:</strong><br> <%= map.getMapName() %>
		</td>

		<td colspan="2"><strong>Map ID:</strong> <%= map.getId() %>
		</td>
	</tr>

	<tr>
    		<td colspan="4"><center><strong>Portals</strong></center>
    		</td>
  	</tr>

	<tr>	
		<% if(portals != "") { %>
		<%= portals %>
    		<% } else { %>
    		<td colspan="4">No Portals Available.</td>
    		<% } %>
  	</tr>

	<tr>
    		<td colspan="4"><center><strong>NPCs</strong></center>
    		</td>
  	</tr>

	
	<tr>	
		<% if(npcs != "") { %>
		<%= npcs %>
    		<% } else { %>
    		<td colspan="4">No NPC's Available.</td>
    		<% } %>
  	</tr>

	
	 

	<tr>
    		<td colspan="4"><center><strong>Monsters</strong></center>
    		</td>
  	</tr>

	<tr>	
		<% if(monsters != "") { %>
		<%= monsters %>
    		<% } else { %>
    		<td colspan="4">No Monster Available.</td>
    		<% } %>
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
