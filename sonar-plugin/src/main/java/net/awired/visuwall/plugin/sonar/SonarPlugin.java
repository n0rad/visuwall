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

package net.awired.visuwall.plugin.sonar;

import java.util.Arrays;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.plugin.QualityPlugin;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public final class SonarPlugin implements QualityPlugin {

    private String url;
    private String login;
    private String password;

    private MeasureFinder measureCreator;

    private static final Logger LOG = LoggerFactory.getLogger(SonarPlugin.class);

    @Override
    public void init() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url " + url);
        }
        measureCreator = new MeasureFinder(url, login, password);
        measureCreator.init();
    }

    @Override
    public QualityResult populateQuality(ProjectId projectId, String... metrics) {
        Preconditions.checkNotNull(projectId, "projectId");
        QualityResult qualityResult = new QualityResult();
        if (projectId.getArtifactId() != null) {
            if (metrics.length == 0) {
                metrics = measureCreator.getAllMetricKeys();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("metrics to find : "+Arrays.toString(metrics));
            }

            for (String key : metrics) {
                try {
                    QualityMeasure qualityMeasure = measureCreator.createQualityMeasure(projectId.getArtifactId(), key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found qualityMeasure : "+qualityMeasure);
                    }
                    qualityResult.add(key, qualityMeasure);
                } catch(SonarMetricNotFoundException e) {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug(e.getMessage());
                    }
                }
            }
        }
        return qualityResult;
    }

    @Override
    public boolean contains(ProjectId projectId) {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");

        String artifactId = projectId.getArtifactId();
        if (artifactId == null) {
            return false;
        }

        try {
            measureCreator.findMeasure(artifactId, "comment_blank_lines");
        } catch(SonarMetricNotFoundException e) {
            return false;
        }
        return true;
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

    @VisibleForTesting
    void setMeasureCreator(MeasureFinder measureCreator) {
        this.measureCreator = measureCreator;
    }
}
