visuwall.mvc.controller.navigation = new function() {
	var $this = this;

	this.wallservice;
	this.navigationView;
	
	$(function() {			
		$this.wallservice = visuwall.business.service.Wall;
		$this.navigationView = visuwall.mvc.view.Navigation;
	});

	this.updateWallList = function() {
		LOG.error("Update wall list");
		$this.wallservice.wall(function(wallNameList) {
			$this.navigationView.replaceWallList(wallNameList);
		});
	};
};
