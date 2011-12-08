define(['jquery'], function($) {


	var wallService = {

		wall : function(callback) {
			$.getJSON('wall', {}, function(data) {
				callback(data.data);
			});
		},

		getCreate : function(callback) {
			$.get('wall/create', {}, callback);
		},
		
		create : function(data, successCallback, failureCallback) {
			$.ajax({
				url : 'wall/',
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
			$.getJSON('wall/' + wallName + '/', {}, function(data) {
				callback(data.data);
			});
		},

		deleteWall : function(wallName) {
			$.ajax({
				url : 'wall/' + wallName + '/',
				type : 'DELETE'
			});
		},

		status : function(wallName, callback) {
			$.getJSON('wall/' + wallName + '/status', {}, callback);
		}
	};
	
	return wallService;
});