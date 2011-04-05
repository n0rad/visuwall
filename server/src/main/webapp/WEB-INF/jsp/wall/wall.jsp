<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<label>Name</label>
<input type="text" class="ui-widget-content ui-corner-all" value="" id="name" name="name">


<div id="softwareTabs">
	<ul>
		<li><a href="#tabs-Fluxx">Fluxx</a> <span class="ui-icon ui-icon-close">Remove Tab</span></li>
		<li><a href="#tabs-Awired">Awired</a> <span class="ui-icon ui-icon-close">Remove Tab</span></li>
		<li>
			<div id="softwareAdd" class="ui-state-default ui-corner-all" title=".ui-icon-plusthick"><span class="ui-icon ui-icon-plusthick"></span></div>		
		</li>
<!-- 		<li><span id="softwareAdd" class="ui-icon ui-icon-plusthick">Add</span></li> -->
	</ul>
	<div id="tabs-Fluxx">
		<jsp:include page="software.jsp"/>
	</div>
	<div id="tabs-Awired">
		<jsp:include page="software.jsp"/>
	</div>	
</div>

<script type="text/javascript">
$(function() {
	$('#softwareTabs').tabs();
	$('OL.projects').selectable({selected : function(e) {
	    $(this).toggleClass("selected");
	}});
	
	//hover states on the static widgets
	$('#softwareAdd').hover(
		function() { $(this).addClass('ui-state-hover'); }, 
		function() { $(this).removeClass('ui-state-hover'); }
	);
	
	$("INPUT#allProjects").button();
	
	
});
</script>
