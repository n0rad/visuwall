define(['jquery', 'when','Visuwall/Service/wallService', //
        'Visuwall/Theme/Global/WallForm/wallFormView', //
        'Visuwall/Theme/Global/WallForm2/WallFormView' //
], function($, when, wallService, wallFormView, WallFormView2) {
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
			var closePromise = when.defer();
			closePromise.promise.then(
					function(wallName) {
						$.history.queryBuilder().removeController('wall/create2').
							addController('wall', wallName).load();
					},
					function() {
						$.history.queryBuilder().removeController('wall/create2').load();
					}
				);
			var wallFormView2 = new WallFormView2($('#visuwallForm'), closePromise);
			wallFormView2.displayForm();
		},
		edit2 : function(wallId) {
			var closePromise = when.defer();
			closePromise.promise.then(
					function(wallName) {
						$.history.queryBuilder().removeController('wall/edit2').load();
					},
					function() {
						$.history.queryBuilder().removeController('wall/edit2').load();
					}
				);
			
			wallService.get(wallId, function(data) {
				var wallFormView2 = new WallFormView2($('#visuwallForm'), closePromise);
				wallFormView2.displayForm(data);
			});
		}
	};
	return wallFormController;
});
