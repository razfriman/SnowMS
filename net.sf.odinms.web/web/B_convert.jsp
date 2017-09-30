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
<%@page import="java.nio.ByteBuffer"%>
<%@page import="java.nio.ByteOrder"%>

<%@page import="net.sf.odinms.database.DatabaseConnection"%>
<%@page import="net.sf.odinms.tools.MaplePacketCreator"%>
<%@page import="net.sf.odinms.tools.data.output.LittleEndianWriter"%>
<%@page import="net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter"%>


<%
MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
String input = "";
String outputB = "";
String outputL = "";
if(request.getParameter("int") != null){
input = request.getParameter("int");
Integer.parseInt(request.getParameter("int")).toHexString();
}

if(request.getParameter("short") != null){
input = request.getParameter("short");
mplew.writeShort(Integer.parseInt(request.getParameter("short")));
}

if(request.getParameter("long") != null){
input = request.getParameter("long");
mplew.writeLong(Integer.parseInt(request.getParameter("long")));
}

if(request.getParameter("ibyte") != null){
input = request.getParameter("ibyte");
mplew.write(Integer.parseInt(request.getParameter("ibyte")));
}

if(request.getParameter("ascii") != null){
input = request.getParameter("ascii");
mplew.writeAsciiString(request.getParameter("ascii"));
}

if(request.getParameter("mascii") != null){
input = request.getParameter("mascii");
mplew.writeMapleAsciiString(request.getParameter("mascii"));
}

if(request.getParameter("hex") != null){
input = request.getParameter("hex");
mplew.writeHexString(request.getParameter("hex"));
}

if(request.getParameter("ntascii") != null){
input = request.getParameter("ntascii");
mplew.writeNullTerminatedAsciiString(request.getParameter("ntascii"));
}

if(mplew.getPacket().getBytes().length == 0){
mplew.write(0);
}
ByteBuffer bbuf = ByteBuffer.allocate(mplew.getPacket().getBytes().length);
bbuf.order(ByteOrder.BIG_ENDIAN);
ByteBuffer lbuf = ByteBuffer.allocate(mplew.getPacket().getBytes().length);
lbuf.order(ByteOrder.LITTLE_ENDIAN);

bbuf.wrap(mplew.getPacket().getBytes());
lbuf.wrap(mplew.getPacket().getBytes());
outputB = mplew.toString();
outputL = mplew.toString();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Maple Packet Converter - WebWiz</title>
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
		<table id="mplew" width="99%" border="1" align="center" cellpadding="3">
		


		<tr>
			<td colspan="1"><div align="center"><strong>Original Input:</strong> <%= input %></strong></div></td>
		</tr>

		<tr>
			<td colspan="1"><div align="center"><strong>Big Endian:</strong> <%= outputB %></strong></div></td>
		</tr>

		<tr>
			<td colspan="1"><div align="center"><strong>Little Endian:</strong> <%= outputL %></strong></div></td>
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