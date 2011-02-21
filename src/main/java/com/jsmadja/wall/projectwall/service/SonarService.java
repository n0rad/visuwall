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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.jsmadja.wall.projectwall.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.domain.TechnicalDebt;

public class SonarService {

    private final String sonarUrl;
    private final Sonar sonar;

    private static final Logger LOG = LoggerFactory.getLogger(SonarService.class);

    public SonarService(String sonarUrl) {
        this.sonarUrl = sonarUrl;
        sonar = Sonar.create(sonarUrl);
    }

    /**
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    public final Double getCoverage(String projectId) throws ProjectNotFoundException {
        Measure coverage = getMeasure(projectId, "coverage");
        if (LOG.isInfoEnabled()) {
            LOG.info("Coverage measure for project #"+projectId+" is "+coverage);
        }
        return coverage.getValue();
    }

    /**
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    public final Double getRulesCompliance(String projectId) throws ProjectNotFoundException {
        Measure rulesCompliance = getMeasure(projectId, "violations_density");
        if (LOG.isInfoEnabled()) {
            LOG.info("Rules compliance measure for project #"+projectId+" is "+rulesCompliance);
        }
        return rulesCompliance.getValue();
    }

    private Measure getMeasure(String projectId, String measureKey) throws ProjectNotFoundException {
        Resource project = sonar.find(ResourceQuery.createForMetrics(projectId, measureKey));
        if (project == null) {
            throw new ProjectNotFoundException("Project with id #"+projectId+" not found in sonar "+sonarUrl);
        }
        return project.getMeasure(measureKey);
    }

    /**
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    public TechnicalDebt getTechnicalDebt(String projectId) throws ProjectNotFoundException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Fetch technical debt for project #"+projectId);
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

}
