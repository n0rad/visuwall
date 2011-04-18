package net.awired.visuwall.server.web.controller;

import java.util.Collection;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.service.ProjectService;
import net.awired.visuwall.core.service.WallService;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/wall/{wallName}/project")
public class ProjectController {

    @Autowired
    WallService wallService;

    @Autowired
    ProjectService projectService;
    
    @RequestMapping
    public @ResponseBody Collection<Project> getWallProjects(@PathVariable String wallName) throws NotFoundException {
        Wall wall = wallService.find(wallName);
    	return wall.getProjects();
    }

    @RequestMapping("{projectName}")
    public @ResponseBody Project getProject(@PathVariable String wallName, @PathVariable String projectName) throws Exception {
        Wall wall = wallService.find(wallName);
        projectService.updateWallProject(wall, projectName);
        return wall.getProjectFromName(projectName);
    }

    @RequestMapping("{projectName}/build")
    public @ResponseBody Build getBuild(@PathVariable String wallName, @PathVariable String projectName) throws Exception {
        throw new NotImplementedException();
    }

    @RequestMapping("{projectName}/build/{buildId}")
    public @ResponseBody Build getBuild(@PathVariable String wallName, @PathVariable String projectName, @PathVariable int buildId) throws Exception {
        Wall wall = wallService.find(wallName);
        return projectService.findBuildByBuildNumber(wall, projectName, buildId);
    }

}