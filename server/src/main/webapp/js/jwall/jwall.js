jwall = {
		business : {service : {}},
		mvc : {controller : {}, view: {}, event: {}},
		persistence : {dao : {}},
		
		/**
		 * init on ready
		 */
		init : function() {
			$(function (){
				
				jQuery.timeago.settings.strings = {
							suffixAgo: " ago",
					        suffixFromNow: "from now",
					        seconds: "<1min",
					        minute: "1min",
					        minutes: "%dmin",
					        hour: "1h",
					        hours: "%dh",
					        day: "1d",
					        days: "%dd",
					        month: "1M",
					        months: "%dM",
					        year: "1y",
					        years: "%dy"
						};
			
				jQuery("abbr.timeago").timeago();
				
				// register controllers
				ajsl.dispatcher.registerAll(jwall.mvc.controller);
				
				// register main controller
				ajsl.dispatcher.registerMain(jwall.mvc.MainController);
				
				ajsl.log.setLevel('warn');
				
				// register events
				//ajsl.event.registerAll(jwall.mvc.event);
		
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
	
				jwall.mvc.controller.ProjectController.buildProjects();
				
				// create updater event
				var updater = setInterval(jwall.mvc.event.Updater['input#updater|click'], 10000);

				// Initialize history plugin.
				$.historyInit(ajsl.dispatcher.dispatch);
			});
		}
};



jwall.init();
