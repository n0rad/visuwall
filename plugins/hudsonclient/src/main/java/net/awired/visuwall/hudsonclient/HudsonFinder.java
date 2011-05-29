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

package net.awired.visuwall.hudsonclient;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.builder.TestResultBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonModelUser;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class HudsonFinder {

	private static final Logger LOG = LoggerFactory.getLogger(HudsonFinder.class);

	private TestResultBuilder hudsonTestService = new TestResultBuilder();

	private HudsonUrlBuilder hudsonUrlBuilder;

	private Cache cache;

	private Client client;

	public HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder) {
		this.hudsonUrlBuilder = hudsonUrlBuilder;
		CacheManager cacheManager = CacheManager.create();
		cache = cacheManager.getCache("hudson_projects_cache");
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses();
		client = buildJerseyClient(clientConfig);
	}

	Client buildJerseyClient(ClientConfig clientConfig) {
		return Client.create(clientConfig);
	}

	public HudsonBuild find(String projectName, int buildNumber) throws HudsonBuildNotFoundException,
	        HudsonProjectNotFoundException {
		Preconditions.checkNotNull(projectName, "projectName is mandatory");
		Preconditions.checkArgument(buildNumber >= 0, "buidNumber must be positive");

		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Find build with project name [" + projectName + "] and buildNumber [" + buildNumber + "]");
			}

			HudsonMavenMavenModuleSetBuild setBuild = findBuildByProjectNameAndBuildNumber(projectName, buildNumber);
			return createHudsonBuildFrom(projectName, buildNumber, setBuild);
		} catch (UniformInterfaceException e) {
			String message = "No build #" + buildNumber + " for project " + projectName;
			if (LOG.isDebugEnabled()) {
				LOG.debug(message, e);
			}
			throw new HudsonBuildNotFoundException(message, e);
		} catch (WebApplicationException e) {
			String message = "Error while loading build #" + buildNumber + " for project " + projectName;
			if (LOG.isDebugEnabled()) {
				LOG.debug(message, e);
			}
			throw new HudsonBuildNotFoundException(message, e);
		}
	}

	private HudsonBuild createHudsonBuildFrom(String projectName, int buildNumber,
	        HudsonMavenMavenModuleSetBuild setBuild) {
		String cacheKey = "hudsonbuild_" + projectName + "_" + buildNumber;
		Element element = cache.get(cacheKey);
		if (element != null) {
			return (HudsonBuild) element.getObjectValue();
		}
		String testResultUrl = hudsonUrlBuilder.getTestResultUrl(projectName, buildNumber);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Test result url : " + testResultUrl);
		}
		HudsonBuild hudsonBuild = new HudsonBuild();
		hudsonBuild.setState(getState(setBuild));
		hudsonBuild.setDuration(setBuild.getDuration());
		hudsonBuild.setStartTime(new Date(setBuild.getTimestamp()));
		hudsonBuild.setSuccessful(isSuccessful(setBuild));
		hudsonBuild.setCommiters(getCommiters(setBuild));
		hudsonBuild.setBuildNumber(buildNumber);
		insertTestsResults(testResultUrl, hudsonBuild);
		cache.put(new Element(cacheKey, hudsonBuild));
		return hudsonBuild;
	}

	private void insertTestsResults(String testResultUrl, HudsonBuild hudsonBuild) {
		WebResource testResultResource = client.resource(testResultUrl);
		try {
			HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource
			        .get(HudsonMavenReportersSurefireAggregatedReport.class);

			TestResult unitTestResult = hudsonTestService.buildUnitTestResult(surefireReport);
			TestResult integrationTestResult = hudsonTestService.buildIntegrationTestResult(surefireReport);
			hudsonBuild.setUnitTestResult(unitTestResult);
			hudsonBuild.setIntegrationTestResult(integrationTestResult);
		} catch (UniformInterfaceException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no test result for " + testResultResource.getURI().toString());
			}
		} catch (ClientHandlerException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no test result " + testResultResource.getURI().toString());
			}
		}
	}

	HudsonMavenMavenModuleSetBuild findBuildByProjectNameAndBuildNumber(String projectName, int buildNumber)
	        throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
		Preconditions.checkNotNull(projectName, "projectName is mandatory");
		Preconditions.checkArgument(buildNumber >= 0, "buidNumber must be positive");

		String cacheKey = "build_" + projectName + "_" + buildNumber;
		Element element = cache.get(cacheKey);
		if (element != null) {
			return (HudsonMavenMavenModuleSetBuild) element.getObjectValue();
		}
		HudsonMavenMavenModuleSetBuild setBuild = findSetBuild(projectName, buildNumber);
		cache.put(new Element(cacheKey, setBuild));
		return setBuild;
	}

	private String[] getCommiters(HudsonMavenMavenModuleSetBuild setBuild) {
		List<HudsonModelUser> users = setBuild.getCulprit();
		String[] commiters = new String[users.size()];
		for (int i = 0; i < users.size(); i++) {
			commiters[i] = users.get(i).getFullName();
		}
		return commiters;
	}

	private boolean isSuccessful(HudsonMavenMavenModuleSetBuild job) {
		Node element = (org.w3c.dom.Element) job.getResult();

		if (element == null) {
			return false;
		}

		Node result = element.getFirstChild();
		if (result == null) {
			return false;
		}

		return "SUCCESS".equals(result.getNodeValue());
	}

	private HudsonMavenMavenModuleSetBuild findSetBuild(String projectName, int buildNumber)
	        throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
		String buildUrl = hudsonUrlBuilder.getBuildUrl(projectName, buildNumber);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Build url : " + buildUrl);
		}
		WebResource jobResource = client.resource(buildUrl);
		HudsonMavenMavenModuleSetBuild setBuild;
		try {
			setBuild = jobResource.get(HudsonMavenMavenModuleSetBuild.class);
		} catch (UniformInterfaceException e) {
			if (projectExists(projectName)) {
				throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for project "
				        + projectName, e);
			} else {
				throw new HudsonProjectNotFoundException("Project " + projectName + " not found", e);
			}
		}
		return setBuild;
	}

	private boolean projectExists(String projectName) {
		try {
			findJobByProjectName(projectName);
		} catch (HudsonProjectNotFoundException e) {
			return false;
		}
		return true;
	}

	HudsonMavenMavenModuleSet findJobByProjectName(String projectName) throws HudsonProjectNotFoundException {
		Preconditions.checkNotNull(projectName, "projectName is mandatory");
		Element element = cache.get(projectName);
		if (element != null) {
			return (HudsonMavenMavenModuleSet) element.getObjectValue();
		}
		try {
			String projectUrl = hudsonUrlBuilder.getProjectUrl(projectName);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Project url : " + projectUrl);
			}
			if (isNotMavenProject(projectUrl))
				throw new HudsonProjectNotFoundException(projectName + " is not a maven project");
			WebResource projectResource = client.resource(projectUrl);
			HudsonMavenMavenModuleSet moduleSet = projectResource.get(HudsonMavenMavenModuleSet.class);
			cache.put(new Element(projectName, moduleSet));
			return moduleSet;
		} catch (UniformInterfaceException e) {
			throw new HudsonProjectNotFoundException(e);
		}
	}

	private boolean isNotMavenProject(String projectUrl) {
		try {
			byte[] bytes = ByteStreams.toByteArray(new URL(projectUrl).openStream());
			String content = new String(bytes);
			return !content.startsWith("<mavenModuleSet>");
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Can't define if " + projectUrl + " is a maven project, cause: " + e.getCause());
			}
			return true;
		}
	}

	public List<String> findProjectNames() {
		List<String> projectNames = new ArrayList<String>();
		String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
		WebResource hudsonResource = client.resource(projectsUrl);
		HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
		for (Object job : hudson.getJob()) {
			org.w3c.dom.Element element = (org.w3c.dom.Element) job;
			String name = getProjectName(element);
			projectNames.add(name);
		}
		return projectNames;
	}

	String getProjectName(Node element) {
		return element.getFirstChild().getFirstChild().getNodeValue();
	}

	public List<Object> findHudsonProjects() {
		String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
		if (LOG.isDebugEnabled()) {
			LOG.debug("All project url : " + projectsUrl);
		}
		WebResource hudsonResource = client.resource(projectsUrl);
		HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
		List<Object> hudsonProjects = hudson.getJob();
		if (LOG.isDebugEnabled()) {
			LOG.debug(hudsonProjects.size() + " projects to update");
		}
		return hudsonProjects;
	}

	String getState(HudsonMavenMavenModuleSetBuild setBuild) {
		if (setBuild == null) {
			return "NEW";
		}
		org.w3c.dom.Element element = (org.w3c.dom.Element) setBuild.getResult();
		if (element == null) {
			return "NEW";
		}

		return element.getFirstChild().getNodeValue();
	}
}
