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

visuwall.ctrl.controller.wallController = new function() {
	var $this = this;
	
	this.currentWall;
	this.currentWallUpdater;
	
	this.showWall = function(wallName) {
		//TODO use DI
		$this.currentWall = new visuwall.ctrl.process.Wall(wallName);
		ajsl.service.inject($this.currentWall);
		$this.currentWallUpdater = setInterval(visuwall.theme.def.event.navigationEvent['#refresh|click'], 10000);
		$this.updateStatus();
	};
	
	this.updateStatus = function() {
		$this.currentWall.updateStatus();
	};

};
