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
package fr.norad.visuwall.web.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.base.Strings;
import fr.norad.visuwall.PluginServiceInterface;
import fr.norad.visuwall.domain.PluginInfo;
import fr.norad.visuwall.domain.SoftwareInfo;

//@Controller
//@RequestMapping("/plugin")
public class PluginController {

    @Autowired
    private PluginServiceInterface pluginService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<PluginInfo> getPluginList() {
        return pluginService.getPluginsInfo();
    }

    @RequestMapping(value = "getSoftwareInfo", method = RequestMethod.GET)
    public @ResponseBody
    SoftwareInfo getSoftwareInfo(@RequestParam String url, String login, String password)
            throws MalformedURLException {
        Map<String, String> properties = new HashMap<String, String>();
        if (!Strings.isNullOrEmpty(login)) {
            properties.put("login", login);
        }
        if (!Strings.isNullOrEmpty(password)) {
            properties.put("password", password);
        }
        SoftwareInfo softwareInfo = pluginService.getSoftwareInfoFromUrl(new URL(url), properties);
        return softwareInfo;
    }

}
