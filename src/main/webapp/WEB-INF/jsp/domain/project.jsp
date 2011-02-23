<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<td class="project 
<c:choose>
	<c:when test="${project.hudsonProject.lastBuild.successful == true}">
	success
	</c:when>
	<c:otherwise>
	<c:set var="fail" value="true"/>
	failure</c:otherwise>
</c:choose>
">

	<p class="projectName"><c:out value="${project.name}"/><span id="when">(1d)</span></p>	
<%-- 	<c:if test="${not empty fail}">--%>
		${project.hudsonProject.lastBuild.testResult.failCount}
		${project.hudsonProject.lastBuild.testResult.passCount}
		${project.hudsonProject.lastBuild.testResult.skipCount}
		${project.hudsonProject.lastBuild.testResult.totalCount}
		${project.hudsonProject.lastBuild.testResult.integrationTestCount}
<%--	</c:if>--%>
</td>
