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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.base.TestConstants;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.NodeNullException;
import com.paiondata.aristotle.common.exception.NodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphNodeDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.NodeReturnDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.service.impl.GraphNodeServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import cn.hutool.core.lang.UUID;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Test class for the Graph Node Service.
 * Uses Mockito to mock dependencies and validate graph node service operations.
 */
@ExtendWith(MockitoExtension.class)
public class GraphNodeServiceSpec {

    @InjectMocks
    private GraphNodeServiceImpl graphNodeService;

    @Mock
    private GraphNodeRepository graphNodeRepository;

    @Mock
    private GraphService graphService;

    @Mock
    private Neo4jService neo4jService;

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
    void getGraphNodeByUuidGraphNodeExistsShouldReturnNode() {
        // Given
        final String uuid = TestConstants.TEST_ID1;
        final GraphNode graphNode = new GraphNode();
        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(graphNode);

        // When
        final Optional<GraphNode> result = graphNodeService.getNodeByUuid(uuid);

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(graphNode, result.get());
        verify(graphNodeRepository).getGraphNodeByUuid(uuid);
    }

    /**
     * Tests that getting a GraphNode by UUID returns an empty Optional when the node does not exist.
     */
    @Test
    void getGraphNodeByUuidNodeDoesNotExistShouldReturnEmpty() {
        // Given
        final String uuid = TestConstants.TEST_ID1;
        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(null);

        // When
        final Optional<GraphNode> result = graphNodeService.getNodeByUuid(uuid);

        // Then
        Assertions.assertFalse(result.isPresent());
        verify(graphNodeRepository).getGraphNodeByUuid(uuid);
    }

