define(['jquery', 'Visuwall/Theme/Global/WallForm2/WallFormView'],
function($, WallFormView) {
	var formDiv = $('#visuwallForm');
	return new WallFormView(formDiv);
});