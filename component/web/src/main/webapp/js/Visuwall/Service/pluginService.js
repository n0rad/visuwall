define(['jquery'], function($) {
	"use strict";

	var pluginService = new function() {
		
		this.wall = function(callback) {
			$.getJSON('wall', {}, function(data) {
				callback(data.data);
			});
		};

		this.manageable = function(url, success, failure) {
			$.ajax({
				url : 'plugin/getSoftwareInfo',
				dataType : 'json',
				data : {
					url : url
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


	

