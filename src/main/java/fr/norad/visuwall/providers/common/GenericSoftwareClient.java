/**
 *
 *     Copyright (C) norad.fr
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
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import com.google.common.io.ByteStreams;

public class GenericSoftwareClient {

    private static final HashMap<String, String> EMPTY_MAP = new HashMap<String, String>();

//    private Client client;

    private static final ResourceCache CACHE = new ResourceCache();

    public GenericSoftwareClient() {
//        ClientConfig clientConfig = new DefaultClientConfig();
//        client = Client.create(clientConfig);
    }

    public GenericSoftwareClient(String login, String password) {
        checkNotNull(login, "login is mandatory");
        checkNotNull(password, "password is mandatory");
//        ClientConfig clientConfig = new DefaultClientConfig();
//        client = Client.create(clientConfig);
//        client.addFilter(new HTTPBasicAuthFilter(login, password));
    }

    public <T> T resource(String url, Class<T> clazz) throws ResourceNotFoundException {
        return resource(url, clazz, EMPTY_MAP);
    }

    protected <T> T resource(String url, Class<T> clazz, Map<String, String> headers) throws ResourceNotFoundException {
        checkUrl(url);
        checkClass(clazz);
//        try {
//            T object = CACHE.get(url, clazz);
//            if (object == null) {
//                WebResource resource = client.resource(url);
//                Builder builder = null;
//                for (Map.Entry<String, String> header : headers.entrySet()) {
//                    builder = resource.header(header.getKey(), header.getValue());
//                }
//                if (builder == null) {
//                    object = resource.get(clazz);
//                } else {
//                    object = builder.get(clazz);
//                }
//                CACHE.put(object, url, clazz);
//            }
//            return object;
//        } catch (Throwable t) {
//            String errorMessage = "Can't get resource of type " + clazz.getName() + " at '" + url + "'";
//            throw new ResourceNotFoundException(errorMessage, t);
//        }
        return null;
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
//        checkUrl(url);
//        checkClass(clazz);
//        checkMediaType(mediaType);
//        try {
//            T object = CACHE.get(url, clazz, mediaType);
//            if (object == null) {
//                WebResource resource = client.resource(url);
//                object = resource.accept(mediaType).get(clazz);
//                CACHE.put(object, url, clazz, mediaType);
//            }
//            return object;
//        } catch (UniformInterfaceException e) {
//            throw new ResourceNotFoundException(e);
//        } catch (ClientHandlerException e) {
//            throw new ResourceNotFoundException(e);
//        }
        return null;
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

    public String download(URL apiUrl) throws ResourceNotFoundException {
        return resource(apiUrl.toString(), String.class, MediaType.TEXT_PLAIN_TYPE);
    }

}
