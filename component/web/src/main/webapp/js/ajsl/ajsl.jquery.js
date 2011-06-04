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

function objectEquals(obj1, obj2) {
    for (var i in obj1) {
        if (obj1.hasOwnProperty(i)) {
            if (!obj2.hasOwnProperty(i)) return false;
            if (obj1[i] != obj2[i]) return false;
        }
    }
    for (var i in obj2) {
        if (obj2.hasOwnProperty(i)) {
            if (!obj1.hasOwnProperty(i)) return false;
            if (obj1[i] != obj2[i]) return false;
        }
    }
    return true;
}

String.prototype.ucfirst = function() {
	return this.charAt(0).toUpperCase() + this.slice(1);
};

function buildVisualDuration(duration) {
	var inst = {
		options : {
			compact : true,
			format : 'dhms'
		},
		_periods : [ 0, 0, 0, 0, 0, 0, 0 ],
		_show : [ '!', '!', '!', '!', '!', '!', '!' ]
	};

	inst._now = new Date();
	inst._until = new Date(inst._now.getTime() + duration);
	inst._show = jQuery.countdown._determineShow(inst);
	var res = jQuery.countdown
			._calculatePeriods(inst, inst._show, 0, inst._now);
	var html = jQuery.countdown._generateHTML(inst);
	var good = strip(html);
	if (inst._periods[0] == 0 && inst._periods[1] == 0 && inst._periods[2] == 0
			&& inst._periods[3] == 0 && inst._periods[4] == 0
			&& inst._periods[5] == 0) {
		good += 's';
	}
	return good;
}

function strip(html) {
	var tmp = document.createElement("DIV");
	tmp.innerHTML = html;
	return tmp.textContent || tmp.innerText;
}

function exists(obj, prop) {
	var parts = prop.split('.');
	for ( var i = 0, l = parts.length; i < l; i++) {
		var part = parts[i];
		if (obj !== null && typeof obj === "object" && part in obj) {
			obj = obj[part];
		} else {
			return false;
		}
	}
	return true;
}

Object.size = function(obj) {
	var size = 0, key;
	for (key in obj) {
		if (obj.hasOwnProperty(key))
			size++;
	}
	return size;
};

function ISODateString(d) {
	function pad(n) {
		return n < 10 ? '0' + n : n;
	}
	return d.getUTCFullYear() + '-' + pad(d.getUTCMonth() + 1) + '-'
			+ pad(d.getUTCDate()) + 'T' + pad(d.getUTCHours()) + ':'
			+ pad(d.getUTCMinutes()) + ':' + pad(d.getUTCSeconds()) + 'Z';
}

(function($) {

	jQuery.fn.slideFadeToggle = function(speed, easing, callback) {
		return this.animate({
			opacity : 'toggle',
			height : 'toggle'
		}, speed, easing, callback);
	};

	$.fn.switchClasses = function(classSwitch, newClass, duration) {
		var obj = this;
		var oldClass;
		for ( var i = 0; i < classSwitch.length; i++) {
			if (obj.hasClass(classSwitch[i])) {
				oldClass = classSwitch[i];
				break;
			}
		}
		if (oldClass != newClass) {
			obj.switchClass(oldClass, newClass, duration);
		}
	};

	$.fn.stopBlink = function(options) {
		this.removeClass("highlight");
	};

	$.fn.blink = function(options) {

		var defaults = {
			highlightClass : "highlight",
			blinkCount : 3, // -1 infinite
			fadeDownSpeed : "slow",
			fadeUpSpeed : "slow",
			fadeToOpacity : 0.33
		};
		var options = $.extend(defaults, options);

		return this
				.each(function() {
					var obj = $(this);
					if (obj.hasClass(options.highlightClass)) {
						return;
					}
					var blinkCount = 0;

					obj.addClass(options.highlightClass);
					doBlink();

					function doBlink() {
						if (obj.hasClass("highlight")
								&& (options.blinkCount == -1 || blinkCount < options.blinkCount)) {
							obj.fadeTo(options.fadeDownSpeed,
									options.fadeToOpacity, function() {
										obj.fadeTo(options.fadeUpSpeed, 1.0,
												doBlink);
									});
						} else {
							obj.removeClass(options.highlightClass);
						}
						blinkCount++;
					}
				});

	};
})(jQuery);
