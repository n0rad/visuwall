describe("ajslTest", function() {
//	it("should_register_main_page", function() {
//		
//		var main = "genre";
//		ajsl.dispatcher.registerMain(main);
//		
//		expect(ajsl.dispatcher._mainCtrl == main).toBe(true);
//	});
	
	it("test !!", function() {
		var pluginService = new Visuwall.Business.Service.PluginService();
		var mock = jasmine.createSpy().andCallFake(function(a, b, c) {
			c({});
		});
		var jQueryMock = {ajax: jasmine.createSpy(), getJSON : mock};
		pluginService.$ = jQueryMock;
		var mockCallback = jasmine.createSpy();
		
		pluginService.wall(mockCallback);
		
		 expect(jQueryMock.getJSON).toHaveBeenCalledWith('wall', {},  jasmine.any(Function));		
		 expect(mockCallback).toHaveBeenCalled();
	});
	
//	it("should be able to mock DOM call", function () {
//	    spyOn($.fn, "val").andReturn("bar");
//	    var result = $("#Something").val();
//	    expect(result).toEqual("bar");
//	});
});

