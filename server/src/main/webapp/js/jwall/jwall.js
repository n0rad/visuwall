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
				ajsl.dispatcher.registerMain(jwall.mvc.MainController);
				
				// register events
				ajsl.event.registerAll(jwall.mvc.event);
		
				jwall.mvc.view.Loader.init();
				
				// init loader
				ajsl.loader.init(jwall.mvc.view.Loader);
				
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
								
				// get projects and send to init
				jwall.business.service.Project.projects(jwall.mvc.view.ProjectTable.initProjects);
				
				// create updater event
				var updater = setInterval(jwall.mvc.event.Updater['input#updater|click'], 10000);

				// Initialize history plugin.
				$.historyInit(ajsl.dispatcher.dispatch);
				
			});
		}
};


jwall.init();
