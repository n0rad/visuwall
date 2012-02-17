define(['jquery', 'visuwallRootUrl'], function($, rootUrl) {
	"use strict";

	var pluginService = new function() {
		
		this.manageable = function(url, login, password, success, failure) {
			$.ajax({
				url : rootUrl + 'plugin/getSoftwareInfo',
				dataType : 'json',
				data : {
					url : url,
					login : login,
					password : password
				},
				success : function(softwareInfo) {
					success(softwareInfo);
				},
				error : function() {
					failure();
				},
				// TODO fucking change that
				statusCode : {
					500 : function() {
						failure();
					}
				}
			});
		};
	};

	return pluginService;
});


	

