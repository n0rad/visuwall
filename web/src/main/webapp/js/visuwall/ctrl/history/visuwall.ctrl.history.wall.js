visuwall.ctrl.history.wallHistory = new function() {
	var $this = this;

	this.__serviceNames__ = ['wallController', 'navigationController'];

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
