package net.awired.visuwall.api.service;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.quality.QualityResult;

public interface QualityService extends Service {

    void populateQuality(Project project, QualityResult quality, String ... metrics);

}