package com.jsmadja.wall.projectwall.domain.quality;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metrics")
public class Metrics {

    protected List<QualityMetric> metrics;

}
