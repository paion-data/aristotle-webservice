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
package com.paiondata.aristotle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.common.base.TestConstants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.util.CaffeineCacheUtil;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.FilterQueryGraphDTO;
import com.paiondata.aristotle.model.dto.GetRelationDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.RelationVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.impl.GraphServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Test class for the Graph Service.
 * Uses Mockito to mock dependencies and validate graph service operations.
 */
@ExtendWith(MockitoExtension.class)
public class GraphServiceTest {

    private static final String ID_1 = TestConstants.TEST_ID1;

    private static final String ID_2 = TestConstants.TEST_ID2;

    private static final String TITLE = TestConstants.TEST_TITLE1;

    private static final String DESCRIPTION = TestConstants.TEST_DESCRIPTION1;

    private static final Map<String, String> PROPERTIES = Collections.singletonMap(TestConstants.TEST_KEY1,
            TestConstants.TEST_VALUE1);

    @InjectMocks
    private GraphServiceImpl graphService;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private GraphMapper graphMapper;

    @Mock
    private NodeMapper nodeMapper;

    @Mock
    private CommonService commonService;

    @Mock
    private CaffeineCacheUtil caffeineCache;

    /**
     * Setup method to initialize mocks and test data.
     */
    @BeforeEach
    public void setup() {
    }

    /**
     * Tests that getting a GraphVO by UUID returns the correct GraphVO when the graph exists in the caffeine cache.
     */
    @Test
    public void testGetGraphVOByFilterParamsCaffeineCacheHit() {
        // Arrange
        final String id1 = ID_1;

        final GraphVO cachedGraphVO = new GraphVO(id1, TestConstants.TEST_TITLE2, TestConstants.TEST_DESCRIPTION1,
                TestConstants.TEST_TIME_01, TestConstants.TEST_TIME_02,
                Collections.emptyList(), Collections.emptyList(), TestConstants.DEFALUT_PAGE_NUMBER,
                TestConstants.DEFALUT_PAGE_SIZE, TestConstants.EXPECT_TOTAL_COUNT_01);
        when(caffeineCache.getCache(anyString())).thenReturn(Optional.of(cachedGraphVO));

        // Act
        final GraphVO result = graphService.getGraphVOByFilterParams(new FilterQueryGraphDTO(id1, PROPERTIES,
                TestConstants.DEFALUT_PAGE_NUMBER, TestConstants.DEFALUT_PAGE_SIZE));

        // Assert
        assertEquals(cachedGraphVO, result);
        verify(caffeineCache, times(1)).getCache(anyString());
        verify(graphRepository, never()).getGraphByUuid(anyString());
    }

