define([ 'jquery' ], function($) {
	"use strict";
	
	var projectService = {
		findProject : function(wallName, projectName, callback) {
			$.getJSON('wall/' + wallName + '/project/' + projectName + '/', {},
					callback);
		}
	};

	return projectService;

});
