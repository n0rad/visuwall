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
package fr.norad.visuwall;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.capabilities.BuildCapabilityProcess;
import fr.norad.visuwall.domain.CapabilitiesResult;
import fr.norad.visuwall.domain.Project;
import fr.norad.visuwall.domain.ProjectKey;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.domain.TestResult;
import fr.norad.visuwall.domain.quality.QualityResult;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.MavenIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.persistence.entity.SoftwareAccess;
import fr.norad.visuwall.persistence.entity.Wall;
import fr.norad.visuwall.plugin.capability.BasicCapability;
import fr.norad.visuwall.plugin.capability.BuildCapability;
import fr.norad.visuwall.plugin.capability.MetricCapability;
import fr.norad.visuwall.plugin.capability.TestCapability;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    BuildCapabilityProcess buildProcess;

    @Autowired
    MetricCapabilityProcess metricCapabilityProcess;

    public Runnable getProjectCreationRunner(final Wall wallWhereToAdd, final SoftwareAccess buildSoftwareAccess,
            final SoftwareProjectId projectId) {
        Preconditions.checkState(buildSoftwareAccess.getConnection() instanceof BuildCapability,
                "softwareAccess needs to point to BuildCapability plugin connection");
        return new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                LOG.debug("Running project creation task for project id " + projectId + " on software "
                        + buildSoftwareAccess + " in wall " + wallWhereToAdd);
                BuildCapability buildConnection = (BuildCapability) buildSoftwareAccess.getConnection();

                Project connectedProject = new Project(projectId, buildConnection);
                Runnable updateProjectRunner = getUpdateProjectRunner(wallWhereToAdd, connectedProject);

                LOG.debug("Launching first project updater for " + connectedProject);
                updateProjectRunner.run();

                ScheduledFuture<?> updateProjectTask = taskScheduler.scheduleAtFixedRate(updateProjectRunner,
                        buildSoftwareAccess.getProjectStatusDelaySecond() * 1000);
                connectedProject.setUpdateProjectTask(updateProjectTask);
                LOG.debug("Adding project " + connectedProject + " to wall " + wallWhereToAdd);
                wallWhereToAdd.getProjects().add(connectedProject);
            }
        };
    }

    Runnable getUpdateProjectRunner(final Wall wall, final Project project) {
        Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        return new Runnable() {

            private boolean neverRun = true;

            @Override
            public void run() {
                LOG.debug("Running Project Updater task for project " + project);
                try {
                    String[] buildsToUpdate = buildProcess.updateStatusAndReturnBuildsToUpdate(project);
                    if (neverRun || buildsToUpdate.length != 0) {
                        try {
                            if (!neverRun) {
                                LOG.debug("Project build change and needs a update from software " + project);
                            }
                            SoftwareProjectId projectId = project.getBuildProjectId();

                            // name
                            try {
                                String name = project.getBuildConnection().getName(projectId);
                                project.setName(name);
                            } catch (Exception e) {
                                LOG.warn("Can not found project name for project " + project, e);
                            }

                            if (neverRun) {
                                try {
                                    ProjectKey projectKey = new ProjectKey();
                                    project.setProjectKey(projectKey);
                                    String mavenId = project.getBuildConnection().getMavenId(
                                            project.getBuildProjectId());
                                    projectKey.setMavenId(mavenId);
                                    projectKey.setName(project.getName());
                                } catch (MavenIdNotFoundException e) {
                                    LOG.debug("Maven project id not found for project" + project, e);
                                }
                            }

                            // description
                            try {
                                String description = project.getBuildConnection().getDescription(projectId);
                                project.setDescription(description);
                            } catch (Exception e) {
                                LOG.warn("Can not found description for project " + project, e);
                            }

                            // disabled
                            boolean projectDisabled = project.getBuildConnection().isProjectDisabled(projectId);
                            project.setDisabled(projectDisabled);

                            try {
                                List<String> buildIds = project.getBuildConnection().getBuildIds(projectId);
                                project.setBuildId(buildIds);
                                buildProcess.updateLastNotBuildingId(project);
                                buildProcess.updatePreviousCompletedBuild(project);
                            } catch (Exception e) {
                                LOG.warn("Can not update previous completed build for project " + project, e);
                            }

                            for (String buildId : buildsToUpdate) {
                                buildProcess.updateBuild(project, buildId);
                            }

                            // TODO be sure to not remove a project cause of a capability ProjectNotFoundException 
                        } finally {
                            project.setLastUpdate(new Date());
                        }
                    }

                    updateCapabilities(project);
                } catch (ProjectNotFoundException e) {
                    LOG.info("Project not found by build Software, and will be removed");
                    LOG.debug("Project not found cause", e);
                    wall.getProjects().deleteAndCleanProject(project.getId());
                } catch (BuildNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    neverRun = false;
                }
            }

        };
    }

    private void updateCapabilities(final Project project) throws BuildNotFoundException {
        // update capabilities
        if (project.getLastNotBuildingId() != null) {
            for (SoftwareProjectId softwareProjectId : project.getCapabilities().keySet()) {
                //TODO we are currently able to manage last build only 
                if (!project.getLastBuild().getCapabilitiesResults().containsKey(softwareProjectId)) {
                    try {
                        BasicCapability capability = project.getCapabilities().get(softwareProjectId);
                        LOG.info("new Software {} for project {}", capability, project);

                        CapabilitiesResult capabilitiesResult = new CapabilitiesResult();
                        project.getLastBuild().getCapabilitiesResults().put(softwareProjectId, capabilitiesResult);

                        if (capability instanceof MetricCapability) {
                            if (project.getLastNotBuildingId() != null) {
                                QualityResult qualityResult = ((MetricCapability) capability).analyzeQuality(
                                        softwareProjectId, MetricCapabilityProcess.metrics);
                                capabilitiesResult.setQualityResult(qualityResult);
                            }
                        }

                        if (capability instanceof TestCapability) {
                            if (project.getLastNotBuildingId() != null) {
                                TestResult testResult = ((TestCapability) capability)
                                        .analyzeUnitTests(softwareProjectId);
                                capabilitiesResult.setUnitTestResult(testResult);
                            }
                            if (project.getLastNotBuildingId() != null) {
                                TestResult testResult = ((TestCapability) capability)
                                        .analyzeIntegrationTests(softwareProjectId);
                                capabilitiesResult.setIntegrationTestResult(testResult);
                            }
                        }
                    } finally {
                        project.setLastUpdate(new Date());
                    }
                }
            }
        }
    }

}
