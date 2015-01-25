/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.providers.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Tests {

    public static Collection<Object[]> createUrlInstanceParametersFromProperty(final String instanceProperty) {
        List<Object[]> parameters = new ArrayList<Object[]>();
        String instancePropertyValue = System.getProperty(instanceProperty);
        if (instancePropertyValue != null) {
            addInstances(parameters, instancePropertyValue);
        } else {
            System.out.println("Can't find property '" + instanceProperty + "'");
        }
        return parameters;
    }

    private static void addInstances(List<Object[]> parameters, String instancePropertyValue) {
        String[] instances = instancePropertyValue.split(";");
        for (String instance : instances) {
            parameters.add(new Object[] { instance });
        }
    }

}
