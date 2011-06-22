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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class Bamboo {

    @VisibleForTesting
    BambooUrlBuilder bambooUrlBuilder;

    @VisibleForTesting
    GenericSoftwareClient client = new GenericSoftwareClient();

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
            project.setKey(projectKey);

            int buildNumber = getBuildNumber(results.results.get(0).result, projectKey);
            project.setBuildNumbers(new int[] { buildNumber });

			String isBuildingUrl = bambooUrlBuilder.getIsBuildingUrl(projectKey);
			Plan plan = client.resource(isBuildingUrl, Plan.class);
			project.setBuilding(plan.isBuilding);
			return project;
		} catch (ResourceNotFoundException e) {
            throw new BambooProjectNotFoundException("Can't find bamboo project with projectKey " + projectKey, e);
		}
	}

    private int getBuildNumber(List<Result> results, String projectKey) {
        for (Result result : results) {
            if (result.key.startsWith(projectKey)) {
                return result.number;
            }
		}
        return -1;
	}

    public int getLastBuildNumber(String projectKey) throws BambooBuildNumberNotFoundException {
        checkProjectKey(projectKey);
		try {
            String lastBuildUrl = bambooUrlBuilder.getLatestBuildResult(projectKey);
			Results results = client.resource(lastBuildUrl, Results.class);
			return results.results.get(0).result.get(0).number;
		} catch (ResourceNotFoundException e) {
			throw new BambooBuildNumberNotFoundException(e);
		}
	}

    public BambooBuild findBuild(String projectKey, int buildNumber) throws BambooBuildNotFoundException {
        checkProjectKey(projectKey);
		try {
            String buildUrl = bambooUrlBuilder.getBuildUrl(projectKey, buildNumber);
			Result result = client.resource(buildUrl, Result.class);
			BambooBuild build = createBuildFrom(result);
			return build;
		} catch (ResourceNotFoundException e) {
			throw new BambooBuildNotFoundException(e.getMessage(), e);
		}
	}

	private BambooBuild createBuildFrom(Result result) {
		BambooBuild bambooBuild = new BambooBuild();
		bambooBuild.setBuildNumber(result.number);
        bambooBuild.setDuration(result.buildDuration);
		bambooBuild.setStartTime(result.buildStartedTime);
		bambooBuild.setState(result.state);
		bambooBuild.setPassCount(result.successfulTestCount);
		bambooBuild.setFailCount(result.failedTestCount);
		return bambooBuild;
	}

    public String getState(String projectKey) throws BambooStateNotFoundException {
        checkProjectKey(projectKey);
		try {
            int lastBuildNumber = getLastBuildNumber(projectKey);
            String lastBuildUrl = bambooUrlBuilder.getLastBuildUrl();
			Builds builds = client.resource(lastBuildUrl, Builds.class);
			List<Build> allBuilds = builds.builds.get(0).build;
            String key = projectKey + "-" + lastBuildNumber;
			for (Build build : allBuilds) {
				if (key.equals(build.key)) {
					return build.state;
				}
			}
		} catch (ResourceNotFoundException e) {
            throw new BambooStateNotFoundException("Not state found for projectKey: " + projectKey, e);
		} catch (BambooBuildNumberNotFoundException e) {
            throw new BambooStateNotFoundException("Not state found for projectKey: " + projectKey, e);
		}
        throw new BambooStateNotFoundException("Not state found for projectKey: " + projectKey);
	}

    public Date getEstimatedFinishTime(String projectKey) throws BambooProjectNotFoundException {
        checkProjectKey(projectKey);
        BambooProject project = findProject(projectKey);
        BambooBuild build = project.getCurrentBuild();
		Date startTime = build.getStartTime();
        long averageBuildDurationTime = getAverageBuildDurationTime(projectKey);
		DateTime estimatedFinishTime = new DateTime(startTime.getTime()).plus(averageBuildDurationTime);
		if (LOG.isDebugEnabled()) {
            LOG.debug("Estimated finish time of project " + projectKey + " is " + estimatedFinishTime + " ms");
		}
		return estimatedFinishTime.toDate();
	}

    long getAverageBuildDurationTime(String projectKey) throws BambooProjectNotFoundException {
        checkProjectKey(projectKey);
        BambooProject bambooProject = findProject(projectKey);
		long averageTime;
		if (isNeverSuccessful(bambooProject)) {
			averageTime = maxDuration(bambooProject);
		} else {
			averageTime = computeAverageBuildDuration(bambooProject);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Average build time of " + projectKey + " is " + averageTime + " ms");
		}
		return averageTime;
	}

	private long computeAverageBuildDuration(BambooProject bambooProject) {
		String projectKey = bambooProject.getKey();
		float sumBuildDurationTime = 0;
		int[] buildNumbers = bambooProject.getBuildNumbers();
		for (int buildNumber : buildNumbers) {
			try {
				BambooBuild build = findBuild(projectKey, buildNumber);
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
				BambooBuild build = findBuild(bambooProject.getKey(), buildNumber);
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
        String projectKey = bambooProject.getKey();
		int[] buildNumbers = bambooProject.getBuildNumbers();
		for (int buildNumber : buildNumbers) {
			try {
                BambooBuild build = findBuild(projectKey, buildNumber);
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
        String projectKey = result.key;
        int buildNumber = result.number;
        String buildUrl = bambooUrlBuilder.getBuildUrl(projectKey, buildNumber);
        try {
            Result buildResult = client.resource(buildUrl, Result.class);
            BambooBuild build = createBuildFrom(buildResult);
            project.setCurrentBuild(build);
        } catch (ResourceNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't find build result at " + buildUrl, e);
            }
        }
        return project;
	}

    private void checkProjectKey(String projectKey) {
        Preconditions.checkNotNull(projectKey, "projectKey");
    }

}
