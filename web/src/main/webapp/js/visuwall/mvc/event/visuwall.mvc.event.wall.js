visuwall.mvc.wall = {

	context : 'DIV#modal',

	softTabsCount : 1,

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
		
		var formFields = "input, checkbox, select, textarea";
		
		tabs.tabs({
					tabTemplate : "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
					add : function(event, ui) {
						var contents = $('div[id^="tabs-"]', context);
					
						// -1 is the last but the last is the current added one, so its -2 
						var newContent = $(contents[contents.length - 2]).clone();
						
						// reset value
						newContent.find(formFields).val('');
						
						// increment name
						newContent.find(formFields).each(function(){
							this.name = this.name.replace(/\[(\d+)\]/, function(str, p1) {
								return '[' + (parseInt(p1 , 10) + 1) + ']';
							});
								
							this.id = this.id.replace(/(\d+)\./, function(str, p1) {
								return (parseInt(p1 , 10) + 1) + '.';
							});
						});
						
						// increment name
						newContent.find('label').each(function(){								
							this.htmlFor = this.htmlFor.replace(/(\d+)\./, function(str, p1) {
								return (parseInt(p1 , 10) + 1) + '.';
							});
						});
						
						
						var childrens = newContent.children();
						for (var int = 0; int < childrens.length; int++) {
							$(ui.panel).append(childrens[int]);							
						}
						var tt;
					},
					remove : function(event, ui) {
				//		removeFunction(event, ui.panel);						
					},
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
		tabsElement.tabs("add", "#tabs-"
				+ event.data.eventObj.softTabsCount, "New");
		event.data.eventObj.softTabsCount++;
	}

	
};