jwall.persistence.dao.ProjectDAO = {
		
		_projects : {},
		
		_projectService : null,
		
		init : function() {
			this._projectService = jwall.business.service.Project;
		},
		
		saveProject : function(project) {
			this._projects[project.name] = project;
			this._rebuildBuilding(project.name);
		},
		
		removeProject : function(projectName) {
			delete this._projects[projectName];
		},
		
		isProject : function(projectName) {
			return this._projects[projectName] == undefined;
		},
		
		getProject : function(projectName) {
			return this._projects[projectName];
		},
		
		getProjects : function() {
			return this._projects;
		},
		
		getLastTwoBuildIfAvailable : function(projectName, callback) {
			var $this = this;
			var project = this._projects[projectName];
			if (project.hudsonProject.buildNumbers[1] != undefined) {
				this.getLastBuild(projectName, function (lastBuild) {
					$this._projectService.getBuild(projectName, project.hudsonProject.buildNumbers[1], function(buildData) {
						callback(lastBuild, buildData);
					});
				});
			} else {
				return false;
			}
		},
		
		/**
		 * return false is not build found
		 */
		getLastBuild : function(projectName, callback) {
			var project = this._projects[projectName];
			if (project.hudsonProject.lastBuild != null) {
				callback(project.hudsonProject.lastBuild);
			} else if (project.hudsonProject.buildNumbers[0] != undefined) {
				var $this = this;
				this._projectService.getBuild(projectName, project.hudsonProject.buildNumbers[0], function(buildData) {
					project.hudsonProject.lastBuild = buildData;
					callback(buildData);
				});
			} else {
				return false;
			}
		},
		
		/////////////////////////////////////
		
		_rebuildBuilding : function(projectName) {
			var project = this._projects[projectName];
			if (project.hudsonProject.building) {
				project.hudsonProject.building = project.hudsonProject.lastBuild;
				project.hudsonProject.buildNumbers.shift();
				project.hudsonProject.lastBuild = null;
			} else {
				project.hudsonProject.building = null;
			}
		}
		
};

$(function() {
	jwall.persistence.dao.ProjectDAO.init();
});