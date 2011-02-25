jwall = {
		business : {service : {}},
		mvc : {view: {}},
		
		/**
		 * init on ready
		 */
		init : function() {
			$(function (){

				jwall.mvc.view.loader.init();
				
				ajsl.loader.init(jwall.mvc.view.loader);
				
				
				// loader test
				$("#loader1").bind('click', function() {
					if ($("#loader1").hasClass('selected')) {
						$("#loader1").removeClass('selected');
						ajsl.loader.del('loader1');
					} else {
						$("#loader1").addClass('selected');
						ajsl.loader.add('loader1', 'description of loader1', function() {
							LOG.info('cancel loader1');					
						});
					}
				});
				$("#loader2").bind('click', function() {
					if ($("#loader2").hasClass('selected')) {
						$("#loader2").removeClass('selected');
						ajsl.loader.del('loader2');
					} else {
						$("#loader2").addClass('selected');
						ajsl.loader.add('loader2', 'description of loader2', function() {
							LOG.info('cancel loader2');					
						});
					}
				});
				// Initialize history plugin.
				// The callback is called at once by present location.hash.
				//$.historyInit(ajsl.dispatcher.dispatch);
				
				
				
//				
				jwall.business.service.project.getProjects(function(projects) {
					jwall.mvc.view.projectTable.initProjects(projects);
				});
				
			});
		}
};


jwall.init();