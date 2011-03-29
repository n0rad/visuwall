package net.awired.visuwall.server.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final Map<String, Wall> walls = new HashMap<String, Wall>();

    @Autowired
    WallService wallService;

    @PostConstruct
    public void postConstruct() {
        Wall wall = createAwiredWall();
        walls.put(wall.getName(), wall);
        wallService.addWall(wall);
    }

    private Wall createAwiredWall() {
        Wall wall = new Wall("awired");
        wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://ci.awired.net/jenkins"));
        wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://ci.visuwall.awired.net"));
        wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR, "http://sonar.awired.net"));
        return wall;
    }

    @RequestMapping
    public @ResponseBody Collection<Project> getProjects(@RequestParam("wallName") String wallName) {
        Wall wall = walls.get(wallName);
        Collection<Project> projects = wall.getProjects();
        LOG.info("Projects collection size :" + projects.size());
        return projects;
    }

    @RequestMapping("status")
    public @ResponseBody List<ProjectStatus> getStatus(@RequestParam("wallName") String wallName) {
        Wall wall = walls.get(wallName);
        return wall.getStatus();
    }

    @RequestMapping("get")
    public @ResponseBody Project getProject(@RequestParam("wallName") String wallName, @RequestParam("projectName") String projectName) throws Exception {
        Wall wall = walls.get(wallName);
        return wall.findFreshProject(projectName);
    }

    @RequestMapping("finishTime")
    public @ResponseBody Date getFinishTime(@RequestParam("wallName") String wallName, @RequestParam("projectName") String projectName) throws Exception {
        Wall wall = walls.get(wallName);
        return wall.getEstimatedFinishTime(projectName);
    }

    @RequestMapping("build")
    public @ResponseBody Build getBuild(@RequestParam("wallName") String wallName, @RequestParam("projectName") String projectName, @RequestParam("buildId") int buildId) throws Exception {
        Wall wall = walls.get(wallName);
        return wall.findBuildByBuildNumber(projectName, buildId);
    }

}