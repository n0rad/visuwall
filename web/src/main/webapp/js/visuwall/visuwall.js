/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

visuwall = {
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
				ajsl.dispatcher.registerAll(visuwall.mvc.controller);

				// register main controller
				ajsl.dispatcher.registerMain(visuwall.mvc.MainController);

				ajsl.log.setLevel('debug');
				
				// register events
				ajsl.event.registerAll(visuwall.mvc.event);
		
				visuwall.mvc.view.Loader.init();
				
				// init loader
				ajsl.loader.init(visuwall.mvc.view.Loader);
				
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
	
				//visuwall.mvc.controller.ProjectController.buildProjects();
				
				// create updater event
				var updater = setInterval(visuwall.mvc.event.Updater['input#updater|click'], 10000);

				// Initialize history plugin.
				$.historyInit(ajsl.dispatcher.dispatch);
			});
		}
};


$(function (){
	visuwall.init();
});