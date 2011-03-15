jwall.persistence.dao.ProjectDAO = {
		
		_projects : {},
		
		_projectService : null,
		
		init : function() {
			this._projectService = jwall.business.service.Project;
		},
		
		saveProject : function(project) {
			this._projects[project.name] = project;
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
		
		callbackPreviousCompletedBuild : function(projectName, callback) {
			var buildIdIndex = this._getPreviousCompletedBuildIdIndex(projectName);
			this._callbackPreviousCompletedBuildRec(projectName, callback, buildIdIndex);
		},
		
		_callbackPreviousCompletedBuildRec : function(projectName, callback, buildIdIndex) {
			var project = this._projects[projectName];
			var $this = this;
			$this._projectService.getBuild(projectName, project.hudsonProject.buildNumbers[buildIdIndex], function(buildData) {
				if (buildData.state != 'ABORTED') {
					callback(buildData);
					return;
				} else {
					$this._callbackPreviousCompletedBuildRec(projectName, callback, buildIdIndex + 1);
				}
			});
		},
		
		

		/////////////////////////////////////

		_getPreviousCompletedBuildIdIndex : function(projectName) {
			var project = this._projects[projectName];
			var buildNumbers = project.hudsonProject.buildNumbers;
			for (var i = 0; i <= buildNumbers.length; i++) {
				
				// skip building
				if (i == 0 && buildNumbers[i] == project.hudsonProject.currentBuild.buildNumber) {
					continue;
				}
				
				// skip lastBuild
				if (buildNumbers[i] == project.hudsonProject.completedBuild.buildNumber) {
					continue;
				}
				
				return i;
			}
			return null;
		}
		
};

$(function() {
	jwall.persistence.dao.ProjectDAO.init();
});