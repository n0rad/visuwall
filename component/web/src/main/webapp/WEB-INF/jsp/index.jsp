<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Visuwall</title>
	<link rel="shortcut icon" href="favicon.ico" />
	
<%--
	${cssLinks}
	${jsLinks}
--%>
	<script type="text/javascript">curl = {baseUrl: 'res/js'};</script>
	<script type="text/javascript" src="res/js/curl.js"></script>
	
	<script type="text/javascript">
	curl(['main'], function(main) {
			main.start(${jsData});
	});
	</script>
</head>
<body>
<ul id="projectsTable"></ul>
<div id="overlay"></div>
</body>
</html>