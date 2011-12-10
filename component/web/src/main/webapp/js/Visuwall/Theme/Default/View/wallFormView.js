define(['jquery', //
        'curl', //
        'Ajsl/event', //
        'Ajsl/view', //
        'Visuwall/Service/pluginService', //
        'Visuwall/Service/wallService' //
        ], function($, curl, event, view, pluginService, wallService) {
	'use strict';
	
	var wallFormEvent = new function() {
		var $this = this;
		
	// this.context = 'DIV#modal';
		this.softTabsCount = 1;
		this.tabs;
		
		
		this.init = function() {
			
			$(".projects", this).accordion({
	// fillSpace: true,
				autoHeight: false,
	// collapsible: true
				navigation: true
			});
	// $("select", this).multiselect();
			
			
	// $(".projects", this).accordion({
	// fillSpace: true,
	// autoHeight: false,
	// collapsible: true
	// // navigation: true
	// });
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

							view.incrementFormIndexes(newContent);
							view.resetFormValues(newContent);

							sliderInit($('DIV.projectFinderDelaySecondSlider', newContent), 'projectFinderDelaySecond');
							$('INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)', newContent).change(function() {
								delayChange(this, 'projectFinderDelaySecond');
							});
							sliderInit($('DIV.projectStatusDelaySecondSlider', newContent), 'projectStatusDelaySecond');
							$('INPUT:regex(id,softwareAccesses.*\.projectStatusDelaySecond)', newContent).change(function() {
								delayChange(this, 'projectStatusDelaySecond');
							});

							$("FIELDSET.buildField", newContent).hide();
							
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
			if (index != -1) { 
				$this.tabs.tabs("remove", index);
			}
		};

		
//		this["#wallForm #delete|click"] = function() {		
//			wallService.deleteWall($('#wallForm INPUT#name').val());
	//// $("#modal").dialog('close');
//		};
		
		this["#wallForm|submit"] = function() {
			$("#modal .success").empty();
			$("#modal .failure").empty();
			var wallName = $('#wallForm INPUT#name').val().trim();
			if (!wallName) {
				$("#modal .failure").html("Wall name is mandatory");
				return false;
			}
			$("#wallForm .loader").empty().html('<img src="res/img/ajax-loader.gif" />');
			wallService.create(this, function() { // success
				$("#wallForm .loader").empty();
				$("#modal .success").html("Success");
				setTimeout(function() {
					wallService.wall(function(wallNameList) {
						curl(['Visuwall/Theme/Default/View/navigationView' ,
						      'Visuwall/Controller/wallController'], function(navigationView, wallController) {
							navigationView.replaceWallList(wallNameList);
							if ($.history.queryBuilder().contains('wall', wallName)) {
								wallController.showWall(wallName);
							} else {
								$.history.queryBuilder().addController('wall', wallName).load();
							}
						});
					});
					
					$("#modal").dialog('close');
				}, 1000);
			}, function (msg) { // failure
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
		
		var urlFunc = function(urlObj, loginObj, passObj) {
			var val = urlObj.val();
			if (val[val.length - 1] == '/') {
				urlObj.val(val.slice(0, -1));
				val = urlObj.val();
			}
			
			var softTabs = $('#softTabs');
			var tabIdFull = $('DIV[id^="tabs-"]', softTabs).has(urlObj).attr('id');
			var hostname = getHostname(urlObj.val());
			if (!hostname) {
				hostname = urlObj.val();
			}
			if (!hostname) {
				hostname = 'New';
			}
			$('UL LI A[href="#' + tabIdFull + '"]', softTabs).html(hostname);

			// //////////
			
			var classes = ['failureCheck', 'successCheck', 'loadingCheck', 'warningCheck'];
			var domObj = $('#' + urlObj.attr('id').replace(".", "\\.") + "check", urlObj.parent());
			var tabContent = urlObj.parent();

			var id = urlObj.attr('id');
			var preId = id.substring(0, id.lastIndexOf('.') + 1);

			var name = urlObj.attr('name');
			var preName = name.substring(0, name.lastIndexOf('.') + 1);

			
			if (!urlObj.val().trim()) {
				domObj.switchClasses(classes, '', 1);			
				return; 
			}
			
			domObj.switchClasses(classes, 'loadingCheck', 1);
			
			pluginService.manageable(urlObj.val(), loginObj.val(), passObj.val(), function(softwareInfo) {
				// success
				if (softwareInfo.warnings) {
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
	// show : {
	// when : 'click',
	// solo : true
	// },
	// hide: false, // Don't specify a hide event
			            style: {
	// width: {
	// min: 300
	// },
	// height: {
	// min: 300
	// },
			               border: {
			                  width: 5,
			                  radius: 2
			               },
			               padding: 10, 
			               textAlign: 'center',
			               tip: true, // Give it a speech bubble tip with
										// automatic corner detection
			               name: 'cream' // Style it according to the preset
											// 'cream' style
			            }
					});
					
	// domObj.qtip("show");
					domObj.mouseover();
					
					
				} else {
					// success
					domObj.switchClasses(classes, 'successCheck', 1);				
				}

				
				// display build part
				if ($.inArray('build', softwareInfo.pluginInfo.capabilities) != -1) {
					$('FIELDSET.buildField', tabContent).show();
				} else {
					$('FIELDSET.buildField', tabContent).hide();				
				}
				
				// display properties			
				var propertyDiv = $(".properties", tabContent);
				propertyDiv.empty();
				for (var propertyName in softwareInfo.pluginInfo.properties) {
					var propertyValue = softwareInfo.pluginInfo.properties[propertyName];
					var str = '<div style="float:left">';
					str += '<label for="' + preId + 'properties-' + propertyName + '">' + propertyName.ucFirst() + '</label>';
					str += '<input id="' + preId + 'properties-' + propertyName + '" class="ui-widget-content ui-corner-all" name="' + preName + 'properties[' + propertyName + ']" value="' + propertyValue + '" />';
					str += '</div>';
					propertyDiv.append(str);
				}
				
				
				// project Names
				var projectNamesFormElem = $('SELECT:regex(id,softwareAccesses.*\.projectNames)', tabContent);
				var oldVal = projectNamesFormElem.val();
				if (oldVal == null) {
					oldVal = $(projectNamesFormElem).data('newVal');
				}
				projectNamesFormElem.empty();
				for (var key in softwareInfo.projectNames) {
					var projectName = softwareInfo.projectNames[key];
					projectNamesFormElem.append($("<option></option>").attr("value", key).text(projectName));
				}
				projectNamesFormElem.val(oldVal);
				
				// views
				var projectViewsFormElem = $('SELECT:regex(id,softwareAccesses.*\.viewNames)', tabContent);
				var oldVal = projectViewsFormElem.val();
				if (oldVal == null) {
					oldVal = $(projectViewsFormElem).data('newVal');
				}
				projectViewsFormElem.empty();
				if (softwareInfo.viewNames) {
					for (var i = 0; i < softwareInfo.viewNames.length; i++) {
						var viewName = softwareInfo.viewNames[i];
						projectViewsFormElem.append($("<option></option>").attr("value",viewName).text(viewName));
					}
				}
				projectViewsFormElem.val(oldVal);
				
				
			}, function() {
				// fail
				domObj.switchClasses(classes, 'failureCheck', 1);			
			});
		};
		this['INPUT:regex(id,softwareAccesses.*\.url)|change|live'] = function() {
			var login = $('INPUT:regex(id,softwareAccesses.*\.login)', $(this).parent());
			var password = $('INPUT:regex(id,softwareAccesses.*\.password)', $(this).parent());			
			urlFunc($(this), login, password);
		};
		
		this['INPUT:regex(id,softwareAccesses.*\.login)|change|live'] = function() {
			var url = $('INPUT:regex(id,softwareAccesses.*\.url)', $(this).parent().parent().parent());
			var password = $('INPUT:regex(id,softwareAccesses.*\.password)', $(this).parent().parent());			
			urlFunc(url, $(this), password);
		};

		this['INPUT:regex(id,softwareAccesses.*\.password)|change|live'] = function() {
			var url = $('INPUT:regex(id,softwareAccesses.*\.url)', $(this).parent().parent().parent());		
			var login = $('INPUT:regex(id,softwareAccesses.*\.login)', $(this).parent().parent());
			urlFunc(url, login, $(this));
		};
		

		var delayChange = function(context, id) {
			$('DIV.' + id + 'Slider', $(context).parent()).slider("option", "value", $(context).val());
		};
		
		this['INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)|change'] = function() {
			delayChange(this, 'projectFinderDelaySecond');
		};
		
		this['INPUT:regex(id,softwareAccesses.*\.projectStatusDelaySecond)|change'] = function() {
			delayChange(this, 'projectStatusDelaySecond');
		};
		
		var sliderInit = function(context, id) {
			var func = function( event, ui ) {
				$("SPAN." + id, $(context).parent()).html( ui.value + "s" );
				$('INPUT:regex(id,softwareAccesses.*\.' + id +')', $(context).parent()).val(ui.value);
			};
			
			if (id == 'projectStatusDelaySecond') {
				$(context).slider({
					value:0,
					min: 10,
					max: 200,
					step: 10,
					slide: func,
					change: func
				});
				$(context).slider("option", "value", '30');
			} else {
				$(context).slider({
					value:0,
					min: 60,
					max: 500,
					step: 30,
					slide: func,
					change: func
				});
				$(context).slider("option", "value", '200');			
			}
		};

		this['DIV.projectFinderDelaySecondSlider|init'] = function() {
			sliderInit(this, 'projectFinderDelaySecond');
		};

		this['DIV.projectStatusDelaySecondSlider|init'] = function() {
			sliderInit(this, 'projectStatusDelaySecond');
		};

		
		this['INPUT:regex(id,softwareAccesses.*\.allProject)|change|live'] = function() {
			if ($(this).is(':checked')) {
				$('SELECT:regex(id,softwareAccesses.*\.projectNames)', $(this).parent()).attr("disabled","disabled");
				$('SELECT:regex(id,softwareAccesses.*\.viewNames)', $(this).parent()).attr("disabled","disabled");
			} else {
				$('SELECT:regex(id,softwareAccesses.*\.projectNames)', $(this).parent()).removeAttr('disabled');
				$('SELECT:regex(id,softwareAccesses.*\.viewNames)', $(this).parent()).removeAttr('disabled');
				
			}
		};
		
		this['DIV#softAdd|click'] = function(event) {
			wallFormView.addFormSoftwareAccesses();
		};

	};

	
	
	
	var wallFormView = new function() {
		var $this = this;
		
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
			event.register(wallFormEvent, data);
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
	
	return wallFormView;
});