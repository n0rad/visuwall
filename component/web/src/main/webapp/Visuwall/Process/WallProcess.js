define(['jquery', // 
        'log', //
        'Visuwall/Service/wallService', //
        'Visuwall/wallView', //
        'Visuwall/Service/projectService' //
        ], function($, log, wallService, wallView, projectService) {
	"use strict";

	var wallProcess = function(wallName) {
		var $this = this;
		
		this.wallName = wallName;
			
		this.addProject = function(projectData) {
			wallView.isProject(projectData.id, function(result) {
				if (!result) {
					wallView.addProject(projectData.id, projectData.name);
					$this._updateProject(projectData);
				} else {
					log.warn('project with name ', projectData.name, ' already exists');
				}
			});
		};
		
		this.updateStatus = function() {
			log.debug("run updater");
			wallService.status($this.wallName, function(projectsStatus) {
				var projectDone = [];

				var updateFunc = function(status) {
					if(status != null) {
						projectDone.push(status.id);
					}

					// looking for project to delete only when all done
					if (projectDone.length == projectsStatus.length) {
						wallView.getProjectIds(function(projectIds) {
							for (var i = 0; i < projectIds.length; i++) {
								if ($.inArray(projectIds[i], projectDone) == -1) {
									wallView.removeProject(projectIds[i]);
								}
							}
						});
					}
				};

				for (var i = 0; i < projectsStatus.length; i++) {
					var status = projectsStatus[i];
					wallView.isProject(status.id, function(isProjectRes) {
						var stat = status;
						if (!isProjectRes) {
							// this is a new project
							projectService.findProject($this.wallName,
									stat.id, function(newProjectData) {
										$this.addProject(newProjectData);
										wallView.setLastUpdate(newProjectData.id, status.lastUpdate);				
										updateFunc(stat);
									});
						} else {
							wallView.getLastUpdate(status.id, function(lastUpdate) {
								if (lastUpdate != status.lastUpdate) {
									$this._updateBuilding(status.id, status.building, status.buildingTimeleftSecond);
									projectService.findProject($this.wallName, status.id, function(newProjectData) {
										$this._updateProject(newProjectData);
										wallView.setLastUpdate(newProjectData.id, newProjectData.lastUpdate);
									});
								}
							});
							updateFunc(stat);
						}
					});
				}
				if (projectsStatus.length == 0) {
					updateFunc(null);
				}
				
			});
		};

		// ///////////////////////////////////////////////////////
		
		this._updateProject = function(project) {
			$this._updateLastBuild(project);
		};
		
		

		this._updateLastBuild = function(project) {		
			if (!project.lastNotBuildingId) {
				wallView.displayNew(project.id);
				return;
			}
			var lastNotBuild = project.builds[project.lastNotBuildingId];
			var lastBuild = project.builds[project.lastBuildId];
			
			var stateFunction = 'display' + lastNotBuild.state.toLowerCase().ucFirst();
			wallView[stateFunction](project.id);
			
			$this._updateBuilding(project.id, lastBuild.building, 0 /* we don't have buildingTimeleftSecond here */);
			$this._updateTimers(project, lastBuild);
			wallView.updateCommiters(project.id, lastNotBuild.commiters);
			if (lastNotBuild.qualityResult) {
				wallView.updateQuality(project.id, lastNotBuild.qualityResult.measures);
			}
			
			if (lastNotBuild.unitTestResult) {
				wallView.updateUTCoverage(project.id, lastNotBuild.unitTestResult.coverage);
			}
			if (lastNotBuild.unitTestResult) {
				wallView.updateUT(project.id,
						lastNotBuild.unitTestResult.failCount,
						lastNotBuild.unitTestResult.passCount,
						lastNotBuild.unitTestResult.skipCount);
			}

			if (lastNotBuild.integrationTestResult) {
				wallView.updateITCoverage(project.id, lastNotBuild.integrationTestResult.coverage);
			}
			if (lastNotBuild.integrationTestResult) {
				wallView.updateIT(project.id,
						lastNotBuild.integrationTestResult.failCount,
						lastNotBuild.integrationTestResult.passCount,
						lastNotBuild.integrationTestResult.skipCount);
			}
			
			if (project.previousCompletedBuildId != 0) {
//				var failDiff = lastBuild.unitTestResult.failCount - previousBuild.unitTestResult.failCount;
//				var successDiff = lastBuild.unitTestResult.totalCount - previousBuild.unitTestResult.totalCount;
//				var skipDiff = lastBuild.unitTestResult.skipCount - previousBuild.unitTestResult.skipCount;
//				wallView.updateUTDiff(project.id, failDiff, successDiff, skipDiff);
			}
		};

		this._updateTimers = function(project, build) {
			if (project.lastNotBuildingId != 0) {
				var build = project.builds[project.lastNotBuildingId]; 
				var finishDate = new Date(build.startTime + build.duration);
				wallView.updateAgo(project.id, finishDate);
				wallView.updateBuildTime(project.id, build.duration);
			} else {
				wallView.updateAgo(project.id, 0);
			}
		};

		this._updateBuilding = function(projectId, building, finishTime) {
			if (building) {
				wallView.showBuilding(projectId);
				if (finishTime > 0) {
					wallView.setCountdown(projectId, new Date(new Date().getTime() + (finishTime * 1000)));
				}
			} else {
				wallView.stopBuilding(projectId);			
			}
		};

		
		var currentWallUpdater = setInterval(this.updateStatus, 10000);

		
		this.close = function() {
			clearInterval(currentWallUpdater);
			wallView.removeAllProjects();
		};

		setTimeout(function() {
			$this.updateStatus();
		}, 1000);
		
	};
		
	return wallProcess;
});