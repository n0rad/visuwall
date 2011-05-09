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

visuwall.ctrl.controller.wall = new function() {
	$this = this;
	
	this.currentWall;
	this.currentWallUpdater;
	
	this.showWall = function(wallName) {
		$this.currentWall = new visuwall.mvc.controller.process.Wall(wallName);
		$this.currentWallUpdater = setInterval(visuwall.mvc.event.navigation['#refresh|click'], 10000);
	};

};
