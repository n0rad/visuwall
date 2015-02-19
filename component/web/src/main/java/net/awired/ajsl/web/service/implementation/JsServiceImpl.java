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
package net.awired.ajsl.web.service.implementation;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.awired.ajsl.core.io.FileFlatTree;
import net.awired.ajsl.core.io.PathFileFilter;
import net.awired.ajsl.core.io.PathFileOrder;
import net.awired.ajsl.core.lang.StringUtils;
import net.awired.ajsl.web.domain.JsServiceMap;
import net.awired.ajsl.web.service.interfaces.JsService;
import net.awired.ajsl.web.service.interfaces.JsonService;
import org.apache.commons.lang.NotImplementedException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObjectUtils;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

@Service
public class JsServiceImpl implements JsService {

    private static final Logger LOG = LoggerFactory.getLogger(JsServiceImpl.class);

    private static final String JS_DIRECTORY = "js";

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JsonService jsonService;

    @Override
    public String getJsLinks(String prefix) throws Exception {
        return getJsLinks(prefix, getJsFiles());
    }

    @Override
    public String getJsLinks(String prefix, Map<File, String> jsMap) throws Exception {
        StringBuilder result = new StringBuilder();
        for (File file : jsMap.keySet()) {
            // type="text/javascript"
            result.append("<script src=\"");
            result.append(prefix);
            result.append(jsMap.get(file));
            result.append("\"></script>\n");
        }
        return result.toString();
    }

    @Override
    public JsServiceMap buildServiceMapFromJsMap(Map<File, String> jsMap) {
        JsServiceMap jsServiceMap = new JsServiceMap();
        Pattern pattern = Pattern.compile("[\\.]*([A-Za-z0-9\\-]*)\\.js");
        //		Pattern className = Pattern.compile("\\/([A-Za-z\\.]*)\\.js");
        Pattern className = Pattern.compile("([^/]*)\\.js");
        for (File file : jsMap.keySet()) {
            String webPath = jsMap.get(file);
            Matcher m = pattern.matcher(webPath);
            m.find();
            String beanName = m.group(1);

            Matcher classNameMatcher = className.matcher(webPath);
            classNameMatcher.find();
            String res = classNameMatcher.group(1);
            jsServiceMap.addService(beanName, res);
        }
        return jsServiceMap;
    }

    @Override
    public String buildJsServiceMapString(JsServiceMap jsServiceMap) {
        StringWriter writer = new StringWriter();
        jsonService.serialize(jsServiceMap, writer);
        return writer.toString();
    }

    @Override
    public Map<String, List<String>> getServicesMethods(Map<File, String> jsFiles,
            Map<String, List<String>> serviceMap) throws Exception {
        Global global = new Global();
        Context cx = ContextFactory.getGlobal().enter();
        global.init(cx);
        cx.setOptimizationLevel(-1);
        cx.setLanguageVersion(Context.VERSION_1_5);

        Scriptable scope = cx.initStandardObjects(global);
        cx.evaluateReader(scope,
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("env.rhino.1.2.js")),
                "env.rhino.js", 1, null);

        // import js files to interpreter
        for (File jsFile : jsFiles.keySet()) {
            FileReader reader = new FileReader(jsFile);
            cx.evaluateReader(scope, reader, jsFiles.get(jsFile), 1, null);
        }

        Map<String, List<String>> result = new HashMap<String, List<String>>();

        for (String serviceName : serviceMap.keySet()) {
            List<String> value = new ArrayList<String>();
            result.put(serviceName, value);

            for (String className : serviceMap.get(serviceName)) {
                Object o1 = cx.evaluateReader(scope, new StringReader("window." + className), "classReader", 1, null);
                if (o1 instanceof Undefined) {
                    LOG.warn("js class file " + className + " found but class not found in context");
                    continue;
                }

                String[] stringTable = ScriptableObjectUtils.getMethodArrayFromJsObject(o1);

                for (String element : stringTable) {
                    if (element.charAt(0) != '_') {
                        value.add(element);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Map<File, String> getJsFiles() throws Exception {
        Resource res = context.getResource(JS_DIRECTORY);
        File file = res.getFile();

        PathFileFilter pathfilter = new PathFileFilter();
        pathfilter.setGlobalFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() && !pathname.getName().equals(".svn")) {
                    return true;
                }
                if (pathname.getName().endsWith(".js")) {
                    return true;
                }
                return false;
            }
        });

        PathFileOrder pathOrder = new PathFileOrder();
        pathOrder.setGlobalOrder(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long o1l = o1.getName().length();
                long o2l = o2.getName().length();
                if (o1l == o2l) {
                    return 0;
                }
                if (o1l < o2l) {
                    return -1;
                }
                return 1;
            }
        });

        pathOrder.addFileOrder("js", new Comparator<File>() {
            List<String> order = new ArrayList<String>();

            {
                order.add("jquery");
                order.add("ajsl");
                order.add("visuwall");
            }

            @Override
            public int compare(File o1, File o2) {
                int o1pos = order.indexOf(o1.getName());
                int o2pos = order.indexOf(o2.getName());

                if (o1pos == -1 && o2pos != -1) {
                    return 1;
                }
                if (o1pos != -1 && o2pos == -1) {
                    return -1;
                }
                if (o1pos != -1 && o2pos != -1) {
                    if (o1pos > o2pos) {
                        return 1;
                    }
                    return -1;
                }

                return o1.compareTo(o2);

            }
        });

        Map<File, String> jsFiles = FileFlatTree.getTree(file, JS_DIRECTORY, pathfilter, pathOrder);
        return jsFiles;
    }

    @Override
    public String getJsData() throws Exception {
        		Map<File, String> jsFiles = this.getJsFiles();
        
        		StringBuilder builder = new StringBuilder();
        		for (File file2 : jsFiles.keySet()) {
        			FileReader reader = new FileReader(file2);
        			StringUtils.inputStreamReaderToStringBuilder(reader, builder);
        		}
        
        		Reader reader = new StringReader(builder.toString());
        		StringWriter writer = new StringWriter();
        		ErrorReporter reporter = new ToolErrorReporter(true);
        
        		JavaScriptCompressor compressor = new JavaScriptCompressor(reader,
        				reporter);
        		compressor.compress(writer, -1, true, true, false, false);
        		return writer.toString();
    }

}
