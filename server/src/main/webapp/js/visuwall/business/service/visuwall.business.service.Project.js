visuwall.business.service.Project = {
		
	projects : function(wallName, callback) {
	    $.getJSON('wall/' + wallName + '/project', {}, callback);
	},
		
	project : function(wallName, projectName, callback) {
	    $.getJSON('wall/' + wallName + '/project/' + projectName, {}, callback);
	},
	
	getBuild : function(wallName, projectName, buildId, callback) {
		LOG.info('loading build ' + buildId + ' for project ' + projectName + ' in wall ' + wallName);
		$.getJSON('wall/' + wallName + '/project/' + projectName + '/build/' + buildId, {}, callback);		
	}
};