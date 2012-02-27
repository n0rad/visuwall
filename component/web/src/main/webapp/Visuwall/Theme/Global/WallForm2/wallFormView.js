define(['jquery', //
        'require', //
        'text!Visuwall/Theme/Global/WallForm2/WallFormTemplate.html', //
        'Ajsl/event', //
        'Ajsl/view', //
        'Visuwall/Service/pluginService', //
        'Visuwall/Service/wallService', //
        'Visuwall/Theme/Global/WallForm2/WallFormEvent', //
        
        'css!Visuwall/Theme/Global/WallForm2/wallForm.css', //
        'css!Visuwall/Theme/Global/WallForm2/visuwallForm.css', //
        'css!Visuwall/Theme/Global/WallForm2/visuwallForm-softTab.css', //
        'css!Visuwall/Theme/Global/WallForm2/visuwallForm-projectTab.css', //
        ], function($, require, WallFormTemplate, event, view, pluginService, wallService, WallFormEvent) {
	'use strict';
	
	var wallFormEvent = new WallFormEvent(this);
	
	
	
	var wallFormView = new function() {
		var $this = this;
		
		this.wallFormData;
		
		this.softTabsCount = 1;
		
		this.context;
		
		this.displayForm = function(data) {
			var formDiv = $('#visuwallForm');
			$('#over').show();
			formDiv.slideDown();
			formDiv.html(WallFormTemplate);
			event.register(wallFormEvent, formDiv);
			view.rebuildFormRec(formDiv, data, this);
			

			$('#projectTab', formDiv).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
			$('#projectTab li', formDiv).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
			
//			$( '#projectTab',  formDiv).tabs().find( ".ui-tabs-nav" ).sortable({ connectWith: ".connectedSortable" });
			
//			$( "#tabs li" ).draggable({ revert: "invalid" });
//			
//			$( "#tabs li" ).droppable({
//				connectWith: ".connectedSortable",
//				activeClass: "ui-state-hover42",
//				hoverClass: "ui-state-active42",
//				drop: function( event, ui ) {
//					$( this )
//						.addClass( "ui-state-highlight42" )
//						.find( "p" )
//							.html( "Dropped!" );
//				}
//			});
//			
			$( ".projectsAccordion", formDiv).accordion();
			
			
			$("#radio", formDiv).buttonset();
			$( "#format", formDiv ).buttonset();
			$( "#allProjects", formDiv).button();
			
			
			$( "#slider-range" ).slider({
				range: true,
				min: 0,
				max: 500,
				values: [ 75, 300 ],
				slide: function( event, ui ) {
					$( "#amount" ).val( "$" + ui.values[ 0 ] + " - $" + ui.values[ 1 ] );
				}
			});
			$( "#amount" ).val( "$" + $( "#slider-range" ).slider( "values", 0 ) +
				" - $" + $( "#slider-range" ).slider( "values", 1 ) );

			var el = $( "#slider-range a" )[0];
			$(el).addClass('warning');
			
			
			$("#projectTabNav", formDiv).sortable({connectWith: ".softwareElementList"});
			$(".softwareElementList", formDiv).sortable({connectWith: "#projectTabNav" });
			$('.projectList, .viewList', formDiv).sortable({
				connectWith: "#projectTabNav",
				helper: "clone",
				appendTo: "body",
				});
			
//			$( "#sortable1, #sortable2" ).sortable({
//				connectWith: ".connectedSortable",
//				helper: "clone", appendTo: "body",
//			}).disableSelection();
		};
		
		this.hideForm = function() {
			var formDiv = $('#visuwallForm');
			formDiv.slideUp();
			var v = $('LABEL:regex(id,softwareAccesses.*\.urlcheck)');
			v.mouseout();

			$.history.queryBuilder()
        	.removeController(isCreate ? 'wall/create2' : 'wall/edit2')
        	.load();

			
			// TODO unregister live events
			//		        $this.wallFormEvent.__getObject__(function(bean) {
			//		        	ajsl.event.unregisterLive(bean, domObject);
			//		        }); 
//			$.history.queryBuilder().removeController(closeController).load();			
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