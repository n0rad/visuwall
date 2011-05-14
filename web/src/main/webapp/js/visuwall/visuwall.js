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
	business : {
		service : {}
	},
	ctrl : {
		controller : {},
		history : {},
		process : {}
		
	},
	persistence : {
		dao : {}
	},

	theme : {
		def : {
			event : {},
			view : {}
		}
	},

	/**
	 * init on ready
	 */
	init : function(jsData) {

		// DI
		ajsl.service.registerAll(jsData.jsService);
		ajsl.service.defineInterfacesAll(jsData.jsServiceMethod);
		
		jQuery.timeago.settings.strings = {
			suffixAgo : " ago",
			suffixFromNow : "from now",
			seconds : "<1min",
			minute : "1min",
			minutes : "%dmin",
			hour : "1h",
			hours : "%dh",
			day : "1d",
			days : "%dd",
			month : "1M",
			months : "%dM",
			year : "1y",
			years : "%dy"
		};

		jQuery("abbr.timeago").timeago();

		// DI
		ajsl.service.injectAll(visuwall.ctrl.history);
		
		// register controllers
		ajsl.dispatcher.registerMain(visuwall.ctrl.history.main.run);
		ajsl.dispatcher.registerAll(visuwall.ctrl.history);

		ajsl.log.setLevel('debug');

		// register events
		//ajsl.event.registerAll(visuwall.theme.def.event);



		// Initialize history plugin.
		$.history.init(ajsl.dispatcher.dispatch, {unescape : true, ctrls : ajsl.dispatcher._ctrls});

		// run init
		ajsl.service.get('initController', function(bean) {
			bean.run(jsData.init);
		});
		
	}
};
