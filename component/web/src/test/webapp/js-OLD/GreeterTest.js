GreeterTest = TestCase("GreeterTest");

GreeterTest.prototype.testGreet = function() {
  var greeter = new myapp.Greeter();
  assertEquals("42", greeter.greet("World"));
  jstestdriver.console.log("JsTestDriver", greeter.greet("World"));
  console.log(greeter.greet("Browser", "World"));
};


