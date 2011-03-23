package net.awired.visuwall.server.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.service.WallService;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/wall/{wallName}/project")
public class ProjectController {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class.getName());

    @Autowired
    WallService wallService;
    
    ///////////////////////////////////////////////////////////////////
    
    @RequestMapping
    public @ResponseBody Collection<Project> getWallProjects(@PathVariable String wallName) {
    	return wallService.getWall(wallName).getProjects();
    }

    @RequestMapping("{projectName}")
    public @ResponseBody Project getProject(@PathVariable String wallName, @PathVariable String projectName) throws Exception {
        return wallService.getWall(wallName).findProjectByName(projectName);
    }



    @RequestMapping("{projectName}/build")
    public @ResponseBody Build getBuild(@PathVariable String wallName, @PathVariable String projectName) throws Exception {
    	throw new NotImplementedException();
    }

    @RequestMapping("{projectName}/build/{buildId}")
    public @ResponseBody Build getBuild(@PathVariable String wallName, @PathVariable String projectName, @PathVariable int buildId) throws Exception {
        return wallService.getWall(wallName).findBuildByProjectNameAndBuilderNumber(projectName, buildId);
    }
    
    ///////////////////////////////////////////////////////////////////
    
}