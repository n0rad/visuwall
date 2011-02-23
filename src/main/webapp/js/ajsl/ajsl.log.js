ajsl.log = {
	_levels : {
		'trace' : 5,
		'debug' : 4,
		'info' : 3,
		'log' : 3,
		'warn' : 2,
		'error' : 1,
		'none' : 0
	},

	_fbug : false,

	_lvl : 5,

	_div : null,

	init : function() {
		// register in window
	window.LOG = this;

	// look for firebug
	if (window.console && window.console.firebug) {
		this._fbug = true;
	}

	// register in jquery
	// TODO: change this
	var $this = this;
	jQuery.fn.LOG = function(msg) {
		$this.log("%s: %o", msg, this);
		return this;
	};

	// register ajax events
	$().ajaxError(function (e, xhr, settings, exception) {
		LOG.info("load error", e);
	});

	// register to window onerror
	window.onerror = function(errorMsg, url, lineNumber) {
		LOG.error("Uncaught exception " + errorMsg + " at " + url + " :" + lineNumber);
		return true;
	};

},

// this._out = {'console', 'div'};

	// ///////////////////////

	setLevel : function(level) {
		if (this._levels[level] == undefined) {
			LOG.error("can not set ajsl logger to level ", level);
		}
		this._lvl = this._levels[level];
	},

	// ////////////

	trace : function() {
		if (this._lvl >= 5) {
			this._logger('trace', arguments);
		}
	},

	debug : function() {
		if (this._lvl >= 4) {
			this._logger('debug', arguments);
		}
	},

	info : function() {
		if (this._lvl >= 3) {
			this._logger('info', arguments);
		}
	},

	log : function() {
		if (this._lvl >= 3) {
			this._logger('log', arguments);
		}
	},

	warn : function() {
		if (this._lvl >= 2) {
			this._logger('warn', arguments);
		}
	},

	error : function() {
		if (this._lvl >= 1) {
			this._logger('error', arguments);
		}
	},

	// ////////////

	assert : function(expr) {
		if (this._lvl >= 1) {
			try {
				throw "assert fail";
			} catch (e) {
				LOG.exception(e);
			}
		}
		// console.assert("str".length == 23);
},

exception : function(e, back) {
	if (this._lvl >= 1) {
		if (this._fbug) {
			var tmp = [ e ];
			eval('console.exception(tmp[0])');
		}

		// div error
}
},

_logger : function(lvl, args) {
// firebug
	if (this._fbug) {
		var fbugLvl = lvl;
		if (lvl == 'trace') {
			fbugLvl = 'debug';
		}
		this._fbugConsole(fbugLvl, args);
	}
	// div error
	this._divConsole(lvl, args);
},

_fbugConsole : function(lvl, args) {
	var res = "";
	for ( var i = 0; args[i] != undefined; i++) {
		if (i != 0) {
			res += ",";
		}
		res += "args[" + i + "]";
	}
	eval('console[lvl](' + res + ')');
},

_divConsole : function(lvl, args) {
	if (this._div == null) {
		this._createDiv();
	}
	
	var argsArray = Array.prototype.slice.call(args);
	
	var logDiv = $("<div>" + lvl + " : " + argsArray.join(" ") + "</div>");
	
	this._div.append(logDiv);
},

_createDiv : function() {
	this._div = $('<div id="console" class="ui-widget-content"><h3 class="ui-widget-header">Console</h3></div>');
	this._div.css( {position: "absolute", bottom : 0, right : 0, border : "1px solid red", backgroundColor : "white", 'z-index' : 100000, height: '100px', overflow : 'auto'});
	this._div.resizable({animate: true});
	$('body').append(this._div);
}

};

ajsl.log.init();
