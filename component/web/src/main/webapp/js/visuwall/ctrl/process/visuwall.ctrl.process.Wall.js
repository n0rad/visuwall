/*
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

visuwall.ctrl.process.Wall = function(wallName) {
	var $this = this;

	this.wallName = wallName;

	this.__inject__ = [ 'wallView', 'projectService', 'wallService'];

	
	this.addProject = function(projectData) {
		$this.wallView.isProject(projectData.id, function(result) {
			if (!result) {
				$this.wallView.addProject(projectData.id, projectData.name);
				$this._updateProject(projectData);
			} else {
				LOG.warn('project with name ', projectData.name, ' already exists');
			}
		});
	};
	
	this.updateStatus = function() {
		LOG.debug("run updater");
		$this.wallService.status($this.wallName, function(projectsStatus) {
			var projectDone = [];

			var updateFunc = function(status, projectsStatus) {
				projectDone.push(status.id);

				// looking for project to delete only when all done
				if (projectDone.length == projectsStatus.length) {
					$this.wallView.getProjectIds(function(projectIds) {
						for (var i = 0; i < projectIds.length; i++) {
							if (!projectDone.contains(projectIds[i])) {
								$this.wallView.removeProject(projectIds[i]);
							}
						}
					});
				}
			};

			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];
				$this.wallView.isProject(status.id, function(isProjectRes) {
					var stat = status;
					if (!isProjectRes) {
						// this is a new project
						$this.projectService.findProject($this.wallName,
								stat.id, function(newProjectData) {
									$this.addProject(newProjectData);
									$this.wallView.setLastUpdate(newProjectData.id, status.lastUpdate);				
									updateFunc(stat, projectsStatus);
								});
					} else {
						$this._updateBuilding(status.id, status.building, status.buildingTimeleftSecond);
						$this.wallView.getLastUpdate(status.id, function(lastUpdate) {
							if (lastUpdate != status.lastUpdate) {
								$this.projectService.findProject($this.wallName, status.id, function(newProjectData) {
									$this._updateProject(newProjectData);
									$this.wallView.setLastUpdate(newProjectData.id, newProjectData.lastUpdate);
								});
							}
						});
						updateFunc(stat, projectsStatus);
					}
				});
			}
		});
	};

	// ///////////////////////////////////////////////////////
	
	this._updateProject = function(project) {
		$this._updateLastBuild(project);
	};
	
	

	this._updateLastBuild = function(project) {		
		if (project.lastNotBuildingNumber == 0) {
			$this.wallView.displayNew(project.id);
			return;
		}
		var lastBuild = project.builds[project.lastNotBuildingNumber]; 
	
		
		var stateFunction = 'display' + lastBuild.state.toLowerCase().ucfirst();
		$this.wallView[stateFunction](project.id);

		
		$this._updateBuilding(project.id, project.building, project.finishTime);
		$this._updateTimers(project, lastBuild);
		$this.wallView.updateCommiters(project.id, $this._getCommiterNames(lastBuild.commiters));
		if (lastBuild.qualityResult) {
			$this.wallView.updateQuality(project.id, lastBuild.qualityResult.measures);
		}
		
		if (lastBuild.unitTestResult && lastBuild.unitTestResult.coverage) {
			$this.wallView.updateUTCoverage(project.id, lastBuild.unitTestResult.coverage);
		}
		if (lastBuild.unitTestResult) {
			$this.wallView.updateUT(project.id,
					lastBuild.unitTestResult.failCount,
					lastBuild.unitTestResult.passCount,
					lastBuild.unitTestResult.skipCount);
		}

		if (lastBuild.integrationTestResult && lastBuild.integrationTestResult.coverage) {
			$this.wallView.updateITCoverage(project.id, lastBuild.integrationTestResult.coverage);
		}
		if (lastBuild.integrationTestResult) {
			$this.wallView.updateIT(project.id,
					lastBuild.integrationTestResult.failCount,
					lastBuild.integrationTestResult.passCount,
					lastBuild.integrationTestResult.skipCount);
		}
		
		if (project.previousCompletedBuildNumber != 0) {
//			var failDiff = lastBuild.unitTestResult.failCount - previousBuild.unitTestResult.failCount;
//			var successDiff = lastBuild.unitTestResult.totalCount - previousBuild.unitTestResult.totalCount;
//			var skipDiff = lastBuild.unitTestResult.skipCount - previousBuild.unitTestResult.skipCount;
//			$this.wallView.updateUTDiff(project.id, failDiff, successDiff, skipDiff);
		}
	};
	
	this._getCommiterNames = function(commiters) {
		var res = [];
		for (var i = 0; i < commiters.length; i++) {
			res[i] = commiters[i].name;
		}
		return res;
	};

	this._updateTimers = function(project, build) {
		if (project.lastNotBuildingNumber != 0) {
			var build = project.builds[project.lastNotBuildingNumber]; 
			var finishDate = new Date(build.startTime + build.duration);
			$this.wallView.updateAgo(project.id, finishDate);
			$this.wallView.updateBuildTime(project.id, build.duration);
		} else {
			$this.wallView.updateAgo(project.id, 0);
		}
	};

	this._updateBuilding = function(projectId, building, finishTime) {
		if (building) {
			$this.wallView.showBuilding(projectId);
			if (finishTime) {
				$this.wallView.setCountdown(projectId, new Date(new Date().getTime() + (finishTime * 1000)));
			}
		} else {
			this.wallView.stopBuilding(projectId);			
		}
	};

};