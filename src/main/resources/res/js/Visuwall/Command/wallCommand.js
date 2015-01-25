define(['Visuwall/Controller/navigationController', //
        'Visuwall/Controller/wallController', //
        ], function(navigationController, wallController) {
	"use strict";
	

	var wallCommand = new function() {

		this.run = function(vars, wallId) {
			wallController.showWall(wallId);
		};

		this.create = function() {
			navigationController.create();
		};

		this.edit = function(vars, wallId) {
			navigationController.edit(wallId);		
		};
		
	};
	
	return wallCommand;
});


