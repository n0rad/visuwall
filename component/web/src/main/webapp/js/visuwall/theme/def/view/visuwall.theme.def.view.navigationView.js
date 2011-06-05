/*
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

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
//		var emptyOption = $('<option disabled="disabled">Select a wall</option>');
//		select.append(emptyOption);
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
			height: 320,
			width: 600,
			title: title,
			resizable: false,
			modal: true,
			dragStart: function(event, ui) {
				var v = $('LABEL:regex(id,softwareAccesses.*\.urlcheck)');
				v.mouseout();
			},
			close: function(event, ui) {
				var v = $('LABEL:regex(id,softwareAccesses.*\.urlcheck)');
				v.mouseout();
				$.history.queryBuilder().removeController(closeController).load();
			}
		});
	};

};