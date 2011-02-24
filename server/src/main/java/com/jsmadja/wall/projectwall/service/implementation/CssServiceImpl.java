package com.jsmadja.wall.projectwall.service.implementation;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import net.awired.ajsl.core.io.FileFlatTree;
import net.awired.ajsl.core.io.PathFileFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.jsmadja.wall.projectwall.service.interfaces.CssService;

@Service
public class CssServiceImpl implements CssService {

    private static final String CSS_DIRECTORY = "css";

    @Autowired
    private ApplicationContext  context;

    @Override
    public Map<File, String> getCssFiles() throws Exception {
        Map<File, String> cssFiles = new HashMap<File, String>();

        Resource res = context.getResource(CSS_DIRECTORY);
        File file = res.getFile();

        PathFileFilter pathfilter = new PathFileFilter();
        pathfilter.setGlobalFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() && !pathname.getName().equals(".svn")) {
                    return true;
                }
                if (pathname.getName().endsWith(".css")) {
                    return true;
                }
                return false;
            }
        });

        cssFiles = FileFlatTree.getTree(file, CSS_DIRECTORY, pathfilter, null);

        return cssFiles;
    }

    @Override
    public String getCssLinks(String prefix) throws Exception {
        StringBuilder result = new StringBuilder();

        Map<File, String> cssFiles = getCssFiles();

        for (File file : cssFiles.keySet()) {
            result.append("<link rel=\"stylesheet\" href=\"");
            result.append(prefix);
            result.append(cssFiles.get(file));
            result.append("\" type=\"text/css\">\n");
        }
        return result.toString();
    }

}
