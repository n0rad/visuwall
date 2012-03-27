define(['jquery', 'Ajsl/event', 'Ajsl/view', 'text!Visuwall/Theme/Global/WallForm2/Software/SoftwareView.html',
        'Visuwall/Service/pluginService', 'text!Visuwall/Theme/Global/WallForm2/Software/SoftwareInfoView.html',
        
        'css!Visuwall/Theme/Global/WallForm2/Software/SoftwareView.css'],
function($, event, viewHelper, SoftwareTemplate, pluginService, SoftwareInfoTemplate) {

	var urlStatus = [ 'failureCheck', 'successCheck', 'loadingCheck', 'warningCheck' ];
	
	function SoftwareView(context) {
		this.context = $(context);
		var self = this;

		this.softTabsCount = 1;
		this.context.html(SoftwareTemplate);
		
		$(".projectsAccordion", context).accordion({autoHeight : false, navigation : true});
		this.tabs = $(context);
		this.tabs.tabs({
			tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
			add : addTabElement,
			select : function(event, ui) {
				$('LABEL:regex(id,softwareAccesses.*\.urlcheck)').mouseout();
			},
			preremove : function(event, ui) {
				if (self.tabs.tabs('length') > 1) {
					return false;
				} else {
					return true;
				}
			}
		});
		
		function addTabElement(event, ui) {
			var contents = $('div[id^="tabs-"]', this.context);

			// -1 is the last but the last is the current added one, so its -2
			var newContent = $(contents[contents.length - 2]).clone();

			viewHelper.incrementFormIndexes(newContent);
			viewHelper.resetFormValues(newContent);

			self._sliderInit($('DIV.projectFinderDelaySecondSlider', newContent), 'projectFinderDelaySecond');
			$('INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)', newContent).change(function() {
				self._delayChange(this, 'projectFinderDelaySecond');
				});
			self._sliderInit($('DIV.projectStatusDelaySecondSlider', newContent), 'projectStatusDelaySecond');
			$('INPUT:regex(id,softwareAccesses.*\.projectStatusDelaySecond)', newContent).change(function() {
				self._delayChange(this, 'projectStatusDelaySecond');
				});

			$("FIELDSET.buildField", newContent).hide();

			var childrens = newContent.children();
			for ( var i = 0; i < childrens.length; i++) {
				$(ui.panel).append(childrens[i]);

				// projects selector
				if ($(childrens[i]).is(".projects")) {
					$(childrens[i]).accordion({
						autoHeight : false,
						navigation : true
					});
				}
			}
		};
				
		this.events = {
			'#softAdd|mouseenter' : function() {
				$(this).addClass('ui-state-hover');
			},
			'#softAdd|mouseleave' : function() {
				$(this).removeClass('ui-state-hover');
			},
			'INPUT:regex(id,softwareAccesses.*\.url)|change|live' : function() {
				var login = $('INPUT:regex(id,softwareAccesses.*\.login)', $(this).parent());
				var password = $('INPUT:regex(id,softwareAccesses.*\.password)', $(this).parent());
				self._discoverSoftware($(this), login, password);
			},
			'INPUT:regex(id,softwareAccesses.*\.login)|change|live' : function() {
				var url = $('INPUT:regex(id,softwareAccesses.*\.url)', $(this).parent().parent().parent());
				var password = $('INPUT:regex(id,softwareAccesses.*\.password)', $(this).parent().parent());
				self._discoverSoftware(url, $(this), password);
			},
			'INPUT:regex(id,softwareAccesses.*\.password)|change|live' : function() {
				var url = $('INPUT:regex(id,softwareAccesses.*\.url)', $(this).parent().parent().parent());
				var login = $('INPUT:regex(id,softwareAccesses.*\.login)', $(this).parent().parent());
				self._discoverSoftware(url, login, $(this));
			},
			'INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)|change' : function() {
				self._delayChange(this, 'projectFinderDelaySecond');
			},
			'INPUT:regex(id,softwareAccesses.*\.projectStatusDelaySecond)|change' : function() {
				self._delayChange(this, 'projectStatusDelaySecond');
			},
			'DIV.projectFinderDelaySecondSlider|init' : function() {
				self._sliderInit(this, 'projectFinderDelaySecond');
			},
			'DIV.projectStatusDelaySecondSlider|init' : function() {
				self._sliderInit(this, 'projectStatusDelaySecond');
			},
			'INPUT:regex(id,softwareAccesses.*\.allProject)|change|live' : function() {
				if ($(this).is(':checked')) {
					$('SELECT:regex(id,softwareAccesses.*\.projectNames)', $(this).parent()).attr("disabled", "disabled");
					$('SELECT:regex(id,softwareAccesses.*\.viewNames)', $(this).parent()).attr("disabled", "disabled");
				} else {
					$('SELECT:regex(id,softwareAccesses.*\.projectNames)', $(this).parent()).removeAttr('disabled');
					$('SELECT:regex(id,softwareAccesses.*\.viewNames)', $(this).parent()).removeAttr('disabled');
				}
			},
			'DIV#softAdd|click' : function(event) {
				self.addFormSoftwareAccesses();
			}
		};
		event.register(this.events, this.context);
	}
	
	SoftwareView.prototype = {
		_delayChange : function(context, id) {
			$('DIV.' + id + 'Slider', $(context).parent()).slider(
					"option", "value", $(context).val());
		},
		_sliderInit : function(context, id) {
			context = $(context);
			var onChange = function(event, ui) {
				$("SPAN." + id, context.parent()).html(ui.value + "s");
				$('INPUT:regex(id,softwareAccesses.*\.' + id + ')', context.parent()).val(ui.value);
			};
			if (id == 'projectStatusDelaySecond') {
				context.slider({value : 0, min : 10, max : 200, step : 10, slide : onChange, change : onChange});
				context.slider("option", "value", '30');
			} else {
				context.slider({value : 0, min : 60, max : 500, step : 30, slide : onChange, change : onChange});
				context.slider("option", "value", '200');
			}
		},
		addFormSoftwareAccesses : function(id) {
			if ($('#tabs-' + id, this.context).length) {
				return;
			}
			if (id == undefined) {
				id = this.softTabsCount;
				this.softTabsCount++;
			} else {
				// be sure that next tab id is not in used
				if (this.softTabsCount <= id) {
					this.softTabsCount = id + 1;
				}
			}
			this.tabs.tabs('add', '#tabs-' + id, "New");
		},
		_cleanAndUpdateUrl : function(urlObj) {
			var val = urlObj.val();
			if (val[val.length - 1] == '/') {
				urlObj.val(val.slice(0, -1));
				val = urlObj.val();
			}
			var tabIdFull = $('DIV[id^="tabs-"]', this.context).has(urlObj).attr('id');
			var hostname = getHostname(urlObj.val());
			if (!hostname) {
				hostname = urlObj.val();
			}
			if (!hostname) {
				hostname = 'New';
			}
			$('UL LI A[href="#' + tabIdFull + '"]', softTabs).html(hostname);
		},
		_displaySoftwareInfo : function() {
			var infoContent = _.template(SoftwareInfoTemplate, {
				"pluginInfo.name" : softwareInfo.pluginInfo.name,
				"pluginInfo.version" : softwareInfo.pluginInfo.version,
				"softwareId.name" : softwareInfo.softwareId.name,
				"softwareId.version" : softwareInfo.softwareId.version,
				"softwareId.warnings" : softwareInfo.softwareId.warnings});
			domObj.qtip({
				content : infoContent,
				position : {corner : {tooltip : 'topLeft', target : 'bottomRight'}},
				style : {border : {width : 5, radius : 2},
					padding : 10, textAlign : 'center', tip : true, name : 'cream'}
			});
			domObj.mouseover();
		},
		_discoverSoftware : function(urlObj, loginObj, passObj) {
			var statusDom = $('#' + urlObj.attr('id').replace(".", "\\.") + "check", urlObj.parent());
			if (!urlObj.val().trim()) {
				statusDom.switchClasses(urlStatus, '', 1);
				return;
			}
			
			this._cleanAndUpdateUrl(urlObj);
			var tabContent = urlObj.parent();

			var id = urlObj.attr('id');
			var preId = id.substring(0, id.lastIndexOf('.') + 1);

			var name = urlObj.attr('name');
			var preName = name.substring(0, name.lastIndexOf('.') + 1);

			statusDom.switchClasses(urlStatus, 'loadingCheck', 1);

			pluginService.manageable(urlObj.val(), loginObj.val(), passObj.val(), function(softwareInfo) {
				// success
				if (softwareInfo.softwareId.warnings) {
					statusDom.switchClasses(urlStatus, 'warningCheck', 1);
				} else {
					// success
					statusDom.switchClasses(urlStatus, 'successCheck', 1);
				}

				// display build part
				if ($.inArray('build', softwareInfo.pluginInfo.capabilities) != -1) {
					$('FIELDSET.buildField', tabContent).show();
				} else {
					$('FIELDSET.buildField', tabContent).hide();
				}

				// display properties
//				var propertyDiv = $(".properties", tabContent);
//				propertyDiv.empty();
//				for ( var propertyName in softwareInfo.pluginInfo.properties) {
//					var propertyValue = softwareInfo.pluginInfo.properties[propertyName];
//					var str = '<div style="float:left">';
//					str += '<label for="' + preId + 'properties-' + propertyName + '">' + propertyName.ucFirst() + '</label>';
//					str += '<input id="' + preId + 'properties-' + propertyName + '" class="ui-widget-content ui-corner-all" name="'
//							+ preName + 'properties[' + propertyName + ']" value="' + propertyValue + '" />';
//					str += '</div>';
//					propertyDiv.append(str);
//				}

				// project Names
				var projectNamesFormElem = $('SELECT:regex(id,softwareAccesses.*\.projectNames)', tabContent);
				var oldVal = projectNamesFormElem.val();
				if (oldVal == null) {
					oldVal = $(projectNamesFormElem)
							.data('newVal');
				}
				projectNamesFormElem.empty();
				for ( var key in softwareInfo.projectNames) {
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
					for ( var i = 0; i < softwareInfo.viewNames.length; i++) {
						var viewName = softwareInfo.viewNames[i];
						projectViewsFormElem.append($("<option></option>").attr("value", viewName).text(viewName));
					}
				}
				projectViewsFormElem.val(oldVal);

			}, function() {
				// fail
				statusDom.switchClasses(urlStatus, 'failureCheck', 1);
			});
		}
	};
	
	return SoftwareView;
});