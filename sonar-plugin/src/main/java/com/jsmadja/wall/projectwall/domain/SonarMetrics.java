package com.jsmadja.wall.projectwall.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.jsmadja.wall.projectwall.domain.quality.QualityMetric;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metrics")
public class SonarMetrics {

    public List<QualityMetric> metric;

}
