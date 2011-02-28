jwall.mvc.view.Project = {
		
		statusClasses : [ "failure", "success", "unstable"],

		projects : {
			// TD : project
		},
		
		buildProject : function(project, projectsByRow) {
			var width = 100 / projectsByRow;
			LOG.info("Display project", project);
			var projectTD = $('<td style="width:' + width + '%" id="' + project.name + '" class="project"></td>');
			projectTD.append($('<p class="projectName">' + project.name + '<span id="when"></span></p>'));
			projectTD.append($('<p>alemaire, jsmadja</p>'));
			projectTD.append($('<p class="timeleft"></p>'));
			projectTD.append($('<div class="unitTest"></div>'));
			projectTD.append($('<div class="iTest"></div>'));
			
			
			jwall.mvc.view.Stats.TestStatus([[30.2], [20.5], [10.4]], $(".unitTest", projectTD)[0]);
			jwall.mvc.view.Stats.TestStatus([[20.2], [10.5], [40.4]], $(".iTest", projectTD)[0]);
			
			
			//$(".commiters", projectTD).marquee({});
			
//			var metrics = $('<ul class="projectMetrics"></ul>');
//			metrics.append($('<li>coverage : ' + project.coverage + '</li>'));
//			metrics.append($('<li>rulesCompliance : ' + project.rulesCompliance + '</li>'));
//			metrics.append($('<li>failCount : ' + project.hudsonProject.lastBuild.testResult.failCount + '</li>'));
//			metrics.append($('<li>skipCount : ' + project.hudsonProject.lastBuild.testResult.skipCount + '</li>'));
//			metrics.append($('<li>totalCount : ' + project.hudsonProject.lastBuild.testResult.totalCount + '</li>'));
//			metrics.append($('<li>passCount : ' + project.hudsonProject.lastBuild.testResult.passCount + '</li>'));
//			metrics.append($('<li>integrationTestCount : ' + project.hudsonProject.lastBuild.testResult.integrationTestCount + '</li>'));
//			metrics.sortable();		
//			projectTD.append(metrics);
						
			this.updateProject(projectTD, project);
			return projectTD;
		},
		
		updateStatus : function(projectTD, projectStatus) {
			LOG.debug('Update status for project ' + projectStatus.name);
			

			var projectData = this._getProjectDataFromTD(projectTD);

			if (projectData == undefined) {
				LOG.error('ProjectData not found for project ' + projectTD.id);
			}

			this._updateBuilding(projectTD, projectStatus.building);

			if (this._checkVersionChangeAndNotBuilding(projectData, projectStatus)
				|| this._wasBuildingAndIsOver(projectData, projectStatus)) {
				LOG.info("Server is not building and version has change, we need an update");
				$this = this;
				jwall.business.service.Project.project(projectData.name, function(newProjectData) {
					$this.updateProject(projectTD, newProjectData);
				});				
			}
		},

		updateProject : function(projectTD, project) {
			
			this._updateStatus(projectTD, project);
			
			this._updateBuilding(projectTD, project.hudsonProject.building);

			// save project for updates
			this.projects[project.name] = project;		
		},
		
		/////////////////////////////////////////////////////////////

		_getProjectDataFromTD : function(projectTD) {
			for (var key in this.projects) {
				if (key == projectTD.attr('id')) {
					return this.projects[key];
				}
			}
		},
		
		_checkVersionChangeAndNotBuilding : function(projectData, projectStatus) {
			if (projectData.hudsonProject.lastBuildNumber != projectStatus.lastBuildId
				&& !projectStatus.building) {
				return true;
			}
			return false;
		},
		
		_wasBuildingAndIsOver : function(projectData, projectStatus) {
			if (projectData.hudsonProject.building && !projectStatus.building) {
				return true;
			}
			return false;
		},
		
		_updateStatus : function(projectTD, project) {
			var statusClass;
			if (project.hudsonProject.lastBuild.successful == true) {
				statusClass = 'success';
			} else {
				statusClass = 'failure';
			}
			projectTD.switchClasses(this.statusClasses, statusClass, 3000);	
		},
		
		_updateBuilding : function(projectTD, isBuilding) {
			if (isBuilding) {
				projectTD.blink({fadeDownSpeed : 3000, fadeUpSpeed : 3000, blinkCount : -1});
			} else {
				projectTD.stopBlink();
			}
		}
};