visuwall.mvc.controller.edit = {
	run : function(wallId) {
		LOG.info("Edit wall with id : " + wallId);
		$("#modal").load("wall/create", function(data) {
			$(this).dialog({
				height: 140,
				modal: true,
				close: function(event, ui) {
					$.historyLoad('');
				}
			});
		});
	}
};