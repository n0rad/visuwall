/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jsmadja.wall.projectwall.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jsmadja.wall.projectwall.domain.HudsonProject;
import com.jsmadja.wall.projectwall.domain.Project;

@Service
public class ProjectWallService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectWallService.class);

    @Autowired
    private HudsonService hudsonService;

    @Autowired
    private SonarService sonarService;

    /**
     * @return List of all available projects
     */
    public final List<Project> getProjects() {
        List<Project> projects = new ArrayList<Project>();

        List<HudsonProject> jobs = hudsonService.findAllProjects();
        for (HudsonProject job:jobs) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Found "+job.getName());
            }
            try {
                Project project = new Project();
                project.setName(job.getName());
                project.setDescription(job.getDescription());
                project.setCoverage(sonarService.getCoverage(job.getArtifactId()));
                project.setRulesCompliance(sonarService.getRulesCompliance(job.getArtifactId()));
                project.setHudsonJob(job);
                projects.add(project);
            } catch(ProjectNotFoundException e) {
                LOG.warn(e.getMessage());
            }
        }

        return projects;
    }

}
