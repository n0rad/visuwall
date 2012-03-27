curl.undefine(true);
var showWall = jasmine.createSpy('wallController.showWall');

define('Visuwall/Controller/wallFormController', function() {
});

define('Visuwall/Controller/wallController', function() {	
	return {showWall : function() {
		throw "yop";
	}};
});

define(['Visuwall/Command/wallCommand'], function(wallCommand) {
	describe('wallCommandTest42', function() {
		it('should delegate to controller to show wallss', function() {			
			
			wallCommand.run(null, 'mywallname');
			
			expect(showWall).toHaveBeenCalledWith('mywallname');
		});
	});
});