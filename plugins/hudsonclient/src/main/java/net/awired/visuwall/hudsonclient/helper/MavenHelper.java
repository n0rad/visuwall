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

package net.awired.visuwall.hudsonclient.helper;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public class MavenHelper {

	private static final Logger LOG = LoggerFactory.getLogger(MavenHelper.class);

	public static boolean isMavenProject(String projectUrl) {
		checkProjectUrl(projectUrl);
		try {
			byte[] bytes = ByteStreams.toByteArray(new URL(projectUrl).openStream());
			String content = new String(bytes);
			return isMaven(content);
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Can't define if " + projectUrl + " is a maven project, cause: " + e.getCause());
			}
			return true;
		}
	}

	public static boolean isNotMavenProject(String projectUrl) {
		checkProjectUrl(projectUrl);
		return !isMavenProject(projectUrl);
	}

	private static boolean isMaven(String content) {
		content = content.toLowerCase();
		return content.startsWith("<mavenmoduleset>");
	}

	private static void checkProjectUrl(String projectUrl) {
		Preconditions.checkNotNull(projectUrl, "projectUrl is mandatory");
	}

}
