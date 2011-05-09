ajsl.service = new function() {
	var $this = this;

	this.factory = {};
	
	this.access = {};

	this.inject = function(obj) {
		if (!obj.__serviceNames__) {
			return;
		}
		
		for (var i = 0; i < obj.__serviceNames__.length; i++) {
			var serviceName = obj.__serviceNames__[i];
			if ($this.access[serviceName]) {
				obj[serviceName] = $this._getProxy(serviceName, $this.access[serviceName]);
			} else {
				LOG.error("No bean found for service name : ", serviceName);
			}
		}
	};

	this.injectAll = function(objs) {
		for (var objName in objs) {
			$this.inject(objs[objName]);
		}
	};

	this.register = function(name, clazz) {
		if (!$this.access[name]) {
			$this.access[name] = {};
		}
		$this.access[name] = clazz; 
	};
	
	this.registerAll = function(accessesArrays) {
		$.extend($this.access, accessesArrays);
	};
	
	/////////////////////////////////////////////////////////////////
	
	this._getProxy = function(beanName, url) {
		var deferred = $.ajax({url: "res/" + url, dataType: 'script'});
		var proxy = $.extend({__beanName__ : beanName, deferred : deferred},  $this._proxy);
//		var methodName = 'getData';
//		addPropertyyy(proxy, methodName, function() {
//			return function() {
//				var args = arguments;
//				this.deferred.then(function() {
//					service[methodName].apply(this||window, args);
//				});
//			};
//		}, function() {
//			alert('you are trying to replace a service');
//		});
		return proxy;
	};

	this._proxy = {
		__noSuchMethod__ : function(id, args) {
			// calling a method but its still the proxy
			this.deferred.then(function() {
				// call method
				service[id].apply(this||window, args);
			});
		}
	};

};