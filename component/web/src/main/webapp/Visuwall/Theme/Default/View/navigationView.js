define(['jquery', // 
        'Visuwall/Theme/Default/View/wallFormView', //
        'Visuwall/Service/wallService', //
        'Ajsl/event', //
        'Ajsl/view', //
        ], function($, wallFormView, wallService, event, view) {
	"use strict";
	
	var navigationEvent = new function() {
		var $this = this;

		this.context;

		this.toggleFlag = 'show';

		$(function() {
			$this.context = 'DIV#navigationContainer';
		});

		this['|init'] = function() {
			$this['|mouseleave']();
		};

		this['|mouseenter'] = function() {
			if ($this.toggleFlag == 'wait') {
				$this.toggleFlag = 'show';
			} else if ($this.toggleFlag == 'hide') {
				$this.toggleFlag = 'show';
				$("#navigation").slideDown("fast");
			}
		};

		this['|mouseleave'] = function() {
			$this.toggleFlag = 'wait';
			window.setTimeout(function() {
				$("#navigation").each(function() {
					if ($this.toggleFlag != 'wait') {
						return;
					}
					$(this).slideUp("fast", function() {
						$this.toggleFlag = 'hide';
					});
				});
			}, 1000);
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
			var content = $('#helperdiv');
			var data = content.clone().show();
			$("#helperimg").qtip({
				content : data,
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
		
		this.displayEditForm = function(htmlData, formData) {
			var domObject = $("#modal").html(htmlData);
			$this._displayForm(domObject, "Wall configuration", 'wall/edit');		
			view.rebuildFormRec(domObject, formData, wallFormView);
		};
		
		this.displayCreationForm = function(htmlData) {
			var domObject = $("#modal").html(htmlData);
			$this._displayForm(domObject, "Wall creation", 'wall/create');
		};
		
		///////////////////////////////////////////////////////////
		
		this._displayForm = function(domObject, title, closeController) {
			domObject.dialog({
				height: 470,
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
					// TODO unregister live events
					//		        $this.wallFormEvent.__getObject__(function(bean) {
					//		        	ajsl.event.unregisterLive(bean, domObject);
					//		        }); 
					$.history.queryBuilder().removeController(closeController).load();
				}
			});
		};

	};

	event.register(navigationEvent, $('DIV#navigationContainer'));
	
	return navigationView;
});