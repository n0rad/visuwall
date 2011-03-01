jwall.mvc.view.Project = {
		
		statusClasses : [ "failure", "success", "unstable"],

		projects : {
			// TD : project
		},
		
		buildProject : function(project, projectsByRow) {
			var width = 100 / projectsByRow;
			LOG.info("Display project", project);
			var projectTD = $('<td style="width:' + width + '%" id="' + project.name + '" class="project success"><div id="content"></div></td>');
			projectTD.append($('<div class="projectName">' + project.name + '<span id="when"></span></div>'));
			projectTD.append($('<p class="commiters"></p>'));
			projectTD.append($('<p class="timeleft"></p>'));
			projectTD.append($('<div class="unitTest"></div>'));
			projectTD.append($('<div class="iTest"></div>'));
			
			
			//jwall.mvc.view.Stats.TestStatus([[30.2], [20.5], [10.4]], $(".unitTest", projectTD)[0]);
			//jwall.mvc.view.Stats.TestStatus([[20.2], [10.5], [40.4]], $(".iTest", projectTD)[0]);
			
			
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
				$('p.commiters').hide().html('');
			} else {
				statusClass = 'failure';
				var commiters = this._buildCommiters(project);
				$('p.commiters').html(commiters).show();
			}
			projectTD.switchClasses(this.statusClasses, statusClass, 3000);	
		},
		
		_updateBuilding : function(projectTD, isBuilding) {
			if (isBuilding) {
				jwall.business.service.Project.finishTime(projectTD.attr('id'), function(data) {
					$('p.timeleft', projectTD).countdown({until: new Date(data), compact: true, format: 'dHMS', onExpiry : function() {
						$('p.timeleft', projectTD).html('N/A');
					}}).show();
				});
				projectTD.blink({fadeDownSpeed : 2000, fadeUpSpeed : 2000, blinkCount : -1, fadeToOpacity : 0.6});
			} else {
				projectTD.stopBlink();
				$('p.timeleft', projectTD).hide().html('');
			}
		},
		
		_buildCommiters : function(project) {
			var commiterString = "";
			for (var i = 0; i < project.hudsonProject.lastBuild.commiters.length; i++) {
				var commiter =  project.hudsonProject.lastBuild.commiters[i];
				if (i > 0) {
					commiterString += ", ";
				}
				commiterString += commiter; 
			}
			return commiterString;
		}
};