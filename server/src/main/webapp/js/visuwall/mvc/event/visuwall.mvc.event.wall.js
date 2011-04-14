visuwall.mvc.wall = {

	context : 'DIV#modal',

	softTabsCount : 2,

	init : function(context) {
		var tabs = $("#softTabs");

		
//        var mainDynamicForm = $("#tabs-1").dynamicForm(addFunction, removeFunction, {
//            limit:5, 
//            formPrefix:"wallForm",
//            afterClone:function(clone){
//                    if(window.console && window.console.log){
//                            console.log("I'm a clone", clone);
//                    }
//            },
//            createColor:"green"
//        });
//			
//
//		var addFunction = function(event, elem) {
//			mainDynamicForm.outerClickOnPlus()(event);
//		};
//		var removeFunction = function(event, elem) {
//			mainDynamicForm.outerClickOnMinus()(event);
//		};
		
		tabs.tabs({
					tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
//					add : function(event, ui) {
//						addFunction(event, ui.panel);
//					},
//					remove : function(event, ui) {
//						removeFunction(event, ui.panel);						
//					},
					preremove : function(event, ui) {
						if (tabs.tabs('length') > 1) {
							return false;
						} else {
							return true;
						}
					}
				});

		$("#softTabs span.ui-icon-close").live("click", function(var1, var2) {
			var index = $("li", tabs).index($(this).parent());
			tabs.tabs("remove", index);
		});

		
		
		$('#softAdd').hover(function() {
			$(this).addClass('ui-state-hover');
		}, function() {
			$(this).removeClass('ui-state-hover');
		});

		
		
//		$('OL.projects').selectable({
//			selected : function(e) {
//				$(this).toggleClass("selected");
//			}
//		});

		//$("INPUT#allProjects").button();
	},

	'DIV#softAdd|click' : function(event) {
		var tabsElement = $('#softTabs', event.data.eventObj.context);
		tabsElement.tabs("add", "#softTabs-"
				+ event.data.eventObj.softTabsCount, "New");
		event.data.eventObj.softTabsCount++;
	}

};