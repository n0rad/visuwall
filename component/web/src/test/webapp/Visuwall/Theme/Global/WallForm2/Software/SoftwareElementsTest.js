define(['curl/tdd/runner', 'require'], function (runner, require) {
	runner(require).run(function(require) {

require(['Visuwall/Theme/Global/WallForm2/Software/SoftwareElements'], function(SoftwareElements) {
	describe('', function() {
		var context;
		
		beforeEach(function() {
			context = $("<div></div>");
			$("body").append(context);
		});
		
		afterEach(function() {
			context.remove();
		});
			
		describe('SoftwareElements', function() {
			
			it('should add project and views', function() {
				var softElem = new SoftwareElements(context);
				
				softElem.updateElements({'ajsl': 'genrestyle'}, ['myview']);
				
				expect($('.projectList', context).size()).toBe(1);
				expect($('.viewList', context).size()).toBe(1);
			});
			
			
			it('should call projectView to add project when click add', function() {
				var addProject = jasmine.createSpy('addProject');
				
				var softElem = new SoftwareElements(context, {addProject : addProject});
				softElem.updateElements({'ajsl': 'genrestyle'}, ['myview']);
				
				$('.projectList .addProject', context).trigger('click');
				
				expect(addProject).toHaveBeenCalledWith('ajsl');
			});

		});
		
	});
	
	
});

	}).then(loaded);
});
