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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import fr.norad.visuwall.service.WallHolderService;
import fr.norad.visuwall.ProjectService;
import fr.norad.visuwall.domain.Project;
import fr.norad.visuwall.persistence.entity.Wall;

@Controller
@RequestMapping("/wall/{wallName}/project")
public class ProjectController {

    @Autowired
    WallHolderService wallService;

    @Autowired
    ProjectService projectService;

    @RequestMapping("{projectId}")
    public @ResponseBody
    Project getProject(@PathVariable String wallName, @PathVariable String projectId) throws Exception {
        Wall wall = wallService.find(wallName);
        Project project = wall.getProjects().getById(projectId);
        return project;
    }

}
