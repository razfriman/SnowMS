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
<%@page import="net.sf.odinms.database.DatabaseConnection"%>
<%@page import="net.sf.odinms.server.maps.MapleMap"%>
<%@page import="net.sf.odinms.server.life.MapleLifeFactory"%>
<%@page import="net.sf.odinms.server.life.MapleNPC"%>
<%@page import="net.sf.odinms.web.MapleMapProvider"%>
<%@page import="net.sf.odinms.web.NPCSearch"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.FileReader"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Search Results - WebWiz</title>
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
	<%
	Properties dbProps = new Properties();
	dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
	DatabaseConnection.setProps(dbProps);
	if (session.getAttribute("npcSearch") == null)
		return;
	NPCSearch search = (NPCSearch) session.getAttribute("npcSearch");
	int startAt = (request.getParameter("startat") == null ? 0 : Integer.parseInt(request.getParameter("startat")));
	int perPage = (request.getParameter("perpage") == null ? 20 : Integer.parseInt(request.getParameter("perpage")));
	List<Integer> toShow = search.getResults().subList(startAt, Math.min(startAt + perPage, search.getResults().size()));
        %>
	<h2 class="ad" border="0">Search results</h2>
	<em>Results <%= startAt + 1 %> - <%= Math.min(startAt + perPage, search.getResults().size()) %> of <%= search.getResults().size() %></em>
	<table border="1" style="border: 1px dotted" cellpadding="1">
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>Picture</th>

	</tr>
	<%
	for (int npcId : toShow) {
                MapleNPC npc = MapleLifeFactory.getNPC(npcId);
	%>
		<tr>
			<td>
				<%= npc.getId() %>
			</td>
			<td>
				<a href="npc.jsp?npc=<%= npc.getId() %>"><%= npc.getName() %></a>
			</td>
			<td>
			<img src="IconExtractor?npc=<%= npc.getId() %>" alt=<%= npc.getName() %> />
			</td>
		</tr>
	<%
	}
	%>
        </table>
	<%
	if (startAt > 0) {
	%>
	<a href="?startat=<%= startAt - perPage %>&amp;perpage=<%= perPage %>">&lt; Prev</a>
	<%
	}
	if (startAt + perPage < search.getResults().size()) {
	%>
	&nbsp;<a href="?startat=<%= startAt + perPage %>&amp;perpage=<%= perPage %>">Next &gt;</a>
	<%
	}
	%>
	<br/>
	<%
	for (int i = 0; i < Math.ceil(search.getResults().size() / (double) perPage); i++) {
		%>
		&nbsp;<a href="?startat=<%= i * perPage %>&amp;perpage=<%= perPage %>"><%= i + 1 %></a>
		<%
	}
	%>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer">
				Layout by Chip @ Hidden Street | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>
