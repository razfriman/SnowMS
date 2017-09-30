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
<%@page import="net.sf.odinms.server.life.MapleLifeFactory"%>
<%@page import="net.sf.odinms.server.life.MapleMonster"%>
<%@page import="net.sf.odinms.web.MonsterSearch"%>
<%@page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Monster Search Results - WebWiz</title>
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
	<%
	if (session.getAttribute("monsterSearch") == null)
		return;
	MonsterSearch search = (MonsterSearch) session.getAttribute("monsterSearch");
	int startAt = (request.getParameter("startat") == null ? 0 : Integer.parseInt(request.getParameter("startat")));
	int perPage = (request.getParameter("perpage") == null ? 20 : Integer.parseInt(request.getParameter("perpage")));
	List<Integer> toShow = search.getResults().subList(startAt, Math.min(startAt + perPage, search.getResults().size()));
        %>
	<b>Search Results</b><br>
	<em>&nbsp;&nbsp;Results <%= startAt + 1 %> - <%= Math.min(startAt + perPage, search.getResults().size()) %> of <%= search.getResults().size() %></em>
	<table width="99%" border="1" style="border: 1px dotted" align="center" cellpadding="1">
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>Level</th>
		<th>HP</th>
		<th>MP</th>
		<th>Exp</th>
	</tr>
	<%
	for (int monsterId : toShow) {
                MapleMonster monster = MapleLifeFactory.getMonster(monsterId);
	%>
		<tr>
			<td>
				<%= monsterId %>
			</td>
			<% try { %>
            <td>
				<a href="monster.jsp?monster=<%= monsterId %>"><%= monster.getName() %></a>
            </td>
			<td>
				<%= monster.getLevel() %>
			</td>
			<td>
				<%= monster.getHp() %>
			</td>
			<td>
				<%= monster.getMp() %>
			</td>
			<td>
				<%= monster.getExp() %>
			</td>
		</tr>
                <% } catch(NullPointerException e) { %>            
				</a>NullPointerException
            </td>
			<td>
				NPE
			</td>
			<td>
				NPE
			</td>
			<td>
				NPE
			</td>
			<td>
				NPE
			</td>
		</tr>
        <% }
	}
	%>
        </table>
	<%
	if (startAt > 0) {
	%>
	<a href="?startat=<%= startAt - perPage %>&amp;perpage=<%= perPage %>">&nbsp;&lt; Prev</a>
	<%
	}
	if (startAt + perPage < search.getResults().size()) {
	%>
	&nbsp;&nbsp;<a href="?startat=<%= startAt + perPage %>&amp;perpage=<%= perPage %>">Next &gt;</a>
	<%
	}
	%>
	<br/>
	<%
	for (int i = 0; i < Math.ceil(search.getResults().size() / (double) perPage); i++) {
		%>
		&nbsp;&nbsp;<a href="?startat=<%= i * perPage %>&amp;perpage=<%= perPage %>"><%= i + 1 %></a>
		<%
	}
	%>
<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer" align="">
				Layout by Chip @ Hidden Street | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>