    /**
     * Tests that getting a GraphVO by UUID returns the correct GraphVO when the graph exists in the database.
     */
    @Test
    void testGetGraphVOByFilterParamsCaffeineCacheMiss() {
        // Arrange
        final String id1 = ID_1;
        final String id2 = ID_2;
        final String name = TestConstants.TEST_NAME1;
        final String currentTime = TestConstants.TEST_TIME_01;

        when(caffeineCache.getCache(anyString())).thenReturn(Optional.empty());

        when(graphRepository.getGraphByUuid(id1)).thenReturn(Graph.builder()
                .uuid(id1)
                .title(TITLE)
                .description(DESCRIPTION)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build());

        when(nodeMapper.getRelationByGraphUuid(ID_1, PROPERTIES,
                TestConstants.DEFALUT_PAGE_NUMBER, TestConstants.DEFALUT_PAGE_SIZE))
                .thenReturn(new GetRelationDTO(
                        Collections.singletonList(RelationVO.builder()
                        .uuid(id1)
                        .name(name)
                        .createTime(currentTime)
                        .updateTime(currentTime)
                        .sourceNode(id1)
                        .targetNode(id2)
                        .build()),
                        Collections.singletonList(NodeVO.builder()
                                .uuid(id2)
                                .properties(PROPERTIES)
                                .createTime(currentTime)
                                .updateTime(currentTime)
                                .build()),
                        TestConstants.EXPECT_TOTAL_COUNT_01));

        // Act
        final GraphVO graphVO = graphService.getGraphVOByFilterParams(new FilterQueryGraphDTO(ID_1, PROPERTIES,
                TestConstants.DEFALUT_PAGE_NUMBER, TestConstants.DEFALUT_PAGE_SIZE));

        // Assert
        assertEquals(ID_1, graphVO.getUuid());
        assertEquals(TITLE, graphVO.getTitle());
        assertEquals(DESCRIPTION, graphVO.getDescription());
        assertEquals(currentTime, graphVO.getCreateTime());
        assertEquals(currentTime, graphVO.getUpdateTime());
        assertEquals(ID_1, graphVO.getRelations().get(0).getUuid());
        assertEquals(ID_2, graphVO.getNodes().get(0).getUuid());

        verify(graphRepository, times(1)).getGraphByUuid(ID_1);
        verify(nodeMapper, times(1)).getRelationByGraphUuid(ID_1, PROPERTIES,
                TestConstants.DEFALUT_PAGE_NUMBER, TestConstants.DEFALUT_PAGE_SIZE);
        verify(caffeineCache, times(1)).setCache(anyString(), any(GraphVO.class));
    }

    /**
     * Tests that getting a GraphVO by UUID returns the correct GraphVO when the graph does not exist.
     */
    @Test
    void testGetGraphVOByFilterParamsGraphNotFound() {
        // Arrange
        final String id1 = ID_1;
        when(caffeineCache.getCache(anyString())).thenReturn(Optional.empty());
        when(graphRepository.getGraphByUuid(id1)).thenReturn(null);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> graphService.getGraphVOByFilterParams(
                new FilterQueryGraphDTO(id1, PROPERTIES,
                        TestConstants.DEFALUT_PAGE_NUMBER, TestConstants.DEFALUT_PAGE_SIZE)));

