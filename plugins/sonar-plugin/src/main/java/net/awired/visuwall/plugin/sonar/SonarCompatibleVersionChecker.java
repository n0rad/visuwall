/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package fr.norad.visuwall.plugin.sonar;

public class SonarCompatibleVersionChecker {

    private int minimumCompatibleVersionIntPart;
    private int minimumCompatibleVersionDecimalPart;

    public SonarCompatibleVersionChecker(Double sonarMinimumCompatibleVersion) {
        minimumCompatibleVersionIntPart = sonarMinimumCompatibleVersion.intValue();
        minimumCompatibleVersionDecimalPart = getDecimalPart(sonarMinimumCompatibleVersion);
    }

    public boolean versionIsCompatible(String version) {
        try {
            double versionAsFloat = Double.valueOf(version);
            return versionIsCompatible(versionAsFloat);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean versionIsCompatible(Double version) {
        int intPart = version.intValue();

        if (minimumCompatibleVersionIntPart < intPart) {
            return true;
        }

        if (minimumCompatibleVersionIntPart >= intPart) {
            int decimalPart = getDecimalPart(version);
            if (minimumCompatibleVersionDecimalPart > decimalPart) {
                return false;
            }
        }

        return true;
    }

    private Integer getDecimalPart(Double value) {
        if (!value.toString().contains(".")) {
            return 0;
        }
        return Integer.valueOf(value.toString().split("\\.")[1]);
    }

}
