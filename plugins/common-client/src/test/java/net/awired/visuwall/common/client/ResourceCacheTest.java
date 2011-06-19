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

package net.awired.visuwall.common.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

public class ResourceCacheTest {

    @Test
    public void should_put_element_in_cache() {
        ResourceCache cache = new ResourceCache();
        Class<Object> clazz = Object.class;
        Object object = new Object();
        String url = "url";
        cache.put(object, url, clazz);

        assertEquals(object, cache.get(url, clazz));
    }

    @Test
    public void should_put_element_with_media_type_in_cache() {
        ResourceCache cache = new ResourceCache();
        Class<Object> clazz = Object.class;
        Object object = new Object();
        String url = "url";
        MediaType mediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
        cache.put(object, url, clazz, mediaType);

        assertEquals(object, cache.get(url, clazz, mediaType));
    }

    @Test
    public void should_not_get_element() {
        ResourceCache cache = new ResourceCache();
        assertNull(cache.get("url", ResourceCache.class));
    }

    @Test
    public void should_not_get_element_wth_media_type() {
        ResourceCache cache = new ResourceCache();
        assertNull(cache.get("url", ResourceCache.class, MediaType.TEXT_HTML_TYPE));
    }

}
