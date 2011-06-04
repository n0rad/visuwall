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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:hidden path="softwareAccesses[0].id"/>

<form:label path="softwareAccesses[0].url">Url</form:label>
<form:input path="softwareAccesses[0].url" class="ui-widget-content ui-corner-all url"/>

<form:label path="softwareAccesses[0].pluginClassName">Type</form:label>
<form:select path="softwareAccesses[0].pluginClassName">
	<form:options items="${softwares}" itemLabel="name" itemValue="className"/>
</form:select>

<form:label path="softwareAccesses[0].name" >Name</form:label>
<form:input path="softwareAccesses[0].name" class="ui-widget-content ui-corner-all"/>

<form:label path="softwareAccesses[0].login" >Login</form:label>
<form:input path="softwareAccesses[0].login" class="ui-widget-content ui-corner-all"/>

<form:label path="softwareAccesses[0].password">Password</form:label>
<form:password path="softwareAccesses[0].password" class="ui-widget-content ui-corner-all"/>

<%-- <form:label path="softwareAccesses[0].showAll" >Selection</form:label> --%>
<%-- <form:input path="softwareAccesses[0].showAll" type="checkbox" id="allProjects" /><label for="allProjects">All Projects</label> --%>

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