describe("VisuwallTest", function() {
	
	it("test !!", function() {
		
		curl({baseUrl: 'res/js'}, ['Visuwall'], function(Visuwall) {
			
			alert("salut");

			 //expect(mockCallback).toHaveBeenCalled();
		});
		
	});
});


//var pluginService = new Visuwall.Business.Service.PluginService();
//var mock = jasmine.createSpy().andCallFake(function(a, b, c) {
//	c({});
//});
//var jQueryMock = {ajax: jasmine.createSpy(), getJSON : mock};
//pluginService.$ = jQueryMock;
//var mockCallback = jasmine.createSpy();
//
//pluginService.wall(mockCallback);
//
// expect(jQueryMock.getJSON).toHaveBeenCalledWith('wall', {},  jasmine.any(Function));		
