jwall.business.service.project = {

	projects : {},
		
	serviceUri : "project",
	
		
	getProjects : function(callback) {
		LOG.debug('calling getProjets');
		
		$this = this;
		$.getJSON(this.serviceUri, {}, function(data) {
			
			for (var i = 0; i < data.length; i++) {
				var currentProject = data[i];
				$this.projects[currentProject.name] = currentProject;
			}
			
			callback(data);
		});
	},
	
	getProject : function(projectName, callback) {
	    $.getJSON(this.serviceUri, {name : projectName}, callback);
	},
	
	update : function() {
		$this = this;
		$.getJSON("project/status", {}, function(data) {
			LOG.debug("receive update");
			
			for (var i = 0; i < data.length; i++) {
				var currentProject = data[i];
				var myBuildingStatus = $this.projects[currentProject.name].hudsonProject.building;
				var myLastBuildNum = $this.projects[currentProject.name].hudsonProject.lastBuildNumber;
				var serverBuilderStatus = currentProject.building;
				var serverLastBuildNum = currentProject.lastBuildId;
				
				if (myBuildingStatus == true && serverBuilderStatus == false) {
					LOG.info("build is over on server for " + currentProject.name);
				}
				if (myBuildingStatus == false && serverBuilderStatus == true) {
					LOG.info("server is now building" + currentProject.name);
				}
				
				if (myLastBuildNum != serverLastBuildNum) {
					LOG.info("server build is updated");
				}
				
				
			}
		});
	}

};