package net.awired.visuwall.api.service;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityResult;

public interface QualityService extends Service {

    QualityResult populateQuality(ProjectId projectId, String ... metrics);

    boolean contains(ProjectId projectId);

}