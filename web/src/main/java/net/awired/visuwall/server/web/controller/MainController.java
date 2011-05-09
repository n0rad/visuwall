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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.awired.ajsl.web.domain.JsServiceMap;
import net.awired.ajsl.web.service.interfaces.CssService;
import net.awired.ajsl.web.service.interfaces.JsService;
import net.awired.ajsl.web.service.interfaces.JsonService;
import net.awired.visuwall.server.application.VisuwallApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

@Controller
public class MainController {

	private static final String ROOT_CONTEXT = "/index.html";

	@Autowired
	VisuwallApplication visuwallApplication;

	@Autowired
	CssService cssService;

	@Autowired
	JsService jsService;
	
	@Autowired
	JsonService jsonService;

	@RequestMapping(ROOT_CONTEXT)
	public ModelAndView getIndex() throws Exception {
		ModelMap modelMap = new ModelMap();

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
		StringWriter writer = new StringWriter();
		jsonService.serialize(val, writer);
		
		modelMap.put("jsService", writer.toString());
		modelMap.put("jsLinks", jsService.getJsLinks("res/", jsMap));
		modelMap.put("cssLinks", cssService.getCssLinks("res/"));
		modelMap.put("version", visuwallApplication.getVersion());
		// test2();
		test();
		return new ModelAndView("index", modelMap);
	}

	public void test() {
	}

	public void test2() throws Exception {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

		Map<File, String> jsFiles = jsService.getJsFiles();
		for (File jsFile : jsFiles.keySet()) {
			if (jsFiles.get(jsFile).startsWith("js/visuwall")) {
				FileReader reader = new FileReader(jsFile);
				jsEngine.eval(reader);
			}
		}

		System.out.println("there");
	}

	@RequestMapping("/")
	public void getSlash(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(ROOT_CONTEXT);
		dispatcher.forward(request, response);
	}
}
