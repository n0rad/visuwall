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
			$this.projectsView.addProjects(projects);
			for (var i = 0; i < projects.length; i++) {
				var project = projects[i];
				// save project
				$this.projects[project.name] = project;

				
				$this.projectsView.updateCommiters(project.name, project.hudsonProject.lastBuild.commiters);
		
				$this._updateState(project);
				
				
				$this.projectsView.updateAgo(project.name, new Date(project.hudsonProject.lastBuild.startTime + project.hudsonProject.lastBuild.duration));
				
				
				if (project.rulesCompliance != 0) {
					$this.projectsView.updateCompliance(project.name, project.rulesCompliance);
				}

				$this.projectsView.setUTCoverage(project.name, project.coverage);
				$this.projectsView.setITCoverage(project.name, 45);

				
				$this.projectsView.displayIT(project.name, 
						3,
						5,
						10,
						45);
				
				$this.projectsView.displayUT(project.name, 
						project.hudsonProject.lastBuild.testResult.failCount,
						project.hudsonProject.lastBuild.testResult.passCount,
						project.hudsonProject.lastBuild.testResult.skipCount,
						project.coverage);
				
				// call updateBuilding like we just receive the status
				var wasBuilding = project.hudsonProject.building;
				project.hudsonProject.building = false;
				$this._updateBuilding(project, wasBuilding);
			}
		});
	},
	


	updateStatus : function() {
		var $this = this;
		$this.projectService.status(function (projectsStatus) {
			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];
				var project = $this.projects[status.name];

				LOG.debug('Update status for project ' + status.name);

				$this._updateBuilding(project, status.building);
				
				$this._checkVersionChange(project, status);
				
			}	
			//TODO find new projects
			//TODO find removed projects
		});
	},
	
	_updateProject : function(newProjectData) {
		this._updateState(newProjectData);
		
		this._updateBuilding(newProjectData, newProjectData.hudsonProject.building);

		// save project for updates
		this.projects[newProjectData.name] = newProjectData;		
	},
	
	
	/////////////////////////////////////////////////////////////////////////
	
	_updateState : function(project) {
		if (project.hudsonProject.lastBuild.successful) {
			this.projectsView.displaySuccess(project.name);
		} else {
			this.projectsView.displayFailure(project.name);			
		}
	},
	
	_updateBuilding : function(project, isBuilding) {
		if (isBuilding) {
			if (!project.hudsonProject.building) {
				var $this = this;
				jwall.business.service.Project.finishTime(project.name, function(data) {
					$this.projectsView.showCountdown(project.name, new Date(data));
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