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

package net.awired.visuwall.bambooclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.bambooclient.builder.BambooUrlBuilder;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.exception.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooBuildNumberNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooProjectNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooStateNotFoundException;
import net.awired.visuwall.bambooclient.rest.Build;
import net.awired.visuwall.bambooclient.rest.Builds;
import net.awired.visuwall.bambooclient.rest.Plan;
import net.awired.visuwall.bambooclient.rest.Plans;
import net.awired.visuwall.bambooclient.rest.Result;
import net.awired.visuwall.bambooclient.rest.Results;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Bamboo {

	private BambooUrlBuilder bambooUrlBuilder;

	private GenericSoftwareClient client = new GenericSoftwareClient();

	private static final Logger LOG = LoggerFactory.getLogger(Bamboo.class);

	public Bamboo(String bambooUrl) {
		bambooUrlBuilder = new BambooUrlBuilder(bambooUrl);
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialize bamboo with url " + bambooUrl);
		}
	}

	public List<BambooProject> findAllProjects() {
		String projectsUrl = bambooUrlBuilder.getAllProjectsUrl();
		List<BambooProject> projects = new ArrayList<BambooProject>();
		try {
			Plans plans = client.resource(projectsUrl, Plans.class);
			projects.addAll(createProjectsFrom(plans));
		} catch (ResourceNotFoundException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(e.getMessage(), e);
			}
		}
		return projects;
	}

	public BambooProject findProject(String projectKey) throws BambooProjectNotFoundException {
		Preconditions.checkNotNull(projectKey, "projectKey is a mandatory parameter");
		try {
			String projectUrl = bambooUrlBuilder.getProjectUrl(projectKey);
			Results results = client.resource(projectUrl, Results.class);

			BambooProject project = createProjectFrom(results.results.get(0).result.get(0));
			project.setName(projectKey);

			int[] buildNumbers = getBuildNumbers(results.results.get(0).result);
			project.setBuildNumbers(buildNumbers);

			String isBuildingUrl = bambooUrlBuilder.getIsBuildingUrl(projectKey);
			Plan plan = client.resource(isBuildingUrl, Plan.class);
			project.setBuilding(plan.isBuilding);
			return project;
		} catch (ResourceNotFoundException e) {
			throw new BambooProjectNotFoundException("Can't find bamboo project with projectKey " + projectKey);
		}
	}

	private int[] getBuildNumbers(List<Result> results) {
		int[] buildNumbers = new int[results.size()];

		for (int i = 0; i < results.size(); i++) {
			buildNumbers[i] = results.get(i).number;
		}

		return buildNumbers;
	}

	public int getLastBuildNumber(String projectName) throws BambooBuildNumberNotFoundException {
		checkProjectName(projectName);
		try {
			String lastBuildUrl = bambooUrlBuilder.getLatestBuildResult(projectName);
			Results results = client.resource(lastBuildUrl, Results.class);
			return results.results.get(0).result.get(0).number;
		} catch (ResourceNotFoundException e) {
			throw new BambooBuildNumberNotFoundException(e);
		}
	}

	public BambooBuild findBuild(String projectName, int buildNumber) throws BambooBuildNotFoundException {
		checkProjectName(projectName);
		try {
			String buildUrl = bambooUrlBuilder.getBuildUrl(projectName, buildNumber);
			Result result = client.resource(buildUrl, Result.class);
			BambooBuild build = createBuildFrom(result);
			return build;
		} catch (ResourceNotFoundException e) {
			throw new BambooBuildNotFoundException(e.getMessage(), e);
		}
	}

	private void checkProjectName(String projectName) {
		Preconditions.checkNotNull(projectName, "projectName");
	}

	private BambooBuild createBuildFrom(Result result) {
		BambooBuild bambooBuild = new BambooBuild();
		bambooBuild.setBuildNumber(result.number);
		bambooBuild.setDuration(result.buildDuration.longValue());
		bambooBuild.setStartTime(result.buildStartedTime);
		bambooBuild.setState(result.state);
		bambooBuild.setPassCount(result.successfulTestCount);
		bambooBuild.setFailCount(result.failedTestCount);
		return bambooBuild;
	}

	public String getState(String projectName) throws BambooStateNotFoundException {
		checkProjectName(projectName);
		try {
			String lastBuildUrl = bambooUrlBuilder.getLastBuildUrl();
			int lastBuildNumber = getLastBuildNumber(projectName);
			String key = projectName + "-" + lastBuildNumber;

			Builds builds = client.resource(lastBuildUrl, Builds.class);
			List<Build> allBuilds = builds.builds.get(0).build;
			for (Build build : allBuilds) {
				if (key.equals(build.key)) {
					return build.state;
				}
			}
		} catch (ResourceNotFoundException e) {
			throw new BambooStateNotFoundException("Not state found for projectName: " + projectName);
		} catch (BambooBuildNumberNotFoundException e) {
			throw new BambooStateNotFoundException("Not state found for projectName: " + projectName);
		}
		throw new BambooStateNotFoundException("Not state found for projectName: " + projectName);
	}

	public Date getEstimatedFinishTime(String projectName) throws BambooProjectNotFoundException {
		checkProjectName(projectName);

		BambooProject project = findProject(projectName);
		BambooBuild build = project.getCurrentBuild();
		Date startTime = build.getStartTime();
		long averageBuildDurationTime = getAverageBuildDurationTime(projectName);
		DateTime estimatedFinishTime = new DateTime(startTime.getTime()).plus(averageBuildDurationTime);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Estimated finish time of project " + projectName + " is " + estimatedFinishTime + " ms");
		}

		return estimatedFinishTime.toDate();
	}

	public long getAverageBuildDurationTime(String projectName) throws BambooProjectNotFoundException {
		BambooProject bambooProject = findProject(projectName);

		long averageTime;

		if (isNeverSuccessful(bambooProject)) {
			averageTime = maxDuration(bambooProject);
		} else {
			averageTime = computeAverageBuildDuration(bambooProject);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Average build time of " + projectName + " is " + averageTime + " ms");
		}

		return averageTime;
	}

	private long computeAverageBuildDuration(BambooProject bambooProject) {
		String projectName = bambooProject.getName();
		float sumBuildDurationTime = 0;
		int[] buildNumbers = bambooProject.getBuildNumbers();

		for (int buildNumber : buildNumbers) {
			try {
				BambooBuild build = findBuild(projectName, buildNumber);
				if (build.isSuccessful()) {
					sumBuildDurationTime += build.getDuration();
				}
			} catch (BambooBuildNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}

		return (long) (sumBuildDurationTime / buildNumbers.length);

	}

	private long maxDuration(BambooProject bambooProject) {
		long max = 0;
		int[] buildNumbers = bambooProject.getBuildNumbers();

		for (int buildNumber : buildNumbers) {
			try {
				BambooBuild build = findBuild(bambooProject.getName(), buildNumber);
				max = Math.max(max, build.getDuration());
			} catch (BambooBuildNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}

		return max;

	}

    private boolean isNeverSuccessful(BambooProject bambooProject) {
		int[] buildNumbers = bambooProject.getBuildNumbers();
		for (int buildNumber : buildNumbers) {
			try {
				BambooBuild build = findBuild(bambooProject.getName(), buildNumber);
				if (build.isSuccessful()) {
					return false;
				}
			} catch (BambooBuildNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}
		return true;
	}

	private List<BambooProject> createProjectsFrom(Plans plans) {
		Preconditions.checkNotNull(plans, "plans");

		List<BambooProject> projects = new ArrayList<BambooProject>();
		if (plans.plan != null) {
			for (Plan plan : plans.plan) {
				projects.add(createProjectFrom(plan));
			}
		}
		if (plans.plans != null) {
			projects.addAll(createProjectsFrom(plans.plans));
		}
		return projects;
	}

	private BambooProject createProjectFrom(Plan plan) {
		BambooProject project = new BambooProject();
		project.setName(plan.name);
		project.setKey(plan.key);
		project.setLink(plan.link.href);
		return project;
	}

	private BambooProject createProjectFrom(Result result) {
		BambooProject project = new BambooProject();
		BambooBuild build = createBuildFrom(result);
		project.setCurrentBuild(build);
		return project;
	}
}
