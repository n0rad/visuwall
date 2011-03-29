package net.awired.visuwall.api.domain.quality;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;


public final class QualityResult {

    private Map<String, QualityMeasure> measures = new HashMap<String, QualityMeasure>();

    public QualityMeasure getMeasure(String key) {
        return measures.get(key);
    }

    public void add(String key, QualityMeasure measure) {
        measures.put(key, measure);
    }

    public Set<Entry<String, QualityMeasure>> getMeasures() {
        return measures.entrySet();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("measures", measures.values()) //
        .toString();
    }
}
