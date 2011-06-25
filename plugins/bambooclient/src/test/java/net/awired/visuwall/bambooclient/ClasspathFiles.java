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

package net.awired.visuwall.bambooclient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class ClasspathFiles {

    private ClasspathFiles() {
    }

    public static String getUrlFile(String fileName) {
        try {
            Class<?> clazz = ClasspathFiles.class;
            ClassLoader classLoader = clazz.getClassLoader();
            URL resource = classLoader.getResource(fileName);
            URI uri = resource.toURI();
            String pomUrl = "file://" + uri.getPath();
            return pomUrl;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getUrl(String fileName) {
        try {
            String strUrl = getUrlFile(fileName);
            return new URL(strUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object load(String fileName, Class<?> clazz) {
        try {
            String file = ClasspathFiles.getUrlFile(fileName);
            URL url = new URL(file);
            JAXBContext newInstance = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = newInstance.createUnmarshaller();
            return unmarshaller.unmarshal(url);
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }

}
