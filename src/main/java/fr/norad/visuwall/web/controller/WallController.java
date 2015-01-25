/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.visuwall.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import fr.norad.visuwall.PluginServiceInterface;
import fr.norad.visuwall.domain.Project;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.NotFoundException;
import fr.norad.visuwall.persistence.entity.Wall;
import fr.norad.visuwall.service.WallHolderService;
import fr.norad.visuwall.web.model.ProjectStatus;

//@Controller
//@RequestMapping("/wall")
public class WallController {

    private static final Logger LOG = LoggerFactory.getLogger(WallController.class);

    private static final String WALL_JSP = "wall/wallForm";

    @Autowired
    private WallHolderService wallService;

    @Autowired
    private PluginServiceInterface pluginService;

    @ExceptionHandler(Exception.class)
    public void handleAllExceptions(HttpServletResponse response, Exception e) throws IOException {
        LOG.error("error :", e);
        response.sendError(500, e.getMessage());
    }

    @RequestMapping
    public String getWallNames(ModelMap modelMap) {
        Set<String> wallNames = wallService.getWallNames();
        modelMap.put("data", wallNames);
        return "wall/wallList";
    }

    @RequestMapping("{wallName}")
    public String getWall(@PathVariable String wallName, ModelMap modelMap) throws NotFoundException {
        Wall wall = wallService.find(wallName);
        modelMap.put("data", wall);
        return WALL_JSP;
    }

    @RequestMapping("{wallName}/status")
    public @ResponseBody
    List<ProjectStatus> getStatus(@PathVariable String wallName, ModelMap modelMap) throws NotFoundException {
        Wall wall = wallService.find(wallName);
        List<ProjectStatus> statusList = new ArrayList<ProjectStatus>();
        for (Project project : wall.getProjects()) {
            ProjectStatus projectStatus = new ProjectStatus(project);
            projectStatus.setLastBuildId(project.getLastNotBuildingId());
            try {
                projectStatus.setBuilding(project.getLastBuild().isBuilding());
                projectStatus.setLastUpdate(project.getLastUpdate());
                if (project.getLastBuild().getEstimatedFinishTime() != null) {
                    long durationFromNow = project.getLastBuild().getEstimatedFinishTime().getTime()
                            - new Date().getTime();
                    projectStatus.setBuildingTimeleftSecond((int) durationFromNow / 1000);
                }
            } catch (BuildNotFoundException e) {
                LOG.debug("No current build found to say the project is building + timeleft in projectStatus for "
                        + project);
            }

            statusList.add(projectStatus);
        }
        return statusList;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String getCreate(ModelMap modelMap) {
        Wall wall = new Wall();
        modelMap.put("data", wall);
        modelMap.put("softwares", pluginService.getPluginsInfo());
        return WALL_JSP;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Object update(@Valid Wall wall) {
        wallService.update(wall);
        return true;
    }

    @RequestMapping(value = "{wallName}", method = RequestMethod.DELETE)
    public void DeleteWall(@PathVariable String wallName) {
        wallService.deleteWall(wallName);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.setAutoGrowNestedPaths(false);
    }

}
