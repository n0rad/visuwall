visuwall.ctrl.controller.navigationController = new function() {
	var $this = this;

	this.__inject__ = ['wallService', 'navigationView', 'wallFormView'];
	
	this.create = function() {
		LOG.info("Creating wall");
		$this.wallFormView.getFormData(function(htmlData) {
			$this.navigationView.displayCreationForm(htmlData);
		});
	};

	this.edit = function(wallId) {
		LOG.info("Edit wall with id : " + wallId);
		$this.wallFormView.getFormData(function(htmldata) {
			var htmldata2 = htmldata;
			$this.wallService.get(wallId, function(data) {
				$this.navigationView.displayEditForm(htmldata2, data);
			});
		});
	};

	this.updateWallList = function() {
		LOG.debug("Update wall list");
		$this.wallService.wall(function(wallNameList) {
			$this.navigationView.replaceWallList(wallNameList);
		});
	};
	
};
