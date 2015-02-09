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

package fr.norad.visuwall.providers.common;

import static com.google.common.io.Closeables.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

public class DocumentLoader {

    public Document loadFromUrl(String strUrl) throws DocumentNotLoadedException {
        Preconditions.checkNotNull(strUrl, "strUrl is mandatory");
        InputStream stream = null;
        try {
            URL url = new URL(strUrl);
            stream = url.openStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            return documentBuilder.parse(stream);
        } catch (MalformedURLException e) {
            throw new DocumentNotLoadedException("Can't load document from url: " + strUrl, e);
        } catch (IOException e) {
            throw new DocumentNotLoadedException("Can't load document from url: " + strUrl, e);
        } catch (SAXException e) {
            throw new DocumentNotLoadedException("Can't load document from url: " + strUrl, e);
        } catch (ParserConfigurationException e) {
            throw new DocumentNotLoadedException("Can't load document from url: " + strUrl, e);
        } finally {
            closeQuietly(stream);
        }
    }

    public Document loadFromContent(String pomContent) throws DocumentNotLoadedException {
        InputStream stream = null;
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            stream = new ByteArrayInputStream(pomContent.getBytes());
            return documentBuilder.parse(stream);
        } catch (ParserConfigurationException e) {
            throw new DocumentNotLoadedException("Can't load document : " + pomContent, e);
        } catch (SAXException e) {
            throw new DocumentNotLoadedException("Can't load document : " + pomContent, e);
        } catch (IOException e) {
            throw new DocumentNotLoadedException("Can't load document : " + pomContent, e);
        } finally {
            closeQuietly(stream);
        }
    }
}
