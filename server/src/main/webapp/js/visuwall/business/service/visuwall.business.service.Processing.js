visuwall.business.service.Processing = {
	
		
	finishTime : function(wallName, projectName, callback) {
		$.getJSON('processing/finishTime', {wallName : wallName, projectName : projectName}, callback);
	}
		
};