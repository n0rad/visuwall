visuwall.theme.def.view.navigationView = new function() {
	var $this = this;
	
	this.__inject__ = ['wallFormEvent', 'wallFormView'];	
	
	this.navigation;
		
	this.init = function() {
		this.navigation = $('#navigation');
	};

	this.replaceWallList = function(newWallList) {
		var select = $('#wallSelect', this.navigation);
		var selectedValue = select.val();
		select.children().remove();
		var emptyOption = $('<option>Select a wall</option>');
		select.append(emptyOption);
		for ( var i = 0; i < newWallList.length; i++) {
			var newOption = $('<option value="' + newWallList[i] + '">' + newWallList[i] + '</option>');
			select.append(newOption);
		}
		select.val(selectedValue);
	};
	
	this.displayEditForm = function(htmlData, formData) {
		var domObject = $("#modal").html(htmlData);
		$this._displayForm(domObject, "Wall configuration", 'wall/edit');		
		ajsl.view.rebuildFormRec(domObject, formData, $this.wallFormView);
	};
	
	this.displayCreationForm = function(htmlData) {
		var domObject = $("#modal").html(htmlData);
		$this._displayForm(domObject, "Wall creation", 'wall/create');
	};
	
	///////////////////////////////////////////////////////////
	
	this._displayForm = function(domObject, title, closeController) {
		domObject.dialog({
			height: 400,
			width: 600,
			title: title,
			resizable: false,
			modal: true,
			close: function(event, ui) {
				$.history.queryBuilder().removeController(closeController).load();
			}
		});
	};

};