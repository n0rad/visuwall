package com.jsmadja.wall.projectwall.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.ProjectStatus;
import com.jsmadja.wall.projectwall.domain.Software;
import com.jsmadja.wall.projectwall.domain.SoftwareAccess;
import com.jsmadja.wall.projectwall.domain.Wall;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private static final Logger LOG = Logger.getLogger(ProjectController.class.getName());

    private Wall wall;

    public ProjectController() {
        wall = new Wall();
        wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://fluxx.fr.cr:8080/hudson"));
        wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR, "http://fluxx.fr.cr:9000"));
    }

    @RequestMapping
    public @ResponseBody Collection<Project> getProjects() {
        Collection<Project> projects = wall.findAllProjects();
        LOG.info("Projects collection size :" + projects.size());
        return projects;
    }

    @RequestMapping("status")
    public @ResponseBody List<ProjectStatus> getStatus() {
        return wall.getStatus();
    }

    @RequestMapping("get")
    public @ResponseBody Project getProject(@RequestParam("projectName") String projectName) throws Exception {
        return wall.findProjectByName(projectName);
    }

    @RequestMapping("finishTime")
    public @ResponseBody Date getFinishTime(@RequestParam("projectName") String projectName) throws Exception {
        return wall.getEstimatedFinishTime(projectName);
    }



}