jwall.business.service.project = {
	serviceUri : "project",
		
	getProjects : function(callback) {
		LOG.info('calling getProjets');
		$.getJSON(this.serviceUri, {}, callback);
	},
	
	getProject : function(projectName, callback) {
	    $.getJSON(this.serviceUri, {name : projectName}, callback);
	}

};