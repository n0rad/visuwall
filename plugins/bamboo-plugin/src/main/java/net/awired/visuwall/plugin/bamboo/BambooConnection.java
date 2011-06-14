/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.bambooclient.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.BambooProjectNotFoundException;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;

import com.google.common.base.Preconditions;

public class BambooConnection implements Connection, BuildCapability {

    private static final String BAMBOO_ID = "BAMBOO_ID";

    private Bamboo bamboo;

    private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();

    static {
        STATE_MAPPING.put("Successful", State.SUCCESS);
        STATE_MAPPING.put("Failed", State.FAILURE);
    }

    public BambooConnection(String url, String login, String password) {
        this(url);
    }

    public BambooConnection(String url) {
        Preconditions.checkNotNull(url, "Use setUrl() before calling init method");
        bamboo = new Bamboo(url);
    }

    @Override
    public List<ProjectId> findAllProjects() {
        List<ProjectId> projects = new ArrayList<ProjectId>();
        for (BambooProject bambooProject : bamboo.findAllProjects()) {
            ProjectId projectId = new ProjectId();
            projectId.setName(bambooProject.getName());
            projectId.addId(BAMBOO_ID, bambooProject.getKey());
            projects.add(projectId);
        }
        return projects;
    }

    @Override
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        String projectKey = getProjectKey(projectId);

        BambooProject bambooProject = bamboo.findProject(projectKey);

        Project project = new Project(projectId);
        project.setName(bambooProject.getName());
        return project;
    }

    private String getProjectKey(ProjectId projectId) {
        return projectId.getId(BAMBOO_ID);
    }

    @Override
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException {
        String projectName = getProjectKey(projectId);
        try {
            return createBuild(bamboo.findBuild(projectName, buildNumber));
        } catch (BambooBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    private Build createBuild(BambooBuild bambooBuild) {
        Build build = new Build();
        build.setBuildNumber(bambooBuild.getBuildNumber());
        build.setDuration(bambooBuild.getDuration());
        build.setStartTime(bambooBuild.getStartTime());
        build.setState(getState(bambooBuild.getState()));
        TestResult unitTestResult = createUnitTestResult(bambooBuild);
        build.setUnitTestResult(unitTestResult);
        return build;
    }

	private TestResult createUnitTestResult(BambooBuild bambooBuild) {
	    TestResult unitTestResult = new TestResult();
        unitTestResult.setFailCount(bambooBuild.getFailCount());
        unitTestResult.setPassCount(bambooBuild.getPassCount());
	    return unitTestResult;
    }

    private State getState(String bambooState) {
        State state = STATE_MAPPING.get(bambooState);
        if (state == null) {
            throw new RuntimeException("No state mapping for bambooState: " + bambooState);
        }
        return state;
    }

    @Override
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        String projectName = getProjectKey(projectId);
        try {
            return bamboo.getEstimatedFinishTime(projectName);
        } catch (BambooProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        String projectName = getProjectKey(projectId);
        BambooProject bambooProject = bamboo.findProject(projectName);
        return bambooProject.isBuilding();
    }

    @Override
    public State getState(ProjectId projectId) throws ProjectNotFoundException {
        String projectName = getProjectKey(projectId);
        String bambooState = bamboo.getState(projectName);
        return getState(bambooState);
    }

    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");
        String id = getProjectKey(projectId);
        Preconditions.checkNotNull(id, BAMBOO_ID);
        return bamboo.getLastBuildNumber(id);
    }

    @Override
    public List<String> findProjectNames() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean contains(ProjectId projectId) {
        return false;
    }

    @Override
    public List<ProjectId> findProjectsByNames(List<String> names) {
        return null;
    }

    @Override
    public void close() {
    }

}
