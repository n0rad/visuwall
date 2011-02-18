package com.jsmadja.wall.projectwall.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.jsmadja.wall.projectwall.ProjectNotFoundException;

public class SonarService {

    private final String sonarUrl;
    private final Sonar sonar;

    private final static Logger LOG = LoggerFactory.getLogger(SonarService.class);

    public SonarService(String sonarUrl) {
        this.sonarUrl = sonarUrl;
        sonar = Sonar.create(sonarUrl);
    }

    public Double getCoverage(String projectId) throws ProjectNotFoundException {
        Measure coverage = getMeasure(projectId, "coverage");
        if (LOG.isInfoEnabled()) {
            LOG.info("Coverage measure for project #"+projectId+" is "+coverage);
        }
        return coverage.getValue();
    }

    public Double getRulesCompliance(String projectId) throws ProjectNotFoundException {
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

}
