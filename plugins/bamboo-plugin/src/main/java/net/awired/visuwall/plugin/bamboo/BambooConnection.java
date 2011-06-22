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
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.exception.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooBuildNumberNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooProjectNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooStateNotFoundException;
import net.awired.visuwall.plugin.bamboo.builder.BuildBuilder;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class BambooConnection implements Connection, BuildCapability {

	public static final String BAMBOO_ID = "BAMBOO_ID";

	private Bamboo bamboo;

	private BuildBuilder buildBuilder = new BuildBuilder();

	private boolean connected;

	public void connect(String url, String login, String password) {
		connect(url);
	}

    void connect(String url) {
		Preconditions.checkNotNull(url, "url is mandatory");
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("url can't be null.");
		}
		bamboo = new Bamboo(url);
		connected = true;
	}

	@Override
	public List<ProjectId> findAllProjects() {
		checkConnected();
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
	public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
	        ProjectNotFoundException {
		checkProjectId(projectId);

		String projectName = getProjectKey(projectId);
		try {
			BambooBuild bambooBuild = bamboo.findBuild(projectName, buildNumber);
			return buildBuilder.createFrom(bambooBuild);
		} catch (BambooBuildNotFoundException e) {
			throw new BuildNotFoundException(e);
		}
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
		checkProjectId(projectId);
		try {
			String projectName = getProjectKey(projectId);
			BambooProject bambooProject = bamboo.findProject(projectName);
			return bambooProject.isBuilding();
		} catch (BambooProjectNotFoundException e) {
			throw new ProjectNotFoundException("Can't find project with ProjectId:" + projectId, e);
		}
	}

	@Override
	public State getLastBuildState(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		try {
			String projectName = getProjectKey(projectId);
			String bambooState = bamboo.getState(projectName);
			return States.asVisuwallState(bambooState);
		} catch (BambooStateNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNumberNotFoundException {
		checkProjectId(projectId);
		String id = getProjectKey(projectId);
		Preconditions.checkNotNull(id, BAMBOO_ID);
		try {
			return bamboo.getLastBuildNumber(id);
		} catch (BambooBuildNumberNotFoundException e) {
            throw new BuildNumberNotFoundException(e);
		}
	}

	@Override
	public List<String> findProjectNames() {
        List<String> projectNames = new ArrayList<String>();
        List<BambooProject> projects = bamboo.findAllProjects();
        for (BambooProject project : projects) {
            projectNames.add(project.getName());
        }
        return projectNames;
	}

	@Override
	public boolean contains(ProjectId projectId) {
        checkProjectId(projectId);
        String key = projectId.getId(BAMBOO_ID);
        List<BambooProject> projects = bamboo.findAllProjects();
        for (BambooProject project : projects) {
            String projectKey = project.getKey();
            if (key.equals(projectKey)) {
                return true;
            }
        }
        return false;
	}

	@Override
	public List<ProjectId> findProjectIdsByNames(List<String> names) {
        Preconditions.checkNotNull(names, "names is mandatory");
        List<ProjectId> projectIds = new ArrayList<ProjectId>();
        List<BambooProject> projects = bamboo.findAllProjects();
        for (BambooProject project : projects) {
            String name = project.getName();
            if (names.contains(name)) {
                ProjectId projectId = createProjectId(project);
                projectIds.add(projectId);
            }
        }
        return projectIds;
	}

    @Override
	public void close() {
        connected = false;
	}

    @Override
    public String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public int[] getBuildNumbers(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        return new ArrayList<SoftwareProjectId>();
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        return new ArrayList<SoftwareProjectId>();
    }

    @Override
    public State getLastBuildState(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId projectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    private String getProjectKey(ProjectId projectId) {
        return projectId.getId(BAMBOO_ID);
    }

    private void checkProjectId(ProjectId projectId) {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    private ProjectId createProjectId(BambooProject project) {
        ProjectId projectId = new ProjectId();
        projectId.setName(project.getName());
        projectId.addId(BAMBOO_ID, project.getKey());
        return projectId;
    }

}
