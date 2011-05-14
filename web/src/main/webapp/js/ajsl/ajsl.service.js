ajsl.service = new function() {
	var $this = this;
	var $beanFactory = this;

	this.factory = {};

	this.access = {};

	this.interfaces = {};

	this.inject = function(obj) {
		if (!obj.__inject__) {
			return;
		}

		for ( var i = 0; i < obj.__inject__.length; i++) {
			var serviceName = obj.__inject__[i];
			if ($this.access[serviceName]) {
				obj[serviceName] = $this._getProxy(serviceName, $this.access[serviceName]);
			} else {
				LOG.error("No bean found for service name : ", serviceName);
			}
		}
	};

	this.get = function(beanName, callback) {
		// var clazz = $this.access[beanName][0];
		// var lastIndex = clazz.lastIndexOf('.');
		// var packageName = clazz.substring(0, lastIndex);
		// var url = "res/js/" + packageName.replace(/\./g, "/") + '/' + clazz +
		// '.js';
		// var deferred = $.ajax({url: url, dataType: 'script'});
		// deferred.then(function() {
		eval("$this.inject(" + $this.access[beanName][0] + ");");
		eval("callback(" + $this.access[beanName][0] + ");");
		// });
	};

	this.injectAll = function(objs) {
		for ( var objName in objs) {
			$this.inject(objs[objName]);
		}
	};

	this.register = function(name, clazz) {
		if (!$this.access[name]) {
			$this.access[name] = {};
		}
		$this.access[name] = clazz;
	};

	this.registerAll = function(accesses) {
		$.extend($this.access, accesses);
	};

	this.defineInterfacesAll = function(interfaces) {
		$.extend($this.interfaces, interfaces);
	};

	// ///////////////////////////////////////////////////////////////

//	this._addProperty = function(obj, label, getter, setter) {
//		if (Object.defineProperty) {
//			Object.defineProperty(obj, label, {
//				get : getter,
//				set : setter
//			});
//		} else {
//			if (getter) {
//				obj.__defineGetter__(label, getter);
//			}
//			if (setter) {
//				obj.__defineSetter__(label, setter);
//			}
//		}
//	};

	this._getProxy = function(beanName, url) {
		// var proxy = $.extend({__beanName__ : beanName, deferred : deferred},
		// $this._proxy);
//		var proxy = $("<div></div>")[0];
//		$.extend(proxy, $this._proxy);
		var proxy = new $this._proxy(beanName);

		var interfac = $this.interfaces[beanName];
		for (var i = 0; i < interfac.length; i++) {
			eval("var func = function() {proxy.__noSuchMethod__('" + interfac[i] + "', arguments);}");
			proxy[interfac[i]] = func;
			//proxy.__addProxyProperty__(interfac[i]);
		}
		return proxy;
	};

	
	this._proxy = function(beanName) {
		var $this = this;

		this.__beanName__ = beanName;

		this.__noSuchMethod__ = function(id, args) {
			$beanFactory.get(beanName, function(bean) {
				bean[id].apply(this || window, args);
			});

//			 this.deferred.then(function() {
//				 service[methodName].apply(this||window, args);
//			 });
		};
		
		this.__getObject__ = function(callback) {
			$beanFactory.get(beanName, callback);
		};
		
		
//		this.__addProxyProperty__ = function(name) {
//			$beanFactory._addProperty($this, name, function() {
//				 return function() {
//					 var args = arguments;
//					 $this.__noSuchMethod__(name, args);
//				 };
//			}, function() {
//				 alert('you are trying to replace a service');
//			});
			
			
		// var methodName = 'getData';
		// addPropertyyy(proxy, methodName, function() {
		// return function() {
		// var args = arguments;
		// this.deferred.then(function() {
		// service[methodName].apply(this||window, args);
		// });
		// };
		// }, function() {
		// alert('you are trying to replace a service');
		// });
		
			
//		};

	};

};