jwall.business.service.project = {
	serviceUri : "project",
		
	getProjects : function(callback) {
		LOG.debug('calling getProjets');
		$.getJSON(this.serviceUri, {}, callback);
	},
	
	getProject : function(projectName, callback) {
	    $.getJSON(this.serviceUri, {name : projectName}, callback);
	},
	
	update : function() {
		$.getJSON("project/status", {}, function(data) {
			LOG.debug("receive update");
		});
	}

};