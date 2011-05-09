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

visuwall.theme.def.event.WallForm = function() {
	var $this = this;

	this.context = 'DIV#modal';
	this.softTabsCount = 1;
	
	this.wallFormView;

	$(function() {
		$this.wallFormView = visuwall.theme.def.view.wallForm;
	});
	
	this.init = function(context) {
		var tabs = $("#softTabs");

		tabs
				.tabs({
					tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
					add : function(event, ui) {
						var contents = $('div[id^="tabs-"]', context);

						// -1 is the last but the last is the current added one,
						// so its -2
						var newContent = $(contents[contents.length - 2])
								.clone();

						ajsl.view.resetFormValues(newContent);
						ajsl.view.incrementFormIndexes(newContent);

						var childrens = newContent.children();
						for ( var int = 0; int < childrens.length; int++) {
							$(ui.panel).append(childrens[int]);
						}
						var tt;
					},
					remove : function(event, ui) {
						// removeFunction(event, ui.panel);
					},
					preremove : function(event, ui) {
						if (tabs.tabs('length') > 1) {
							return false;
						} else {
							return true;
						}
					}
				});
	};

	this['#softAdd|mouseenter'] = function() {
		$(this).addClass('ui-state-hover');
	};
	this['#softAdd|mouseleave'] = function() {
		$(this).removeClass('ui-state-hover');
	};

	this["#softTabs span.ui-icon-close|click|live"] = function() {
		var index = $("li", tabs).index($(this).parent());
		tabs.tabs("remove", index);
	};

	this["#wallForm|submit"] = function() {
		$.ajax({
			url : 'wall/',
			type : 'POST',
			data : $(this).serialize(),
			success : function(data) {
				alert("Data Loaded: " + data);
			}
		});
		return false;
	};

	// TODO
	// keydown
	// keyup
	// mousedown
	// mouseup
	// mousemove

	this['INPUT:regex(id,softwareAccesses.*\.name)|blur|live'] = function() {
		var softTabs = $('#softTabs');
		var tabIdFull = $('DIV[id^="tabs-"]', softTabs).has(this).attr('id');
		$('UL LI A[href="#' + tabIdFull + '"]', softTabs).html($(this).val());
	};

	this['DIV#softAdd|click'] = function(event) {
		$this.wallFormView.addFormSoftwareAccesses();
	};

};