visuwall.theme.def.view.wallForm = new function() {
	var $this = this;
	var wallFormEvent;
	
	this.getWallData = function() {
		var formContent = $("#contents #formCreation");
		$this.wallformEvent = visuwall.theme.def.event.wallForm;
	};
	
	this.getFormData = function(callback) {
		var data = $('#formCreation').clone().show();
		ajsl.event.register($this.wallFormEvent);
		callback(data);
	};
	
	this.addFormSoftwareAccesses = function(id) {
		if ($('#tabs-' + id).length) {
			return;
		}

		if (id == undefined) {
			id = $this.softTabsCount;
			$this.softTabsCount++;
		} else {
			// be sure that next tab id is not in used
			if ($this.softTabsCount <= id) {
				$this.softTabsCount = id + 1;
			}
		}
		var tabsElement = $('#softTabs', $this.context);
		tabsElement.tabs('add', '#tabs-' + id, "New");
	};

};