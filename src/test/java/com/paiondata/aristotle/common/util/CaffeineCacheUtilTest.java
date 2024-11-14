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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.github.benmanes.caffeine.cache.Cache;
import com.paiondata.aristotle.common.base.TestConstants;
import com.paiondata.aristotle.model.vo.GraphVO;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the CaffeineCacheUtil class.
 */
@ExtendWith(MockitoExtension.class)
public class CaffeineCacheUtilTest {

    /**
     * Constant representing the cache enabled field name.
     */
    private static final String CACHE_ENABLED = "cacheEnabled";

    /**
     * Mocked Caffeine cache for testing.
     */
    @Mock
    private Cache<String, GraphVO> graphCache;

    /**
     * The class under test, injected with mocks.
     */
    @InjectMocks
    private CaffeineCacheUtil caffeineCacheUtil;

    /**
     * Sets up the test environment by enabling caching.
     */
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(caffeineCacheUtil, CACHE_ENABLED, true); // Enable caching for tests
    }

    /**
     * Tests the behavior of getting a cached item when caching is enabled and the key exists.
     */
    @Test
    public void testGetCacheWhenCacheEnabledAndKeyExists() {
        // Arrange
        final String key = TestConstants.TEST_KEY1;
        final GraphVO graphVO = new GraphVO();
        when(graphCache.getIfPresent(key)).thenReturn(graphVO);

        // Act
        final Optional<GraphVO> result = caffeineCacheUtil.getCache(key);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(graphVO, result.get());
    }

    /**
     * Tests the behavior of getting a cached item when caching is enabled and the key does not exist.
     */
    @Test
    public void testGetCacheWhenCacheEnabledAndKeyDoesNotExist() {
        // Arrange
        final String key = TestConstants.TEST_KEY1;
        when(graphCache.getIfPresent(key)).thenReturn(null);

        // Act
        final Optional<GraphVO> result = caffeineCacheUtil.getCache(key);

        // Assert
        assertFalse(result.isPresent());
    }

    /**
     * Tests the behavior of getting a cached item when caching is disabled.
     */
    @Test
    public void testGetCacheWhenCacheDisabled() {
        // Arrange
        ReflectionTestUtils.setField(caffeineCacheUtil, CACHE_ENABLED, false);
        final String key = TestConstants.TEST_KEY1;

        // Act
        final Optional<GraphVO> result = caffeineCacheUtil.getCache(key);

        // Assert
        assertFalse(result.isPresent());
        verify(graphCache, never()).getIfPresent(anyString());
    }

    /**
     * Tests the behavior of setting a cached item when caching is enabled.
     */
    @Test
    public void testSetCacheWhenCacheEnabled() {
        // Arrange
        final String key = TestConstants.TEST_KEY1;
        final GraphVO graphVO = new GraphVO();

        // Act
        caffeineCacheUtil.setCache(key, graphVO);

        // Assert
        verify(graphCache).put(eq(key), eq(graphVO));
    }

    /**
     * Tests the behavior of setting a cached item when caching is disabled.
     */
    @Test
    public void testSetCacheWhenCacheDisabled() {
        // Arrange
        ReflectionTestUtils.setField(caffeineCacheUtil, CACHE_ENABLED, false);
        final String key = TestConstants.TEST_KEY1;
        final GraphVO graphVO = new GraphVO();

        // Act
        caffeineCacheUtil.setCache(key, graphVO);

        // Assert
        verify(graphCache, never()).put(anyString(), any(GraphVO.class));
    }

    /**
     * Tests the behavior of deleting cached items when caching is enabled.
     */
    @Test
    public void testDeleteCacheWhenCacheEnabled() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        final Set<String> keys = Set.of(uuid + "_node1", uuid + "_node2", TestConstants.TEST_ID2);

        // Mock the asMap method to return a non-null map
        final ConcurrentMap<String, GraphVO> mockMap = new ConcurrentHashMap<>();
        mockMap.putAll(keys.stream().collect(Collectors.toMap(key -> key, key -> new GraphVO())));
        when(graphCache.asMap()).thenReturn(mockMap);

        // Act
        caffeineCacheUtil.deleteCache(uuid);

        // Assert
        verify(graphCache, times(2)).invalidate(anyString());
        verify(graphCache, never()).invalidate(TestConstants.TEST_ID2);
    }

    /**
     * Tests the behavior of deleting cached items when caching is disabled.
     */
    @Test
    public void testDeleteCacheWhenCacheDisabled() {
        // Arrange
        ReflectionTestUtils.setField(caffeineCacheUtil, CACHE_ENABLED, false);
        final String uuid = TestConstants.TEST_ID1;

        // Act
        caffeineCacheUtil.deleteCache(uuid);

        // Assert
        verify(graphCache, never()).asMap();
    }
}
