define(['main'], function(main) {

	define('visuwallRootUrl', [], function() {
		return "${visuwallServerUrl}";
	});

	define('visuwallWebVersion', [], function() {
		return "${project.version}";
	});

	return main;
});
