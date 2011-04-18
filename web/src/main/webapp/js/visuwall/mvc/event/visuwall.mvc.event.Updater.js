visuwall.mvc.event.Updater = {
		
	
	
	"input#updater|click" : function() {
		LOG.info("run updater");
		visuwall.mvc.controller.ProjectController.updateStatus();
	}
		
};
