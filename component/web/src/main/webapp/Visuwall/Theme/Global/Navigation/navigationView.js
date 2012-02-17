define(['jquery', // 
        'text!Visuwall/Theme/Global/Navigation/HelperTemplate.html', //
        'text!Visuwall/Theme/Global/Navigation/Navigation.html', //
        'Visuwall/Theme/Global/WallForm/wallFormView', //
        'Visuwall/Service/wallService', //
        'Ajsl/event', //
        'Ajsl/view', //
        
        'css!Visuwall/Theme/Global/Navigation/navigation.css', //
        ], function($, HelperTemplate, NavigationTemplate, wallFormView, wallService, event, view) {
	"use strict";
	
	$("#navigationContainer").html(NavigationTemplate);
	
	var toggleFlag = 'show';
	var navigationLeaveEvent = function() {
		$("#navigation").each(function() {
			if (toggleFlag != 'wait') {
				return;
			}
			$(this).slideUp("fast", function() {
				toggleFlag = 'hide';
			});
		});
	};
	
	var navigationEvent = new function() {
		var $this = this;

		this.context;

		$(function() {
			$this.context = 'DIV#navigationContainer';
		});

		this['|init'] = function() {
			$this['|mouseleave']();
		};

		this['|mouseenter'] = function() {
			if (toggleFlag == 'wait') {
				toggleFlag = 'show';
			} else if (toggleFlag == 'hide') {
				toggleFlag = 'show';
				$("#navigation").slideDown("fast");
			}
		};

		this['|mouseleave'] = function() {
			toggleFlag = 'wait';
			window.setTimeout(navigationLeaveEvent, 1000);
		};

		this['#fontSizeSlider|init'] = function() {
			var slideFunc = function(e, ui) {
				$('#projectsTable').css({
					fontSize : ui.value + 'em'
				});
				$.cookie('sliderValue', ui.value);
			};

			$(this).slider({
				min : 0.5,
				max : 1.2,
				step : 0.01,
				slide : slideFunc,
				change : slideFunc
			});
			
			var start = $.cookie('sliderValue');
			if (!start) {
				start = 0.6;
				$.cookie('sliderValue', start);
			}
			$(this).slider("option", "value", start);
		};

		this['#helperimg|init'] = function() {
			$("#helperimg").qtip({
				content : $(HelperTemplate),
				position : {
					corner : {
						tooltip : 'topRight',
						target : 'bottomLeft'
					}
				},
//				show : {
//					when : 'click',
//					solo : true
//				},
//	            hide: false, // Don't specify a hide event
	            style: {
//	               width: {
//	            	   min: 300
//	               },
//	               height: {
//	            	   min: 300
//	               },
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
		};

		this['#wallSelector #wallSelect|init'] = function() {
			wallService.wall(function(wallNameList) {
				navigationView.replaceWallList(wallNameList);
			});
		};

		this['#wallSelector #wallSelect|change'] = function() {
			$.history.queryBuilder().addController('wall', $('#wallSelector #wallSelect').val()).load();
		};

		this['#wallSelector #edit|click'] = function() {
			navigationLeaveEvent();
			var wallId = $('#wallSelector #wallSelect').val();
			if (wallId) {
				$.history.queryBuilder().addController('wall/edit', wallId)
						.load();
			}
		};

		this['#wallSelector #add|click'] = function() {
			$.history.queryBuilder().addController('wall/create').load();
		};

	};

	
	var navigationView =  new function() {
		var $this = this;
		
		this.navigation;
			
		this.init = function() {
			this.navigation = $('#navigation');
		};

		this.replaceWallList = function(newWallList) {
			var select = $('#wallSelect', this.navigation);
			var selectedValue = select.val();
			select.children().remove();
//			var emptyOption = $('<option disabled="disabled">Select a wall</option>');
//			select.append(emptyOption);
			for ( var i = 0; i < newWallList.length; i++) {
				var newOption = $('<option value="' + newWallList[i] + '">' + newWallList[i] + '</option>');
				select.append(newOption);
			}
			select.val(selectedValue);
		};
		
		this.setVersion = function(version) {
			$('A#visuwallVersion', this.navigation).html(version);
		};
	};

	event.register(navigationEvent, $('DIV#navigationContainer'));
	
	return navigationView;
});