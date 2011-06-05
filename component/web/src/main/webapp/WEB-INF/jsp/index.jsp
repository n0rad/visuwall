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
		<label id="softwareAccesses0.urlcheck" class="check"></label>
		<input id="softwareAccesses0.url" name="softwareAccesses[0].url" class="ui-widget-content ui-corner-all url" type="text" value=""/>
		<label for="softwareAccesses0.login">Login</label> 
		<input id="softwareAccesses0.login" name="softwareAccesses[0].login" class="ui-widget-content ui-corner-all disabled" type="text" value="" disabled="disabled"/> 
		<label for="softwareAccesses0.password">Password</label> 
		<input id="softwareAccesses0.password" name="softwareAccesses[0].password" class="ui-widget-content ui-corner-all disabled" type="password" value="" disabled="disabled"/> 

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
			<input class="submit" type="submit" value="Save"/> 
			<div class="loader"></div>
			<div class="success"></div>
			<div class="failure"></div>
<!-- 			<input id="delete" type="button" value="Delete"/>  -->
		</form>	
	</div>
	<div style="display:none" id="helperdiv">
		<table>
			<thead>
				<tr>
					<th>Color</th>
					<th>Project State</th>
				</tr>
			</thead>
		
			<tr>
				<td class="success-state"></td>
				<td>Project in Success</td>
			</tr>
			<tr>
				<td class="failure-state"></td>
				<td>Project compilation failure</td>
			</tr>
			<tr>
				<td class="new-state"></td>
				<td>New Project without any builds</td>
			</tr>
			<tr>
				<td class="aborted-state"></td>
				<td>Last build was aborted</td>
			</tr>
			<tr>
				<td class="unstable-state"></td>
				<td>project with fail test(s)</td>
			</tr>
			<tr>
				<td class="notbuilt-state"></td>
				<td>the build was not able to run</td>
			</tr>
			<tr>
				<td class="unknown-state"></td>
				<td>Unknown state of project</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>