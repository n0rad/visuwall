define(['Visuwall/Theme/Default/View/wallFormView', //
        'Visuwall/Theme/Default/View/navigationView', //
        'Visuwall/Theme/Default/Event/navigationEvent'], function(wallFormview, navigationView, navigationEvent) {
	'use strict';
	
	var initController = new function() {
		var $this = this;
		
		
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
				$.history.queryBuilder().addController('wall/' + initData.wallNames[0]).load();			
			}
		};
	};
	
	return initController;
});