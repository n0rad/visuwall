<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<form:form id="wallForm" action="wall" method="post" modelAttribute="data">
	<jsp:include page="wall.jsp" />
</form:form>