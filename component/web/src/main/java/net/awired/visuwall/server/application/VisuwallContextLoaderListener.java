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

package net.awired.visuwall.server.application;

import javax.servlet.ServletContextEvent;

import net.awired.visuwall.core.application.common.ApplicationHelper;

import org.fusesource.jansi.AnsiConsole;
import org.springframework.web.context.ContextLoaderListener;

public class VisuwallContextLoaderListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		AnsiConsole.systemInstall();
		String home = ApplicationHelper.findHomeDir();
		System.setProperty(ApplicationHelper.HOME_KEY, home);

		ApplicationHelper.changeLogLvl();
		super.contextInitialized(event);
	}

}
