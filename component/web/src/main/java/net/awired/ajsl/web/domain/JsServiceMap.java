/**
 *
 *     Copyright (C) norad.fr
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
