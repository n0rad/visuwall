visuwall.ctrl.controller.initController = new function() {
	var $this = this;
	
	this.__inject__ = ['wallFormView', 'navigationView', 'navigationEvent'];
	
	this.run = function(initData) {
		//TODO manage events in DI
		$this.navigationEvent.__getObject__(function(bean) {
			ajsl.event.register(bean, $('DIV#navigationContainer'));
		});
		$this.navigationView.replaceWallList(initData.wallNames);
		if (initData.wallNames.length == 0) {
			$.history.queryBuilder().addController('wall/create').load();
		} else {
			$('#wallSelector #wallSelect').val(initData.wallNames[0]).change();
//			$.history.queryBuilder().addController('wall/' + initData.wallNames[0]).load();			
		}
	};
};