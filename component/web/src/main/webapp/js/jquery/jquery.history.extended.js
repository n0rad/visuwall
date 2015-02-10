
(function($) {
	
	$.extend($.history, {
		parseRequest : function(request) {			
			var result = [];
			if (request) {
				var controllers = request.split(';');
		       	for (var i = 0; i < controllers.length; i++) {
		       		var ctrl = this._queryBuilderObj._parseController(controllers[i]);
		       		result.push(ctrl);
		       	}
			}
	       	return result;
		},
		
	    _queryBuilderObj : {
	    	
	    	_data : null,
	    	
			_parseController : function(url) {
				var result = {};
				var varsSepar = url.split('?');
				if (varsSepar.length > 2) {
					throw 'malform url with more than one "?" in controller' + controllers[0];
				}
				result.ctrlVars = varsSepar[0].split('/');
				result.ctrl = result.ctrlVars.shift();

				// vars
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
				for (var i = 0; i < this._data.length; i++) {
					if (res) {
						res += ';';
					}
					res += this._data[i].ctrl;
					var ctrlVars = this._data[i].ctrlVars.join('/');
					if (ctrlVars) {
						res += '/' + ctrlVars;
					}
					
					var vars = this._data[i].vars;
					if (_.size(vars)) {
						res += '?';
					}
					var flag = 0;
					for (var varName in vars) {
						if (flag) {
							res += '&';
						}
 						res += varName + '=' + vars[varName];
						flag++;
					}
				}
				return res;
			},

			addController : function(ctrlName, param) {
				var ctrlsNames = ctrlName.split('/');
	    		var ctrl = this._parseController(param ? ctrlName + '/' + param : ctrlName);
	    		var replaced = false;
	    		for (var i = 0; i < this._data.length; i++) {
	    			if (ctrlsNames.length > 1) {
	    				if (this._data[i].ctrl == ctrlsNames[0] && this._data[i].ctrlVars[0] == ctrlsNames[1]) {
		    				this._data.splice(i, 1, ctrl);	    					
		    				replaced = true;
		    				break;
	    				}
	    			} else if (this._data[i].ctrl == ctrlsNames[0]) {
	    				this._data.splice(i, 1, ctrl);
	    				replaced = true;
	    				break;
	    			}
	    		}
	    		
	    		if (!replaced) {
	    			this._data.push(ctrl);
	    		}
	    		return this;
	    	},
	    	
	    	contains : function(ctrlName, param) {
	    		var ctrl = this._parseController(param ? ctrlName + '/' + param : ctrlName);
	    		for (var i = 0; i < this._data.length; i++) {
	    			if (_.isEqual(ctrl, this._data[i])) {
	    				return true;
	    			}
	    		}
	    		return false;
	    	},

	    	removeController : function(ctrlName) {
	    		var ctrlNames = ctrlName.split('/');
	    		for (var i = 0; i < this._data.length; i++) {
	    			var current = this._data[i];
	    			if (ctrlNames.length >= 2) {
	    				if (current.ctrl == ctrlNames[0]
	    						&& current.ctrlVars[0] == ctrlNames[1]) {
	    					this._data.splice(i, 1);
		    				break;
	    				}
	    			} else if (current.ctrl == ctrlNames[0]) {	    				
    					this._data.splice(i, 1);
	    				break;
	    			}
	    		}
	    		return this;
	    	},

			load : function() {
				$.history.load(this._buildRequest(this._data));
	    		return this;
			}
	    	
	    },

		queryBuilder : function() {
	    	var builder = $.extend({}, this._queryBuilderObj);
	    	builder._data = this.parseRequest(this.location.get());
	    	return builder;
	    }
	    		
	});

})(jQuery);
