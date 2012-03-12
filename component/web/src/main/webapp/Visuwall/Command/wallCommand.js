define(['Visuwall/Controller/wallFormController', //
        'Visuwall/Controller/wallController' //
        ], function(wallFormController, wallController) {
	"use strict";
	
	var wallCommand = {

		run : function(vars, wallId) {
			wallController.showWall(wallId);
		},

		create : function() {
			wallFormController.create();
		},

		create2 : function() {
			wallFormController.create2();
		},

		edit : function(vars, wallId) {
			wallFormController.edit(wallId);		
		},
		
		edit2 : function(vars, wallId) {
			wallFormController.edit2(wallId);
		}
	};
	
	return wallCommand;
});


