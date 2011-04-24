(function($) {
	
	$.extend($.history, {
		parseRequest : function(request) {
			var result = {};
			if (request) {
				var controllers = request.split('|');
		       	for (var i = 0; i < controllers.length; i++) {
		       		var ctrl = this._queryBuilderObj._parseController(controllers[i]);
		       		result[ctrl.ctrl] = ctrl;
		       	}
			}
	       	return result;
		},
		
	    _queryBuilderObj : {
	    	
	    	_data : null,
	    	_ctrl : null,
	    	
			_parseController : function(url) {
				var result = {};
				var varsSepar = url.split('?');
				if (varsSepar.length > 2) {
					throw 'malform url with more than one "?" in controller' + controllers[0];
				}
				result.ctrlVars = varsSepar[0].split('/');
				result.ctrl = result.ctrlVars.shift();
				if (varsSepar.length > 1) {
					result.vars = this._parseQueryVars(varsSepar[1]);
				} else {
					result.vars = {};
				}
				return result; 
			},

		    _parseQueryVars : function(varStr) {
		       	var res = {};
		       	var pairs = varStr.split('&');
		       	for (var i = 0; i < pairs.length; i++) {
		       	    var keyval = pairs[i].split('=');
		       	    res[keyval[0]] = keyval[1];
		       	}
		       	return res;
		    },

			_buildRequest : function() {
				var res = '';
				for (var ctrl in this._data) {
					if (res) {
						res += '|';
					}
					res += ctrl;
					var ctrlVars = this._data[ctrl].ctrlVars.join('/');
					if (ctrlVars) {
						res += '/' + ctrlVars;
					}
					
					var vars = this._data[ctrl].vars;
					if (Object.size(vars)) {
						res += '?';
					}
					var i = 0;
					for (var varName in vars) {
						if (i) {
							res += '&';
						}
 						res += varName + '=' + vars[varName];
						i++;
					}
				}
				return res;
			},

			addController : function(url) {
	    		var ctrl = this._parseController(url);
	    		this._data[ctrl.ctrl] = ctrl;
	    		return this;
	    	},

	    	removeController : function(ctrlName) {
	    		delete this._data[ctrlName];
	    		return this;
	    	},

			load : function() {
				$.history.load(this._buildRequest(this._data));
	    		return this;
			}
	    	
	    },

		queryBuilder : function() {
	    	var builder = $.extend({}, this._queryBuilderObj);
	    	builder._ctrl = this._options.ctrls;
	    	builder._data = this.parseRequest(this.location.get());
	    	return builder;
	    }
	    		
	});

})(jQuery);
