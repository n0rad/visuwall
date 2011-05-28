/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

visuwall.persistence.dao.projectDAO = new function() {
	var $this = this;

	this.__inject__ = [ 'projectService' ];

	this._projects = {};

	this.saveProject = function(project) {
		$this._projects[project.name] = project;
	};

	this.removeProject = function(projectName) {
		delete $this._projects[projectName];
	};

	this.isProject = function(projectName, callback) {
		callback($this._projects[projectName] != undefined);
	};

	this.getProject = function(projectName, callback) {
		callback($this._projects[projectName]);
	};

	this.getProjects = function(callback) {
		callback($this._projects);
	};

	this.callbackPreviousCompletedBuild = function(wallName, projectName,
			callback) {
		var buildIdIndex = $this._getPreviousCompletedBuildIdIndex(projectName);
		if (buildIdIndex == null) {
			return false;
		}
		$this._callbackPreviousCompletedBuildRec(wallName, projectName,
				callback, buildIdIndex);
	};

	this._callbackPreviousCompletedBuildRec = function(wallName, projectName,
			callback, buildIdIndex) {
		var project = $this._projects[projectName];
		if (project.buildNumbers[buildIdIndex] == null) {
			return;
		}

		$this.projectService.getBuild(wallName, projectName,
				project.buildNumbers[buildIdIndex], function(buildData) {
					if (buildData.state != 'ABORTED') {
						callback(buildData);
						return;
					} else {
						$this._callbackPreviousCompletedBuildRec(wallName, projectName,
								callback, buildIdIndex + 1);
					}
				});
	};

	// ///////////////////////////////////

	this._getPreviousCompletedBuildIdIndex = function(projectName) {
		var project = $this._projects[projectName];
		var buildNumbers = project.buildNumbers;
		for ( var i = 0; i < buildNumbers.length; i++) {

			// skip building
			if (i == 0 && buildNumbers[i] == project.currentBuild.buildNumber) {
				continue;
			}

			// skip lastBuild
			if (buildNumbers[i] == project.completedBuild.buildNumber) {
				continue;
			}

			return i;
		}
		return null;
	};

};
