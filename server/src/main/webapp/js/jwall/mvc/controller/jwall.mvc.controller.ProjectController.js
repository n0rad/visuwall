jwall.mvc.controller.ProjectController = {
		
	projectsView : null,
	
	projectService : null,

	projectDAO : null,
	
	init : function() {
		this.projectsView = jwall.mvc.view.Projects;
		this.projectService = jwall.business.service.Project;
		this.projectDAO = jwall.persistence.dao.ProjectDAO;
	},
	
	buildProjects : function() {
		var $this = this;
		jwall.business.service.Project.projects(function(projects) {
			for (var i = 0; i < projects.length; i++) {
				$this.addProject(projects[i]);
			}

//			for (var i = 0; i < projects.length; i++) {
//				$this.addProject(projects[i]);
//			}			
		
		//	updateUTDiff
			
		});

		// TODO remove
		$('ul#projectsTable li:last-child').live('click', function() {
		    $(this).prependTo('ul#projectsTable');
		});
	},
	
	addProject : function(projectData) {
		this.projectDAO.saveProject(projectData);
		var project = this.projectDAO.getProject(projectData.name);
		
		this.projectsView.addProject(project.name, project.description);
		this._updateProject(project);
	},

	updateStatus : function() {
		var $this = this;
		$this.projectService.status(function (projectsStatus) {
			var projectDone = [];
			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];

				if ($this.projectDAO.isProject(status.name)) {
					// this is a new project
					$this.projectService.project(status.name, function(newProjectData) {
						$this.addProject(newProjectData);
					});
					continue;
				}
				var project = $this.projectDAO.getProject(status.name);
				LOG.debug('Update status for project ' + status.name);
				$this._updateBuilding(project, status.building);
				$this._checkVersionChange(project, status);

				projectDone.push(status.name);
			}
			
			// looking for project to delete
			for (var key in $this.projectDAO.getProjects()) {
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
	},

	/////////////////////////////////////////////////////////////////////////
	
	
	_updateLastBuild : function(project) {
		this.projectsView.updateBuildTime(project.name, project.hudsonProject.completedBuild.duration);
		this.projectsView.updateCommiters(project.name, project.hudsonProject.completedBuild.commiters);
		if (project.rulesCompliance != 0) {
			this.projectsView.updateQuality(project.name, project.qualityResult.measures);
		} else {
			this.projectsView.updateQuality(project.name, { });					
		}
		this.projectsView.updateUTCoverage(project.name, project.coverage);
		this.projectsView.updateUT(project.name, 
				project.hudsonProject.completedBuild.testResult.failCount,
				project.hudsonProject.completedBuild.testResult.passCount,
				project.hudsonProject.completedBuild.testResult.skipCount,
				project.coverage);

		this.projectsView.updateITCoverage(project.name, 0);
		this.projectsView.updateIT(project.name, 0,0,0);
	
		var $this = this;
		var completedBuild = project.hudsonProject.completedBuild;
		
		this.projectDAO.callbackPreviousCompletedBuild(project.name, function(previousBuild) {
			if (completedBuild == null || previousBuild == null) {
				return;
			}
			
			var failDiff = completedBuild.testResult.failCount - previousBuild.testResult.failCount;
			var successDiff = completedBuild.testResult.totalCount - previousBuild.testResult.totalCount;
			var skipDiff = completedBuild.testResult.skipCount - previousBuild.testResult.skipCount;
			
			$this.projectsView.updateUTDiff(project.name, failDiff, successDiff, skipDiff);
		});
	},
	
	_updateAgo : function(project) {
		if (project.hudsonProject.lastBuild != null) {
			this.projectsView.updateAgo(project.name, new Date(project.hudsonProject.lastBuild.startTime + project.hudsonProject.lastBuild.duration));					
		} else {
			this.projectsView.updateAgo(project.name, 0);
		}
	},	

	_removeProject : function(projectName) {
		this.projectDAO.removeProject(projectName);
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