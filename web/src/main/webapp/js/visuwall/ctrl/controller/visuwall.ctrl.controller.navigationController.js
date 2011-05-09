visuwall.ctrl.controller.navigationController = new function() {
	var $this = this;

	this.__serviceNames__ = ['wallService', 'navigationView', 'wallFormView'];
	
	this.create = function() {
		LOG.info("Creating wall");
		$this.wallFormView.getFormData(function(htmlData) {
			$this.navigationView.displayCreationForm(htmlData);
		});
	};

	this.edit = function(wallId) {
		LOG.info("Edit wall with id : " + wallId);
		$this.wallFormView.getFormData(function(htmldata) {
			$this.wallService.get(wallId, function(data) {
				$this.navigationView.displayEditForm(htmlData, data);
			});
		});
	};

	this.updateWallList = function() {
		LOG.debug("Update wall list");
		$this.wallService.wall(function(wallNameList) {
			$this.navigationView.replaceWallList(wallNameList);
		});
	};

	this.updateWallListTEST = function() {
		LOG.debug("Update wall list");
		ajsl.get('wallService', function(wallService) {
			wallService.wall(function(wallNameList) {
				ajsl.get('navigationView', function(navigationView) {
					navigationView.replaceWallList(wallNameList);
				});
			});
		});
	};

	this.updateWallListTEST = function() {
		LOG.debug("Update wall list");
//		ajsl.get(this, 'wallService', 'navigationView', function() {
			
			$this.wallService.wall(function(wallNameList) {
				$this.navigationView.replaceWallList(wallNameList);
			});
//		});
	};
	
};
