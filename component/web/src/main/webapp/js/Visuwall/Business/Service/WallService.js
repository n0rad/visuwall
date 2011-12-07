define(['jquery'], function($) {

	var WallService = function() {

	};

	WallService.prototype = {

		wall : function(callback) {
			$.getJSON('wall', {}, function(data) {
				callback(data.data);
			});
		},

		getCreate : function(callback) {
			$.get('wall/create', {}, callback);
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
	
	return WallService;
});