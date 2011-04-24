visuwall.business.service.Wall ={

		wall : function(callback) {
			$.getJSON('wall', {}, function(data) {
				callback(data.data);
			});
		},
		
//		get : function(wallName, callback) {
//			$.getJSON('wall/' + wallName + '/', {}, callback);
//		},
		
		status : function(wallName, callback) {
			$.getJSON('wall/' + wallName + '/status', {}, callback);
		}
};