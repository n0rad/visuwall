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

import static fr.norad.visuwall.application.Visuwall.VISUWALL;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

@Controller
@Path("/")
public class RootController {

    private String template;
    private final Map<String, String> properties = new HashMap<>();

    @Autowired
    private JsService jsService;

    public RootController() {
        template = loadTemplate("/index.html");
        properties.put("version", VISUWALL.getVersion());
    }

    private String loadTemplate(String tplPath) {
        InputStream tplStream = RootController.class.getResourceAsStream(tplPath);
        if (tplStream == null) {
            return null;
        }
        try {
            return IOUtils.toString(tplStream);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read template", e);
        }
    }

    @GET
    public Response get(@Context HttpServletRequest req) throws Exception {

        HashMap<String, String> hashMap = new HashMap<>(properties);
        String contextPath = req.getContextPath();
//        if (contextPathSuffix != null && !contextPathSuffix.equals("")) {
//            contextPath += contextPathSuffix;
//        }

        ObjectMapper objectMapper = new ObjectMapper();

        hashMap.put("contextPath", contextPath);
        hashMap.put("fullWebPath", req.getRequestURL().toString().replace(req.getRequestURI(), contextPath));
        hashMap.put("jsData", objectMapper.writeValueAsString(dd()));

        String response = StrSubstitutor.replace(template, hashMap);

        return Response.ok().entity(response).build();
    }

    public Map<String, Object> dd( ) throws Exception {
        Map<String, Object> jsData = new HashMap<String, Object>();
        Map<String, Object> init = new HashMap<String, Object>();
        jsData.put("init", init);

        Map<File, String> jsMap = jsService.getJsFiles();
        JsServiceMap serviceMap = jsService.buildServiceMapFromJsMap(jsMap);
        Predicate<List<String>> predicate = new Predicate<List<String>>() {
            @Override
            public boolean apply(List<String> value) {
                for (String string : value) {
                    return string.startsWith("visuwall");
                }
                return true;
            }
        };
        Map<String, List<String>> val = Maps.filterValues(serviceMap, predicate);
        //   Map<String, List<String>> serviceMethods = jsService.getServicesMethods(jsMap, val);

        //     jsData.put("jsServiceMethod", serviceMethods);
        jsData.put("jsService", val);
        init.put("wallNames", new ArrayList<>());

//        modelMap.put("jsData", jsonService.serialize(jsData));
//        modelMap.put("jsLinks", jsService.getJsLinks("res/", jsMap));
//        modelMap.put("cssLinks", cssService.getCssLinks("res/"));
//        modelMap.put("version", visuwallApplication.getVersion());
        return jsData;
    }
}
