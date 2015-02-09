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

package fr.norad.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class SonarVersionExtractorTest {

    SonarVersionExtractor sve = new SonarVersionExtractor();

    ClassLoader classLoader = SonarVersionExtractorTest.class.getClassLoader();

    @Test
    public void should_extract_version_from_sonar_properties() {
        InputStream stream = classLoader.getResourceAsStream("sonar_version_page.xml");

        String version = sve.propertiesVersion(loadProperties(stream));

        assertEquals("2.8", version);
    }

    @Test
    public void should_extract_version_from_sonar_welcome_page_v211() {
        URL resource = classLoader.getResource("sonar_2.11.html");

        String version = sve.welcomePageVersion(resource);

        assertEquals("2.11", version);
    }

    @Test
    public void should_extract_version_from_sonar_welcome_page_v201() {
        URL resource = classLoader.getResource("sonar_2.0.1.html");

        String version = sve.welcomePageVersion(resource);

        assertEquals("2.0.1", version);
    }

    @Test
    public void should_extract_version_from_sonar_welcome_page_v212() {
        URL resource = classLoader.getResource("sonar_2.12.html");

        String version = sve.welcomePageVersion(resource);

        assertEquals("2.12", version);
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
