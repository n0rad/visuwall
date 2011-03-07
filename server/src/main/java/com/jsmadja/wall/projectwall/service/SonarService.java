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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.google.common.base.Preconditions;
import com.jsmadja.wall.projectwall.domain.Build;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.TechnicalDebt;

public class SonarService implements Service {

    private String url;
    private String login;
    private String password;

    private Sonar sonar;

    private static final Logger LOG = LoggerFactory.getLogger(SonarService.class);

    @Override
    public void init() {
        if (isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = Sonar.create(url);
        } else {
            sonar = Sonar.create(url, login, login);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url "+url);
        }
    }

    /**
     * @param projectId
     * @return
     * @throws SonarProjectNotFoundException
     */
    public final Double getCoverage(String projectId) throws SonarProjectNotFoundException {
        Measure coverage = getMeasure(projectId, "coverage");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Coverage measure for project #"+projectId+" is "+coverage);
        }
        return coverage.getValue();
    }

    /**
     * @param projectId
     * @return
     * @throws SonarProjectNotFoundException
     */
    public final Double getRulesCompliance(String projectId) throws SonarProjectNotFoundException {
        Measure rulesCompliance = getMeasure(projectId, "violations_density");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Rules compliance measure for project #"+projectId+" is "+rulesCompliance);
        }
        return rulesCompliance.getValue();
    }

    private Measure getMeasure(String projectId, String measureKey) throws SonarProjectNotFoundException {
        Preconditions.checkNotNull(projectId);
        Resource project = sonar.find(ResourceQuery.createForMetrics(projectId, measureKey));
        if (project == null) {
            throw new SonarProjectNotFoundException("Project with id #"+projectId+" not found in sonar "+url);
        }
        return project.getMeasure(measureKey);
    }

    /**
     * @param projectId
     * @return
     * @throws SonarProjectNotFoundException
     */
    public TechnicalDebt getTechnicalDebt(String projectId) throws SonarProjectNotFoundException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetch technical debt for project #"+projectId);
        }

        Measure ratio = getMeasure(projectId, "technical_debt_ratio");
        Measure cost = getMeasure(projectId, "technical_debt");
        Measure days = getMeasure(projectId, "technical_debt_days");

        TechnicalDebt technicalDebt = new TechnicalDebt();
        technicalDebt.setRatio(ratio.getValue());
        technicalDebt.setCost(cost.getIntValue());
        technicalDebt.setDays(days.getIntValue());

        if (LOG.isInfoEnabled()) {
            LOG.info(technicalDebt.toString());
        }

        return technicalDebt;
    }

    @Override
    public List<Project> findAllProjects() {
        return new ArrayList<Project>();
    }

    @Override
    public void populate(Project project) throws ProjectNotFoundException {
        String projectId = project.getId();
        try {
            project.setRulesCompliance(getRulesCompliance(projectId));
            project.setCoverage(getCoverage(projectId));
        } catch (SonarProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public Date getEstimatedFinishTime(Project project) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("Not implemented!");
    }

    @Override
    public Project findProjectByName(String projectName) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("Not implemented!");
    }

    @Override
    public Build findBuildByProjectNameAndBuildNumber(String projectName, int buildNumber) throws BuildNotFoundException {
        throw new BuildNotFoundException("Not implemented!");
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

}
