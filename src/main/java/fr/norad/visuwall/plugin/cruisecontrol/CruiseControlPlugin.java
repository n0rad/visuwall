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
package fr.norad.visuwall.plugin.cruisecontrol;

import java.net.URL;
import java.util.Map;
import fr.norad.visuwall.domain.SoftwareId;
import fr.norad.visuwall.exception.ConnectionException;
import fr.norad.visuwall.exception.SoftwareNotFoundException;
import fr.norad.visuwall.plugin.VisuwallPlugin;

public class CruiseControlPlugin implements VisuwallPlugin<CruiseControlConnection> {

    @Override
    public CruiseControlConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<CruiseControlConnection> getConnectionClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        return null;
    }

}
