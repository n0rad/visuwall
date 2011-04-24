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

