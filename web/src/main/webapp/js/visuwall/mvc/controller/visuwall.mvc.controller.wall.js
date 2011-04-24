visuwall.mvc.controller.wall = new function() {
	
	this.projectController;
	
	this.wallController;
	
	var $this = this;
	
	$(function() {
		$this.projectController = visuwall.mvc.controller.ProjectController;
	});

	this.run = function(vars, wallName) {
		$this.projectController.loadWall(wallName);
	};
};
