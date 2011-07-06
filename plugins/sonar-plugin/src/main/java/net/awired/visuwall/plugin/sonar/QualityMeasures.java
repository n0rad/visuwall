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

import net.awired.clients.sonar.domain.SonarQualityMeasure;
import org.sonar.wsclient.services.Measure;
import com.google.common.base.Preconditions;

class QualityMeasures {

    private QualityMeasures() {
    }

    static SonarQualityMeasure asQualityMeasure(Measure measure, String measureKey) {
        Preconditions.checkNotNull(measure, "measure is mandatory");
        Preconditions.checkArgument(!measureKey.isEmpty(), "measureKey is mandatory");
        Preconditions.checkNotNull(measure.getValue(), "measure must have a value");
        Double value = measure.getValue();
        SonarQualityMeasure qualityMeasure = new SonarQualityMeasure();
        qualityMeasure.setKey(measureKey);
        qualityMeasure.setValue(value);
        qualityMeasure.setFormattedValue(measure.getFormattedValue());
        return qualityMeasure;
    }
}
