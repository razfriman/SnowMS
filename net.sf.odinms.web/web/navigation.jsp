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
	<style type="text/css">
    table.tstyle {
        width: 700px;
        margin-left: auto;
        margin-right: auto;
        border-width: 1px 1px 1px 1px;
        border-spacing: 2px;
        border-style: dotted dotted dotted dotted;
        border-color: black black black black;
        border-collapse: collapse;
        background-color: white;
    }
    table.tstyle th {
        border-width: 1px 1px 1px 1px;
        padding: 1px 1px 1px 1px;
        border-style: dotted dotted dotted dotted;
        border-color: black black black black;
        background-color: white;
        -moz-border-radius: 0px 0px 0px 0px;
    }
    table.tstyle td {
        border-width: 1px 1px 1px 1px;
        padding: 1px 1px 1px 1px;
        border-style: dotted dotted dotted dotted;
        border-color: black black black black;
        background-color: white;
        -moz-border-radius: 0px 0px 0px 0px;
    }
    </style>
	<table border='0' cellpadding='0' cellspacing='1' width="700" align="center">
    <tr><td><center><img src="header.png"></center></td></tr>
    </table>
    	<table class="tstyle">
		<tr><td><a href="index.jsp"><center>Home</center></a></td>
		<td><a href="itemSearch.jsp"><center>Item</center></a></td>
		<td><a href="monsterSearch.jsp"><center>Monster</center></a></td>
		<td><a href="mapSearch.jsp"><center>Map</center></a></td>
		<td><a href="npcSearch.jsp"><center>NPC</center></a></td>
		<td><a href="jobSearch.jsp"><center>Job</center></a></td>
		<td><a href="skillSearch.jsp"><center>Skill</center></a></td>
		<td><a href="cashshopsnSearch.jsp"><center><CashShop-SN</center></a></td>
</tr>
    </table>
    <Frameset Scrolling="auto" Border="1">
    <frame src="http://localhost/drops11/getdrops.php?monster=100100">
    </Frameset>
