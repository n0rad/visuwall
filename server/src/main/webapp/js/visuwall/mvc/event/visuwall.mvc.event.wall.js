visuwall.mvc.event.wall = {

	init : function() {

		// Activate the main dynamic form
		var mainDynamicForm = $("#people").dynamicForm("#plus", "#minus", {
			limit : 5,
			formPrefix : "mainForm",
			afterClone : function(clone) {
				if (window.console && window.console.log) {
					console.log("I'm a clone", clone);
				}
			},
			createColor : "green"
		});

		// Activate two nested dynamic form
		$("#phoneTemplate").dynamicForm("#plus5", "#minus5", {
			limit : 5,
			formPrefix : "mainPhone"
		});
		$("#phone2Template").dynamicForm("#plus6", "#minus6", {
			limit : 5,
			createColor : "red"
		});
	}
};