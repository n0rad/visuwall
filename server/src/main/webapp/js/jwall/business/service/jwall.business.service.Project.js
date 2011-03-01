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
	}
};