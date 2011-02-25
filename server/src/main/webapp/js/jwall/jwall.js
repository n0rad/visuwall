jwall = {
		business : {service : {}},
		mvc : {controller : {}, view: {}, event: {}},
		
		/**
		 * init on ready
		 */
		init : function() {
			$(function (){

				// register controllers
				ajsl.dispatcher.registerAll(jwall.mvc.controller);
				
				// register main controller
				ajsl.dispatcher.registerMain(jwall.mvc.mainController);
				
				// register events
				ajsl.event.registerAll(jwall.mvc.event);
				
				// init loader view
				jwall.mvc.view.loader.init();
				
				// init loader
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
								
				jwall.business.service.project.getProjects(function(projects) {
					jwall.mvc.view.projectTable.initProjects(projects);
				});
				
				// Initialize history plugin.
				$.historyInit(ajsl.dispatcher.dispatch);
				
				// create updater
				var updater = setInterval(jwall.mvc.event.updater['input#updater|click'], 10000);
				
			});
		}
};


jwall.init();