/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall;

import net.awired.visuwall.core.domain.Software;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;
import net.awired.visuwall.plugin.sonar.SonarPlugin;

public interface IntegrationTestData {

    String BAMBOO_URL = "http://bamboo.visuwall.awired.net";
    String HUDSON_URL = "http://ci.visuwall.awired.net";
    String HUDSON_ID = "HUDSON_ID";
    String SONAR_URL = "http://sonar.awired.net";
    String STRUTS_ARTIFACT_ID = "org.apache.struts:struts-core";

    SoftwareAccess HUDSON_ACCESS = new SoftwareAccess(HudsonPlugin.class, HUDSON_URL, "");
    SoftwareAccess SONAR_ACCESS = new SoftwareAccess(SonarPlugin.class, SONAR_URL, "");

}
