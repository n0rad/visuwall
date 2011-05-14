visuwall.ctrl.history.wall = new function() {
	var $this = this;

	this.__inject__ = ['wallController', 'navigationController'];

	this.run = function(vars, wallId) {
		$this.wallController.showWall(wallId);
	};

	this.create = function() {
		$this.navigationController.create();
	};

	this.edit = function(vars, wallId) {
		$this.navigationController.edit(wallId);		
	};
	
};
