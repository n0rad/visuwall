/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
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

package net.awired.clients.common;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.w3c.dom.Document;

public class DocumentLoaderTest {

    DocumentLoader documentLoader = new DocumentLoader();

    @Test
    public void should_load_valid_xml_file() throws Exception {
        String fileUrl = ClasspathFiles.getAbsolutePathFile("pom-sample.xml");
        Document document = documentLoader.loadFromUrl(fileUrl);
        assertNotNull(document);
    }

    @Test(expected = DocumentNotLoadedException.class)
    public void should_fail_with_other_type_than_xml() throws Exception {
        String fileUrl = ClasspathFiles.getAbsolutePathFile("simple-text-file.txt");
        documentLoader.loadFromUrl(fileUrl);
    }

    @Test(expected = NullPointerException.class)
    public void should_not_accept_null_parameter() throws Exception {
        documentLoader.loadFromUrl(null);
    }

}
