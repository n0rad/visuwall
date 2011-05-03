/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

visuwall.mvc.controller.wall = new function() {
	var $this = this;
	
	this.projectController;	
	this.wallController;

	this.editFormMng;
	
	$(function() {
		$this.projectController = visuwall.mvc.controller.ProjectController;
		$this.wallController = visuwall.business.service.Wall;
		$this.editFormMng = visuwall.mvc.wall;
	});

	this.run = function(vars, wallName) {
		$this.projectController.loadWall(wallName);
	};
	
	this.create = function() {
		$("#modal").load("wall/create", function(data) {
			$(this).dialog({
				height: 400,
				width: 600,
				title: 'Wall Configuration',
				resizable: false,
				modal: true,
				close: function(event, ui) {
					$.history.queryBuilder().removeController('wall/create').load();
				}
			});
		});
	};

	
	this.edit = function(vars, wallId) {
		LOG.info("Edit wall with id : " + wallId);
		$("#modal").load("wall/create", function(data) {
			$(this).dialog({
				height: 400,
				width: 600,
				title: 'Wall Configuration',
				resizable: false,
				modal: true,
				close: function(event, ui) {
					$.history.queryBuilder().removeController('wall/edit').load();
				}
			});
			
			
			
			var rebuildFormRec = function(form, data, root, rootMethod) {
				if (root == undefined) {
					root = '';
				}
				if (rootMethod == undefined) {
					rootMethod = 'addForm';
				}
				
				for(var formElem in data) {
					
					if (data[formElem] instanceof Array) {
						var assertMethodName = rootMethod + formElem.ucfirst();
						for (var i = 0; i < data[formElem].length; i++) {
							if (assertMethodName in $this.editFormMng) {
								$this.editFormMng[assertMethodName](i);
							}
							rebuildFormRec(form, data[formElem][i], root + formElem + '[' + i + '].', assertMethodName);
						}
					} else {
						//.trigger('change');
						$('[name="' + root + formElem + '"]', form).val(data[formElem]).blur();
					}
				}
			};
			
			$this.wallController.get(wallId, function(data) {
				var form = $('FORM#wallForm');
				rebuildFormRec(form, data);
			});
			
		});
	};
};
