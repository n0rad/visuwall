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

	this.__inject__ = [ 'wallView', 'projectService', 'wallService',
			'projectDAO', 'processingService' ];

	this.wallName = wallName;

	this.addProject = function(projectData) {
		$this.projectDAO.isProject(projectData.id, function(result) {
			if (!result) {
				$this.projectDAO.saveProject(projectData);
				$this.projectDAO.getProject(projectData.id, function(project) {
					$this.wallView.addProject(project.id, project.name);
					$this._updateProject(project);
				});				
			} else {
				LOG.warn('project with id ', projectData.id, ' already exists');
			}
		});
	};

	this.updateStatus = function() {
		LOG.debug("run updater");
		$this.wallService.status($this.wallName, function(projectsStatus) {
			var projectDone = [];
			
			var updateFunc = function(status, projectsStatus) {
				$this.projectDAO.getProject(status.id, function(project) {
					LOG.debug('Update status for project ' + status.id);
					$this._updateBuilding(project, status.building);
					$this._checkVersionChange(project, status);
					projectDone.push(status.id);

					// looking for project to delete only when all done
					if (projectDone.length == projectsStatus.length) {
						$this.projectDAO.getProjects(function(projects) {
							for (var key in projects) {
								if (!projectDone.contains(key)) {
									$this._removeProject(key);
								}
							}
						});
					}
				});
			};
			
			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];

				$this.projectDAO.isProject(status.id, function(isProjectRes) {
					var stat = status;
					if (!isProjectRes) {
						// this is a new project
						$this.projectService.project($this.wallName,
								stat.id, function(newProjectData) {
									$this.addProject(newProjectData);
									updateFunc(stat, projectsStatus);
								});
					} else {
						updateFunc(stat, projectsStatus);
					}


				});
			}
		});
	};
	
	this._updateProject = function(project) {
		$this.projectDAO.saveProject(project);

		$this._updateLastBuild(project);
		$this._updateAgo(project);

		// call updateBuilding like we just receive the status
		var wasBuilding = project.building;
		project.building = false;
		$this._updateBuilding(project, wasBuilding);
		$this._updateState(project);
	};

	// ///////////////////////////////////////////////////////////////////////

	this._updateLastBuild = function(project) {
		if (project.completedBuild == null) {
			return

		}
		$this.wallView.updateBuildTime(project.id,
				project.completedBuild.duration);
		$this.wallView.updateCommiters(project.id,
				project.completedBuild.commiters);
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

		var completedBuild = project.completedBuild;

		$this.projectDAO.callbackPreviousCompletedBuild($this.wallName , project.id, function(
				previousBuild) {
			if (completedBuild == null || previousBuild == null) {
				return;
			}

			var failDiff = completedBuild.unitTestResult.failCount
					- previousBuild.unitTestResult.failCount;
			var successDiff = completedBuild.unitTestResult.totalCount
					- previousBuild.unitTestResult.totalCount;
			var skipDiff = completedBuild.unitTestResult.skipCount
					- previousBuild.unitTestResult.skipCount;

			$this.wallView.updateUTDiff(project.id, failDiff,
					successDiff, skipDiff);
		});
	};

	this._updateAgo = function(project) {
		if (project.completedBuild != null) {
			$this.wallView.updateAgo(project.id, new Date(
					project.completedBuild.startTime
							+ project.completedBuild.duration));
		} else {
			$this.wallView.updateAgo(project.id, 0);
		}
	};

	this._removeProject = function(projectName) {
		$this.projectDAO.removeProject(projectName);
		$this.wallView.removeProject(projectName);
	};

	this._updateState = function(project) {
		//TODO replace with dynamic call
		switch (project.state) {
		case 'SUCCESS':
			$this.wallView.displaySuccess(project.id);
			break;
		case 'NEW':
			$this.wallView.displayNew(project.id);
			break;
		case 'ABORTED':
			$this.wallView.displayAborted(project.id);
			break;
		case 'FAILURE':
			$this.wallView.displayFailure(project.id);
			break;
		case 'UNSTABLE':
			$this.wallView.displayUnstable(project.id);
			break;
		case 'NOT_BUILT':
			$this.wallView.displayNotBuilt(project.id);
			break;
		case 'UNKNOWN':
		default:
			$this.wallView.displayUnknown(project.id);
		}
	};

	this._updateBuilding = function(project, isBuilding) {
		if (isBuilding) {
			if (!project.building) {
				$this.processingService.finishTime($this.wallName,
						project.id, function(data) {
							$this.wallView.updateCountdown(project.id,
									new Date(data));
						});
				LOG.info("project is now building : " + project.id);
				$this.wallView.showBuilding(project.id);
				project.building = true;
			}
		} else if (project.building) {
			LOG.info("building is now over for project : " + project.id);
			this.wallView.stopBuilding(project.id);
			project.building = false;

			$this.projectService.project($this.wallName, project.id,
					function(newProjectData) {
						$this._updateProject(newProjectData);
					});
		} else {
			$this.wallView.stopBuilding(project.id);
		}
	};

	this._checkVersionChange = function(project, status) {
		if ($this._checkVersionChangeAndNotBuilding(project, status)) {
			LOG
					.info("Server is not building and version has change, we need an update");
			$this.projectService.project($this.wallName, project.id,
					function(newProjectData) {
						$this._updateProject(newProjectData);
					});
		}
	};

	this._checkVersionChangeAndNotBuilding = function(projectData,
			projectStatus) {
		// if ever build && not building && last build on server != last
		// completed in js
		if (projectStatus.lastBuildId != -1
				&& !projectStatus.building
				&& projectStatus.lastBuildId != projectData.completedBuild.buildNumber) {
			return true;
		}
		return false;
	};

};
