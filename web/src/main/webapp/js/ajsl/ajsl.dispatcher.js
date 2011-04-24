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

	_ctlrs : {},

	_mainCtlr : null,

	register : function(ctlr, name) {
		if (this._ctlrs[name] != undefined) {
			LOG.warn("controller with name ", name, "is already registered");
		}
		this._ctlrs[name] = ctlr;
	},

	registerMain : function(ctlr) {
		if (typeof (ctlr.run) == undefined) {
			LOG.error('main controller is not a simple controller');
			return;
		}
		this._mainCtlr = ctlr;
	},

	registerAll : function(obj) {
		// TODO check double ?
		// if (this._controllers[name] != undefined) {
		// LOG.warn("controller with name ", name, "is already registered");
		// }
		$.extend(this._ctlrs, obj);
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

		// check if main
		if (url.trim() == '') {
			ajsl.dispatcher._mainCtlr.run();
			return;
		}

		var splits = url.split('/');
		// find controller
		if (ajsl.dispatcher._ctlrs[splits[0]] == undefined) {
			LOG.error("no controller for '", splits[0], "'");
			return;
		}
		var ctlr = ajsl.dispatcher._ctlrs[splits[0]];

		var action = null;
		if (ctlr.run != undefined && typeof ctlr.run == 'function') {
			// controller is simple
			action = ctlr.run;
		} else {
			// multicontroller

			// TODO check that splits[1] is a function
			if (ajsl.dispatcher._ctlrs[splits[0]][splits[1]] == undefined) {
				LOG.error("no action named '", splits[1], "' in controller '",
						splits[0], "'");
				return;
			}

			action = ctlr[splits[0]];

			// remove controller to remove action on next shift
			splits.shift();
		}

		// remove an element (controller or action)
		splits.shift();

		var args = splits.join("','");
		eval("action('" + args + "');");
	}
};