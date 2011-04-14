<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${param.code != null}">
Error code : ${param.code}
</c:if>

<form id="loginForm" action="login" method="post">
	<input name="j_username" />
	<input type="password" name="j_password" />
	<input type="submit" value="Login" />
</form>