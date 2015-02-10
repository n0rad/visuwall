define([ 'require', 'log', 'jquery', 'underscore' ], function(require, log, $, _) {
	"use strict";

	var defaultOptions = {
			suffix: "Command",
			mainModuleMain: null,
			modules: "Command/",
	};
	
	var lastDispatch = [];
	var options;

	var Dispatcher = function(userOptions) {
		options = $.extend({}, defaultOptions, userOptions);
		if (options.modules.charAt(options.modules.length - 1) != '/') {
			options.modules += '/';
		}
	};
	
	var isAlreadyDispatch = function(ctrl) {
		for (var i = 0; i < lastDispatch.length; i++) {
			if (_.isEqual(ctrl, lastDispatch[i])) {
				return true;
			}
		}
		return false;
	};

	Dispatcher.prototype = {

		dispatch : function(url) {
			log.debug("dispatch for url '", url, "'");
			if (!url) {
				if (options.mainModuleMain) {
					var moduleName = options.modules + options.mainModuleMain + options.suffix;
					curl([ moduleName ], function(
							MainController) {
						new MainController().run();
						lastDispatch = {};
					});
				}
				lastDispatch = [];
				return;
			}

			var parsedCtrls = $.history.parseRequest(url);

			for(var i = 0; i < parsedCtrls.length; i++) {

					if (isAlreadyDispatch(parsedCtrls[i])) {
						log.debug("skip history '", parsedCtrls[i], "' as already dispatched in last url");
						continue;
					}
					
					
					var ctrlModuleName = options.modules + parsedCtrls[i].ctrl + options.suffix;
					
					var tmp = function(index) { // hack? for i closure
					require([ ctrlModuleName ], function(controller) {
								var ctrlVars = parsedCtrls[index].ctrlVars.slice();
								var vars = parsedCtrls[index].vars;
								
								// TODO use a class
								var method = 'run';
								if (ctrlVars[0] && controller[ctrlVars[0]]) {
									method = ctrlVars[0];
									ctrlVars.shift();
								}
								ctrlVars.unshift(vars);
								controller[method].apply(this || window, ctrlVars);
							});
					};
					
					tmp(i);
			}
			lastDispatch = parsedCtrls;
		}
	};

	return Dispatcher;
});