jwall.mvc.event.Updater = {
	
	"input#updater|click" : function() {
		LOG.info("run updater");
		jwall.business.service.Project.status(jwall.mvc.view.ProjectTable.updateStatus);
	}
		
};
