jwall.mvc.event.updater = {
		
	"input#updater|click" : function() {
		LOG.info("run updater");
		jwall.business.service.project.update();
	}
		
};