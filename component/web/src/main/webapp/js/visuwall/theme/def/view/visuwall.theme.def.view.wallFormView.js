visuwall.theme.def.view.wallFormView = new function() {
	var $this = this;

	this.__inject__ = ['wallFormEvent'];
	
	this.wallFormData;
	
	this.softTabsCount = 1;
	
	this.context;
	
	this.getFormData = function(callback) {
		//TODO get form from server instead of html dom element
		if (!$this.wallFormData) {
			var container = $('#formCreation');
			$this.wallFormData = container.clone().show();
			container.remove();
		}
		var data = $this.wallFormData.clone();		
		$this.context = data;
		$this.wallFormEvent.__getObject__(function(bean) {
			ajsl.event.register(bean, data);
		});		
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