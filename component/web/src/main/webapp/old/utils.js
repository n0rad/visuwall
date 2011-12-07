
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
