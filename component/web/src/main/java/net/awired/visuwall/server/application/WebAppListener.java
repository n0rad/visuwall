/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.awired.visuwall.server.application;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.awired.visuwall.core.application.common.ApplicationHelper;

import org.apache.karaf.main.Main;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public class WebAppListener implements ServletContextListener {

    @Autowired
    private ApplicationContext context;
	
	private Main main;

	public void contextInitialized(ServletContextEvent sce) {
		
//        try {
//        	URL res = sce.getServletContext().getResource("/WEB-INF/karaf/");
//			File file = new File(res.getFile());
//			System.out.println(">>" + file);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
		
		String home = System.getProperty(ApplicationHelper.HOME_KEY) + "/karaf";
		new File(home + "/etc").mkdirs();

		
		try {
			System.setProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
							"net.awired.visuwall.api.plugin; version=0.3.0.SNAPSHOT,"
							+ "net.awired.visuwall.api.domain; version=0.3.0.SNAPSHOT,"
							+ "net.awired.visuwall.api.exception; version=0.3.0.SNAPSHOT,"
							+ "net.awired.visuwall.api.plugin.tck; version=0.3.0.SNAPSHOT,"
							+ "net.awired.visuwall.api.plugin.capability; version=0.3.0.SNAPSHOT,"
							+ "net.awired.visuwall.api.domain.quality; version=0.3.0.SNAPSHOT");

			
			
			System.err.println("contextInitialized");
			String root = new File(sce.getServletContext().getRealPath("/") + "/WEB-INF/karaf").getAbsolutePath();
			System.err.println("Root: " + root);
			home = root;
			System.setProperty("karaf.home", root);
            System.setProperty("karaf.base", home);
            System.setProperty("karaf.data", home + "/data");
            System.setProperty("karaf.history", home + "/data/history.txt");
            System.setProperty("karaf.instances", home + "/instances");
			System.setProperty("karaf.startLocalConsole", "true");
			System.setProperty("karaf.startRemoteShell", "true");
            System.setProperty("karaf.lock", "false");
			main = new Main(new String[0]);
            main.launch();
		} catch (Exception e) {
			main = null;
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			System.err.println("contextDestroyed");
			if (main != null) {
                main.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
