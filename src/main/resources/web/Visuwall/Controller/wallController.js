define(['Visuwall/Process/WallProcess'], function(WallProcess) {
	'use strict';
	
	var currentWall = null;
	
	var wallController = {
		showWall : function(wallName) {
			if (currentWall) {
				currentWall.close();
			}
			currentWall = new WallProcess(wallName);
		}
	};

	return wallController;
});
