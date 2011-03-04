jwall.mvc.event.Updater = {
		
	
	
	"input#updater|click" : function() {
		LOG.info("run updater");
		jwall.mvc.controller.ProjectController.updateStatus();
	}
		
};
