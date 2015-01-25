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

import java.util.Map;

public class GenericSoftwareClientFactory {

    public GenericSoftwareClient createClient(Map<String, String> properties) {
        if (properties != null && properties.containsKey("login") && properties.containsKey("password")) {
            String login = properties.get("login");
            String password = properties.get("password");
            return new GenericSoftwareClient(login, password);
        }
        return new GenericSoftwareClient();
    }

}
