visuwall.ctrl.process.Wall = function(wall) {
	var $this = this;

	this.wallName = wall.name;

	this.__inject__ = [ 'wallView', 'projectService', 'wallService',
			'processingService' ];

	this.updateStatus = function() {
		LOG.debug("run updater");
		$this.wallService.status($this.wallName, function(projectsStatus) {
			var projectDone = [];

			var updateFunc = function(status) {
				LOG.debug('Update status for project ' + status.id);
				$this._updateBuilding(status.id, status.building);
				$this._checkVersionChange(status.id, status);
				projectDone.push(status.id);

				// looking for project to delete only when all done
				if (projectDone.length == projectsStatus.length) {
					$this.projectView.getDisplayedProjectIds(function(
							projectIds) {
						for ( var i = 0; i < projectIds.length; i++) {
							if (!projectDone.contains(projectIds[i])) {
								$this._removeProject(projectIds[i]);
							}
						}
					});
				}
			};

			for (var i = 0; i < projectsStatus.length; i++) {
				var status = projectsStatus[i];
				$this.projectView.isProjectDisplay(status.id, function(isProjectRes) {
					var stat = status;
					if (!isProjectRes) {
						// this is a new project
						$this.projectService.findProject($this.wallName, stat.id,
								function(newProjectData) {
									$this.addProject(newProjectData);
									updateFunc(stat);
								});
					} else {
						updateFunc(stat);
					}
				});
			}
		});
	};

	// ///////////////////////////////////////////////////////

}