package net.awired.visuwall.server.service.implementation;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.ajsl.core.io.FileFlatTree;
import net.awired.ajsl.core.io.PathFileFilter;
import net.awired.ajsl.core.io.PathFileOrder;
import net.awired.visuwall.server.service.interfaces.JsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


@Service
public class JsServiceImpl implements JsService {

    private static final String JS_DIRECTORY = "js";

    @Autowired
    private ApplicationContext  context;

    @Override
    public String getJsLinks(String prefix) throws Exception {
        StringBuilder result = new StringBuilder();

        Map<File, String> jsFiles = getJsFiles();

        for (File file : jsFiles.keySet()) {
            // type="text/javascript" 
            result.append("<script src=\"");
            result.append(prefix);
            result.append(jsFiles.get(file));
            result.append("\"></script>\n");
        }

        return result.toString();
    }

    @Override
    public Map<File, String> getJsFiles() throws Exception {
        Map<File, String> jsFiles = new HashMap<File, String>();

        Resource res = context.getResource(JS_DIRECTORY);
        File file = res.getFile();

        PathFileFilter pathfilter = new PathFileFilter();
        pathfilter.setGlobalFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() && !pathname.getName().equals(".svn")
                        && !pathname.getName().equals("ui-full")) {
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
                order.add("jwall");
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

        jsFiles = FileFlatTree.getTree(file, JS_DIRECTORY, pathfilter, pathOrder);

        return jsFiles;
    }

}
