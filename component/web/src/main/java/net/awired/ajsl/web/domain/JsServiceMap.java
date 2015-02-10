package net.awired.ajsl.web.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsServiceMap extends HashMap<String, List<String>> {

	private static final long serialVersionUID = 1L;
	
	public void addService(String name, String clazz) {
		List<String> value = this.get(name);
		if (value == null) {
			value = new ArrayList<String>();
			this.put(name, value);
		}
		value.add(clazz);
	}
	
	
}
