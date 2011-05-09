/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.server.web.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;

import net.awired.ajsl.web.domain.JsServiceMap;
import net.awired.ajsl.web.service.interfaces.JsService;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.core.domain.PluginInfo;
import net.awired.visuwall.core.domain.Software;
import net.awired.visuwall.core.service.PluginService;

import org.sonar.wsclient.services.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/plugin")
public class PluginController {

	@Autowired
	private PluginService pluginService;
	
	@Autowired
	JsService jsService;
	
	@RequestMapping("js")
	public @ResponseBody Map<String, List<String>> res() throws Exception {
		Map<File, String> jsMap = jsService.getJsFiles();
		
		JsServiceMap serviceMap = jsService.buildServiceMapFromJsMap(jsMap);
		Predicate<List<String>> predicate = new Predicate<List<String>>() {
			@Override
			public boolean apply(List<String> value) {
				for (String string : value) {
					return string.startsWith("js/visuwall");
				}
				return true;
			}
		};

		Map<String, List<String>> val = Maps.filterValues(serviceMap, predicate);

		//String obj = jsService.buildJsServiceMapString(serviceMap);
		return val;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<PluginInfo> getSoftwareList() {
		return pluginService.getPluginsInfo();
	}
}
