<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:label path="softwareAccesses[0].url">Url</form:label>
<form:input path="softwareAccesses[0].url" class="ui-widget-content ui-corner-all url"/>

<form:label path="softwareAccesses[0].software">Type</form:label>
<form:select path="softwareAccesses[0].software">
	<form:options items="${softwares}" itemLabel="name" itemValue="id"/>
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