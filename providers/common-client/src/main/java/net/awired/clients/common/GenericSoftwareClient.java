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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class GenericSoftwareClient {

    private Client client;

    private static final ResourceCache CACHE = new ResourceCache();


    static {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    public GenericSoftwareClient() {
        ClientConfig clientConfig = new DefaultClientConfig();
        client = Client.create(clientConfig);
    }

    public GenericSoftwareClient(String login, String password) {
        checkNotNull(login, "login is mandatory");
        checkNotNull(password, "password is mandatory");
        ClientConfig clientConfig = new DefaultClientConfig();
        client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(login, password));
    }

    public <T> T resource(String url, Class<T> clazz) throws ResourceNotFoundException {
        checkUrl(url);
        checkClass(clazz);
        try {
            T object = CACHE.get(url, clazz);
            if (object == null) {
                WebResource resource = client.resource(url);
                object = resource.get(clazz);
                CACHE.put(object, url, clazz);
            }
            return object;
        } catch (Throwable t) {
            String errorMessage = "Can't get resource of type " + clazz.getName() + " at '" + url + "'";
            throw new ResourceNotFoundException(errorMessage, t);
        }
    }

    public <T> boolean exist(String url, Class<T> clazz) {
        try {
            resource(url, clazz);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public <T> T resource(String url, Class<T> clazz, MediaType mediaType) throws ResourceNotFoundException {
        checkUrl(url);
        checkClass(clazz);
        checkMediaType(mediaType);
        try {
            T object = CACHE.get(url, clazz, mediaType);
            if (object == null) {
                WebResource resource = client.resource(url);
                object = resource.accept(mediaType).get(clazz);
                CACHE.put(object, url, clazz, mediaType);
            }
            return object;
        } catch (UniformInterfaceException e) {
            throw new ResourceNotFoundException(e);
        } catch (ClientHandlerException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    public <T> T existingResource(String url, Class<T> clazz, MediaType mediaType) {
        try {
            return resource(url, clazz, mediaType);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("You should check your url before calling existingResource()", e);
        }
    }

    private void checkMediaType(MediaType mediaType) {
        checkNotNull(mediaType, "mediaType is mandatory");
    }

    private <T> void checkClass(Class<T> clazz) {
        checkNotNull(clazz, "clazz is mandatory");
    }

    private void checkUrl(String url) {
        checkNotNull(url, "url is mandatory");
    }

    public boolean contains(URL url, String word) {
        try {
            InputStream stream = url.openStream();
            byte[] byteArray = ByteStreams.toByteArray(stream);
            String string = new String(byteArray);
            return string.contains(word);
        } catch (Exception e) {
            return false;
        }
    }

}
