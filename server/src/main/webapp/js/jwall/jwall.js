jwall = {
		business : {},
		view : {},
		
		/**
		 * init on ready
		 */
		init : function() {
			$(function (){

//				
				jwall.business.service.project.getProjects(function(projects) {
					LOG.info("Reading projects");
					for (var i = 0; i < projects.length; i++) {
						jwall.view.project.buildProject(projects[i]);
					}
				});

				
				
				jwall.view.project.projectCount();
				
				alert();
				
			});
		}
};


jwall.init();