function getHostname(str) {
	var re = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
	var matched = str.match(re);
	if (matched) {
		return matched[1].toString();
	}
	return null;
}
