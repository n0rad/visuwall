define(['Visuwall/Controller/wallFormController', //
        'Visuwall/Controller/wallController', //
        ], function(wallFormController, wallController) {
	"use strict";
	
	var wallCommand = new function() {

		this.run = function(vars, wallId) {
			wallController.showWall(wallId);
		};

		this.create = function() {
			wallFormController.create();
		};

		this.edit = function(vars, wallId) {
			wallFormController.edit(wallId);		
		};
		
	};
	
	return wallCommand;
});


