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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Conversion - WebWiz</title>
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
        <h2 class="ad" border="0">Convert to Bytes</h2>
	<fieldset class="box">
	<form method="post" action="convert.jsp">
	<input type="text" name="int"/><input type="submit" value="Convert Int"/>
	</form>
	</fieldset>
	
	<fieldset class="box">

	<form method="post" action="convert.jsp">
	<input type="text" name="short"/><input type="submit" value="Convert Short"/>
	</form>
	</fieldset>

	<fieldset class="box">
	<form method="post" action="convert.jsp">
	<input type="text" name="long"/><input type="submit" value="Convert Long"/>
	</form>
	</fieldset>

	<fieldset class="box">
<form method="post" action="convert.jsp">
	<input type="text" name="byte"/><input type="submit" value="Convert Byte"/>
	</form>
	</fieldset>

	<fieldset class="box">
<form method="post" action="convert.jsp">
	<input type="text" name="ascii"/><input type="submit" value="Convert Ascii-String"/>
	</form>
	</fieldset>

	<fieldset class="box">
<form method="post" action="convert.jsp">
	<input type="text" name="mascii"/><input type="submit" value="Convert Maple-Ascii-String"/>
	</form>
	</fieldset>

	<fieldset class="box">
<form method="post" action="convert.jsp">
	<input type="text" name="hex"/><input type="submit" value="Convert Hex-String"/>
	</form>
	</fieldset>

	<fieldset class="box">
<form method="post" action="convert.jsp">
	<input type="text" name="ntascii"/><input type="submit" value="Convert Null-Terminated-Ascii-String"/>
	</form>
	</fieldset>










		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer">
				Layout by Chip @ Hidden Street | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>
