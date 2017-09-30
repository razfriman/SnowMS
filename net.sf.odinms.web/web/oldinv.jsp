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
<%@page import="net.sf.odinms.database.DatabaseConnection"%>
<%@page import="net.sf.odinms.client.MapleCharacter"%>
<%@page import="net.sf.odinms.client.IItem"%>
<%@page import="net.sf.odinms.client.MapleInventoryType"%>
<%@page import="net.sf.odinms.client.MapleClient"%>
<%@page import="net.sf.odinms.server.TimerManager"%>
<%@page import="net.sf.odinms.server.MapleItemInformationProvider"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>OdinMS Web</title>
		<link rel="shortcut icon" href="images/favicon.ico" />
    </head>
    <body>
	<%
	Properties dbProps = new Properties();
	dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
	DatabaseConnection.setProps(dbProps);
	TimerManager.getInstance().start();
	MapleCharacter chr = MapleCharacter.loadCharFromDB(Integer.parseInt(request.getParameter("char")), new MapleClient(null, null, null), false);
	MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
	%>
        <h2><%= chr.getName() %>'s inventory</h2>
	<a href="?char=<%= chr.getId() %>&amp;type=1">EQUIP</a> <a href="?char=<%= chr.getId() %>&amp;type=2">USE</a> <a href="?char=<%= chr.getId() %>&amp;type=3">SETUP</a> <a href="?char=<%= chr.getId() %>&amp;type=4">ETC</a> <a href="?char=<%= chr.getId() %>&amp;type=5">CASH</a><br/>
	<table>
	<%
	MapleInventoryType type;
	if (request.getParameter("type") != null)
		type = MapleInventoryType.getByType(Byte.parseByte(request.getParameter("type")));
	else
		type = MapleInventoryType.EQUIP;
	for (IItem item : chr.getInventory(type)) {
		%>
		<tr>
			<td>
				<img src="IconExtractor?id=<%= item.getItemId() %>" alt="Item"/><br/>
			</td>
			<td>
				<%= item.getQuantity() %>
			</td>
			<td>
				<a href="item.jsp?char=<%= chr.getId() %>&amp;type=<%= type.getType() %>&amp;slot=<%= item.getPosition() %>"><%= ii.getName(item.getItemId()) %></a>
			</td>
		</tr>
		<%
	}
	%>
	</table>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer">
				Layout by Chip @ Hidden Street | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>
