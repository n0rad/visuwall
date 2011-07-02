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

ajsl.event = {

//	registerAll : function(eventObjs) {
//		for (var evObj in eventObjs) {
//			this.register(eventObjs[evObj]);
//		}
//	},

	register : function(eventObj, context) {
		for (var ev in eventObj) {
			
//			var context = null;
//			if (typeof eventObj.context == 'function') {
//				context = eventObj.context();
//			} else if (eventObj.context) {
//				context = $(eventObj.context);
//			}
			
			if ($.isFunction(eventObj[ev])) {
				
				var eventSplit = ev.split('|');
				
				var selector = eventSplit[0].trim();
				var event = eventSplit[1];
				var run = eventSplit[2];
				
				
				var elements = context;
				
				if (selector == 'init' && !event && !run) {
					event = selector;
					selector = null;
				}

				
                if (!selector) {
                	var elements = context;
                } else {
                	var elements = $(selector, context);
                }
				if (elements.length == 0 && event != 'init') {
					LOG.error('nothing found to register event : ', ev);
					continue;
				}
				
				if (event == 'init') {
					elements.each(eventObj[ev]);
//					LOG.debug('init "', ev, '" event to', elements);
//					eventObj[ev](elements);
				} else {
					if (run) {
						LOG.debug('bind live "', ev, '" event to', elements);
						elements.live(event, {eventObj : eventObj}, eventObj[ev]);
					} else {
						LOG.debug('bind "', ev, '" event to', elements);
						elements.bind(event, {eventObj : eventObj}, eventObj[ev]);
					}
				}
				
			}
		}
	}
	
};