define(['Visuwall/Process/WallProcess'], function(WallProcess) {
	'use strict';
	
	var wallController = new function() {
		var $this = this;

		this.currentWall;
		this.currentWallUpdater;

		this.showWall = function(wallName) {
			$this.currentWall = new WallProcess(wallName);
		};

	};

	return wallController;
});
