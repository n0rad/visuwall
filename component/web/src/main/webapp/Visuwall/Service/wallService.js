define(['jquery', 'visuwallRootUrl'], function($, rootUrl) {


	var wallService = {

		wall : function(callback) {
			$.getJSON(rootUrl + 'wall', {}, function(data) {
				callback(data.data);
			});
		},

		getCreate : function(callback) {
			$.get(rootUrl + 'wall/create', {}, callback);
		},
		
		create : function(data, successCallback, failureCallback) {
			$.ajax({
				url : rootUrl + 'wall/',
				type : 'POST',
				data : $(data).serialize(),
				error : function(jqXHR, textStatus, errorThrown) {
					failureCallback(textStatus + " " + jqXHR.statusText);
				},
				success : function(data, textStatus, jqXHR) {
					successCallback();
				}
			});	
		},

		get : function(wallName, callback) {
			$.getJSON(rootUrl + 'wall/' + wallName + '/', {}, function(data) {
				callback(data.data);
			});
		},

		deleteWall : function(wallName) {
			$.ajax({
				url : rootUrl + 'wall/' + wallName + '/',
				type : 'DELETE'
			});
		},

		status : function(wallName, callback) {
			$.getJSON(rootUrl + 'wall/' + wallName + '/status', {}, callback);
		}
	};
	
	return wallService;
});