define(['jquery', 'Ajsl/event',
        'jquery-ui'], 
function($, event) {
	'use strict';

	var sliderInit = function(context, id) {
		var func = function(event, ui) {
			$("SPAN." + id, $(context).parent()).html(ui.value + "s");
			$('INPUT:regex(id,softwareAccesses.*\.' + id + ')', $(context).parent()).val(ui.value);
		};

		if (id == 'projectStatusDelaySecond') {
			$(context).slider({value : 0, min : 10, max : 200, step : 10, slide : func, change : func});
			$(context).slider("option", "value", '30');
		} else {
			$(context).slider({value : 0, min : 60, max : 500, step : 30, slide : func, change : func});
			$(context).slider("option", "value", '200');
		}
	};

	var delayChange = function(context, id) {
		$('DIV.' + id + 'Slider', $(context).parent()).slider(
				"option", "value", $(context).val());
	};
	
	var addTabElement = function(event, ui) {
		var contents = $('div[id^="tabs-"]', context);

		// -1 is the last but the last is the current added one, so its -2
		var newContent = $(contents[contents.length - 2]).clone();

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
	
	function WallFormEvent(element, wallFormView) {
		
		var $this = this;

		this.context = element;
		this.softTabsCount = 1;
		this.tabs;
		event.register(wallFormEvent, element);

		this.init = function() {

			$(".projects", this).accordion({
				// fillSpace: true,
				autoHeight : false,
				// collapsible: true
				navigation : true
			});
			$this.tabs = $("#softTabs", this);
			var context = this;

			$this.tabs.tabs({
				tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
				add : addTabElement,
				remove : function(event, ui) {
					// removeFunction(event, ui.panel);
				},
				select : function(event, ui) {
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

		this["#wallForm|submit"] = function() {
			wallFormView.submitForm();
			return false;
		};

		this['INPUT:regex(id,softwareAccesses.*\.url)|change|live'] = function() {
			var login = $('INPUT:regex(id,softwareAccesses.*\.login)', $(this).parent());
			var password = $('INPUT:regex(id,softwareAccesses.*\.password)', $(this).parent());
			wallFormView.discoverSoftware($(this), login, password);
		};

		this['INPUT:regex(id,softwareAccesses.*\.login)|change|live'] = function() {
			var url = $('INPUT:regex(id,softwareAccesses.*\.url)', $(this).parent().parent().parent());
			var password = $('INPUT:regex(id,softwareAccesses.*\.password)', $(this).parent().parent());
			wallFormView.discoverSoftware(url, $(this), password);
		};

		this['INPUT:regex(id,softwareAccesses.*\.password)|change|live'] = function() {
			var url = $('INPUT:regex(id,softwareAccesses.*\.url)', $(this).parent().parent().parent());
			var login = $('INPUT:regex(id,softwareAccesses.*\.login)', $(this).parent().parent());
			wallFormView.discoverSoftware(url, login, $(this));
		};

		this['INPUT:regex(id,softwareAccesses.*\.projectFinderDelaySecond)|change'] = function() {
			delayChange(this, 'projectFinderDelaySecond');
		};

		this['INPUT:regex(id,softwareAccesses.*\.projectStatusDelaySecond)|change'] = function() {
			delayChange(this, 'projectStatusDelaySecond');
		};

		this['DIV.projectFinderDelaySecondSlider|init'] = function() {
			sliderInit(this, 'projectFinderDelaySecond');
		};

		this['DIV.projectStatusDelaySecondSlider|init'] = function() {
			sliderInit(this, 'projectStatusDelaySecond');
		};

		this['INPUT:regex(id,softwareAccesses.*\.allProject)|change|live'] = function() {
			if ($(this).is(':checked')) {
				$('SELECT:regex(id,softwareAccesses.*\.projectNames)', $(this).parent()).attr("disabled", "disabled");
				$('SELECT:regex(id,softwareAccesses.*\.viewNames)', $(this).parent()).attr("disabled", "disabled");
			} else {
				$('SELECT:regex(id,softwareAccesses.*\.projectNames)', $(this).parent()).removeAttr('disabled');
				$('SELECT:regex(id,softwareAccesses.*\.viewNames)', $(this).parent()).removeAttr('disabled');
			}
		};

		this['DIV#softAdd|click'] = function(event) {
			wallFormView.addFormSoftwareAccesses();
		};

		this['INPUT.cancel|click'] = function() {
			wallFormView.hideForm();
		};
	};
	
	return WallFormEvent;
	
});