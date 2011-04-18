ajsl.event = {

	registerAll : function(eventObjs) {
		for (var evObj in eventObjs) {
			this.register(eventObjs[evObj]);
		}
	},

	register : function(eventObj) {
		for (var ev in eventObj) {
			
			var context = null;
			if (typeof eventObj.context == 'function') {
				context = eventObj.context();
			} else if (eventObj.context) {
				context = $(eventObj.context);
			}
			
			if (typeof eventObj[ev] == 'function') {
				
				var ppos = ev.indexOf('|');
				var selector = ev.substring(0, ppos);
				var event = ev.substring(ppos + 1).trim();
				if (!selector) {
					var elements = context;
				} else {
					var elements = $(selector, context);
				}
				
				if (elements.length == 0 && event != 'init') {
					LOG.error('nothing found to register event : ', ev);
					return;
				}
				
				if (event == 'init') {
					LOG.debug('init "', ev, '" event to', elements);
					eventObj[ev](elements);
				} else {
					LOG.debug('bind "', ev, '" event to', elements);					
					elements.bind(event, {eventObj : eventObj}, eventObj[ev]);
				}
				
			}
		}
	}
	
};