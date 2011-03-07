function ISODateString(d) {
    function pad(n){
        return n<10 ? '0'+n : n;
    }
    return d.getUTCFullYear()+'-'
    + pad(d.getUTCMonth()+1)+'-'
    + pad(d.getUTCDate())+'T'
    + pad(d.getUTCHours())+':'
    + pad(d.getUTCMinutes())+':'
    + pad(d.getUTCSeconds())+'Z';
}

(function($) {
	
	$.fn.switchClasses = function(classSwitch, newClass, duration) {
		var obj = this;
		var oldClass;
		for (var i = 0; i < classSwitch.length; i++) {
			if (obj.hasClass(classSwitch[i])) {
				oldClass = classSwitch[i];
				break;
			}
		}
		if (oldClass != newClass) {
			obj.switchClass(oldClass, newClass,duration);
		}
	};
	
	
    $.fn.stopBlink = function(options) {
    	this.removeClass("highlight");
    };
	
	$.fn.blink = function(options) {

        var defaults = {
            highlightClass: "highlight",
            blinkCount: 3, // -1 infinite
            fadeDownSpeed: "slow",
            fadeUpSpeed: "slow",
            fadeToOpacity: 0.33
        };
        var options = $.extend(defaults, options);

        return this.each(function() {
            var obj = $(this);
        	if (obj.hasClass(options.highlightClass)) {
        		return;
        	}
            var blinkCount = 0;

            obj.addClass(options.highlightClass);
            doBlink();

            function doBlink() {
                if (obj.hasClass("highlight") && (options.blinkCount == -1 || blinkCount < options.blinkCount)) {
                    obj.fadeTo(options.fadeDownSpeed, options.fadeToOpacity, function() {
                        obj.fadeTo(options.fadeUpSpeed, 1.0, doBlink);
                    });
                } else {
                    obj.removeClass(options.highlightClass);
                }
                blinkCount++;
            }
        });

    };
})(jQuery);
