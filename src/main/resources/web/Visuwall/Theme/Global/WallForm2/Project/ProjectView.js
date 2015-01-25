define(['jquery', 'underscore', 'when',
        'text!./ProjectView.html',
        './ProjectContent',
        
        'css!./ProjectView.css'], 
function($, _, when, projectViewTemplate, ProjectContent) {
	'use strict';

	function ProjectView(context) {
		var self = this;
		var addTabElement = function(event, ui) {
			var removeFunc = (function(projInfo, ui) {
				return function() {
					self.tabsDom.tabs('remove', $(this).parent().index());
					projInfo.deletePromise.resolve();
				};
			})(self.tempProjectInfo, ui);
			$('.ui-icon-close', $(ui.tab).parent()).bind('click', removeFunc);
			var deletePromise = when.defer();
			deletePromise.then(removeFunc);
			new ProjectContent(ui.panel, self.tempProjectInfo, deletePromise);
						
		};

		context.html(projectViewTemplate);
		this.tabsDom = $('#projectTab', context);
		this.tabsDom.hide();
		this.nextTabIndex = 0;
		
		this.tabsDom.tabs({tabTemplate : "<li><a href='#{href}'>#{label}</a> <div class='dropzone'></div><span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
			add : addTabElement, preremove : function(event, ui) {
				return false;
			}
			}).addClass( "ui-tabs-vertical ui-helper-clearfix" );

		this.dropped = false;
		this.draggable_sibling;

		$("#projectTabNav", this.tabsDom).sortable({
			start : function(event, ui) {
				$('.dropzone', $(ui.item).parent()).not($('.dropzone', ui.item)).show();
				self.draggable_sibling = $(ui.item).prev();
			},
			stop : function(event, ui) {
				$('.dropzone', $(ui.item).parent()).hide();
				if (self.dropped) {
					if (self.draggable_sibling.length == 0)
						$('#sortable').prepend(ui.item);

					self.draggable_sibling.after(ui.item);
					self.dropped = false;
				}
			},
			revert: true,
			cursorAt: {left: 83, top: 13}
		}).disableSelection();

	}
	
	ProjectView.prototype = {
		addProject : function(projectInfo) {
			this.tempProjectInfo = projectInfo;
			this.tabsDom.tabs("add", "#tabs-" + this.nextTabIndex++, projectInfo.projectName);
			if (this.tabsDom.tabs("length") == 1) {
				this.tabsDom.slideDown('slow');
			}
			$('li', this.tabsDom).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
			var self = this;
			$(".dropzone").droppable({
				activeClass : 'active',
				hoverClass : 'hovered',
				drop : function(event, ui) {
					self.dropped = true;
					$(event.target).addClass('dropped');
				}
			}).hide();
		},
	};
	
	return ProjectView;
});