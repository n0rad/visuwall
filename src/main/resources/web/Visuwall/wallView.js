define(['jquery', 'Visuwall/Theme/VisuSpace/Wall/WallView'], function($, WallView) {
	var table = $('ul#projectsTable')[0];
	return new WallView(table);
});
