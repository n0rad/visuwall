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

package com.jsmadja.wall.projectwall.it.service;

import static com.jsmadja.wall.projectwall.it.IntegrationTestData.SONAR_URL;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jsmadja.wall.projectwall.service.SonarService;

public class SonarServiceExceptionIT {

    private static SonarService sonarService;

    @BeforeClass
    public static void init() {
        sonarService = new SonarService();
        sonarService.setUrl(SONAR_URL);
        sonarService.init();
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url_passed() {
        new SonarService().init();
    }

}
