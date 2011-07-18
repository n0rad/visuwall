/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.clients.hudson.domain;

public class HudsonTest {

    private String[] suffixes = { "IntegrationTest", "ITest", "IT" };

    private String className;
    private String status;

    public HudsonTest() {
    }

    public HudsonTest(String className, String status) {
        this.className = className;
        this.status = status;
    }

    @Override
    public String toString() {
        return className + ":" + status;
    }

    public boolean isIntegrationTest() {
        if (className.contains(".it."))
            return true;
        for (String suffix : suffixes)
            if (className.endsWith(suffix))
                return true;
        return false;
    }

    public boolean isUnitTest() {
        return !isIntegrationTest();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
