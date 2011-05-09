visuwall.business.service.Plugin = {

	wall : function(callback) {
		$.getJSON('wall', {}, function(data) {
			callback(data.data);
		});
	}
};