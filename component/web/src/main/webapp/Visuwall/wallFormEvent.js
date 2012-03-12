define(['jquery', 'Visuwall/Theme/Global/WallForm2/WallFormEvent', 'Visuwall/wallFormView'],
function($, WallFormEvent, wallFormView) {
	var formDiv = $('#visuwallForm');
	return new WallFormEvent(formDiv, wallFormView);
});