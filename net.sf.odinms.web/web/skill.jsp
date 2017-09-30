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
<%@page import="net.sf.odinms.client.SkillFactory"%>
<%@page import="net.sf.odinms.client.ISkill"%>


<%
ISkill skill = SkillFactory.getSkill(Integer.parseInt(request.getParameter("skill")));
if(skill == null){
	skill = SkillFactory.getSkill(1000);
}
String skillName = SkillFactory.getSkillName(skill.getId());
String stats = "";
for(int i=1; i <= skill.getMaxLevel(); i++){
stats += "<tr><td colspan='1'><strong>LEVEL:</strong>" + i + "</td>"
+
"<td colspan='1'><strong>Watk:</strong>" + skill.getEffect(i).getWatk() + "</td>"
+
"<td colspan='1'><strong>Matk:</strong>" + skill.getEffect(i).getMatk() + "</td>"
+
"<td colspan='1'><strong>Wdef:</strong>" + skill.getEffect(i).getWdef() + "</td>"

+ "<td colspan='1'><strong>Mdef:</strong>" + skill.getEffect(i).getMdef() + "</td></tr>";

}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" version="-//W3C//DTD XHTML 1.1//EN" xml:lang="en">
	<head>
		<title>Skill Lookup: <%= skill.getId() %> - WebWiz</title>
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
		<table id="skill" width="99%" border="1" align="center" cellpadding="3">
		
		<tr>
		 	<td colspan = "5" style="overflow:scroll"><div  align="center"><img src="IconExtractor?skill=<%= skill.getId() %>" alt=<%= skillName %> /></div></td>
		</tr>

		<tr>
		    <td width="115"><div align="center"><strong>Skill Name:</strong> <%= skillName %></strong></div></td>
			
                <td width="70"><strong>Skill ID:</strong> <%= 				skill.getId() %></td>	
		</tr>
	
		<tr>


			<td colspan="1"><center><strong>Job Name:</strong> <a href="job.jsp?job=<%= skill.getJob().getId() %>"><%= skill.getJob().toString() %></a></center></td>

		
			<td colspan="2"><strong>Job ID:</strong> <%= skill.getJob().getId() %></td>


		</tr>
		<tr>
    			<td colspan="4"><center><strong>Stats(<%= skill.getMaxLevel() %>)</strong></center>
    			</td>
	  	</tr>

		<%--
		<tr><% if(stats != "") { %>
		<%= stats %>
    		<% } else { %>
    		<td colspan="4">No Stats Available.</td>
    		<% } %>
  		</tr>
		--%>



		</table>
		<br /><div id="top">
					<a href="#" target="_top">[Top]</a>				</div>
			</div>
			<div id="footer">
				Layout by Snow @ OdinMS | Best viewed with Mozilla Firefox.</div>
		</div>
	</body>
</html>
