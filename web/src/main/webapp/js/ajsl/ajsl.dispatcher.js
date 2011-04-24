/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

ajsl.dispatcher = {

	_ctrls : {},

	_mainCtrl : null,

//	register : function(ctrl, name) {
//		if (this._ctrls[name] != undefined) {
//			LOG.warn("controller with name ", name, "is already registered");
//		}
//		this._ctrls[name] = ctlr;
//	},

	registerMain : function(ctrl) {
		this._mainCtrl = ctrl;
	},

	registerAll : function(obj) {
		// TODO check double ?
		// if (this._controllers[name] != undefined) {
		// LOG.warn("controller with name ", name, "is already registered");
		// }
		var ctrls = {};
		for (var ctrlName in obj) {
			for (var methodName in obj[ctrlName]) {
				if (typeof (obj[ctrlName][methodName]) != 'function'
					|| methodName[0] == '_' || methodName == 'init'
					|| obj[ctrlName][methodName] == this._mainCtrl) {
					continue;
				}
				
				var ctrlRun;
				if (methodName == 'run') {
					ctrlRun = ctrlName;
				} else {
					ctrlRun = ctrlName + '/' + methodName;
				}
				this._ctrls[ctrlRun] = obj[ctrlName][methodName];
			}
		}
	},

// function init() {
	// $(window).history(function(e, ui) {
	// var jsonUrl = "search";
	// var ajax_load = "<img src='img/load.gif' alt='loading...' />";
	// document.asearch.search(ui.value);
	// // $.get(jsonUrl, ui.value, function(responseText) {
	// // $("#result").html(responseText);
	// // }, "html");
	// });
	// };

	dispatch : function(url) {
		LOG.debug("dispatch for url '", url, "'");

		if (!url) {
			ajsl.dispatcher._mainCtrl();
			return;
		}

		var parsedCtrls = $.history.parseRequest(url);
		
		for (var ctrl in parsedCtrls) {
			if (ajsl.dispatcher._ctrls[ctrl] == undefined) {
				LOG.error("no controller for '", ctrl, "'");
				continue;
			}
			var action = ajsl.dispatcher._ctrls[ctrl];
			var vars = parsedCtrls[ctrl].vars;
			var args = parsedCtrls[ctrl].ctrlVars.join("','");
			eval("action(vars, '" + args + "');");
		}
		
		
		
		
		
		return;
		
		var splits = url.split('/');
		// find controller
		if (ajsl.dispatcher._ctrls[splits[0]] == undefined) {
			LOG.error("no controller for '", splits[0], "'");
			return;
		}
		var ctrl = ajsl.dispatcher._ctrls[splits[0]];

		var action = null;
		if (ctrl.run != undefined && typeof ctrl.run == 'function') {
			// controller is simple
			action = ctrl.run;
		} else {
			// multicontroller

			// TODO check that splits[1] is a function
			if (ajsl.dispatcher._ctrls[splits[0]][splits[1]] == undefined) {
				LOG.error("no action named '", splits[1], "' in controller '",
						splits[0], "'");
				return;
			}

			action = ctrl[splits[0]];

			// remove controller to remove action on next shift
			splits.shift();
		}

		// remove an element (controller or action)
		splits.shift();

		var args = splits.join("','");
		eval("action('" + args + "');");
	}
	
};