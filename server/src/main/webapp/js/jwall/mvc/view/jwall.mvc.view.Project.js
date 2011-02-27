jwall.mvc.view.Project = {
		
		statusClasses : [ "failure", "success", "unstable"],

		projects : {
			// TD : project
		},
		
		buildProject : function(project, projectsByRow) {
			var width = 100 / projectsByRow;
			LOG.info("display project", project);
			var projectTD = $('<td style="width:' + width + '%" id="' + project.name + '" class="project"></td>');
			projectTD.append('<p class="projectName">' + project.name + '<span id="when"></span></p>');
			
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
			
			
			
			// save project for updates
			this.projects[project.name] = project;
			
			this.updateProject(projectTD, project);
			return projectTD;
		},

		_getProjectDataFromTD : function(projectTD) {
			for (var key in this.projects) {
				if (key == projectTD.attr('id')) {
					return this.projects[key];
				}
			}
		},
		
		updateStatus : function(projectTD, projectStatus) {
			LOG.debug('update status for project ' + projectStatus.name);

			var projectData = this._getProjectDataFromTD(projectTD);

			if (projectData == undefined) {
				LOG.error('projectData not found for project ' + projectTD.id);
			}

			this._updateBuilding(projectTD, projectStatus.building);

			
			
			return;
			
			if (myBuildingStatus == true && serverBuilderStatus == false) {
				LOG.info('build is over on server for ' + currentProject.name);
				$this.projects[currentProject.name].hudsonProject.building = false;
				jwall.mvc.view.project.stopBuilding(currentProject.name);
				$this.updateStatus(currentProject.name);
			}
			if (myBuildingStatus == false && serverBuilderStatus == true) {
				LOG.info('server is now building ' + currentProject.name);
				$this.projects[currentProject.name].hudsonProject.building = true;
				jwall.mvc.view.project.setBuilding(currentProject.name);
			}
			
			if (myLastBuildNum != serverLastBuildNum) {
				LOG.info("server build is updated");
			}
			
		},

		updateProject : function(projectTD, project) {
			this._updateStatus(projectTD, project);
			
			this._updateBuilding(projectTD, project.hudsonProject.building);
		},
		
		/////////////////////////////////////////////////////////////

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