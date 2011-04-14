visuwall.business.service.Wall ={

		wall : function(callback) {
			$.getJSON('wall', {}, callback);			
		},
		
		get : function(wallName, callback) {
			$.getJSON('wall/' + wallName, {}, callback);
		},
		
		status : function(wallName, callback) {
			$.getJSON('wall/' + wallName + '/status', {}, callback);
		}
};