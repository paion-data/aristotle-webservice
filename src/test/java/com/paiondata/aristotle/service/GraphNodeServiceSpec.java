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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
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
import java.util.*;

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

    @BeforeEach
    public void setup() {
    }

    @Test
    void getGraphNodeByUuid_GraphNodeExists_ShouldReturnNode() {
        // Given
        String uuid = "test-uuid";
        GraphNode graphNode = new GraphNode();
        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(graphNode);

        // When
        Optional<GraphNode> result = graphNodeService.getNodeByUuid(uuid);

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(graphNode, result.get());
        verify(graphNodeRepository).getGraphNodeByUuid(uuid);
    }

    @Test
    void getGraphNodeByUuid_NodeDoesNotExist_ShouldReturnEmpty() {
        // Given
        String uuid = "non-existent-uuid";
        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(null);

        // When
        Optional<GraphNode> result = graphNodeService.getNodeByUuid(uuid);

        // Then
        Assertions.assertFalse(result.isPresent());
        verify(graphNodeRepository).getGraphNodeByUuid(uuid);
    }

    @Test
    void createAndBindGraphAndNode_GraphExists_ShouldCreateAndBindNodes() {
        // Given
        NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();
        nodeCreateDTO.setGraphUuid("test-graph-uuid");
        nodeCreateDTO.setGraphNodeDTO(List.of(new NodeDTO("Id1", "test-title1", "test-description1"),
                new NodeDTO("Id2", "test-title2", "test-description2")));
        nodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO("Id1", "Id2", "relation1"),
                new NodeRelationDTO("Id2", "Id1", "relation2")
        ));

        when(graphService.getGraphByUuid("test-graph-uuid")).thenReturn(Optional.of(new Graph()));

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        String graphNodeUuid = UUID.fastUUID().toString(true);
        String currentTime = getCurrentTime();
        when(graphNodeRepository.createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).
                thenReturn((new GraphNode(0L, graphNodeUuid, "test-title",
                                "test-description", currentTime ,currentTime)));

        // When
        assertDoesNotThrow(() -> graphNodeService.createAndBindGraphAndNode(nodeCreateDTO));

        // Then
        verify(graphService, times(1)).getGraphByUuid("test-graph-uuid");
        verify(graphNodeRepository, times(1)).getGraphUuidByGraphNodeUuid(List.of("Id1", "Id2", "Id2", "Id1"));
        verify(graphNodeRepository, times(2)).createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString());
    }

    @Test
    void createAndBindGraphAndNode_GraphDoesNotExist_ShouldThrowException() {
        // Given
        NodeCreateDTO graphNodeCreateDTO = new NodeCreateDTO();
        graphNodeCreateDTO.setGraphUuid("non-existent-uuid");

        when(graphService.getGraphByUuid("non-existent-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GraphNullException.class, () -> graphNodeService.createAndBindGraphAndNode(graphNodeCreateDTO));

        // Then
        verify(graphService, times(1)).getGraphByUuid("non-existent-uuid");
    }

    @Test
    void createGraphAndBindGraphAndNode_GraphCreated_ShouldCreateGraphAndBindGraphAndNode() {
        // Given
        GraphAndNodeCreateDTO graphNodeCreateDTO = new GraphAndNodeCreateDTO();
        graphNodeCreateDTO.setGraphCreateDTO(new GraphCreateDTO("title", "description", "userUidcid"));
        graphNodeCreateDTO.setGraphNodeDTO(List.of(new NodeDTO("Id1", "test-title1", "test-description1"),
                new NodeDTO("Id2", "test-title2", "test-description2")));
        graphNodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO("Id1", "Id2", "relation1"),
                new NodeRelationDTO("Id2", "Id1", "relation2")
        ));

        when(graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO()))
                .thenReturn(Graph.builder().uuid("test-graph-uuid").build());

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        String graphNodeUuid = UUID.fastUUID().toString(true);
        String currentTime = getCurrentTime();
        when(graphNodeRepository.createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).
                thenReturn((new GraphNode(0L, graphNodeUuid, "test-title",
                        "test-description", currentTime ,currentTime)));

        // When
        assertDoesNotThrow(() -> graphNodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO));

        // Then
        verify(graphService, times(1)).createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());
        verify(graphNodeRepository, times(1)).getGraphUuidByGraphNodeUuid(List.of("Id1", "Id2", "Id2", "Id1"));
        verify(graphNodeRepository, times(2)).createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString());
    }

    @Test
    void bindNodes_NodesExist_ShouldBindNodes() {
        // Given
        List<BindNodeDTO> dtos = Collections.singletonList(new BindNodeDTO("fromId", "toId", "relationName"));

        when(graphNodeRepository.getGraphNodeByUuid("fromId")).thenReturn(new GraphNode());
        when(graphNodeRepository.getGraphNodeByUuid("toId")).thenReturn(new GraphNode());

        // When & Then
        assertDoesNotThrow(() -> graphNodeService.bindNodes(dtos));

        // Then
        verify(graphNodeRepository, times(1)).getGraphNodeByUuid("fromId");
        verify(graphNodeRepository, times(1)).getGraphNodeByUuid("toId");
        verify(graphNodeRepository, times(1)).
                bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void bindNodes_NodesDoesNotExist_ShouldThrowException() {
        // Given
        List<BindNodeDTO> dtos = Collections.singletonList(new BindNodeDTO("fromId", "toId", "relationName"));

        when(graphNodeRepository.getGraphNodeByUuid("fromId")).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.bindNodes(dtos));

        // Then
        verify(graphNodeRepository, times(2)).getGraphNodeByUuid(anyString());
    }

    @Test
    void deleteByUuids_NodesExist_ShouldDeleteNodes() {
        // Given
        List<String> uuids = List.of("uuid1", "uuid2");

        when(graphNodeRepository.getGraphNodeByUuid("uuid1")).thenReturn(new GraphNode());
        when(graphNodeRepository.getGraphNodeByUuid("uuid2")).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.deleteByUuids(uuids));

        // Then
        verify(graphNodeRepository, times(1)).deleteByUuids(uuids);
    }

    @Test
    void deleteByUuids_GraphNodeDoesNotExist_ShouldThrowException() {
        // Given
        List<String> uuids = List.of("uuid1", "uuid2");

        when(graphNodeRepository.getGraphNodeByUuid("uuid1")).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.deleteByUuids(uuids));

        // Then
        verify(graphNodeRepository, times(1)).getGraphNodeByUuid("uuid1");
        verify(graphNodeRepository, never()).getGraphNodeByUuid("uuid2");
    }

    @Test
    void updateGraphNode_GraphNodeExists_ShouldUpdateNode() {
        // Given
        String uuid = "test-uuid";
        GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid(uuid);
        graphUpdateDTO.setTitle("new-title");
        graphUpdateDTO.setDescription("new-description");

        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.updateNode(graphUpdateDTO));

        // Then
        verify(neo4jService, times(1)).updateNodeByUuid(
                eq(uuid),
                eq("new-title"),
                eq("new-description"),
                any(String.class)
        );
    }

    @Test
    void updateGraphNode_NodeDoesNotExist_ShouldThrowException() {
        // Given
        GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid("non-existent-uuid");

        when(graphNodeRepository.getGraphNodeByUuid("non-existent-uuid")).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.updateNode(graphUpdateDTO));

        // Then
        verify(graphNodeRepository).getGraphNodeByUuid("non-existent-uuid");
    }

    @Test
    void updateRelation_WithUpdateMap() {
        // Given
        String graphUuid = "test-graph-uuid";
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("uuid1", "newName");
        updateMap.put("uuid2", "newName");

        when(graphNodeRepository.getRelationByUuid("uuid1")).thenReturn("uuid1");
        when(graphNodeRepository.getRelationByUuid("uuid2")).thenReturn("uuid2");

        RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap, Collections.emptyList());

        // When
        graphNodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(graphNodeRepository, times(1)).updateRelationByUuid("uuid1", "newName", graphUuid);
        verify(graphNodeRepository, times(1)).updateRelationByUuid("uuid2", "newName", graphUuid);
    }

    @Test
    void testUpdateRelation_WithDeleteList() {
        // Given
        String graphUuid = "test-graph-uuid";
        List<String> deleteList = List.of("uuid1", "uuid2");

        when(graphNodeRepository.getRelationByUuid("uuid1")).thenReturn("uuid1");
        when(graphNodeRepository.getRelationByUuid("uuid2")).thenReturn("uuid2");

        RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, Collections.emptyMap(), deleteList);

        // When
        graphNodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(graphNodeRepository, times(1)).deleteRelationByUuid("uuid1", graphUuid);
        verify(graphNodeRepository, times(1)).deleteRelationByUuid("uuid2", graphUuid);
    }

    @Test
    void testUpdateRelation_WithBothUpdateMapAndDeleteList() {
        // Given
        String graphUuid = "test-graph-uuid";
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("uuid1", "newName");
        updateMap.put("uuid2", "newName");

        List<String> deleteList = List.of("uuid3", "uuid4");

        when(graphNodeRepository.getRelationByUuid("uuid1")).thenReturn("uuid1");
        when(graphNodeRepository.getRelationByUuid("uuid2")).thenReturn("uuid2");
        when(graphNodeRepository.getRelationByUuid("uuid3")).thenReturn("uuid3");
        when(graphNodeRepository.getRelationByUuid("uuid4")).thenReturn("uuid4");

        RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap, deleteList);

        // When
        graphNodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(graphNodeRepository).updateRelationByUuid("uuid1", "newName", graphUuid);
        verify(graphNodeRepository).updateRelationByUuid("uuid2", "newName", graphUuid);
        verify(graphNodeRepository).deleteRelationByUuid("uuid3", graphUuid);
        verify(graphNodeRepository).deleteRelationByUuid("uuid4", graphUuid);
    }

    @Test
    void testUpdateRelation_WithNonExistentRelationInUpdateMap() {
        // Given
        String graphUuid = "test-graph-uuid";
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("uuid1", "newName");

        when(graphNodeRepository.getRelationByUuid("uuid1")).thenReturn(null);

        RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap, Collections.emptyList());

        // When & Then
        assertThrows(GraphNodeRelationException.class,
                () -> graphNodeService.updateRelation(relationUpdateDTO));
    }

    @Test
    void testUpdateRelation_WithNonExistentRelationInDeleteList() {
        // Given
        String graphUuid = "test-graph-uuid";
        List<String> deleteList = List.of("uuid1");

        when(graphNodeRepository.getRelationByUuid("uuid1")).thenReturn(null);

        RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, Collections.emptyMap(), deleteList);

        // When & Then
        assertThrows(GraphNodeRelationException.class,
                () -> graphNodeService.updateRelation(relationUpdateDTO));
    }

    private String getCurrentTime() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        }
}
