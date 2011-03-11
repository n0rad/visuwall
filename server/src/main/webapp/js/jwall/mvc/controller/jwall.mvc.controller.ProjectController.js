jwall.mvc.controller.ProjectController = {
		
	projectsView : null,
	
	projectService : null,
	
	projects : {},
	
	init : function() {
		this.projectsView = jwall.mvc.view.Projects;
		this.projectService = jwall.business.service.Project;
	},
	
	buildProjects : function() {
		var $this = this;
		jwall.business.service.Project.projects(function(projects) {
			for (var i = 0; i < projects.length; i++) {
				$this.addProject(projects[i]);
			}
		});

		// TODO remove
		$('ul#projectsTable li:last-child').live('click', function() {
		    $(this).prependTo('ul#projectsTable');
		});
	},
	
	addProject : function(projectData) {
		this.projectsView.addProject(projectData.name, projectData.description);
		this._updateProject(projectData);	
	},

	updateStatus : function() {
		var $this = this;
		$this.projectService.status(function (projectsStatus) {
			var projectDone = [];
			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];

				if ($this.projects[status.name] == undefined) {
					// this is a new project
					$this.projectService.project(status.name, function(newProjectData) {
						$this.addProject(newProjectData);
					});
					continue;
				}
				var project = $this.projects[status.name];
				LOG.debug('Update status for project ' + status.name);
				$this._updateBuilding(project, status.building);
				$this._checkVersionChange(project, status);

				projectDone.push(status.name);
			}
			
			// looking for project to delete
			for (var key in $this.projects) {
				if (!projectDone.contains(key)) {
					$this._removeProject(key);
				}
			}
		});
	},
	
	_updateProject : function(project) {
		this._updateState(project);
		this._updateLastBuild(project);
		this._updateAgo(project);
		
		// call updateBuilding like we just receive the status
		var wasBuilding = project.hudsonProject.building;
		project.hudsonProject.building = false;
		this._updateBuilding(project, wasBuilding);		

		// save project for updates
		this.projects[project.name] = project;	
	},
	
	_updateLastBuild : function(project) {
		if (project.hudsonProject.lastBuild != null) {
			this.projectsView.updateBuildTime(project.name, project.hudsonProject.lastBuild.duration);
			
			this.projectsView.updateCommiters(project.name, project.hudsonProject.lastBuild.commiters);
			if (project.rulesCompliance != 0) {
				this.projectsView.updateQuality(project.name, project.qualityResult.measures);
			} else {
				this.projectsView.updateQuality(project.name, { });					
			}
			this.projectsView.updateUTCoverage(project.name, project.coverage);
			this.projectsView.updateUT(project.name, 
					project.hudsonProject.lastBuild.testResult.failCount,
					project.hudsonProject.lastBuild.testResult.passCount,
					project.hudsonProject.lastBuild.testResult.skipCount,
					project.coverage);
			this.projectsView.updateITCoverage(project.name, 0);
			this.projectsView.updateIT(project.name, 0,0,0);

		}
	},
	
	_updateAgo : function(project) {
		if (project.hudsonProject.lastBuild != null) {
			this.projectsView.updateAgo(project.name, new Date(project.hudsonProject.lastBuild.startTime + project.hudsonProject.lastBuild.duration));					
		} else {
			this.projectsView.updateAgo(project.name, 0);
		}
	},
	
	/////////////////////////////////////////////////////////////////////////
		
	_removeProject : function(projectName) {
		delete this.projects[projectName];
		this.projectsView.removeProject(projectName);
	},
	
	_updateState : function(project) {
		switch(project.state) {
		case 'SUCCESS':
			this.projectsView.displaySuccess(project.name);
			break;
		case 'NEW':
			this.projectsView.displayNew(project.name);
			break;
		case 'ABORTED':
			this.projectsView.displayAborted(project.name);
			break;
		case 'FAILURE':
			this.projectsView.displayFailure(project.name);			
			break;
		case 'UNSTABLE':
			this.projectsView.displayUnstable(project.name);
			break;
		default:
			LOG.error('Unknown project state : ' + project.state);
		}
	},
	
	_updateBuilding : function(project, isBuilding) {
		if (isBuilding) {
			if (!project.hudsonProject.building) {
				var $this = this;
				jwall.business.service.Project.finishTime(project.name, function(data) {
					$this.projectsView.updateCountdown(project.name, new Date(data));
				});
				LOG.info("project is now building : " + project.name);
				this.projectsView.showBuilding(project.name);
				project.hudsonProject.building = true;
			}
		} else if (project.hudsonProject.building) {
			LOG.info("building is now over for project : " + project.name);
			this.projectsView.stopBuilding(project.name);
			project.hudsonProject.building = false;

			var $this = this;
			this.projectService.project(project.name, function(newProjectData) {
				$this._updateProject(newProjectData);
			});			
		} else {
			this.projectsView.stopBuilding(project.name);
		}
	},
	
	_checkVersionChange : function(project, status) {
		if (this._checkVersionChangeAndNotBuilding(project, status)) {
			LOG.info("Server is not building and version has change, we need an update");
			var $this = this;
			this.projectService.project(project.name, function(newProjectData) {
				$this._updateProject(newProjectData);
			});
		}
	},	
	
	_checkVersionChangeAndNotBuilding : function(projectData, projectStatus) {
		if (projectData.hudsonProject.lastBuildNumber != projectStatus.lastBuildId
			&& !projectStatus.building) {
			return true;
		}
		return false;
	}
	
};

$(function (){
	jwall.mvc.controller.ProjectController.init();
});