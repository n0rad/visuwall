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

visuwall.theme.def.event.wallFormEvent = new function() {
	var $this = this;

	this.__inject__ = ['wallFormView', 'wallFormController', 'wallController', 'pluginService', 'navigationView', 'wallService'];
	
	
//	this.context = 'DIV#modal';
	this.softTabsCount = 1;
	this.tabs;
	
	
	this.init = function() {
		
		$(".projects", this).accordion({
//			fillSpace: true,
			autoHeight: false,
//			collapsible: true
			navigation: true
		});
//		$("select", this).multiselect();
		
		
//		$(".projects", this).accordion({
//			fillSpace: true,
//			autoHeight: false,
//			collapsible: true
////			navigation: true
//		});
		$this.tabs = $("#softTabs", this);
		var context = this;
		

		
		$this.tabs.tabs({
					tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
					add : function(event, ui) {
						var contents = $('div[id^="tabs-"]', context);
						

						// -1 is the last but the last is the current added one,
						// so its -2
						var newContent = $(contents[contents.length - 2])
								.clone();

						ajsl.view.incrementFormIndexes(newContent);
						ajsl.view.resetFormValues(newContent);

						sliderInit($('DIV.projectFinderDelaySecondSlider', newContent));
						$('INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)', newContent).change(function() {
							delayChange(this);
						});

						var childrens = newContent.children();
						for (var i = 0; i < childrens.length; i++) {
							$(ui.panel).append(childrens[i]);

							// projects selector
							if ($(childrens[i]).is(".projects")) {
								$(childrens[i]).accordion({
									autoHeight: false,
									navigation: true
								});
							}
						}
						
					},
					remove : function(event, ui) {
						// removeFunction(event, ui.panel);
					},
					select: function(event, ui) {
						var v = $('LABEL:regex(id,softwareAccesses.*\.urlcheck)');
						v.mouseout();
					},
					preremove : function(event, ui) {
						if ($this.tabs.tabs('length') > 1) {
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
		var index = $("li", $this.tabs).index($(this).parent());
		$this.tabs.tabs("remove", index);
	};

	
	this["#wallForm #delete|click"] = function() {		
		$this.wallService.deleteWall($('#wallForm INPUT#name').val());
//		$("#modal").dialog('close');
	};
	
	this["#wallForm|submit"] = function() {
		$("#modal .success").empty();
		$("#modal .failure").empty();
		if ($('#wallForm INPUT#name').val().trim() == "") {
			$("#modal .failure").html("Wall name is mandatory");
			return false;
		}
		$("w#wallForm .loader").empty().html('<img src="res/img/ajax-loader.gif" />');
		$this.wallFormController.submitWallData(this, function() { // success
			$("#wallForm .loader").empty();
			$("#modal .success").html("Success");
			setTimeout(function() {
				$this.wallService.wall(function(wallNameList) {
					$this.navigationView.replaceWallList(wallNameList);
					if (wallNameList.length == 1) {
						$.history.queryBuilder().addController('wall/' + wallNameList[0]).load();
					}
				});
				
				$("#modal").dialog('close');
			}, 1000);
		}, function (msg) { //failure
			$("#wallForm .loader").empty();
			$("#modal .failure").html(msg);
		});
		return false;
	};

	// TODO
	// keydown
	// keyup
	// mousedown
	// mouseup
	// mousemove
	
	var urlFunc = function() {
		var val = $(this).val();
		if (val[val.length - 1] == '/') {
			$(this).val(val.slice(0, -1));
			val = $(this).val();
		}
		
		var softTabs = $('#softTabs');
		var tabIdFull = $('DIV[id^="tabs-"]', softTabs).has(this).attr('id');
		var hostname = getHostname($(this).val());
		if (!hostname) {
			hostname = $(this).val();
		}
		if (!hostname) {
			hostname = 'New';
		}
		$('UL LI A[href="#' + tabIdFull + '"]', softTabs).html(hostname);

		////////////
		
		var classes = ['failureCheck', 'successCheck', 'loadingCheck', 'warningCheck'];
		var domObj = $('#' + $(this).attr('id').replace(".", "\\.") + "check", $(this).parent());
		var tabContent = $(this).parent();
		
		if (!$(this).val().trim()) {
			domObj.switchClasses(classes, '', 1);			
			return; 
		}
		
		domObj.switchClasses(classes, 'loadingCheck', 1);
		$this.pluginService.manageable($(this).val(), function(softwareInfo) {
			if (softwareInfo.warnings) {
				//success
				domObj.switchClasses(classes, 'warningCheck', 1);				
				

var ff = '				<table class="softwareInfo">'
+'				<th colspan="2">Plugin</th>'
+'				<tr>'
+'					<td class="label">name :</td>'
+'					<td>' + softwareInfo.pluginInfo.name + '</td>'
+'				</tr>'
+'				<tr>'
+'					<td class="label">version :</td>'
+'					<td>' + softwareInfo.pluginInfo.version + '</td>'
+'				</tr>'
+'			</table>'	
+'			<table class="softwareInfo">'
+'				<th colspan="2">Software</th>'
+'				<tr>'
+'					<td class="label">name :</td>'
+'					<td>' + softwareInfo.name + '</td>'
+'				</tr>'
+'				<tr>'
+'					<td class="label">version :</td>'
+'					<td>' + softwareInfo.version + '</td>'
+'				</tr>'
+'				<tr>'
+'					<td colspan="2">' + softwareInfo.warnings + '</td>'
+'				</tr>'
+'			</table>';
				
				domObj.qtip({
					content : ff,
					position : {
						corner : {
							tooltip : 'topLeft',
							target : 'bottomRight'
						}
					},
//					show : {
//						when : 'click',
//						solo : true
//					},
//		            hide: false, // Don't specify a hide event
		            style: {
//		               width: {
//		            	   min: 300
//		               },
//		               height: {
//		            	   min: 300
//		               },
		               border: {
		                  width: 5,
		                  radius: 2
		               },
		               padding: 10, 
		               textAlign: 'center',
		               tip: true, // Give it a speech bubble tip with automatic corner detection
		               name: 'cream' // Style it according to the preset 'cream' style
		            }
				});
				
//				domObj.qtip("show");
				domObj.mouseover();
				
				
			} else {
				//success
				domObj.switchClasses(classes, 'successCheck', 1);				
			}
			
			// project Names
			var projectNamesFormElem = $('SELECT:regex(id,softwareAccesses.*\.projectNames)', tabContent);
			var oldVal = projectNamesFormElem.val();
			projectNamesFormElem.empty();
			for (var i = 0; i < softwareInfo.projectNames.length; i++) {
				var projectName = softwareInfo.projectNames[i];
				projectNamesFormElem.append($("<option></option>").attr("value",projectName).text(projectName));
			}
			projectNamesFormElem.val(oldVal);
			
			// views
			var projectViewsFormElem = $('SELECT:regex(id,softwareAccesses.*\.viewNames)', tabContent);
			var oldVal = projectViewsFormElem.val();
			projectViewsFormElem.empty();
			for (var i = 0; i < softwareInfo.viewNames.length; i++) {
				var viewName = softwareInfo.viewNames[i];
				projectViewsFormElem.append($("<option></option>").attr("value",viewName).text(viewName));
			}
			projectViewsFormElem.val(oldVal);
			
			
		}, function() {
			// fail
			domObj.switchClasses(classes, 'failureCheck', 1);			
		});
	};
	this['INPUT:regex(id,softwareAccesses.*\.url)|change|live'] = urlFunc;


	var delayChange = function(context) {
		$('DIV.projectFinderDelaySecondSlider', $(context).parent()).slider("option", "value", $(context).val());
	};
	
	
	this['INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)|change'] = function() {
		delayChange(this);
	};
	
	var sliderInit = function(context) {
		var func = function( event, ui ) {
			$("SPAN.projectFinderDelaySecond", $(context).parent()).html( ui.value + "s" );
			$('INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)', $(context).parent()).val(ui.value);
		};
		
		$(context).slider({
			value:0,
			min: 0,
			max: 500,
			step: 30,
			slide: func,
			change: func
		});
	};

	this['DIV.projectFinderDelaySecondSlider|init'] = function() {
		sliderInit(this);
	};
	
	this['DIV#softAdd|click'] = function(event) {
		$this.wallFormView.addFormSoftwareAccesses();
	};

};