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
package com.paiondata.aristotle.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.model.vo.GraphVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up a Caffeine cache. This class defines a bean for the cache and configures it based
 * on the provided properties. The cache can be configured either by specifying a maximum size in bytes or by
 * specifying the number of subgraphs to cache.
 */
@Configuration
public class CaffeineConfig {

    /**
     * Indicates whether caching is enabled or disabled. Default is true.
     */
    @Value("${spring.read-cache.enabled:true}")
    private boolean cacheEnabled;

    /**
     * The maximum size of the cache in bytes. Can be specified with units like MB, GB, or KB.
     */
    @Value("${spring.read-cache.size:#{null}}")
    private String cacheSize;

    /**
     * The maximum number of subgraphs to cache.
     */
    @Value("${spring.read-cache.num-subgraphs:#{null}}")
    private Integer numSubgraphs;

    /**
     * Creates and returns a Caffeine cache bean. The cache configuration depends on the values of `cacheSize` and
     * `numSubgraphs`. If neither is specified, an exception is thrown.
     * If both are specified, an exception is also thrown.
     *
     * @return a Caffeine cache bean
     *
     * @throws IllegalArgumentException if both `cacheSize` and `numSubgraphs` are specified, or if neither is specified
     */
    @Bean
    public Cache<String, GraphVO> graphCache() {
        if (!cacheEnabled) {
            return Caffeine.newBuilder().build();
        }

        if (cacheSize != null && numSubgraphs != null) {
            throw new IllegalArgumentException("Cannot specify both size and num-subgraphs");
        }

        if (cacheSize != null) {
            // use size to configure cache
            final long maxSizeInBytes = parseSize(cacheSize);
            return Caffeine.newBuilder()
                    .maximumWeight(maxSizeInBytes)
                    .weigher((key, value) -> calculateSizeInBytes((GraphVO) value))
                    .expireAfterWrite(2, TimeUnit.MINUTES)
                    .build();
        } else if (numSubgraphs != null) {
            // use num-subgraphs to configure cache
            return Caffeine.newBuilder()
                    .maximumSize(numSubgraphs)
                    .expireAfterWrite(2, TimeUnit.MINUTES)
                    .build();
        } else {
            throw new IllegalArgumentException("Either size or num-subgraphs must be specified");
        }
    }

    /**
     * Parses the cache size string and converts it to bytes.
     *
     * @param size the cache size string (e.g., "10MB", "5GB", "2KB")
     *
     * @return the cache size in bytes
     *
     * @throws IllegalArgumentException if the size unit is invalid
     */
    private long parseSize(final String size) {
        final long sizeInBytes;
        if (size.endsWith(Constants.STORAGE_UNIT_MB)) {
            sizeInBytes = Long.parseLong(size.replace(Constants.STORAGE_UNIT_MB, "")) * 1024 * 1024;
        } else if (size.endsWith(Constants.STORAGE_UNIT_GB)) {
            sizeInBytes = Long.parseLong(size.replace(Constants.STORAGE_UNIT_GB, "")) * 1024 * 1024 * 1024;
        } else if (size.endsWith(Constants.STORAGE_UNIT_KB)) {
            sizeInBytes = Long.parseLong(size.replace(Constants.STORAGE_UNIT_KB, "")) * 1024;
        } else {
            throw new IllegalArgumentException("Invalid cache size unit: " + size);
        }
        return sizeInBytes;
    }

    /**
     * Calculates the size of a {@link GraphVO} object in bytes.
     *
     * @param graphVO the {@link GraphVO} object
     *
     * @return the size of the object in bytes
     */
    private int calculateSizeInBytes(final GraphVO graphVO) {
        return graphVO.toString().getBytes().length;
    }
}
