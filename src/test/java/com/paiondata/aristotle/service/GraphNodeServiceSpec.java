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
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GraphNodeServiceSpec {

    @Mock
    private GraphNodeRepository graphNodeRepository;

    @Mock
    private GraphService graphService;

    @InjectMocks
    private GraphNodeServiceImpl graphNodeService;

    @BeforeEach
    public void setup() {
    }

    @Test
    void testGetGraphNodeByUuid_GraphNodeExists_ShouldReturnNode() {
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
    void testGetGraphNodeByUuid_NodeDoesNotExist_ShouldReturnEmpty() {
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
    void testCreateAndBindGraphNode_GraphExists_ShouldCreateAndBindNodes() {
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
        Date currentTime = new Date();
        when(graphNodeRepository.createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Date.class))).
                thenReturn((new GraphNode(0L, graphNodeUuid, "test-title",
                                "test-description", currentTime ,currentTime)));

        // When
        assertDoesNotThrow(() -> graphNodeService.createAndBindGraphAndNode(nodeCreateDTO));

        // Then
        verify(graphService, times(1)).getGraphByUuid("test-graph-uuid");
        verify(graphNodeRepository, times(1)).getGraphUuidByGraphNodeUuid(List.of("Id1", "Id2", "Id2", "Id1"));
        verify(graphNodeRepository, times(2)).createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Date.class));
    }

    @Test
    void testCreateAndBindGraphNode_GraphDoesNotExist_ShouldThrowException() {
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
    void testCreateAndBindGraphNode_RelationNodesBoundToDifferentGraph_And_ShouldThrowException() {
        // Given
        NodeCreateDTO graphNodeCreateDTO = new NodeCreateDTO();
        graphNodeCreateDTO.setGraphUuid("test-graph-uuid");
        graphNodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO("fromId1", "toId1", "relation1"),
                new NodeRelationDTO("fromId2", "toId2", "relation2")
        ));

        when(graphService.getGraphByUuid("test-graph-uuid")).thenReturn(Optional.of(new Graph()));
        when(graphNodeRepository.getGraphUuidByGraphNodeUuid(List.of("fromId1", "toId1", "fromId2", "toId2")))
                .thenReturn(List.of("test-graph-uuid", "test-graph-uuid", "another-graph-uuid", "test-graph-uuid"));

        // When & Then
        assertThrows(GraphNodeRelationException.class,
                () -> graphNodeService.createAndBindGraphAndNode(graphNodeCreateDTO));

        // Then
        verify(graphService, times(1)).getGraphByUuid("test-graph-uuid");
        verify(graphNodeRepository,
                times(1)).getGraphUuidByGraphNodeUuid(List.of("fromId1", "toId1", "fromId2", "toId2"));
    }

    @Test
    void testCreateGraphAndBindGraphAndNode_GraphCreated_ShouldCreateGraphAndBindGraphAndNode() {
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
        Date currentTime = new Date();
        when(graphNodeRepository.createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Date.class))).
                thenReturn((new GraphNode(0L, graphNodeUuid, "test-title",
                        "test-description", currentTime ,currentTime)));

        // When
        assertDoesNotThrow(() -> graphNodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO));

        // Then
        verify(graphService, times(1)).createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());
        verify(graphNodeRepository, times(1)).getGraphUuidByGraphNodeUuid(List.of("Id1", "Id2", "Id2", "Id1"));
        verify(graphNodeRepository, times(2)).createAndBindGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Date.class));
    }

    @Test
    void testBindNodes_NodesExist_ShouldBindNodes() {
        // Given
        String uuid1 = "uuid1";
        String uuid2 = "uuid2";
        String relation = "relation";

        when(graphNodeRepository.getGraphNodeByUuid(uuid1)).thenReturn(new GraphNode());
        when(graphNodeRepository.getGraphNodeByUuid(uuid2)).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.bindNodes(uuid1, uuid2, relation));

        // Then
        verify(graphNodeRepository, times(1)).bindGraphNodeToGraphNode(eq(uuid1), eq(uuid2), eq(relation), anyString(),
                any(Date.class));
    }

    @Test
    void testBindNodes_NodesDoesNotExist_ShouldThrowException() {
        // Given
        String uuid1 = "uuid1";
        String uuid2 = "uuid2";
        String relation = "relation";

        when(graphNodeRepository.getGraphNodeByUuid(uuid1)).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.bindNodes(uuid1, uuid2, relation));

        // Then
        verify(graphNodeRepository, times(1)).getGraphNodeByUuid(uuid1);
    }

    @Test
    void testDeleteByUuids_NodesExist_ShouldDeleteNodes() {
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
    void testDeleteByUuids_GraphNodeDoesNotExist_ShouldThrowException() {
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
    void testUpdateGraphNode_GraphNodeExists_ShouldUpdateNode() {
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
        verify(graphNodeRepository, times(1)).updateGraphNodeByUuid(
                eq(uuid),
                eq("new-title"),
                eq("new-description"),
                any(Date.class)
        );
    }

    @Test
    void testUpdateGraphNode_NodeDoesNotExist_ShouldThrowException() {
        // Given
        GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid("non-existent-uuid");

        when(graphNodeRepository.getGraphNodeByUuid("non-existent-uuid")).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.updateNode(graphUpdateDTO));

        // Then
        verify(graphNodeRepository).getGraphNodeByUuid("non-existent-uuid");
    }
}
