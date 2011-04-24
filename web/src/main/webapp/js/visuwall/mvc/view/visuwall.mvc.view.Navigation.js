visuwall.mvc.view.Navigation = {

	navigation : null,
		
	init : function() {
		this.navigation = $('#navigation');
	},

	replaceWallList : function(newWallList) {
		var select = $('#wallSelect', this.navigation);
		var selectedValue = select.val();
		select.children().remove();
		for ( var i = 0; i < newWallList.length; i++) {
			var newOption = $('<option value="' + newWallList[i] + '">' + newWallList[i] + '</option>');
			select.append(newOption);
		}
		select.val(selectedValue);
	}
};