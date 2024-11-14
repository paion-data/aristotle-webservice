/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.common.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.vo.GraphVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A utility class for managing a Caffeine cache. This class provides methods to operate entries in the cache.
 * The cache is enabled or disabled based on the configuration property `spring.read-cache.enabled`.
 */
@Component
public class CaffeineCacheUtil {

    /**
     * Logger instance for logging messages.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CaffeineCacheUtil.class);

    /**
     * The Caffeine cache used to store {@link GraphVO} objects.
     */
    @Autowired
    private Cache<String, GraphVO> graphCache;

    /**
     * Indicates whether caching is enabled or disabled.
     */
    @Value("${spring.read-cache.enabled}")
    private boolean cacheEnabled;

    /**
     * Retrieves a cached {@link GraphVO} object from the cache using the provided key.
     *
     * @param key the key used to identify the cached object
     *
     * @return an {@link Optional} containing the cached {@link GraphVO} object, or an empty {@link Optional}
     */
    public Optional<GraphVO> getCache(final String key) {
        if (cacheEnabled) {
            final GraphVO cachedGraphVO = graphCache.getIfPresent(key);
            if (cachedGraphVO != null) {
                LOG.info(Message.RETURNING_CACHED_GRAPHVO_FOR_UUID, key);
                return Optional.of(cachedGraphVO);
            }
        }
        return Optional.empty();
    }

    /**
     * Adds a {@link GraphVO} object to the cache with the provided key.
     *
     * @param key the key used to identify the cached object
     * @param graphVO the {@link GraphVO} object to be cached
     */
    public void setCache(final String key, final GraphVO graphVO) {
        if (cacheEnabled) {
            LOG.info(Message.CACHING_GRAPHVO_FOR_UUID, key);
            graphCache.put(key, graphVO);
        }
    }

    /**
     * Deletes all cached entries whose keys start with the provided UUID.
     *
     * @param uuid the UUID prefix used to filter the keys to be deleted
     */
    public void deleteCache(final String uuid) {
        if (cacheEnabled) {
            // get all keys that start with the uuid
            final Set<String> keysToRemove = graphCache.asMap().keySet().stream()
                    .filter(key -> key.startsWith(uuid + "_"))
                    .collect(Collectors.toSet());

            keysToRemove.forEach(key -> {
                LOG.info(Message.DELETING_CACHED_GRAPHVO_FOR_UUID, key);
                graphCache.invalidate(key);
            });
        }
    }
}
