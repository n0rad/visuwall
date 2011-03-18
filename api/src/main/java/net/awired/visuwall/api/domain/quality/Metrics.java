package net.awired.visuwall.api.domain.quality;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metrics")
public final class Metrics {

    protected List<QualityMetric> metrics;

}
