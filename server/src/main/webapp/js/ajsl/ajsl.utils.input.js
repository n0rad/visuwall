ajsl.utils.input = {



	focus : function() {
		if (this.defaultValue == this.value) {
			this.value = '';
			$(this).removeClass('default');
		}
	},

	blur : function() {
		if (this.value == '' || this.value == this.defaultValue) {
			this.value = this.defaultValue;
			$(this).addClass('default');
		}
	}
	
};