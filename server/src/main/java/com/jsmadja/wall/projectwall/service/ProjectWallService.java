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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jsmadja.wall.hudsonclient.Hudson;
import com.jsmadja.wall.hudsonclient.HudsonProjectNotFoundException;
import com.jsmadja.wall.hudsonclient.domain.HudsonProject;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.ProjectStatus;

@Service
public class ProjectWallService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectWallService.class);

    @Autowired
    private Hudson hudson;

    @Autowired
    private SonarService sonarService;

    private Set<Project> projects = new HashSet<Project>();

    public List<ProjectStatus> getStatus() {
        List<ProjectStatus> statusList = new ArrayList<ProjectStatus>();
        for (Project project: findAllProjects()) {
            ProjectStatus status = new ProjectStatus();
            status.setBuilding(project.getHudsonProject().isBuilding());
            status.setLastBuildId(project.getHudsonProject().getLastBuildNumber());
            status.setName(project.getName());
            statusList.add(status);
        }
        return statusList;
    }

    /**
     * @return List of all available projects
     */
    public final Collection<Project> findAllProjects() {
        projects = new HashSet<Project>();

        List<HudsonProject> hudsonProjects = hudson.findAllProjects();
        for (HudsonProject hudsonProject:hudsonProjects) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Found "+hudsonProject.getName());
            }
            try {
                Project project = createProject(hudsonProject);
                projects.add(project);
            } catch(HudsonProjectNotFoundException e) {
                LOG.warn(e.getMessage());
            }
        }

        return projects;
    }

    /**
     * Find project among available projects
     * @param projectName
     * @return
     * @throws HudsonProjectNotFoundException
     */
    public Project findProject(String projectName) throws ProjectNotFoundException {
        try {
            HudsonProject hudsonProject = hudson.findProject(projectName);
            Project project = createProject(hudsonProject);
            projects.remove(project);
            projects.add(project);
            return project;
        } catch(HudsonProjectNotFoundException e) {
            LOG.error("Project with name ["+projectName+"] not found", e);
            throw new ProjectNotFoundException(e);
        }
    }

    private Project createProject(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        Project project = new Project();
        project.setName(hudsonProject.getName());
        project.setDescription(hudsonProject.getDescription());
        project.setCoverage(sonarService.getCoverage(hudsonProject.getArtifactId()));
        project.setRulesCompliance(sonarService.getRulesCompliance(hudsonProject.getArtifactId()));
        project.setHudsonProject(hudsonProject);
        return project;
    }

}
