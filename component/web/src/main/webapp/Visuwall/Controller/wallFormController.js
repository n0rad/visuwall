define(['Visuwall/Service/wallService', //
        'Visuwall/Theme/Global/WallForm/wallFormView', //
        'Visuwall/wallFormView' //
], function(wallService, wallFormView, wallFormView2) {
	'use strict';

	var wallFormController = {
		create : function() {
			wallFormView.displayForm(null, true);
		},
		edit : function(wallId) {
			wallService.get(wallId, function(data) {
				wallFormView.displayForm(data, false);
			});
		},
		create2 : function() {
			wallFormView2.displayForm();
		},
		edit2 : function(wallId) {
			wallService.get(wallId, function(data) {
				wallFormView2.displayForm(data);
			});
		}
	};
	return wallFormController;
});
