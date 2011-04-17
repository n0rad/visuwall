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

package net.awired.visuwall.plugin.sonar;

<<<<<<< HEAD:sonar-plugin/src/test/java/net/awired/visuwall/plugin/sonar/it/service/SonarServiceExceptionIT.java
import static net.awired.visuwall.plugin.sonar.it.IntegrationTestData.SONAR_URL;

import net.awired.visuwall.plugin.sonar.SonarConnectionPlugin;
=======
import static net.awired.visuwall.IntegrationTestData.SONAR_URL;
>>>>>>> d1e18665bbedc8f7ed1fe0a53acb4e91bb188f97:it/src/it/java/net/awired/visuwall/plugin/sonar/SonarPluginExceptionIT.java

import org.junit.BeforeClass;
import org.junit.Test;


public class SonarPluginExceptionIT {

<<<<<<< HEAD:sonar-plugin/src/test/java/net/awired/visuwall/plugin/sonar/it/service/SonarServiceExceptionIT.java
    private static SonarConnectionPlugin sonarService;

    @BeforeClass
    public static void init() {
        sonarService = new SonarConnectionPlugin();
        sonarService.setUrl(SONAR_URL);
        sonarService.init();
=======
    private static SonarPlugin sonarPlugin;

    @BeforeClass
    public static void init() {
        sonarPlugin = new SonarPlugin();
        sonarPlugin.setUrl(SONAR_URL);
        sonarPlugin.init();
>>>>>>> d1e18665bbedc8f7ed1fe0a53acb4e91bb188f97:it/src/it/java/net/awired/visuwall/plugin/sonar/SonarPluginExceptionIT.java
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url_passed() {
        new SonarConnectionPlugin().init();
    }

}