        verify(graphRepository, times(1)).getGraphByUuid(id1);
        verify(nodeMapper, never()).getRelationByGraphUuid(id1, PROPERTIES,
                TestConstants.DEFALUT_PAGE_NUMBER, TestConstants.DEFALUT_PAGE_SIZE);
        verify(caffeineCache, never()).setCache(anyString(), any(GraphVO.class));
    }

    /**
     * Tests that deleting a graph throws a NoSuchElementException when the graph does not exist.
     */
    @Test
    public void testDeleteByUuidsGraphNotFound() {
        final String id1 = ID_1;
        final String id2 = ID_2;
        // Arrange
        final GraphDeleteDTO graphDeleteDTO = new GraphDeleteDTO(id1, Arrays.asList(id2));
        when(commonService.getGraphByUuid(id2)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> graphService.deleteByUuids(graphDeleteDTO));
        verify(commonService, times(1)).getGraphByUuid(id2);
        verify(graphRepository, never()).deleteByUuids(anyList());
        verify(nodeRepository, never()).deleteByUuids(anyList());
        verify(caffeineCache, never()).deleteCache(anyString());
    }

    /**
     * Tests that deleting a graph throws a IllegalStateException when the graph is bound to another user.
     */
    @Test
    public void testDeleteByUuidsGraphBoundToAnotherUser() {
        // Arrange
        final String id1 = ID_1;
        final String id2 = ID_2;
        final Graph graph = Graph.builder()
                .uuid(id2)
                .build();

        final GraphDeleteDTO graphDeleteDTO = new GraphDeleteDTO(id1, Arrays.asList(id2));

        when(commonService.getGraphByUuid(id2)).thenReturn(Optional.ofNullable(graph));
        when(graphRepository.getGraphByGraphUuidAndOidcid(id2, id1)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> graphService.deleteByUuids(graphDeleteDTO));
        verify(commonService, times(1)).getGraphByUuid(id2);
        verify(graphRepository, times(1)).getGraphByGraphUuidAndOidcid(id2, id1);
        verify(graphRepository, never()).deleteByUuids(anyList());
        verify(nodeRepository, never()).deleteByUuids(anyList());
        verify(caffeineCache, never()).deleteCache(anyString());
    }

    /**
     * Tests that deleting a graph successfully deletes the graph and related graph nodes.
     */
    @Test
    public void testDeleteByUuidsSuccess() {
        // Arrange
        final String id1 = ID_1;
        final String id2 = ID_2;
        final String nodeUuid = TestConstants.TEST_ID3;
        final Graph graph = Graph.builder()
                .uuid(id1)
                .build();
        final GraphDeleteDTO graphDeleteDTO = new GraphDeleteDTO(id1, Arrays.asList(id2));

        when(commonService.getGraphByUuid(id2)).thenReturn(Optional.ofNullable(graph));
        when(graphRepository.getGraphByGraphUuidAndOidcid(id2, id1)).thenReturn(id2);
        when(graphRepository.getGraphNodeUuidsByGraphUuids(anyList())).thenReturn(Collections.singletonList(nodeUuid));

        doNothing().when(nodeRepository).deleteByUuids(anyList());
        doNothing().when(graphRepository).deleteByUuids(anyList());

        graphService.deleteByUuids(graphDeleteDTO);

        // Act & Assert
        verify(nodeRepository, times(1)).deleteByUuids(Collections.singletonList(nodeUuid));
        verify(graphRepository, times(1)).deleteByUuids(Collections.singletonList(id2));
        verify(caffeineCache, times(1)).deleteCache(id2);
    }

    /**
     * Tests that updating a graph throws a IllegalArgumentException when the transaction is null.
     */
    @Test
    public void testUpdateGraphTransactionNull() {
        // Arrange
        final String id = ID_1;
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO(id, TITLE, DESCRIPTION);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> graphService.updateGraph(graphUpdateDTO, null));

        // Act & Assert
        assertEquals(Message.TRANSACTION_NULL, exception.getMessage());
        verify(graphMapper, never()).updateGraphByUuid(anyString(), anyString(), anyString(), anyString(),
                any(Transaction.class));
        verify(caffeineCache, never()).deleteCache(anyString());
    }

    /**
     * Tests that updating a graph updates the graph when it exists.
     */
    @Test
    void testUpdateGraphSuccess() {
        // Arrange
        final String id = ID_1;
        final Transaction tx = mock(Transaction.class);
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO(id, TITLE, DESCRIPTION);
        final Optional<Graph> graph = Optional.of(new Graph());

        when(commonService.getGraphByUuid(anyString())).thenReturn(graph);

        graphService.updateGraph(graphUpdateDTO, tx);

        // Act & Assert
        verify(graphMapper, times(1)).updateGraphByUuid(eq(graphUpdateDTO.getUuid()), eq(graphUpdateDTO.getTitle()),
                eq(graphUpdateDTO.getDescription()), anyString(), eq(tx));
        verify(caffeineCache, times(1)).deleteCache(id);
    }

    /**
     * Tests that updating a graph throws a NoSuchElementException when the graph does not exist.
     */
    @Test
    void testUpdateGraphGraphNotFoun() {
        // Arrange
        final String id = ID_1;
        final Transaction tx = mock(Transaction.class);
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO(id, TestConstants.TEST_TITLE1,
                TestConstants.TEST_DESCRIPTION1);

        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.empty());

        final NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> graphService.updateGraph(graphUpdateDTO, tx));

        // Act & Assert
        assertEquals(String.format(Message.GRAPH_NULL, graphUpdateDTO.getUuid()), exception.getMessage());
        verify(graphMapper, never()).updateGraphByUuid(anyString(), anyString(), anyString(), anyString(),
                any(Transaction.class));
        verify(caffeineCache, never()).deleteCache(anyString());
    }
}
