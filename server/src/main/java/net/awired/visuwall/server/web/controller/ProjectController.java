package net.awired.visuwall.server.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.service.WallService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/project")
public class ProjectController {

    private static final Logger LOG = Logger.getLogger(ProjectController.class.getName());

    private Wall wall;

    @Autowired
    WallService wallService;

    public ProjectController() {
        wall = new Wall("orange-vallee");
        wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://integration.wormee.orange-vallee.net:8080/hudson"));
        wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR, "http://integration.wormee.orange-vallee.net:9000"));
	     wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://ci.awired.net/jenkins"));
	//     wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://ci.visuwall.awired.net"));
	     wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR, "http://sonar.awired.net"));
	     wall.refreshProjects();
        // wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://fluxx.fr.cr:8080/hudson"));
        // wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR, "http://fluxx.fr.cr:9000"));
    }

    @PostConstruct
    public void postConstruct() {
        wallService.addWall(wall);
    }

    @RequestMapping
    public @ResponseBody Collection<Project> getProjects() {
        Collection<Project> projects = wall.getProjects();
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

    @RequestMapping("build")
    public @ResponseBody Build getBuild(@RequestParam("projectName") String projectName, @RequestParam("buildId") int buildId) throws Exception {
        return wall.findBuildByProjectNameAndBuilderNumber(projectName, buildId);
    }

}