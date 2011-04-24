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

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.service.WallService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wall")
public class WallController {

	private static final Logger LOG = LoggerFactory
			.getLogger(WallController.class.getName());
	private static final String WALL_JSP = "wall/wall";

	@Autowired
	private WallService wallService;

	Wall wall;

	public WallController() {
		wall = new Wall("orange-vallee");
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
//		 "http://integration.wormee.orange-vallee.net:8080/hudson"));
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR,
//		 "http://integration.wormee.orange-vallee.net:9000"));
		wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
				"http://ci.awired.net/jenkins"));
		 wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
		 "http://ci.visuwall.awired.net"));
		wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR,
				"http://sonar.awired.net"));
		 wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
		 "http://fluxx.fr.cr:8080/hudson"));
		 wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR,
		 "http://fluxx.fr.cr:9000"));
		wall.discoverProjects();
	}

	@PostConstruct
	public void postConstruct() {
		wallService.addWall(wall);
	}

	// //////////////////////////////////////////////////////////////////

	@RequestMapping
	public @ResponseBody
	Set<String> getWallNames() {
		return wallService.getWallNames();
	}

	@RequestMapping("{wallName}")
	public @ResponseBody
	Wall getProjects(@PathVariable String wallName) {
		Wall wall = wallService.getWall(wallName);
		LOG.info("Projects collection size :" + wall.getProjects().size());
		return wall;
	}

	@RequestMapping("{wallName}/status")
	public @ResponseBody
	List<ProjectStatus> getStatus(@PathVariable String wallName) {
		return wallService.getStatus(wallName);
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String getCreate() {
		return WALL_JSP;
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public void create(Wall wall) {
		wallService.persist(wall);
	}

	@RequestMapping(value = "{wallName}", method = RequestMethod.DELETE)
	public void Delete(@PathVariable String wallName) {

	}

}