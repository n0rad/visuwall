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

visuwall.ctrl.controller.navigationController = new function() {
	var $this = this;

	this.__inject__ = ['wallService', 'navigationView', 'wallFormView'];
	
	this.create = function() {
		LOG.info("Creating wall");
		$this.wallFormView.getFormData(function(htmlData) {
			$this.navigationView.displayCreationForm(htmlData);
		});
	};

	this.edit = function(wallId) {
		LOG.info("Edit wall with id : " + wallId);
		$this.wallFormView.getFormData(function(htmldata) {
			var htmldata2 = htmldata;
			$this.wallService.get(wallId, function(data) {
				$this.navigationView.displayEditForm(htmldata2, data);
			});
		});
	};

	this.updateWallList = function() {
		LOG.debug("Update wall list");
		$this.wallService.wall(function(wallNameList) {
			$this.navigationView.replaceWallList(wallNameList);
		});
	};
	
};
