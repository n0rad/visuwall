/*
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

ajsl.dispatcher = new function() {
	var $this = this;
	
	this._ctrls = {};

	this._mainCtrl;

	this._lastDispatch;
	
//	register : function(ctrl, name) {
//		if (this._ctrls[name] != undefined) {
//			LOG.warn("controller with name ", name, "is already registered");
//		}
//		this._ctrls[name] = ctlr;
//	},
	
	this.registerMain = function(ctrl) {
		$this._mainCtrl = ctrl;
	};

	this.registerAll = function(obj) {
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
				$this._ctrls[ctrlRun] = obj[ctrlName][methodName];
			}
		}
	};

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

	this.dispatch = function(url) {
		LOG.debug("dispatch for url '", url, "'");

		if (!url) {
			$this._mainCtrl();
			$this._lastDispatch = {};
			return;
		}

		var parsedCtrls = $.history.parseRequest(url);
		
		for (var ctrl in parsedCtrls) {
			if ($this._ctrls[ctrl] == undefined) {
				LOG.error("no history for '", ctrl, "'");
				continue;
			}
			
			if ($this._lastDispatch && $this._lastDispatch[ctrl]
				&& _.isEqual($this._lastDispatch[ctrl], parsedCtrls[ctrl])) {
				LOG.debug("skip history '", ctrl, "'as already dispatched in last url");
				continue;
			}
			
			var action = $this._ctrls[ctrl];
			var ctrlVars = parsedCtrls[ctrl].ctrlVars;
			var vars = parsedCtrls[ctrl].vars;
			ctrlVars.unshift(vars);
			$this._ctrls[ctrl].apply(this || window, ctrlVars);
		}
		
		$this._lastDispatch = parsedCtrls;
	};
	
};