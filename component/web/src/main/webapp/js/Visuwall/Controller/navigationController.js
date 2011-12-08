define(['log', //
        'Visuwall/Service/wallService', //
        'Visuwall/Theme/Default/View/navigationView', //
        'Visuwall/Theme/Default/View/wallFormView' //
], function(log, wallService, navigationView, wallFormView) {
	'use strict';

	var navigationController = new function() {
		this.create = function() {
			log.info("Creating wall");
			wallFormView.getFormData(function(htmlData) {
				navigationView.displayCreationForm(htmlData);
			});
		};

		this.edit = function(wallId) {
			log.info("Edit wall with id : " + wallId);
			wallFormView.getFormData(function(htmldata) {
				var htmldata2 = htmldata;
				wallService.get(wallId, function(data) {
					navigationView.displayEditForm(htmldata2, data);
				});
			});
		};

	};

	return navigationController;
});
