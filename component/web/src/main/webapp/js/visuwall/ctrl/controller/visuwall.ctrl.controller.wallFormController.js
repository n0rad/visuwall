visuwall.ctrl.controller.wallFormController = new function() {
	var $this = this;
	
	this.submitWallData = function(data, successCallback, failureCallback) {
		$.ajax({
			url : 'wall/',
			type : 'POST',
			data : $(data).serialize(),
			error : function(jqXHR, textStatus, errorThrown) {
				failureCallback(textStatus + " - " + errorThrown);
			},
			success : function(data, textStatus, jqXHR) {
				successCallback();
			}
		});
	};
};