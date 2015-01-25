define(['jquery', 'underscore', 'when', 'Ajsl/event',
        'text!./SoftwareElements.html',
        'text!./SoftwareElementProject.html',
        'text!./SoftwareElementView.html',

        'css!./SoftwareElements.css'],
function($, _, when, event, elementsTemplate, projectTemplate, viewTemplate) {
	
	var projectTpl = _.template(projectTemplate);
	var viewTpl = _.template(viewTemplate);
	
	function SoftwareElements(context, projectView) {
		this.context = context;
		this.projectView = projectView;
		
		context.html(elementsTemplate);
		context.accordion({autoHeight : false, navigation : true});

		this.projectContainer = $('.projectList', context);
		this.viewContainer = $('.viewList', context);
	
		var self = this;
		this.events = {
			'.addProject|init' : function(e) {
				$(this).button({icons: { primary: "ui-icon-plusthick"}, text : false});
			},
			'.addProject|click' : function() {
				$this = $(this);
				var deletePromise = when.defer();
				(function(element) {
					deletePromise.then(function() {
						element.parent().show();
					});
				})($this);
				var projectName = $('.projectName', $this.parent()).text();
				var projectInfo = {projectName : projectName,
								   softwareName : 'softwareName',
								   softwareId : 0,
								   deletePromise : deletePromise};
				self.projectView.addProject(projectInfo);
				$this.parent().hide();
				return false; // TODO needed to not submit form, but why !
			}
		};
	};
	
	SoftwareElements.prototype = {
		
		updateElements : function(projects, views) {
			for (var key in projects) {
				var projectHtml = $(projectTpl({projectId : key, projectName : projects[key]}));
				this.projectContainer.append(projectHtml);
				event.register(this.events, projectHtml);
			}

			for (var i = 0; i < views.length; i++) {
				var viewHtml = viewTpl({viewName : views[i].name, viewSize : Object.size(views[i].projects)});
				this.viewContainer.append(viewHtml);
//				$('.size', viewHtml).qtip({content : "<div>salut</div>", show: 'mouseover', hide: 'mouseout'});
			}

			
			// project Names
//			var projectNamesFormElem = $('SELECT:regex(id,softwareAccesses.*\.projectNames)', tabContent);
//			var oldVal = projectNamesFormElem.val();
//			if (oldVal == null) {
//				oldVal = $(projectNamesFormElem)
//						.data('newVal');
//			}
//			projectNamesFormElem.empty();
//			for ( var key in softwareInfo.projectNames) {
//				var projectName = softwareInfo.projectNames[key];
//				projectNamesFormElem.append($("<option></option>").attr("value", key).text(projectName));
//			}
//			projectNamesFormElem.val(oldVal);
//
//			// views
//			var projectViewsFormElem = $('SELECT:regex(id,softwareAccesses.*\.viewNames)', tabContent);
//			var oldVal = projectViewsFormElem.val();
//			if (oldVal == null) {
//				oldVal = $(projectViewsFormElem).data('newVal');
//			}
//			projectViewsFormElem.empty();
//			if (softwareInfo.viewNames) {
//				for ( var i = 0; i < softwareInfo.viewNames.length; i++) {
//					var viewName = softwareInfo.viewNames[i];
//					projectViewsFormElem.append($("<option></option>").attr("value", viewName).text(viewName));
//				}
//			}
//			projectViewsFormElem.val(oldVal);
		}
	};
	
	return SoftwareElements;
});