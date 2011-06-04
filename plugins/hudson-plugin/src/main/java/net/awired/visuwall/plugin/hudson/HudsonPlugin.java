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

package net.awired.visuwall.plugin.hudson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.domain.PluginInfo;
import net.awired.visuwall.api.domain.SoftwareInfo;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class HudsonPlugin implements VisuwallPlugin {

    @Override
    public ConnectionPlugin getConnection(String url, Properties info) {
        HudsonConnectionPlugin hudsonConnectionPlugin = new HudsonConnectionPlugin();
        hudsonConnectionPlugin.connect(url);
        return hudsonConnectionPlugin;
    }

    @Override
    public PluginInfo getInfo() {
    	PluginInfo pluginInfo = new PluginInfo();
    	pluginInfo.setName("Hudson plugin");
    	pluginInfo.setVersion(1.0f);
    	pluginInfo.setClassName(this.getClass().getName());
    	return pluginInfo;
    }
    
    @Override
    public SoftwareInfo isManageable(URL url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        SoftwareInfo softwareInfo = new SoftwareInfo();
        softwareInfo.setPluginInfo(getInfo());
        InputStream stream = null;
        try {
            url = new URL(url.toString() + "/api/");
            stream = url.openStream();
            byte[] content = ByteStreams.toByteArray(stream);
            String xml = new String(content);
//            return xml.contains("Remote API [Hudson]");
            return softwareInfo;
        } catch (IOException e) {
        	return null;
        } finally {
            Closeables.closeQuietly(stream);
        }
    }
}