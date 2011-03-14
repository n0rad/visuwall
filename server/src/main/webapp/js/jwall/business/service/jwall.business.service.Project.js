jwall.business.service.Project = {
		
	projects : function(callback) {
		$.getJSON("project", {}, callback);
	},
	
	project : function(projectName, callback) {
	    $.getJSON("project/get", {projectName : projectName}, callback);
	},
	
	status : function(callback) {
		$.getJSON("project/status", {}, callback);
	},
	
	finishTime : function(projectName, callback) {
		$.getJSON("project/finishTime", {projectName : projectName}, callback);
	},
	
	getBuild : function(projectName, buildId, callback) {
		LOG.info("loading build " + buildId + " for project " + projectName);
		$.getJSON("project/build", {projectName : projectName, buildId : buildId}, callback);		
	}
};