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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.common.base.TestConstants;
import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.util.CaffeineCacheUtil;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.service.impl.NodeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import cn.hutool.core.lang.UUID;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Test class for the Graph Node Service.
 * Uses Mockito to mock dependencies and validate graph node service operations.
 */
@ExtendWith(MockitoExtension.class)
public class NodeServiceTest {

    private static final String ID_1 = TestConstants.TEST_ID1;

    private static final String ID_2 = TestConstants.TEST_ID2;

    private static final String TITLE_1 = TestConstants.TEST_TITLE1;

    private static final String DESCRIPTION_1 = TestConstants.TEST_DESCRIPTION1;

    private static final String TITLE_2 = TestConstants.TEST_TITLE2;

    private static final String DESCRIPTION_2 = TestConstants.TEST_DESCRIPTION2;

    private static final String RELATION_1 = TestConstants.TEST_RELATION1;

    private static final String RELATION_2 = TestConstants.TEST_RELATION2;

    private static final String NAME = TestConstants.TEST_NAME1;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

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
     * Tests that getting a GraphNode by UUID returns the node when it exists.
     */
    @Test
    void getNodeByUuidNodeExistsShouldReturnNode() {
        // Given
        final String uuid = TestConstants.TEST_ID1;
        final NodeVO node = new NodeVO();
        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(node);

        // When
        final Optional<NodeVO> result = nodeService.getNodeByUuid(uuid);

        // Then
        assertTrue(result.isPresent());
        assertEquals(node, result.get());
        verify(nodeMapper).getNodeByUuid(uuid);
    }

    /**
     * Tests that getting a GraphNode by UUID returns an empty Optional when the node does not exist.
     */
    @Test
    void getNodeByUuidNodeDoesNotExistShouldReturnEmpty() {
        // Given
        final String uuid = ID_1;
        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(null);

        // When
        final Optional<NodeVO> result = nodeService.getNodeByUuid(uuid);

        // Then
        assertFalse(result.isPresent());
        verify(nodeMapper).getNodeByUuid(uuid);
    }

    /**
     * Tests that creating and binding a Graph and Node throws a IllegalArgumentException when the transaction is null.
     */
    @Test
    public void createAndBindGraphAndNodeTransactionNullThrowsTransactionException() {
        // Given
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();

        // Then
        assertThrows(IllegalArgumentException.class, () -> nodeService.createAndBindGraphAndNode(nodeCreateDTO, null));
        verify(caffeineCache, never()).deleteCache(anyString());
    }

    /**
     * Tests that creating and binding a Graph and Node throws a NoSuchElementException when the UUID is not found.
     */
    @Test
    public void createAndBindGraphAndNodeGraphUuidNotFoundThrowsNoSuchElementException() {
        // Given
        final String uuid = ID_1;
        final Transaction tx = mock(Transaction.class);
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();
        nodeCreateDTO.setGraphUuid(uuid);

        // When
        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(NoSuchElementException.class, () -> nodeService.createAndBindGraphAndNode(nodeCreateDTO, tx));
        verify(caffeineCache, never()).deleteCache(anyString());
    }

