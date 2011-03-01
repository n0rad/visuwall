package com.jsmadja.wall.projectwall.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    public @ResponseBody Collection<Project> getProjects() {
        return projectWallService.findAllProjects();
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