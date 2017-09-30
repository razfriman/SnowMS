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
<%@page import="net.sf.odinms.server.CashItemFactory"%>
<%@page import="net.sf.odinms.server.CashItemInfo"%>
<%@page import="net.sf.odinms.server.MapleItemInformationProvider"%>
<%@page import="net.sf.odinms.client.Equip"%>
<%@page import="net.sf.odinms.client.IItem"%>


<%
Properties dbProps = new Properties();
dbProps.load(new FileReader(System.getProperty("net.sf.odinms.db")));
DatabaseConnection.setProps(dbProps);
MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
CashItemInfo CItem = null;
int itemId;
int sn;

if(request.getParameter("sn") != null) {
sn = Integer.parseInt(request.getParameter("sn"));
CItem = CashItemFactory.getItem(sn);
itemId = CashItemFactory.getItem(sn).getId();

}else if(request.getParameter("item") != null) {
itemId = Integer.parseInt(request.getParameter("item"));
CItem = CashItemFactory.getSn(itemId);
sn = CItem.getSn();

}else if(request.getParameter("hex") != null) {
String hex = request.getParameter("hex");
hex = hex.replace(" ", "");
hex = hex.replace("'", "");
hex = hex.replace("\"", "");
hex = hex.replace("!", "");
sn = Integer.parseInt(hex, 16);
CItem = CashItemFactory.getItem(sn);
itemId = CashItemFactory.getItem(sn).getId();

}else if(request.getParameter("hexrev") != null) {
String hexreverse = request.getParameter("hexrev");
hexreverse = hexreverse.replace(" ", "");
hexreverse = hexreverse.replace(" ", "");
hexreverse = hexreverse.replace("'", "");
hexreverse = hexreverse.replace("\"", "");
hexreverse = hexreverse.replace("!", "");
hexreverse = hexreverse.replace("@", "");
String part1 = hexreverse.substring(0,2);
String part2 = hexreverse.substring(2,4);
String part3 = hexreverse.substring(4,6);
String part4 = hexreverse.substring(6,8);
hexreverse = part4 + part3 + part2 + part1;
sn = Integer.parseInt(hexreverse, 16);
CItem = CashItemFactory.getItem(sn);
itemId = CashItemFactory.getItem(sn).getId();


}else{

itemId = Integer.parseInt(request.getParameter("item"));
//sn = CashItemFactory.getSN(itemId);
sn = 10000000;
CItem = CashItemFactory.getItem(sn);
}

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>CashShop-SN Lookup: <%= CItem.getId() %> - WebWiz</title>
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
		<table id="cashshopsn" width="99%" border="1" align="center" cellpadding="3">
		
		<tr>
		 	<td colspan="1" style="overflow:scroll"><div  align="center"><img src="IconExtractor?id=<%= CItem.getId() %>" alt=<%= ii.getName(CItem.getId()) %> /></div></td>
		</tr>
		<tr>
		    	<td colspan="1"><div align="center"><strong>Item-Name:</strong> <%= ii.getName(CItem.getId()) %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>SN-ID:</strong> <%= CItem.getSn() %></strong></div></td>
		</tr>
		<tr>
                	<td colspan="1"><center><strong>Item ID:</strong> <a href="item.jsp?item=<%= CItem.getId() %>"><%= CItem.getId() %></a></center></td>

		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>Quantity:</strong> <%= CItem.getCount() %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>Price:</strong> <%= CItem.getPrice() %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>Period:</strong> <%= CItem.getPeriod() %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>Priority:</strong> <%= CItem.getPriority() %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>Req-Level:</strong> <%= CItem.getReqLevel() %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>Gender:</strong> <%= CItem.getGender() %></strong></div></td>
		</tr>
		<tr>
			<td colspan="1"><div align="center"><strong>On-Sale:</strong> <%= CItem.getOnSale() %></strong></div></td>
		</tr>
	





		</table>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer">
				Layout by Snow @ OdinMS | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>