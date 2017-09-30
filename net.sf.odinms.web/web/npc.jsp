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
<%@page import="net.sf.odinms.server.life.MapleLifeFactory"%>
<%@page import="net.sf.odinms.server.life.MapleNPC"%>

<%
Properties dbProps = new Properties();
dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
DatabaseConnection.setProps(dbProps);
MapleNPC npc = MapleLifeFactory.getNPC(Integer.parseInt(request.getParameter("npc")));
String script = npc.getStats().getScript();
if (script == null) {
	script = "NONE";
}
String maps = "";
for(int mapid : npc.getStats().getMaps()) {
//maps += "<tr><td colspan=\"2\"><center>" + mapid + "</center></td></tr>";
maps += "<tr><td colspan=\"2\"><center>" + mapid + "</center></td></tr>";

}


%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>NPC Lookup: <%= npc.getName() %> - WebWiz</title>
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
		<table id="npc" width="99%" border="1" align="center" cellpadding="3">
		
		<tr>
		 	<td colspan="5" style="overflow:scroll"><div align="center"><img src="IconExtractor?npc=<%= npc.getId() %>" alt=<%= npc.getName() %> /></div></td>
		</tr>

		<tr>
		    <td width="115"><div align="center"><strong><%= npc.getName() %></strong></div></td>
			
                <td width="70"><strong>Npc ID:</strong> <%= npc.getId() %></td>	
		</tr>

		<tr>
			<td colspan="6"><center><strong>Function:</strong> <%= npc.getStats().getFunction() %></center></td>
		</tr>
		<tr>
			<td colspan="6"><center><strong>Script:</strong> <%= script %></center></td>

		<tr>
			<td colspan="6"><center><strong>Maps(<%= npc.getStats().getMaps().size() %>)</strong<br></center></td>
		</tr>

		<% if (maps.length() > 0) { %>
		<%= maps %>
		<% } %>


		</table>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a></div>
			</div>
			<div id="footer">
				Layout by Snow @ OdinMS | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>
