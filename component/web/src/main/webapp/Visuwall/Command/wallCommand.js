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

		this.create2 = function() {
			wallFormController.create2();
		};

		this.edit = function(vars, wallId) {
			wallFormController.edit(wallId);		
		};
		
		this.edit2 = function(vars, wallId) {
			wallFormController.edit2(wallId);
		};
		
	};
	
	return wallCommand;
});


