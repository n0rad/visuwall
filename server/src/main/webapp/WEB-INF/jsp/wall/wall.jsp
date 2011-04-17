<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
	ajsl.event.register(visuwall.mvc.wall);
	$("#wallForm").submit(function() {
		$.post(this.action, $(this).serialize(), function(data) {
			   alert("Data Loaded: " + data);
		});
		return false;
	});
	//});
</script>