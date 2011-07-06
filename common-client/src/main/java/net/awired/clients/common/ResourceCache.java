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

package net.awired.clients.common;

import java.io.Serializable;
import javax.ws.rs.core.MediaType;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ResourceCache {

    private final Cache cache;

    private static final boolean ENABLE_STATISTICS = false;

    public ResourceCache() {
        CacheManager cacheManager = CacheManager.create();
        cache = cacheManager.getCache("resource_cache");
        cache.setStatisticsEnabled(ENABLE_STATISTICS);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String url, Class<T> clazz) {
        Serializable cacheKey = key(url, clazz);
        if (cache.isKeyInCache(cacheKey)) {
            Element element = cache.get(cacheKey);
            if (element != null) {
                Object objectValue = element.getObjectValue();
                return (T) objectValue;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String url, Class<T> clazz, MediaType mediaType) {
        Serializable cacheKey = key(url, clazz, mediaType);
        if (cache.isKeyInCache(cacheKey)) {
            Element element = cache.get(cacheKey);
            if (element != null) {
                Object objectValue = element.getObjectValue();
                return (T) objectValue;
            }
        }
        return null;
    }

    public <T> void put(T object, String url, Class<T> clazz) {
        Serializable cacheKey = key(url, clazz);
        cache.put(new Element(cacheKey, object));
    }

    public <T> void put(T object, String url, Class<T> clazz, MediaType mediaType) {
        Serializable cacheKey = key(url, clazz, mediaType);
        cache.put(new Element(cacheKey, object));
    }

    private <T> Serializable key(String url, Class<T> clazz) {
        return url + clazz.getName();
    }

    private <T> Serializable key(String url, Class<T> clazz, MediaType mediaType) {
        return key(url, clazz) + mediaType.toString();
    }

}
