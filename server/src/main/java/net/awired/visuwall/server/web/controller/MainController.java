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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.awired.visuwall.server.service.interfaces.CssService;
import net.awired.visuwall.server.service.interfaces.JsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class MainController {

	@Autowired
	CssService cssService;

	@Autowired
	JsService jsService;

	@RequestMapping("/index.html")
	public ModelAndView getIndex() throws Exception {
		ModelMap modelMap = new ModelMap();

		modelMap.put("jsLinks", jsService.getJsLinks("res/"));
		modelMap.put("cssLinks", cssService.getCssLinks("res/"));

		
		return new ModelAndView("index", modelMap);
	}
	
	@RequestMapping("/")
	public ModelAndView getSlash(HttpServletRequest request) throws Exception {
		return new ModelAndView("redirect:index.html");
	}
}
