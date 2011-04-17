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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.google.common.base.Preconditions;

public final class SonarConnectionPlugin implements QualityConnectionPlugin {

    private final String url;
    private final String login;
    private final String password;

    /**
     * http://docs.codehaus.org/display/SONAR/Web+Service+API
     */
    private Sonar sonar;

    private Map<String, QualityMetric> qualityMetrics = new HashMap<String, QualityMetric>();

    private static final Logger LOG = LoggerFactory.getLogger(SonarConnectionPlugin.class);

    public SonarConnectionPlugin(String url, String login, String password) {
    	this.url = url;
    	this.login = login;
    	this.password = password;
    	
    	if (isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = Sonar.create(url, login, password);
        } else {
            sonar = Sonar.create(url);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url " + url);
        }
        createMetricList();
    }
    
	@Override
	public void close() {
		// TODO close
	}
    
    private void createMetricList() {
        String metricUrl = url+"/api/metrics?format=xml";
        try {
            InputStream xmlStream = new URL(metricUrl).openStream();
            Unmarshaller unmarshaller = JAXBContext.newInstance(SonarMetrics.class).createUnmarshaller();
            SonarMetrics sonarMetrics = SonarMetrics.class.cast(unmarshaller.unmarshal(xmlStream));
            for (QualityMetric metric:sonarMetrics.metric) {
                qualityMetrics.put(metric.getKey(), metric);
            }
        } catch (MalformedURLException e) {
            LOG.error("url: "+metricUrl,e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error("url: "+metricUrl,e);
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            LOG.error("url: "+metricUrl,e);
            throw new RuntimeException(e);
        }
    }

    private Measure getMeasure(String projectId, String measureKey) throws SonarMetricNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId");
        try {
            ResourceQuery query = ResourceQuery.createForMetrics(projectId, measureKey);
            Resource resource = sonar.find(query);
            if (resource == null) {
                throw new SonarMetricNotFoundException("Metric "+measureKey+" not found for project "+projectId+" in Sonar "+url);
            }
            return resource.getMeasure(measureKey);
        } catch(ConnectionException e) {
            throw new SonarMetricNotFoundException("Metric "+measureKey+" not found for project "+projectId+" in Sonar "+url, e);
        }
    }

    @Override
    public QualityResult populateQuality(ProjectId projectId, String... metrics) {
        Preconditions.checkNotNull(projectId, "projectId");
        QualityResult qualityResult = new QualityResult();
        if (projectId.getArtifactId() != null) {
            if (metrics.length == 0) {
                metrics = qualityMetrics.keySet().toArray(new String[]{});
            }
            for (String key : metrics) {
                addMeasure(projectId, qualityResult, key);
            }
        }
        return qualityResult;
    }

    @Override
    public boolean contains(ProjectId projectId) {
        if (projectId.getArtifactId() == null) {
            return false;
        }
        try {
            getMeasure(projectId.getArtifactId(), "comment_blank_lines");
        } catch(SonarMetricNotFoundException e) {
            return false;
        }
        return true;
    }

    private void addMeasure(ProjectId projectId, QualityResult quality, String key) {
        try {
            Measure measure = getMeasure(projectId.getArtifactId(), key);
            if (measure != null) {
                Double value = measure.getValue();
                if (value != null) {
                    QualityMeasure qualityMeasure = new QualityMeasure();
                    qualityMeasure.setName(qualityMetrics.get(key).getName());
                    qualityMeasure.setValue(value);
                    qualityMeasure.setFormattedValue(measure.getFormattedValue());
                    quality.add(key, qualityMeasure);
                }
            }
        }catch(SonarMetricNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
    }

}
