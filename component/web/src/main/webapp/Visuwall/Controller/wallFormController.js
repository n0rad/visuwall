define(['Visuwall/Service/wallService', //
        'Visuwall/Theme/Global/WallForm/wallFormView' //
], function(wallService, wallFormView) {
	'use strict';

	var wallFormController = new function() {
		this.create = function() {
			wallFormView.displayForm();
		};

		this.edit = function(wallId) {
			wallService.get(wallId, function(data) {
				wallFormView.displayForm(data);
			});
		};

	};

	return wallFormController;
});