    /**
     * Tests that creating and binding a graph and its nodes succeeds when the graph exists.
     */
    @Test
    void createAndBindGraphAndNodeGraphExistsShouldCreateAndBindNodes() {
        // Given
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();
        nodeCreateDTO.setGraphUuid(TestConstants.TEST_ID1);
        nodeCreateDTO.setGraphNodeDTO(List.of(new NodeDTO(TestConstants.TEST_ID1, TestConstants.TEST_TILE1,
                        TestConstants.TEST_DESCRIPTION1),
                new NodeDTO(TestConstants.TEST_ID2, TestConstants.TEST_TILE2, TestConstants.TEST_DESCRIPTION2)));
        nodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO(TestConstants.TEST_ID1, TestConstants.TEST_ID2, TestConstants.TEST_RELATION1),
                new NodeRelationDTO(TestConstants.TEST_ID2, TestConstants.TEST_ID1, TestConstants.TEST_RELATION2)
        ));

        when(graphService.getGraphByUuid(TestConstants.TEST_ID1)).thenReturn(Optional.of(new Graph()));

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        final String graphNodeUuid = UUID.fastUUID().toString(true);
        final String currentTime = getCurrentTime();
        when(graphNodeRepository.createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString()))
                .thenReturn((new GraphNode(0L, graphNodeUuid, TestConstants.TEST_TILE1,
                        TestConstants.TEST_DESCRIPTION1, currentTime, currentTime)));

        // When
        final List<NodeReturnDTO> dtos = graphNodeService.createAndBindGraphAndNode(nodeCreateDTO);

        // Then
        verify(graphService, times(1)).getGraphByUuid(TestConstants.TEST_ID1);
        verify(graphNodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2));
        verify(graphNodeRepository, times(2)).createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString());
        Assertions.assertNotNull(dtos);
        Assertions.assertFalse(dtos.isEmpty());
    }

    /**
     * Tests that creating and binding a graph and its nodes throws a GraphNullException when the graph does not exist.
     */
    @Test
    void createAndBindGraphAndNodeGraphDoesNotExistShouldThrowException() {
        // Given
        final NodeCreateDTO graphNodeCreateDTO = new NodeCreateDTO();
        graphNodeCreateDTO.setGraphUuid(TestConstants.TEST_ID1);

        when(graphService.getGraphByUuid(TestConstants.TEST_ID1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GraphNullException.class, () -> graphNodeService.createAndBindGraphAndNode(graphNodeCreateDTO));

        // Then
        verify(graphService, times(1)).getGraphByUuid(TestConstants.TEST_ID1);
    }

    /**
     * Tests that creating a graph and binding nodes succeeds when the graph is created.
     */
    @Test
    void createGraphAndBindGraphAndNodeGraphCreatedShouldCreateGraphAndBindGraphAndNode() {
        // Given
        final GraphAndNodeCreateDTO graphNodeCreateDTO = new GraphAndNodeCreateDTO();
        graphNodeCreateDTO.setGraphCreateDTO(new GraphCreateDTO(TestConstants.TEST_TILE1,
                TestConstants.TEST_DESCRIPTION1, TestConstants.TEST_ID1));
        graphNodeCreateDTO.setGraphNodeDTO(List.of(new NodeDTO(TestConstants.TEST_ID1, TestConstants.TEST_TILE1,
                        TestConstants.TEST_DESCRIPTION1), new NodeDTO(TestConstants.TEST_ID2,
                TestConstants.TEST_TILE2, TestConstants.TEST_DESCRIPTION2)));
        graphNodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO(TestConstants.TEST_ID1, TestConstants.TEST_ID2, TestConstants.TEST_RELATION1),
                new NodeRelationDTO(TestConstants.TEST_ID2, TestConstants.TEST_ID1, TestConstants.TEST_RELATION2)
        ));

        when(graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO()))
                .thenReturn(Graph.builder()
                        .uuid(TestConstants.TEST_ID1)
                        .title(TestConstants.TEST_TILE1)
                        .description(TestConstants.TEST_DESCRIPTION1)
                        .build());

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        final String graphNodeUuid = UUID.fastUUID().toString(true);
        final String currentTime = getCurrentTime();
        when(graphNodeRepository.createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString()))
                .thenReturn((new GraphNode(0L, graphNodeUuid, TestConstants.TEST_TILE1,
                        TestConstants.TEST_DESCRIPTION1, currentTime, currentTime)));

        // When
        final GraphNodeDTO dto = graphNodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO);

        // Then
        verify(graphService, times(1)).createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());
        verify(graphNodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2));
        verify(graphNodeRepository, times(2)).createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString());
        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getUuid());
        Assertions.assertEquals(dto.getTitle(), TestConstants.TEST_TILE1);
        Assertions.assertEquals(dto.getDescription(), TestConstants.TEST_DESCRIPTION1);
        Assertions.assertNotNull(dto.getNodes());
    }

    /**
     * Tests that binding nodes succeeds when both nodes exist.
     */
    @Test
    void bindNodesNodesExistShouldBindNodes() {
        // Given
        final List<BindNodeDTO> dtos = Collections.singletonList(new BindNodeDTO(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2, TestConstants.TEST_RELATION1));

        when(graphNodeRepository.getGraphNodeByUuid(TestConstants.TEST_ID1)).thenReturn(new GraphNode());
        when(graphNodeRepository.getGraphNodeByUuid(TestConstants.TEST_ID2)).thenReturn(new GraphNode());

        // When & Then
        assertDoesNotThrow(() -> graphNodeService.bindNodes(dtos));

        // Then
        verify(graphNodeRepository, times(1)).getGraphNodeByUuid(TestConstants.TEST_ID1);
        verify(graphNodeRepository, times(1)).getGraphNodeByUuid(TestConstants.TEST_ID2);
        verify(graphNodeRepository, times(1))
                .bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    /**
     * Tests that binding nodes throws a NodeNullException when one of the nodes does not exist.
     */
    @Test
    void bindNodesNodesDoesNotExistShouldThrowException() {
        // Given
        final List<BindNodeDTO> dtos = Collections.singletonList(new BindNodeDTO(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2, TestConstants.TEST_RELATION1));

        when(graphNodeRepository.getGraphNodeByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        // When & Then
        assertThrows(NodeNullException.class, () -> graphNodeService.bindNodes(dtos));

        // Then
        verify(graphNodeRepository, times(2)).getGraphNodeByUuid(anyString());
    }

    /**
     * Tests that deleting nodes succeeds when the node exists and belongs to the graph.
     */
    @Test
    public void deleteByUuidsNodeExistsAndBelongsToGraphSuccess() {
        // Arrange
        final String graphUuid = TestConstants.TEST_ID1;
        final String nodeUuid = TestConstants.TEST_ID2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        final GraphNode graphNode = GraphNode.builder()
                .uuid(nodeUuid)
                .build();

        when(graphNodeRepository.getGraphNodeByUuid(nodeUuid)).thenReturn(graphNode);
        when(graphNodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, nodeUuid)).thenReturn(nodeUuid);

        // Act
        graphNodeService.deleteByUuids(dto);

        // Assert
        verify(graphNodeRepository, times(1)).deleteByUuids(Collections.singletonList(nodeUuid));
    }

    /**
     * Tests that deleting nodes throws a NodeNullException when the node does not exist.
     */
    @Test
    public void deleteByUuidsNodeDoesNotExistThrowsException() {
        // Arrange
        final String graphUuid = TestConstants.TEST_ID1;
        final String nodeUuid = TestConstants.TEST_ID2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        when(graphNodeRepository.getGraphNodeByUuid(nodeUuid)).thenReturn(null);

        // Assert handled by expected exception
        assertThrows(NodeNullException.class, () -> graphNodeService.deleteByUuids(dto));
    }

    /**
     * Tests that deleting nodes throws a DeleteException when the node belongs to another graph.
     */
    @Test
    public void deleteByUuidsNodeBelongsToAnotherGraphThrowsException() {
        // Arrange
        final String graphUuid = TestConstants.TEST_ID1;
        final String nodeUuid = TestConstants.TEST_ID2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        final GraphNode graphNode = GraphNode.builder()
                .uuid(nodeUuid)
                .build();

        when(graphNodeRepository.getGraphNodeByUuid(nodeUuid)).thenReturn(graphNode);
        when(graphNodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, nodeUuid)).thenReturn(null);

        // Assert handled by expected exception
        assertThrows(DeleteException.class, () -> graphNodeService.deleteByUuids(dto));
    }

    /**
     * Tests that updating a graph node succeeds when the node exists.
     */
    @Test
    void updateGraphNodeGraphNodeExistsShouldUpdateNode() {
        // Given
        final String uuid = TestConstants.TEST_ID1;
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid(uuid);
        graphUpdateDTO.setTitle(TestConstants.TEST_TILE2);
        graphUpdateDTO.setDescription(TestConstants.TEST_DESCRIPTION2);

        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.updateNode(graphUpdateDTO));

        // Then
        verify(neo4jService, times(1)).updateNodeByUuid(
                eq(uuid),
                eq(TestConstants.TEST_TILE2),
                eq(TestConstants.TEST_DESCRIPTION2),
                any(String.class)
        );
    }

    /**
     * Tests that updating a non-existent graph node throws an exception.
     */
    @Test
    void updateGraphNodeNodeDoesNotExistShouldThrowException() {
        // Given
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid(TestConstants.TEST_ID1);

        when(graphNodeRepository.getGraphNodeByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        // When & Then
        assertThrows(NodeNullException.class, () -> graphNodeService.updateNode(graphUpdateDTO));

        // Then
        verify(graphNodeRepository).getGraphNodeByUuid(TestConstants.TEST_ID1);
    }

    /**
     * Tests updating a graph relation with an update map.
     */
    @Test
    void updateRelationWithUpdateMap() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);
        updateMap.put(TestConstants.TEST_ID2, TestConstants.TEST_NAME1);

        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(TestConstants.TEST_ID1);
        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID2)).thenReturn(TestConstants.TEST_ID2);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap,
                Collections.emptyList());

        // When
        graphNodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(graphNodeRepository, times(1)).updateRelationByUuid(TestConstants.TEST_ID1,
                TestConstants.TEST_NAME1, graphUuid);
        verify(graphNodeRepository, times(1)).updateRelationByUuid(TestConstants.TEST_ID2,
                TestConstants.TEST_NAME1, graphUuid);
    }

    /**
     * Tests updating a graph relation with a delete list.
     */
    @Test
    void testUpdateRelationWithDeleteList() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final List<String> deleteList = List.of(TestConstants.TEST_ID1, TestConstants.TEST_ID2);

        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(TestConstants.TEST_ID1);
        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID2)).thenReturn(TestConstants.TEST_ID2);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                Collections.emptyMap(), deleteList);

        // When
        graphNodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(graphNodeRepository, times(1)).deleteRelationByUuid(TestConstants.TEST_ID1, graphUuid);
        verify(graphNodeRepository, times(1)).deleteRelationByUuid(TestConstants.TEST_ID2, graphUuid);
    }

    /**
     * Tests updating a graph relation with both an update map and a delete list.
     */
    @Test
    void testUpdateRelationWithBothUpdateMapAndDeleteList() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);
        updateMap.put(TestConstants.TEST_ID2, TestConstants.TEST_NAME1);

        final List<String> deleteList = List.of(TestConstants.TEST_ID3, TestConstants.TEST_ID4);

        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(TestConstants.TEST_ID1);
        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID2)).thenReturn(TestConstants.TEST_ID2);
        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID3)).thenReturn(TestConstants.TEST_ID3);
        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID4)).thenReturn(TestConstants.TEST_ID4);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap, deleteList);

        // When
        graphNodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(graphNodeRepository).updateRelationByUuid(TestConstants.TEST_ID1, TestConstants.TEST_NAME1, graphUuid);
        verify(graphNodeRepository).updateRelationByUuid(TestConstants.TEST_ID2, TestConstants.TEST_NAME1, graphUuid);
        verify(graphNodeRepository).deleteRelationByUuid(TestConstants.TEST_ID3, graphUuid);
        verify(graphNodeRepository).deleteRelationByUuid(TestConstants.TEST_ID4, graphUuid);
    }

    /**
     * Tests that updating a non-existent relation in the update map throws an exception.
     */
    @Test
    void testUpdateRelationWithNonExistentRelationInUpdateMap() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);

        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                updateMap, Collections.emptyList());

        // When & Then
        assertThrows(NodeRelationException.class,
                () -> graphNodeService.updateRelation(relationUpdateDTO));
    }

    /**
     * Tests that deleting a non-existent relation in the delete list throws an exception.
     */
    @Test
    void testUpdateRelationWithNonExistentRelationInDeleteList() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final List<String> deleteList = List.of(TestConstants.TEST_ID1);

        when(graphNodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                Collections.emptyMap(), deleteList);

        // When & Then
        assertThrows(NodeRelationException.class,
                () -> graphNodeService.updateRelation(relationUpdateDTO));
    }

    /**
     * Get current time.
     * @return current time
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
