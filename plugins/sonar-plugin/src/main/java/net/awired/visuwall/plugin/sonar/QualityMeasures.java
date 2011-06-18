package net.awired.visuwall.plugin.sonar;

import net.awired.visuwall.api.domain.quality.QualityMeasure;

import org.sonar.wsclient.services.Measure;

import com.google.common.base.Preconditions;

public class QualityMeasures {

    public static QualityMeasure asQualityMeasure(Measure measure, String measureKey) {
        Preconditions.checkNotNull(measure, "measure is mandatory");
        Preconditions.checkArgument(!measureKey.isEmpty(), "measureKey is mandatory");
        Preconditions.checkNotNull(measure.getValue(), "measure must have a value");
        Double value = measure.getValue();
        QualityMeasure qualityMeasure = new QualityMeasure();
        qualityMeasure.setKey(measureKey);
        qualityMeasure.setValue(value);
        qualityMeasure.setFormattedValue(measure.getFormattedValue());
        return qualityMeasure;
    }
}
