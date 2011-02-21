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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>	
<html>
<!-- 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<jsp:include page="res/js.jsp"/>
<jsp:include page="res/css.jsp"/>
</head>
<style>
.projects {
	height: 100%;
	width: 100%;
	border-collapse: separate;
	border-spacing: 5px;
}

.projects tr td {
	padding: 5px;
	vertical-align: middle;
	margin: 1%;
	overflow: hidden;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	-border-radius: 10px;
	color: white;
	font-size: medium;
	white-space: nowrap;
	border-top-left-radius: 10px 10px;
	border-top-right-radius: 10px 10px;
	border-bottom-right-radius: 10px 10px;
	border-bottom-left-radius: 10px 10px;
}

.project p.projectName {
	font-size: 4em;
	font-weight: bold;
	text-align: center;
}

.projects .success {
	background-color: green;
}


.projects .failure {
	background-color: red;
}


.project p.time {
	font-size: 2em;
}




</style>

<body>

<table class="projects" height="100%" width="100%">
	<tr>
	<c:forEach varStatus="status" var="project" items="${projects}">

 		<jsp:include page="domain/project.jsp"/>

		<%-- new row --%>
		<c:if test="${not status.last && status.count % jobsPerRow == 0}">
			<c:if test="${status.count > 0}">
				</tr>
			</c:if>
			<tr>
		</c:if>
	</c:forEach>
	</tr>
</table>





<script type="text/javascript">
// Infinite blink/fade 
function effectFadeIn(classname) {
	$("."+classname).fadeTo(1000, 0.4, effectFadeOut(classname));
}
function effectFadeOut(classname) {
	$("."+classname).fadeTo(1000, 1, effectFadeIn(classname));
}
$(document).ready(function(){
effectFadeIn('projectPart');
});
</script>


</body>
</html>