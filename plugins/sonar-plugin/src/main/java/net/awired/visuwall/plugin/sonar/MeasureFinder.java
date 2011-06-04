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

package net.awired.visuwall.plugin.sonar;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class MeasureFinder {

	private String sonarUrl;

	/**
	 * http://docs.codehaus.org/display/SONAR/Web+Service+API
	 */
	private Sonar sonar;

	public MeasureFinder(String sonarUrl) throws SonarMetricsNotFoundException {
		this(sonarUrl, null, null);
	}

	public MeasureFinder(String sonarUrl, String login, String password) throws SonarMetricsNotFoundException {
		this.sonarUrl = sonarUrl;

		if (isBlank(sonarUrl)) {
			throw new IllegalStateException("sonarUrl can't be null.");
		}
		if (isNotBlank(login) && isNotBlank(password)) {
			sonar = Sonar.create(sonarUrl, login, password);
		} else {
			sonar = Sonar.create(sonarUrl);
		}
	}

	@VisibleForTesting
	MeasureFinder(Sonar sonar) {
		this.sonar = sonar;
	}

	public QualityMeasure findQualityMeasure(String artifactId, String measureKey) throws SonarMeasureNotFoundException {
		Preconditions.checkState(!Strings.isNullOrEmpty(artifactId), "artifactId is a mandatory parameter");
		Preconditions.checkNotNull(measureKey, "measureKey is a mandatory parameter");

		Measure measure = findMeasure(artifactId, measureKey);
		if (measure != null) {
			Double value = measure.getValue();
			if (value != null) {
				QualityMeasure qualityMeasure = new QualityMeasure();
				qualityMeasure.setKey(measureKey);
				qualityMeasure.setValue(value);
				qualityMeasure.setFormattedValue(measure.getFormattedValue());
				return qualityMeasure;
			}
		}
		throw new SonarMeasureNotFoundException("metric '" + measureKey + "' is not found for project '" + artifactId
		        + "'");
	}

	public Measure findMeasure(String artifactId, String measureKey) throws SonarMeasureNotFoundException {
		Preconditions.checkState(!Strings.isNullOrEmpty(artifactId), "artifactId is a mandatory parameter");
		Preconditions.checkNotNull(measureKey, "measureKey is a mandatory parameter");

		try {
			ResourceQuery query = ResourceQuery.createForMetrics(artifactId, measureKey);
			Resource resource = sonar.find(query);
			if (resource == null) {
				throw new SonarMeasureNotFoundException("Metric " + measureKey + " not found for project " + artifactId
				        + " in Sonar " + sonarUrl);
			}
			Measure measure = resource.getMeasure(measureKey);
			if (measure == null) {
				throw new SonarMeasureNotFoundException("Measure [" + measureKey + "] not found for project "
				        + artifactId + " in Sonar " + sonarUrl);
			}

			return measure;
		} catch (ConnectionException e) {
			throw new SonarMeasureNotFoundException("Metric " + measureKey + " not found for project " + artifactId
			        + " in Sonar " + sonarUrl, e);
		}
	}

	public Double findMeasureValue(String artifactId, String measureKey) throws SonarMeasureNotFoundException {
		Preconditions.checkState(!Strings.isNullOrEmpty(artifactId), "artifactId is a mandatory parameter");

		Measure measure = findMeasure(artifactId, measureKey);
		return measure.getValue();
	}

}
