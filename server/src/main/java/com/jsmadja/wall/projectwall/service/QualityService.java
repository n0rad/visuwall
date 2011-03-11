package com.jsmadja.wall.projectwall.service;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.QualityResult;

public interface QualityService extends Service {

    void populateQuality(Project project, QualityResult quality, String ... metrics) throws ProjectNotFoundException;

}