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

package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.plugin.sonar.resource.Properties;

import org.junit.Test;

public class SonarVersionExtractorTest {

	@Test
	public void should_extract_version_in_jenkins_page() throws IOException {
		Class<? extends SonarVersionExtractorTest> clazz = this.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("sonar_version_page.xml");
		SonarVersionExtractor sve = new SonarVersionExtractor(loadProperties(stream));

		String version = sve.version();
		assertEquals("2.8", version);
	}

	private Properties loadProperties(InputStream is) {
		try {
			JAXBContext newInstance = JAXBContext.newInstance(Properties.class);
			Unmarshaller unmarshaller = newInstance.createUnmarshaller();
			return (Properties) unmarshaller.unmarshal(is);
		} catch (Exception t) {
			throw new RuntimeException(t);
		}
	}
}
