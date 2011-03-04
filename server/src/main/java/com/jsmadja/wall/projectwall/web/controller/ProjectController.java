package com.jsmadja.wall.projectwall.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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

	private static final Logger LOG = Logger.getLogger(ProjectController.class.getName());
	
    @Autowired
    private ProjectWallService projectWallService;


    @RequestMapping
    public @ResponseBody Collection<Project> getProjects() {
    	Collection<Project> projects = projectWallService.findAllProjects();
    	LOG.info("Projects collection size :" + projects.size());
    	return projects;
    }

    @RequestMapping("status")
    public @ResponseBody List<ProjectStatus> getStatus() {
        return projectWallService.getStatus();
    }
    
    @RequestMapping("get")
    public @ResponseBody Project getProject(@RequestParam("projectName") String projectName) throws Exception {
        return projectWallService.findProject(projectName);
    }
    
    @RequestMapping("finishTime")
    public @ResponseBody Date getFinishTime(@RequestParam("projectName") String projectName) throws Exception {
    	return projectWallService.getEstimatedFinishTime(projectName);
    }
    
    

}