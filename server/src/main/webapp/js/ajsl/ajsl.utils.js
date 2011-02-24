// add contains to array
if (!Array.prototype.contains){
    Array.prototype.contains = function(obj){
    var len = this.length;
    for (var i = 0; i < len; i++){
      if(this[i]===obj){return true;}
    }
    return false;
  };
}


function asynCall(what, callback) {
	window.setTimeout(function() {
		res = what();
		callback(res);
	}, 0);
}

function asynCall(what) {
	window.setTimeout(function() {
		what();
	}, 0);
}

String.prototype.startsWith = function(str) {
	return (this.match("^" + str) == str);
};

// TODO remove after tests
function sleep(delay) {
	var start = new Date().getTime();
	while (new Date().getTime() < start + delay)
		;
}

function isObjectEmpty(o) {
	  for(var p in o) {
		  // skip proto elements
	    if (o[p] != o.constructor.prototype[p])
	      return false;
	  }
	  return true;
}

// ///////////////////////////////


ajsl.utils = {

	/**
	 * @param preSubmit
	 *            function called before post
	 */
		formAjaxPostRegister : function(form, callback, preSubmit) {
		var form = $(form);
		LOG.debug('register callback for form :', form);
		form.submit(function() {
			if (preSubmit != undefined) {
				preSubmit();
			}
			$.post(form[0].action, form.serialize(), callback);
			return false;
		});
	}
};

