<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<label for="softwareAccesses[0].url">Url</label>
<form:input path="softwareAccesses[0].url" class="ui-widget-content ui-corner-all url" id="url" name="url"/>

<label for="softwareAccesses[0].software">Type</label>
<form:select path="softwareAccesses[0].software">
	<form:options items="${softwares}" itemLabel="name" itemValue="id" />
</form:select>

<form:label path="softwareAccesses[0].name" >Name</form:label>
<form:input path="softwareAccesses[0].name" class="ui-widget-content ui-corner-all" id="name" name="name"/>

<form:label path="softwareAccesses[0].login" >Login</form:label>
<form:input path="softwareAccesses[0].login" class="ui-widget-content ui-corner-all" id="login" name="login"/>

<form:label path="softwareAccesses[0].password">Password</form:label>
<form:input path="softwareAccesses[0].password" type="password" class="ui-widget-content ui-corner-all" id="password" name="password"/>

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