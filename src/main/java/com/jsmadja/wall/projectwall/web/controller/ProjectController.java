package com.jsmadja.wall.projectwall.web.controller;

import java.util.List;

import javax.ws.rs.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.ProjectStatus;
import com.jsmadja.wall.projectwall.service.ProjectWallService;

@Controller
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	private ProjectWallService projectWallService;

	
	@RequestMapping
	public @ResponseBody List<Project> getProjects() {
		return projectWallService.findAllProjects();
	}
	
	@RequestMapping(value="status")
	public @ResponseBody List<ProjectStatus> getStatus() {
		return projectWallService.getStatus();
	}

//	@RequestMapping
//	public @ResponseBody
//	Project getProject(@RequestParam String name) {
//		Project proj = new Project();
//		proj.setName("projectName");
//		proj.setDescription("this is the project description");
//
//		return proj;
//	}
}