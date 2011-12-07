//			this.__inject__ = ['wallController', 'navigationController'];
define(['Visuwall/Command/Controller/WallController'], function(WallController) {
	"use strict";
	
	var WallAction = function() {
		
	};
	
	WallAction.prototype = {

			run : function(vars, wallId) {
				var wallController = new WallController(wallId);
				//$this.currentWallUpdater = setInterval(visuwall.theme.def.event.navigationEvent['#refresh|click'], 10000);
				//$this.wallController.showWall(wallId);
				wallController.updateStatus();
			},

			create : function() {
				$this.navigationController.create();
			},



			edit : function(vars, wallId) {
				$this.navigationController.edit(wallId);		
			}
	};
	
	return WallAction;
});