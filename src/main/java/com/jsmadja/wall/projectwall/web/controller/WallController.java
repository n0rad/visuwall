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

package com.jsmadja.wall.projectwall.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.service.ProjectWallService;

@Controller
@RequestMapping("/wall")
public class WallController {

	@Autowired
	ProjectWallService projectWallService;
	
	@GET
	@RequestMapping
	public ModelAndView getWall() {
		List<Project> projects = projectWallService.findAllProjects();
				
		Map<String, Object> data = new HashMap<String, Object>(); 
		data.put("projects", projects);
		
		
		int jobsPerRow = (int) Math.round(Math.sqrt(projects.size()));
		data.put("jobsPerRow", jobsPerRow);
		
		return new ModelAndView("wall", data);
	}
}
