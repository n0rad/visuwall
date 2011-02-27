jwall.business.service.Project = {
		
	projects : function(callback) {
		$.getJSON("project", {}, callback);
	},
	
	project : function(projectName, callback) {
	    $.getJSON("project/get", {projectName : projectName}, callback);
	},
	
	status : function(callback) {
		$this = this;
		$.getJSON("project/status", {}, callback);
	}
};