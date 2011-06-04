/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.server.application;

import java.util.Properties;

import javax.servlet.ServletContext;

import net.awired.visuwall.cli.common.ApplicationHelper;
import net.awired.visuwall.cli.enumeration.LogLevelEnum;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@Component
public class VisuwallApplication implements ServletContextAware {

	private ServletContext context;
	protected String version;
	protected String home;
	protected Level logLvl;

	// @PostConstruct
	public void init() {
		try {
			home = ApplicationHelper.findHomeDir();
			version = ApplicationHelper.findVersion(context
					.getResourceAsStream("META-INF/MANIFEST.MF"));
			logLvl = findLogLevelFromSystem();

			System.out.println("######################################");
			System.out.println("version : " + version);
			System.out.println("home : " + home);
			System.out.println("######################################");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		if (logLvl != null) {
			Logger root = (Logger) LoggerFactory
					.getLogger(Logger.ROOT_LOGGER_NAME);
			System.out.println("Change log level to :" + logLvl);
			root.setLevel(logLvl);
		}
	}

	public Level findLogLevelFromSystem() {
		LogLevelEnum cliLvl = ApplicationHelper.findLogLvl();
		if (cliLvl == null) {
			return null;
		}
		switch (cliLvl) {
		case trace:
			return Level.TRACE;
		case info:
			return Level.INFO;
		case debug:
			return Level.DEBUG;
		case error:
			return Level.ERROR;
		case warn:
			return Level.WARN;
		default:
			throw new RuntimeException("log level not managed");
		}
	}

	public Properties visuwallProperties() {
		Properties prop = new Properties();
		prop.setProperty(ApplicationHelper.HOME_KEY, home);
		return prop;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.context = servletContext;
		init();
	}
}
