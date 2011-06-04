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