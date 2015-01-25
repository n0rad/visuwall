

// ///////////////////////////////


ajsl.utils = {

	/**
	 * @param preSubmit
	 *            function called before post
	 */
		formAjaxPostRegister : function(form, callback, preSubmit) {
		var form = $(form);
		LOG.debug('register callback for form :', form);
		form.submit(function() {
			if (preSubmit != undefined) {
				preSubmit();
			}
			$.post(form[0].action, form.serialize(), callback);
			return false;
		});
	}
};

