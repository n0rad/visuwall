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

package net.awired.visuwall.hudsonclient.finder;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.Commiters;
import net.awired.visuwall.hudsonclient.HudsonJerseyClient;
import net.awired.visuwall.hudsonclient.builder.HudsonBuildBuilder;
import net.awired.visuwall.hudsonclient.builder.HudsonProjectBuilder;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelJob;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelRun;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.helper.HudsonXmlHelper;
import net.awired.visuwall.hudsonclient.helper.MavenHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.UniformInterfaceException;

public class HudsonFinder {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonFinder.class);

    private HudsonUrlBuilder hudsonUrlBuilder;
    private HudsonJerseyClient hudsonJerseyClient;

    private Cache cache;

    private HudsonBuildBuilder hudsonBuildBuilder;

    public HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder, HudsonJerseyClient hudsonJerseyClient,
            HudsonBuildBuilder hudsonBuildBuilder) {
        this.hudsonJerseyClient = hudsonJerseyClient;
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = hudsonBuildBuilder;
        initCache();
    }

    public HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder) {
        this.hudsonJerseyClient = new HudsonJerseyClient();
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = new HudsonBuildBuilder();
        initCache();
    }

    private void initCache() {
        CacheManager cacheManager = CacheManager.create();
        cache = cacheManager.getCache("hudson_projects_cache");
    }

    public HudsonBuild find(String projectName, int buildNumber) throws HudsonBuildNotFoundException,
            HudsonProjectNotFoundException {
        checkProjectName(projectName);
        checkBuildNumber(buildNumber);
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Find build with project name [" + projectName + "] and buildNumber [" + buildNumber + "]");
            }
            HudsonMavenMavenModuleSetBuild setBuild = findBuildByProjectNameAndBuildNumber(projectName, buildNumber);
            String cacheKey = "hudsonbuild_" + projectName + "_" + buildNumber;
            Element element = cache.get(cacheKey);
            if (element != null) {
                return (HudsonBuild) element.getObjectValue();
            }

            String testResultUrl = hudsonUrlBuilder.getTestResultUrl(projectName, setBuild.getNumber());
            HudsonMavenReportersSurefireAggregatedReport surefireReport = hudsonJerseyClient
                    .getSurefireReport(testResultUrl);

            String[] commiterNames = HudsonXmlHelper.getCommiterNames(setBuild);
            Commiters commiters = findCommiters(commiterNames);
            HudsonBuild hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, surefireReport, commiters);
            cache.put(new Element(cacheKey, hudsonBuild));
            return hudsonBuild;
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

    public HudsonMavenMavenModuleSetBuild findBuildByProjectNameAndBuildNumber(String projectName, int buildNumber)
            throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        checkProjectName(projectName);
        checkBuildNumber(buildNumber);
        String cacheKey = "build_" + projectName + "_" + buildNumber;
        Element element = cache.get(cacheKey);
        if (element != null) {
            return (HudsonMavenMavenModuleSetBuild) element.getObjectValue();
        }
        try {
            String buildUrl = hudsonUrlBuilder.getBuildUrl(projectName, buildNumber);
            HudsonMavenMavenModuleSetBuild setBuild = hudsonJerseyClient.getModuleSetBuild(buildUrl);
            cache.put(new Element(cacheKey, setBuild));
            return setBuild;
        } catch (UniformInterfaceException e) {
            if (projectExists(projectName)) {
                throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for project "
                        + projectName, e);
            } else {
                throw new HudsonProjectNotFoundException("Project " + projectName + " not found", e);
            }
        }
    }

    public List<String> findProjectNames() {
        List<String> projectNames = new ArrayList<String>();
        String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
        HudsonModelHudson hudson = hudsonJerseyClient.getHudsonJobs(projectsUrl);
        for (Object job : hudson.getJob()) {
            Node element = (Node) job;
            String name = getProjectName(element);
            projectNames.add(name);
        }
        return projectNames;
    }

    public boolean projectExists(String projectName) {
        checkProjectName(projectName);
        try {
            String projectUrl = hudsonUrlBuilder.getProjectUrl(projectName);
            if (MavenHelper.isNotMavenProject(projectUrl))
                throw new HudsonProjectNotFoundException(projectName + " is not a maven project");
            hudsonJerseyClient.getModuleSet(projectUrl);
        } catch (UniformInterfaceException e) {
            return false;
        } catch (HudsonProjectNotFoundException e) {
            return false;
        }
        return true;
    }

    private String getProjectName(Node node) {
        Node firstChild = node.getFirstChild();
        Node firstChild2 = firstChild.getFirstChild();
        String projectName = firstChild2.getNodeValue();
        return projectName;
    }

    public HudsonProject findProject(String projectName) throws HudsonProjectNotFoundException {
        try {
            HudsonMavenMavenModuleSet moduleSet = findJobByProjectName(projectName);
            HudsonProjectBuilder hudsonProjectBuilder = new HudsonProjectBuilder(hudsonUrlBuilder, this);
            return hudsonProjectBuilder.createHudsonProjectFrom(moduleSet);
        } catch (HudsonBuildNotFoundException e) {
            throw new HudsonProjectNotFoundException(e);
        }
    }

    public int getLastBuildNumber(String projectName) throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        checkProjectName(projectName);
        HudsonMavenMavenModuleSet job = findJobByProjectName(projectName);
        HudsonModelRun run = job.getLastBuild();
        if (run == null) {
            throw new HudsonBuildNotFoundException("Project " + projectName + " has no last build");
        }
        return run.getNumber();
    }

    public boolean isBuilding(String projectName) throws HudsonProjectNotFoundException {
        checkProjectName(projectName);
        HudsonModelJob job = findJobByProjectName(projectName);
        return HudsonXmlHelper.getIsBuilding(job);
    }

    public Commiters findCommiters(String[] commiterNames) {
        Preconditions.checkNotNull(commiterNames, "commiterNames is mandatory");
        Commiters commiters = new Commiters();
        for (String commiterName : commiterNames) {
            String url = hudsonUrlBuilder.getUserUrl(commiterName);
            HudsonUser hudsonUser = hudsonJerseyClient.getHudsonUser(url);
            Commiter commiter = new Commiter(hudsonUser.getId());
            commiter.setName(commiterName);
            commiter.setEmail(hudsonUser.getEmail());
            commiters.addCommiter(commiter);
        }
        return commiters;
    }

    public String getStateOf(String projectName, int buildNumber) throws HudsonBuildNotFoundException,
            HudsonProjectNotFoundException {
        checkProjectName(projectName);
        checkBuildNumber(buildNumber);
        HudsonMavenMavenModuleSetBuild build = findBuildByProjectNameAndBuildNumber(projectName, buildNumber);
        return HudsonXmlHelper.getState(build);
    }

    private HudsonMavenMavenModuleSet findJobByProjectName(String projectName) throws HudsonProjectNotFoundException {
        try {
            String projectUrl = hudsonUrlBuilder.getProjectUrl(projectName);
            if (MavenHelper.isNotMavenProject(projectUrl))
                throw new HudsonProjectNotFoundException(projectName + " is not a maven project");
            HudsonMavenMavenModuleSet moduleSet = hudsonJerseyClient.getModuleSet(projectUrl);
            return moduleSet;
        } catch (UniformInterfaceException e) {
            throw new HudsonProjectNotFoundException(e);
        }
    }

    private void checkBuildNumber(int buildNumber) {
        Preconditions.checkArgument(buildNumber >= 0, "buidNumber must be positive");
    }

    private void checkProjectName(String projectName) {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");
    }

}
