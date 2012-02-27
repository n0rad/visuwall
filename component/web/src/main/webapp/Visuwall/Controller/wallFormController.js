define(['Visuwall/Service/wallService', //
        'Visuwall/Theme/Global/WallForm/wallFormView', //
        'Visuwall/Theme/Global/WallForm2/wallFormView' //
], function(wallService, wallFormView, wallFormView2) {
	'use strict';

	var wallFormController = new function() {
		this.create = function() {
			wallFormView.displayForm(null, true);
		};

		this.edit = function(wallId) {
			wallService.get(wallId, function(data) {
				wallFormView.displayForm(data, false);
			});
		};

		this.create2 = function() {
			wallFormView2.displayForm();
		};
		this.edit2 = function(wallId) {
			wallService.get(wallId, function(data) {
				wallFormView2.displayForm(data);
			});
		};

	};

	return wallFormController;
});
