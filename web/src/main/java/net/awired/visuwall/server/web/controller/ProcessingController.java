package net.awired.visuwall.server.web.controller;

import java.util.Date;

import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.service.ProjectService;
import net.awired.visuwall.core.service.WallHolderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/processing")
public class ProcessingController {

    @Autowired
    private WallHolderService wallService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping("finishTime")
    public @ResponseBody
    Date getFinishTime(@RequestParam String wallName,
            @RequestParam String projectName) throws Exception {
        Wall wall = wallService.find(wallName);
        return projectService.getEstimatedFinishTime(wall, projectName);
    }
}
