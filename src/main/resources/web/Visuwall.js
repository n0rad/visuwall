define(['jquery', //
        'Ajsl/Dispatcher', //
        'Visuwall/Theme/Global/Navigation/navigationView', //
        'Visuwall/Service/wallService', //
        'js!ajsl-utils.js', //
        
        'css!visuwall.css' //
        ], function($, Dispatcher, navigationView, wallService) {
	"use strict";

	return function() {		
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

		wallService.wall(function(wallNames){
			navigationView.replaceWallList(wallNames);

			if (wallNames.length === 0) {
				$.history.queryBuilder().addController('wall/create').load();
			} else {
				var queryBuilder = $.history.queryBuilder();
				var flag = false;
				for (var i = 0; i < wallNames.length; i++) {
					if (queryBuilder.contains('wall', wallNames[i])) {
						$('#wallSelector #wallSelect').val(wallNames[i]).change();
						flag = true;
						break;
					}
				}
				if (!flag) {
					$.history.queryBuilder().addController('wall',  wallNames[0]).load();				
				}
			}
		});

	};
});
