visuwall.ctrl.process.Wall = function(wallName) {
	var $this = this;

	this.wallName = wallName;

	this.__inject__ = [ 'wallView', 'projectService', 'wallService'];

	this.updateStatus = function() {
		LOG.debug("run updater");
		$this.wallService.status($this.wallName, function(projectsStatus) {
			var projectDone = [];

			var checkProjectToRemove = function(status) {

			};

			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];
				$this.wallView.isProject(status.id, function(isProjectRes) {
					var stat = status;
					if (!isProjectRes) {
						// this is a new project
						$this.projectService.findProject($this.wallName, stat.id,
								function(newProject) {
									$this.wallView.addProject(newProject.id, newProject.name);
									$this._updateProject(newProject);
								});
					} else {
//						$this._updateBuilding(status.id, status.building);
//						$this._checkVersionChange(status.id, status);
					}
					projectDone.push(status.id);
				
					//TODO check if fail on new projets as they are added asyncly cause of $this.projectService.findProject  
					if (projectDone.length == projectsStatus.length) {
						$this.wallView.getProjectIds(function(projectIds) {
							for (var i = 0; i < projectIds.length; i++) {
								if (!projectDone.contains(projectIds[i])) {
									$this.wallView.removeProject(projectIds[i]);
								}
							}
						});
					}
				});
			}
		});
	};

	// ///////////////////////////////////////////////////////
	
	this._updateProject = function(project) {
		$this._updateLastBuild(project);
		$this._updateAgo(project);
		$this._updateBuilding(project);
		$this._updateState(project);
	};
	
	

	this._updateLastBuild = function(project) {
		//TODO use completedBuildId
		if (project.completedBuild == null) {
			return;
		}

		$this.wallView.updateBuildTime(project.id,
				project.completedBuild.duration);
		$this.wallView.updateCommiters(project.id,
				$this._getCommiterNames(project.completedBuild.commiters));
		$this.wallView.updateQuality(project.id,
				project.qualityResult.measures);

		$this.wallView.updateUTCoverage(project.id, project.completedBuild.unitTestResult.coverage);
		$this.wallView.updateUT(project.id,
				project.completedBuild.unitTestResult.failCount,
				project.completedBuild.unitTestResult.passCount,
				project.completedBuild.unitTestResult.skipCount);

		$this.wallView.updateITCoverage(project.id, project.completedBuild.integrationTestResult.coverage);
		$this.wallView.updateIT(project.id,
				project.completedBuild.integrationTestResult.failCount,
				project.completedBuild.integrationTestResult.passCount,
				project.completedBuild.integrationTestResult.skipCount);

//		$this.projectDAO.callbackPreviousCompletedBuild($this.wallName , project.id, function(
//				previousBuild) {
//			if (completedBuild == null || previousBuild == null) {
//				return;
//			}
//
//			var failDiff = completedBuild.unitTestResult.failCount
//					- previousBuild.unitTestResult.failCount;
//			var successDiff = completedBuild.unitTestResult.totalCount
//					- previousBuild.unitTestResult.totalCount;
//			var skipDiff = completedBuild.unitTestResult.skipCount
//					- previousBuild.unitTestResult.skipCount;
//
//			$this.wallView.updateUTDiff(project.id, failDiff,
//					successDiff, skipDiff);
//		});
	};
	
	this._updateState = function(project) {
		var stateFunction = 'display' + project.state.toLowerCase().ucfirst();
		$this.wallView[stateFunction](project.id);
	};

	this._getCommiterNames = function(commiters) {
		var res = [];
		for (var i = 0; i < commiters.length; i++) {
			res[i] = commiters[i].name;
		}
		return res;
	};

	this._updateAgo = function(project) {
		if (project.completedBuild != null) {
			var finishDate = new Date(project.completedBuild.startTime + project.completedBuild.duration);
			$this.wallView.updateAgo(project.id, finishDate);
		} else {
			$this.wallView.updateAgo(project.id, 0);
		}
	};

	this._updateBuilding = function(projectId, building, finishTime) {
		if (building) {
			$this.wallView.showBuilding(projectId);
			$this.wallView.setCountdown(projectId, project.finishTime);
		} else {
			this.wallView.stopBuilding(projectId);			
		}
	};

	this._checkVersionChange = function(projectId, building, latestBuildId) {
		if (!building) {
			$this.wallView.getBuildId(projectId, function(buildId) {
				if (latestBuildId != buildId) {
					$this.projectService.project($this.wallName, projectId, function(newProjectData) {
						$this._updateProject(newProjectData);
					});					
				}
			});
		}
	};

};