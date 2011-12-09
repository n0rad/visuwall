/*
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

define(['jquery', //
        'Ajsl/Dispatcher', //
        'Visuwall/Theme/Default/View/navigationView', //
        'js!ajsl-utils.js', //
        ], function($, Dispatcher, navigationView) {
	"use strict";
	
	return function(jsData) {		
		$.timeago.settings.strings = {
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

		$("abbr.timeago").timeago();
		
		var dispatcher = new Dispatcher({modules : 'Visuwall/Command'});

		$.history.init(dispatcher.dispatch, {unescape : true});
		
		navigationView.replaceWallList(jsData.init.wallNames);

		if (jsData.init.wallNames.length == 0) {
			$.history.queryBuilder().addController('wall/create').load();
		} else {
			var wallNames = jsData.init.wallNames;
			var queryBuilder = $.history.queryBuilder();
			var flag = false;
			for (var i = 0; i < wallNames.length; i++) {
				if (queryBuilder.contains('wall', wallNames[i])) {
					$('#wallSelector #wallSelect').val(jsData.init.wallNames[i]).change();
					flag = true;
					break;
					//	$.history.queryBuilder().addController('wall',  jsData.init.wallNames[0]).load();					
				}
			}
			if (!flag) {
				$.history.queryBuilder().addController('wall',  jsData.init.wallNames[0]).load();				
			}
		}

	};
});
