define(['jquery', 'underscore', 'text!./ProjectContent.html', 'text!./ProjectSoftwareElement.html'],
function($, _, projectContentTemplate, projectSoftwareTemplate) {
	
	var projectContentTpl = _.template(projectContentTemplate);
	var projectSoftwareTpl = _.template(projectSoftwareTemplate);
	
	function ProjectSoftwareElement(context, projectInfo, removePromise) {
		this.context = $(context);
		this.context.html(projectContentTpl({projectName : projectInfo.projectName}));
		this.softwares = $(".softwareElementList", this.context);
		
		this.addSoftware(projectInfo, removePromise);
	}
	
	ProjectSoftwareElement.prototype = {
		addSoftware : function(projectInfo, removePromise) {
			var projectSoft = $(projectSoftwareTpl(projectInfo));
			$('.removeProject', projectSoft).button({icons: { primary: "ui-icon-minusthick"}, text : false})
				.bind('click', function() {
					var size = $(this).parent().parent().size();
					$(this).parent().remove();
					removePromise.resolve(size);
				});
			this.softwares.append(projectSoft);
		}
//	,
//		removeSoftware : function(softwareName) {
//			$("LI[val='"+ softwareName +"']", this.software).remove();
//		}
	};
	
	return ProjectSoftwareElement;
	
});