    /**
     * Tests that creating and binding a graph and its nodes succeeds when the graph exists.
     */
    @Test
    void createAndBindGraphAndNodeGraphExistsShouldCreateAndBindNodes() {
        // Given
        final String id1 = ID_1;
        final String id2 = ID_2;
        final String title1 = TITLE_1;
        final String title2 = TITLE_2;
        final String description1 = DESCRIPTION_1;
        final String description2 = DESCRIPTION_2;
        final String relation1 = RELATION_1;
        final String relation2 = RELATION_2;
        final Transaction tx = mock(Transaction.class);
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();

        nodeCreateDTO.setGraphUuid(id1);
        nodeCreateDTO.setNodeDTO(List.of(
                new NodeDTO(id1, Map.of(Constants.TITLE, title1, Constants.DESCRIPTION, description1)),
                new NodeDTO(id2, Map.of(Constants.TITLE, title2, Constants.DESCRIPTION, description2))));
        nodeCreateDTO.setNodeRelationDTO(List.of(
                new NodeRelationDTO(id1, id2, relation1),
                new NodeRelationDTO(id2, id1, relation2)
        ));

        when(commonService.getGraphByUuid(id1)).thenReturn(Optional.of(new Graph()));

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        final String graphNodeUuid = UUID.fastUUID().toString(true);
        final String currentTime = TestConstants.TEST_TIME_01;

        when(nodeMapper.createNode(anyString(), anyString(), anyString(), anyString(), any(NodeDTO.class),
                any(Transaction.class)))
                .thenReturn((new NodeVO(graphNodeUuid, Map.of(Constants.TITLE, title1,
                        Constants.DESCRIPTION, description1), currentTime, currentTime)));
        doNothing().when(nodeMapper).bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Transaction.class));

        // When
        final List<NodeVO> dtos = nodeService.createAndBindGraphAndNode(nodeCreateDTO, tx);

        // Then
        verify(commonService, times(1)).getGraphByUuid(id1);
        verify(nodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(id1, id2));
        verify(caffeineCache, times(2)).deleteCache(id1);
        assertNotNull(dtos);
        assertFalse(dtos.isEmpty());
    }

    /**
     * Tests that binding existing nodes succeeds when the graph exists and the input nodes are null.
     */
    @Test
    void createAndBindGraphAndNodeNodesNullShouldOnlyBindRelations() {
        // Given
        final String id1 = ID_1;
        final String id2 = ID_2;

        final Transaction tx = mock(Transaction.class);
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();
        nodeCreateDTO.setGraphUuid(id1);
        nodeCreateDTO.setNodeRelationDTO(List.of(
                new NodeRelationDTO(id1, id2, RELATION_1), new NodeRelationDTO(id2, id1, RELATION_2)
        ));

        when(commonService.getGraphByUuid(id1)).thenReturn(Optional.of(new Graph()));

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        doNothing().when(nodeMapper).bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Transaction.class));

        // When
        final List<NodeVO> dtos = nodeService.createAndBindGraphAndNode(nodeCreateDTO, tx);

        // Then
        verify(commonService, times(1)).getGraphByUuid(id1);
        verify(nodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(id1, id2));
        assertTrue(dtos.isEmpty());
    }

    /**
     * Tests that creating a graph and binding nodes succeeds when the graph is created.
     */
    @Test
    void createGraphAndBindGraphAndNodeGraphCreatedShouldCreateGraphAndBindGraphAndNode() {
        // Given
        final String id1 = ID_1;
        final String id2 = ID_2;
        final String title1 = TITLE_1;
        final String title2 = TITLE_2;
        final String description1 = DESCRIPTION_1;
        final String description2 = DESCRIPTION_2;
        final String relation1 = RELATION_1;
        final String relation2 = RELATION_2;

        final Transaction tx = mock(Transaction.class);
        final GraphAndNodeCreateDTO graphNodeCreateDTO = new GraphAndNodeCreateDTO();
        graphNodeCreateDTO.setGraphCreateDTO(new GraphCreateDTO(title1, description1, id1));
        graphNodeCreateDTO.setNodeDTO(List.of(new NodeDTO(id1, Map.of(Constants.TITLE, title1,
                        Constants.DESCRIPTION, description1)), new NodeDTO(id2, Map.of(Constants.TITLE, title2,
                        Constants.DESCRIPTION, description2))));
        graphNodeCreateDTO.setNodeRelationDTO(List.of(
                new NodeRelationDTO(id1, id2, relation1),
                new NodeRelationDTO(id2, id1, relation2)
        ));

        when(commonService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO(), tx))
                .thenReturn(Graph.builder()
                        .uuid(id1)
                        .title(title1)
                        .description(description1)
                        .build());

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        final String graphNodeUuid = UUID.fastUUID().toString(true);
        final String currentTime = TestConstants.TEST_TIME_01;

        when(nodeMapper.createNode(anyString(), anyString(), anyString(), anyString(), any(NodeDTO.class),
                any(Transaction.class)))
                .thenReturn((new NodeVO(graphNodeUuid, Map.of(Constants.TITLE, title1,
                        Constants.DESCRIPTION, description1), currentTime, currentTime)));
        doNothing().when(nodeMapper).bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Transaction.class));

        // When
        final GraphVO dto = nodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO, tx);

        // Then
        verify(commonService, times(1)).createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO(), tx);
        verify(nodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(id1, id2));
        assertNotNull(dto);
        assertNotNull(dto.getUuid());
        assertEquals(dto.getTitle(), title1);
        assertEquals(dto.getDescription(), description1);
        assertNotNull(dto.getNodes());
    }

    /**
     * Tests that deleting nodes succeeds when the node exists and belongs to the graph.
     */
    @Test
    public void deleteByUuidsNodeExistsAndBelongsToGraphSuccess() {
        // Arrange
        final String graphUuid = ID_1;
        final String nodeUuid = ID_2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        final NodeVO node = NodeVO.builder()
                .uuid(nodeUuid)
                .build();

        when(nodeMapper.getNodeByUuid(nodeUuid)).thenReturn(node);
        when(nodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, nodeUuid)).thenReturn(nodeUuid);

        // Act
        nodeService.deleteByUuids(dto);

        // Assert
        verify(nodeRepository, times(1)).deleteByUuids(Collections.singletonList(nodeUuid));
        verify(caffeineCache, times(1)).deleteCache(graphUuid);
    }

    /**
     * Tests that deleting nodes throws a NoSuchElementException when the node does not exist.
     */
    @Test
    public void deleteByUuidsNodeDoesNotExistThrowsException() {
        // Arrange
        final String graphUuid = ID_1;
        final String nodeUuid = ID_2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        when(nodeMapper.getNodeByUuid(nodeUuid)).thenReturn(null);

        // Assert handled by expected exception
        assertThrows(NoSuchElementException.class, () -> nodeService.deleteByUuids(dto));
        verify(caffeineCache, never()).deleteCache(graphUuid);
    }

    /**
     * Tests that deleting nodes throws a IllegalStateException when the node belongs to another graph.
     */
    @Test
    public void deleteByUuidsNodeBelongsToAnotherGraphThrowsException() {
        // Arrange
        final String graphUuid = ID_1;
        final String nodeUuid = ID_2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        final NodeVO node = NodeVO.builder()
                .uuid(nodeUuid)
                .build();

        when(nodeMapper.getNodeByUuid(nodeUuid)).thenReturn(node);
        when(nodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, nodeUuid)).thenReturn(null);

        // Assert handled by expected exception
        assertThrows(IllegalStateException.class, () -> nodeService.deleteByUuids(dto));
        verify(caffeineCache, never()).deleteCache(graphUuid);
    }

    /**
     * Tests that updating a node succeeds when the node exists.
     */
    @Test
    void updateNodeGraphNodeExistsShouldUpdateNode() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final String graphUuid = ID_1;
        final String uuid = ID_2;
        final NodeUpdateDTO nodeUpdateDTO = NodeUpdateDTO.builder()
                .graphUuid(graphUuid)
                .uuid(uuid)
                .properties(Map.of(Constants.TITLE, TITLE_1,
                        Constants.DESCRIPTION, TITLE_2))
                .build();

        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.of(new Graph()));
        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(new NodeVO());

        // When
        assertDoesNotThrow(() -> nodeService.updateNode(nodeUpdateDTO, tx));

        // Then
        verify(nodeMapper, times(1)).updateNodeByUuid(eq(nodeUpdateDTO), anyString(), eq(tx));
        verify(caffeineCache, times(1)).deleteCache(graphUuid);
    }

    /**
     * Tests that updating a non-existent graph throws an exception.
     */
    @Test
    void updateNodeGraphDoesNotExistShouldThrowException() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final String graphUuid = ID_1;
        final String uuid = ID_2;
        final NodeUpdateDTO nodeUpdateDTO = NodeUpdateDTO.builder()
                .graphUuid(graphUuid)
                .uuid(uuid)
                .properties(Map.of(Constants.TITLE, TITLE_1,
                        Constants.DESCRIPTION, DESCRIPTION_2))
                .build();

        // When
        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(NoSuchElementException.class, () -> nodeService.updateNode(nodeUpdateDTO, tx));
        verify(caffeineCache, never()).deleteCache(graphUuid);
    }

    /**
     * Tests that updating a non-existent node throws an exception.
     */
    @Test
    void updateNodeNodeDoesNotExistShouldThrowException() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final String graphUuid = ID_1;
        final String uuid = ID_2;
        final NodeUpdateDTO nodeUpdateDTO = NodeUpdateDTO.builder()
                .graphUuid(graphUuid)
                .uuid(uuid)
                .properties(Map.of(Constants.TITLE, TITLE_1,
                        Constants.DESCRIPTION, DESCRIPTION_1))
                .build();

        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.of(new Graph()));
        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(null);

        // When & Then
        assertThrows(NoSuchElementException.class, () -> nodeService.updateNode(nodeUpdateDTO, tx));

        // Then
        verify(nodeMapper).getNodeByUuid(uuid);
        verify(caffeineCache, never()).deleteCache(graphUuid);
    }

    /**
     * Tests updating a graph relation with an update map.
     */
    @Test
    void updateRelationWithUpdateMap() {
        // Given
        final String graphUuid = ID_1;
        final String id1 = ID_1;
        final String id2 = ID_2;
        final String name = NAME;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(id1, name);
        updateMap.put(id2, name);

        when(nodeRepository.getRelationByUuid(id1)).thenReturn(id1);
        when(nodeRepository.getRelationByUuid(id2)).thenReturn(id2);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap,
                Collections.emptyList());

        // When
        nodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(nodeRepository, times(1)).updateRelationByUuid(id1, name, graphUuid);
        verify(nodeRepository, times(1)).updateRelationByUuid(id2, name, graphUuid);
        verify(caffeineCache, times(1)).deleteCache(graphUuid);
    }

    /**
     * Tests updating a graph relation with a delete list.
     */
    @Test
    void testUpdateRelationWithDeleteList() {
        // Given
        final String graphUuid = ID_1;
        final String id1 = ID_1;
        final String id2 = ID_2;
        final List<String> deleteList = List.of(id1, id2);

        when(nodeRepository.getRelationByUuid(id1)).thenReturn(id1);
        when(nodeRepository.getRelationByUuid(id2)).thenReturn(id2);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                Collections.emptyMap(), deleteList);

        // When
        nodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(nodeRepository, times(1)).deleteRelationByUuid(id1, graphUuid);
        verify(nodeRepository, times(1)).deleteRelationByUuid(id2, graphUuid);
        verify(caffeineCache, times(1)).deleteCache(graphUuid);
    }

    /**
     * Tests updating a graph relation with both an update map and a delete list.
     */
    @Test
    void testUpdateRelationWithBothUpdateMapAndDeleteList() {
        // Given
        final String graphUuid = ID_1;
        final String id1 = ID_1;
        final String id2 = ID_2;
        final String id3 = TestConstants.TEST_ID3;
        final String id4 = TestConstants.TEST_ID4;
        final String name = NAME;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(id1, name);
        updateMap.put(id2, name);

        final List<String> deleteList = List.of(id3, id4);

        when(nodeRepository.getRelationByUuid(id1)).thenReturn(id1);
        when(nodeRepository.getRelationByUuid(id2)).thenReturn(id2);
        when(nodeRepository.getRelationByUuid(id3)).thenReturn(id3);
        when(nodeRepository.getRelationByUuid(id4)).thenReturn(id4);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap, deleteList);

        // When
        nodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(nodeRepository).updateRelationByUuid(id1, name, graphUuid);
        verify(nodeRepository).updateRelationByUuid(id2, name, graphUuid);
        verify(nodeRepository).deleteRelationByUuid(id3, graphUuid);
        verify(nodeRepository).deleteRelationByUuid(id4, graphUuid);
        verify(caffeineCache, times(1)).deleteCache(graphUuid);
    }

    /**
     * Tests that updating a non-existent relation in the update map throws an exception.
     */
    @Test
    void testUpdateRelationWithNonExistentRelationInUpdateMap() {
        // Given
        final String graphUuid = ID_1;
        final String relationUuid = ID_2;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(relationUuid, NAME);

        when(nodeRepository.getRelationByUuid(relationUuid)).thenReturn(null);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                updateMap, Collections.emptyList());

        // When & Then
        assertThrows(NoSuchElementException.class,
                () -> nodeService.updateRelation(relationUpdateDTO));

        verify(caffeineCache, never()).deleteCache(graphUuid);
    }

    /**
     * Tests that deleting a non-existent relation in the delete list throws an exception.
     */
    @Test
    void testUpdateRelationWithNonExistentRelationInDeleteList() {
        // Given
        final String graphUuid = ID_1;
        final String id1 = ID_1;
        final List<String> deleteList = List.of(id1);

        when(nodeRepository.getRelationByUuid(id1)).thenReturn(null);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                Collections.emptyMap(), deleteList);

        // When & Then
        assertThrows(NoSuchElementException.class,
                () -> nodeService.updateRelation(relationUpdateDTO));

        verify(caffeineCache, never()).deleteCache(graphUuid);
    }

    /**
     * Tests the getkDegreeExpansion method when the graph exists in the caffeine cache.
     */
    @Test
    void testGetkDegreeExpansionCaffeineCacheHit() {
        // Arrange
        final String graphUuid = ID_1;
        final String nodeUuid = ID_2;
        final Integer k = 2;

        when(commonService.getGraphByUuid(graphUuid)).thenReturn(Optional.of(new Graph()));
        final GraphVO expectedGraphVO = new GraphVO();
        when(caffeineCache.getCache(anyString())).thenReturn(Optional.of(expectedGraphVO));

        // Execute the method under test
        final GraphVO result = nodeService.getkDegreeExpansion(graphUuid, nodeUuid, k);

        // Verify the result
        assertEquals(expectedGraphVO, result);
        verify(commonService, times(1)).getGraphByUuid(graphUuid);
        verify(caffeineCache, times(1)).getCache(anyString());
        verify(nodeMapper, never()).kDegreeExpansion(eq(graphUuid), eq(nodeUuid), eq(k));
    }

    /**
     * Tests the getkDegreeExpansion method when the graph exists.
     */
    @Test
    void testGetkDegreeExpansionCaffeineCacheMiss() {
        // Arrange
        final String graphUuid = ID_1;
        final String nodeUuid = ID_2;
        final Integer k = 2;

        when(caffeineCache.getCache(anyString())).thenReturn(Optional.empty());

        // Mocking the behavior of commonService
        when(commonService.getGraphByUuid(graphUuid)).thenReturn(Optional.of(new Graph()));

        // Mocking the behavior of nodeMapper
        final GraphVO expectedGraphVO = new GraphVO();
        when(nodeMapper.kDegreeExpansion(eq(graphUuid), eq(nodeUuid), eq(k))).thenReturn(expectedGraphVO);

        // Execute the method under test
        final GraphVO result = nodeService.getkDegreeExpansion(graphUuid, nodeUuid, k);

        // Verify the result
        assertEquals(expectedGraphVO, result);
        verify(commonService, times(1)).getGraphByUuid(graphUuid);
        verify(nodeMapper, times(1)).kDegreeExpansion(eq(graphUuid), eq(nodeUuid), eq(k));
        verify(caffeineCache, times(1)).setCache(anyString(), any(GraphVO.class));
    }

    /**
     * Tests the getkDegreeExpansion method when the graph does not exist.
     */
    @Test
    void testGetkDegreeExpansionGraphDoesNotExistThrowsNoSuchElementException() {
        // Arrange
        final String graphUuid = ID_1;
        final String nodeUuid = ID_2;
        final Integer k = 2;

        // Mocking the behavior of commonService
        when(commonService.getGraphByUuid(graphUuid)).thenReturn(Optional.empty());

        // Execute the method under test and expect an exception
        assertThrows(NoSuchElementException.class, () -> nodeService.getkDegreeExpansion(graphUuid, nodeUuid, k));

        // Verify the result
        verify(commonService, times(1)).getGraphByUuid(graphUuid);
        verify(nodeMapper, never()).kDegreeExpansion(any(), any(), any());
    }
}
