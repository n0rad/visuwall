curl.undefine(true);
var showWall = jasmine.createSpy('wallController.showWall');

define('Visuwall/Controller/wallFormController', function() {
});

define('Visuwall/Controller/wallController', function() {	
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