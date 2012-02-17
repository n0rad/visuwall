define([ 'jquery', 'visuwallRootUrl'], function($, rootUrl) {
	"use strict";
	
	var projectService = {
		findProject : function(wallName, projectName, callback) {
			$.getJSON(rootUrl + 'wall/' + wallName + '/project/' + projectName + '/', {},
					callback);
		}
	};

	return projectService;

});
