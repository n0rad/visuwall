package net.awired.visuwall.api.plugin;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityResult;

public interface QualityPlugin extends Plugin {

    QualityResult populateQuality(ProjectId projectId, String ... metrics);

    boolean contains(ProjectId projectId);

}