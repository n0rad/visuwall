package net.awired.visuwall.plugin.sonar.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.awired.visuwall.api.domain.quality.QualityMetric;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metrics")
public class SonarMetrics {

    public List<QualityMetric> metric;

}
