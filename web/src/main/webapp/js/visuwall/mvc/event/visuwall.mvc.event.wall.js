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

visuwall.mvc.wall = new function() {
	var $this = this;

	this.context = 'DIV#modal';
	this.softTabsCount = 1;

	this.init = function(context) {
		var tabs = $("#softTabs");

		var formFields = "input, checkbox, select, textarea";

		tabs
				.tabs({
					tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
					add : function(event, ui) {
						var contents = $('div[id^="tabs-"]', context);

						// -1 is the last but the last is the current added one,
						// so its -2
						var newContent = $(contents[contents.length - 2])
								.clone();

						// reset value
						newContent.find(formFields).val('');

						// increment name
						newContent
								.find(formFields)
								.each(
										function() {
											this.name = this.name
													.replace(
															/\[(\d+)\]/,
															function(str, p1) {
																return '['
																		+ (parseInt(
																				p1,
																				10) + 1)
																		+ ']';
															});

											this.id = this.id
													.replace(
															/(\d+)\./,
															function(str, p1) {
																return (parseInt(
																		p1, 10) + 1)
																		+ '.';
															});
										});

						// increment name
						newContent.find('label').each(
								function() {
									this.htmlFor = this.htmlFor.replace(
											/(\d+)\./, function(str, p1) {
												return (parseInt(p1, 10) + 1)
														+ '.';
											});
								});

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

		// $(':input','#myform')
		// .not(':button, :submit, :reset, :hidden')
		// .val('')
		// .removeAttr('checked')
		// .removeAttr('selected');

		// (function($)
		// {
		// $.fn.defaultValue = function( options )
		// {
		// var options = $.extend(
		// {
		// defVal: 'Search'
		// },
		// options
		// );
		// return this.each( function()
		// {
		// $( this ).blur( function()
		// {
		// if ( $( this ).val() === '' )
		// $( this ).val( options.defVal );
		// }
		// ).focus( function()
		// {
		// if ( $( this ).val() === options.defVal )
		// $( this ).val( '' );
		// }
		// );
		// }
		// );
		// }
		// }
		// )(jQuery);
		//		

		// $.fn.clearForm = function() {
		// return this.each(function() {
		// var type = this.type, tag = this.tagName.toLowerCase();
		// if (tag == 'form')
		// return $(':input',this).clearForm();
		// if (type == 'text' || type == 'password' || tag == 'textarea')
		// this.value = '';
		// else if (type == 'checkbox' || type == 'radio')
		// this.checked = false;
		// else if (tag == 'select')
		// this.selectedIndex = -1;
		// });
		// };

		$("#softTabs span.ui-icon-close").live("click", function(var1, var2) {
			var index = $("li", tabs).index($(this).parent());
			tabs.tabs("remove", index);
		});

		$('#softAdd').hover(function() {
			$(this).addClass('ui-state-hover');
		}, function() {
			$(this).removeClass('ui-state-hover');
		});

		// $('OL.projects').selectable({
		// selected : function(e) {
		// $(this).toggleClass("selected");
		// }
		// });

		// $("INPUT#allProjects").button();
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
	
//	keydown
//	keyup
//	mousedown
//	mouseup
//	mousemove
	
	this["#wallForm|submit"] = function() {
		$.ajax({url : 'wall/', type : 'POST', data : $(this).serialize(), success : function(data) {
				   alert("Data Loaded: " + data);
			}
		});
		return false;
	};
	
	this['INPUT:regex(id,softwareAccesses.*\.name)|blur|live'] = function() {
		var softTabs = $('#softTabs');
		var tabIdFull = $('DIV[id^="tabs-"]', softTabs).has(this).attr('id');
		$('UL LI A[href="#' + tabIdFull + '"]', softTabs).html($(this).val()); 
	};

	this['DIV#softAdd|click'] = function(event) {
		$this.addFormSoftwareAccesses();
	};

};