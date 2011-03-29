visuwall.mvc.event.navigation = {
	'UL#navigation INPUT#wallEdit|click' : function() {
		LOG.info("Edit project");
	//	$("#modal").load("security/getregister", function(data) {
		//	$(this)
					$("#modal").dialog({
				height: 140,
				modal: true
			});
//		});
		return false;
	}
};