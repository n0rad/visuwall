/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
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

package com.jsmadja.wall.projectwall;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class FileClientHandlerBuilder {

    private Map<String, List<String>> headers;

    private Map<URL, URL> urlMap;


    public static FileClientHandlerBuilder newFileClientHandler() {
        return new FileClientHandlerBuilder();
    }

    private FileClientHandlerBuilder() {
        headers = new HashMap<String, List<String>>();
        urlMap = new HashMap<URL, URL>();
    }

    public FileClientHandlerBuilder withHeader(String key, String value) {
        List<String> values = headers.get(key);
        if (values == null) {
            values = new ArrayList<String>();
            headers.put(key, values);
        }
        values.add(value);
        return this;
    }

    public FileClientHandlerBuilder withFile(URL urlWeb, URL urlFile) {
        urlMap.put(urlWeb, urlFile);
        return this;
    }

    public FileClientHandlerBuilder withFile(String urlWeb, String urlFile) {
        try {
            URL url = getClass().getClassLoader().getResource(urlFile);
            if (url == null) {
                throw new RuntimeException(urlFile+" not found in classpath");
            }
            urlMap.put(new URL(urlWeb), url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Url malformed", e);
        }
        return this;
    }

    public ClientHandler create() {
        return new URLConnectionClientHandler(new CustomHttpURLConnectionFactory(headers, urlMap));
    }

    class CustomHttpURLConnectionFactory implements HttpURLConnectionFactory {

        private Map<String, List<String>> headers = new HashMap<String, List<String>>();

        private Map<URL, URL> urlMap = new HashMap<URL, URL>();

        CustomHttpURLConnectionFactory(Map<String, List<String>> headers, Map<URL, URL> urlMap) {
            this.headers = headers;
            this.urlMap = urlMap;
        }

        @Override
        public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
            if (urlMap.containsKey(url)) {
                url = urlMap.get(url);
            }
            return new HttpURLConnectionFromFile(url, headers);
        }
    }

    static class HttpURLConnectionFromFile extends HttpURLConnection {

        private Map<String, List<String>> headers = new HashMap<String, List<String>>();

        HttpURLConnectionFromFile(URL url, Map<String, List<String>> headers) {
            super(url);
            this.headers = headers;
        }

        @Override
        public int getResponseCode() throws IOException {
            return HTTP_OK;
        }

        @Override
        public Map<String, List<String>> getHeaderFields() {
            return headers;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return url.openStream();
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {
        }

    }
}
