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
import net.awired.visuwall.bambooclient.rest.Build;
import net.awired.visuwall.bambooclient.rest.Builds;
import net.awired.visuwall.bambooclient.rest.Plan;
import net.awired.visuwall.bambooclient.rest.Plans;
import net.awired.visuwall.bambooclient.rest.Result;
import net.awired.visuwall.bambooclient.rest.Results;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public final class Bamboo {

	private Client client;

	private BambooUrlBuilder bambooUrlBuilder;

	private static final Logger LOG = LoggerFactory.getLogger(Bamboo.class);

	public Bamboo(String bambooUrl) {
		client = Client.create();
		bambooUrlBuilder = new BambooUrlBuilder(bambooUrl);

		if (LOG.isInfoEnabled()) {
			LOG.info("Initialize bamboo with url " + bambooUrl);
		}
	}

	public List<BambooProject> findAllProjects() {
		String projectsUrl = bambooUrlBuilder.getAllProjectsUrl();
		if (LOG.isDebugEnabled()) {
			LOG.debug("All project url : " + projectsUrl);
		}
		WebResource bambooResource = client.resource(projectsUrl);
		Plans plans = bambooResource.get(Plans.class);

		List<BambooProject> projects = new ArrayList<BambooProject>();
		projects.addAll(createProjectsFrom(plans));

		return projects;
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

	public BambooProject findProject(String projectKey) {
		Preconditions.checkNotNull(projectKey, "projectKey is a mandatory parameter");

		String projectUrl = bambooUrlBuilder.getProjectUrl(projectKey);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Project url : " + projectUrl);
		}
		WebResource bambooResource = client.resource(projectUrl);
		Results results = bambooResource.get(Results.class);

		BambooProject project = createProjectFrom(results.results.get(0).result.get(0));
		project.setName(projectKey);

		int[] buildNumbers = getBuildNumbers(results.results.get(0).result);
		project.setBuildNumbers(buildNumbers);

		String isBuildingUrl = bambooUrlBuilder.getIsBuildingUrl(projectKey);
		if (LOG.isDebugEnabled()) {
			LOG.debug("IsBuilding url : " + isBuildingUrl);
		}
		bambooResource = client.resource(isBuildingUrl);
		Plan plan = bambooResource.get(Plan.class);
		project.setBuilding(plan.isBuilding);
		return project;
	}

	private int[] getBuildNumbers(List<Result> results) {
		int[] buildNumbers = new int[results.size()];

		for (int i = 0; i < results.size(); i++) {
			buildNumbers[i] = results.get(i).number;
		}

		return buildNumbers;
	}

	public int getLastBuildNumber(String projectName) {
		Preconditions.checkNotNull(projectName, "projectName");

		String lastBuildUrl = bambooUrlBuilder.getLatestBuildResult(projectName);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Last build url : " + lastBuildUrl);
		}
		WebResource bambooResource = client.resource(lastBuildUrl);
		Results results = bambooResource.get(Results.class);
		return results.results.get(0).result.get(0).number;
	}

	public BambooBuild findBuild(String projectName, int buildNumber) throws BambooBuildNotFoundException {
		Preconditions.checkNotNull(projectName, "projectName");

		String buildUrl = bambooUrlBuilder.getBuildUrl(projectName, buildNumber);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Build url : " + buildUrl);
		}
		WebResource bambooResource = client.resource(buildUrl);
		Result result = bambooResource.get(Result.class);
		BambooBuild build = createBuildFrom(result);
		return build;
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

	public String getState(String projectName) {
		Preconditions.checkNotNull(projectName, "projectName");

		String lastBuildUrl = bambooUrlBuilder.getLastBuildUrl();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Last build url : " + lastBuildUrl);
		}

		int lastBuildNumber = getLastBuildNumber(projectName);
		String key = projectName + "-" + lastBuildNumber;

		WebResource bambooResource = client.resource(lastBuildUrl);
		Builds builds = bambooResource.get(Builds.class);
		List<Build> allBuilds = builds.builds.get(0).build;
		for (Build build : allBuilds) {
			if (key.equals(build.key)) {
				return build.state;
			}
		}
		throw new RuntimeException("Not state found for projectName: " + projectName);
	}

	public Date getEstimatedFinishTime(String projectName) throws BambooProjectNotFoundException {
		Preconditions.checkNotNull(projectName, "projectName");

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

	public final long getAverageBuildDurationTime(String projectName) throws BambooProjectNotFoundException {
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

	private boolean isNeverSuccessful(BambooProject bambooProject) throws BambooProjectNotFoundException {
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
}
