var showWall = jasmine.createSpy('wallController.showWall');

define('Visuwall/Controller/wallFormController', function() {
	alert("wallform1");
});

define('Visuwall/Controller/wallController', function() {	
	define('Visuwall/Controller/wallFormController', function() {
		alert("wall1");
	});
	return {showWall : showWall};
});

define(['Visuwall/Command/wallCommand'], function(wallCommand) {
	describe('wallCommandTest', function() {
		it('should delegate to controller to show wall', function() {			
			
			wallCommand.run(null, 'mywallname');
			
			expect(showWall).toHaveBeenCalledWith('mywallname');
		});
	});
});