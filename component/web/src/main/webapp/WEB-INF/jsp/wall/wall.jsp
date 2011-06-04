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

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:hidden path="id"/>

<form:label path="name">Name</form:label>
<form:input path="name" type="text"
	class="ui-widget-content ui-corner-all" id="name" name="name" />

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
		<c:import url="software.jsp">
			<c:param name="beanContext" value="${softwareAccess}" />
		</c:import>
	</div>
</div>


<script type="text/javascript">
	//$(function() {
	ajsl.event.register(visuwall.theme.def.event.wallForm);
	//});
</script>