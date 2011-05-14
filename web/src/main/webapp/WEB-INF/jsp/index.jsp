<%--

    Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>

<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Visuwall</title>
	<link rel="shortcut icon" href="favicon.ico" />
	${cssLinks}
	${jsLinks}
	<script type="text/javascript">
	$(function() {
		visuwall.init(${jsData});	
	});
	
	
	</script>
</head>
<body>
<jsp:include page="navigation.jsp"/>
<ul id="projectsTable"></ul>
<div id="overlay"></div>
<div id="contents">
	<div style="display:none" id="formCreation">
		 
		<form id="wallForm" action="/visuwall-web/wall/create.html" method="post"> 
		<input id="id" name="id" type="hidden" value=""/> 
		 
		<label for="name">Name</label> 
		<input id="name" name="name" name="name" class="ui-widget-content ui-corner-all" type="text" type="text" value=""/> 
		 
		<div id="softTabs"> 
			<ul> 
				<li><a href="#tabs-0">New</a> <span class="ui-icon ui-icon-close">Remove
						Tab</span></li> 
				<li><div id="softAdd" class="ui-state-default ui-corner-all"
						title=".ui-icon-plusthick"> 
						<span class="ui-icon ui-icon-plusthick"></span> 
					</div> 
				</li> 
			</ul> 
			<div id="tabs-0"> 
		<input id="softwareAccesses0.id" name="softwareAccesses[0].id" type="hidden" value=""/> 
		<label for="softwareAccesses0.url">Url</label> 
		<input id="softwareAccesses0.url" name="softwareAccesses[0].url" class="ui-widget-content ui-corner-all url" type="text" value=""/> 
		 
		<label for="softwareAccesses0.pluginClassName">Type</label> 
		<select id="softwareAccesses0.pluginClassName" name="softwareAccesses[0].pluginClassName"> 
			<option value="net.awired.visuwall.plugin.bamboo.BambooPlugin">Bamboo</option><option value="net.awired.visuwall.plugin.hudson.HudsonPlugin">Hudson</option><option value="net.awired.visuwall.plugin.sonar.SonarPlugin">Sonar</option> 
		</select> 
		<label for="softwareAccesses0.name">Name</label> 
		<input id="softwareAccesses0.name" name="softwareAccesses[0].name" class="ui-widget-content ui-corner-all" type="text" value=""/> 
		<label for="softwareAccesses0.login">Login</label> 
		<input id="softwareAccesses0.login" name="softwareAccesses[0].login" class="ui-widget-content ui-corner-all" type="text" value=""/> 
		<label for="softwareAccesses0.password">Password</label> 
		<input id="softwareAccesses0.password" name="softwareAccesses[0].password" class="ui-widget-content ui-corner-all" type="password" value=""/> 

		<!-- 
		<ol class="projects ui-widget-content ui-corner-all">
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj1</li>
			<li>proj2</li>
		</ol> --> 
			</div> 
		</div> 
			<input type="submit" value="submit"/> 
		</form>	
	</div>
</div>
</body>
</html>