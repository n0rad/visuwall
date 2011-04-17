visuwall.mvc.controller.edit = {
	run : function(wallId) {
		LOG.info("Edit wall with id : " + wallId);
		$("#modal").load("wall/create", function(data) {
			$(this).dialog({
				height: 400,
				width: 600,
				title: 'Wall Configuration',
				resizable: false,
				modal: true,
				close: function(event, ui) {
					$.historyLoad('');
				}
			});
		});
	}
};