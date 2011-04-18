<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<ul>
	<c:forEach var="wallName" items="${data}">
		<li><a href="${wallName}">${wallName}</a></li>
	</c:forEach>
</ul>