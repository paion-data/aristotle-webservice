package com.paiondata.aristotle.service;

import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.model.dto.*;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.service.impl.GraphNodeServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphNodeServiceTest {

    @Mock
    private GraphNodeRepository graphNodeRepository;

    @Mock
    private GraphService graphService;

    @InjectMocks
    private GraphNodeServiceImpl graphNodeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetGraphNodeByUuid_GraphNodeExists_ShouldReturnGraphNode() {
        // Given
        String uuid = "test-uuid";
        GraphNode graphNode = new GraphNode();
        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(graphNode);

        // When
        Optional<GraphNode> result = graphNodeService.getGraphNodeByUuid(uuid);

        // Then
        assertTrue(result.isPresent());
        assertEquals(graphNode, result.get());
        verify(graphNodeRepository).getGraphNodeByUuid(uuid);
    }

    @Test
    public void testGetGraphNodeByUuid_GraphNodeDoesNotExist_ShouldReturnEmpty() {
        // Given
        String uuid = "non-existent-uuid";
        when(graphNodeRepository.getGraphNodeByUuid(uuid)).thenReturn(null);

        // When
        Optional<GraphNode> result = graphNodeService.getGraphNodeByUuid(uuid);

        // Then
        assertFalse(result.isPresent());
        verify(graphNodeRepository).getGraphNodeByUuid(uuid);
    }

    @Test
    public void testCreateAndBindGraphNode_GraphExists_ShouldCreateAndBindGraphNode() {
        // Given
        NodeCreateDTO graphNodeCreateDTO = new NodeCreateDTO();
        graphNodeCreateDTO.setGraphUuid("test-graph-uuid");
        graphNodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO("fromId1", "toId1", "relation1"),
                new NodeRelationDTO("fromId2", "toId2", "relation2")
        ));

        when(graphService.getGraphByUuid("test-graph-uuid")).thenReturn(Optional.of(new Graph()));
        when(graphNodeRepository.getGraphUuidByGraphNodeUuid(List.of("fromId1", "toId1", "fromId2", "toId2")))
                .thenReturn(List.of("test-graph-uuid", "test-graph-uuid", "test-graph-uuid", "test-graph-uuid"));

        // When
        assertDoesNotThrow(() -> graphNodeService.createAndBindGraphNode(graphNodeCreateDTO));

        // Then
        verify(graphService).getGraphByUuid("test-graph-uuid");
        verify(graphNodeRepository).getGraphUuidByGraphNodeUuid(List.of("fromId1", "toId1", "fromId2", "toId2"));
    }

    @Test
    public void testCreateAndBindGraphNode_GraphDoesNotExist_ShouldThrowException() {
        // Given
        NodeCreateDTO graphNodeCreateDTO = new NodeCreateDTO();
        graphNodeCreateDTO.setGraphUuid("non-existent-uuid");

        when(graphService.getGraphByUuid("non-existent-uuid")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GraphNullException.class, () -> graphNodeService.createAndBindGraphNode(graphNodeCreateDTO));

        // Then
        verify(graphService).getGraphByUuid("non-existent-uuid");
    }

    @Test
    public void testCreateAndBindGraphNode_RelationNodesBoundToDifferentGraph_ShouldThrowException() {
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
        assertThrows(GraphNodeRelationException.class, () -> graphNodeService.createAndBindGraphNode(graphNodeCreateDTO));

        // Then
        verify(graphService).getGraphByUuid("test-graph-uuid");
        verify(graphNodeRepository).getGraphUuidByGraphNodeUuid(List.of("fromId1", "toId1", "fromId2", "toId2"));
    }

    @Test
    public void testCreateAndBindGraphGraphNode_GraphCreated_ShouldCreateAndBindGraphNode() {
        // Given
        GraphAndNodeCreateDTO graphNodeCreateDTO = new GraphAndNodeCreateDTO();
        graphNodeCreateDTO.setGraphCreateDTO(new GraphCreateDTO());
        graphNodeCreateDTO.setGraphNodeDTO(List.of(
                NodeDTO.builder().title("title1").description("description1").build()));

        when(graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO()))
                .thenReturn(Graph.builder().uuid("test-graph-uuid").build());

        // When
        assertDoesNotThrow(() -> graphNodeService.createAndBindGraphGraphNode(graphNodeCreateDTO));

        // Then
        verify(graphService).createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());
    }

    @Test
    public void testBindGraphNode_GraphNodesExist_ShouldBindGraphNodes() {
        // Given
        String uuid1 = "uuid1";
        String uuid2 = "uuid2";
        String relation = "relation";

        when(graphNodeRepository.getGraphNodeByUuid(uuid1)).thenReturn(new GraphNode());
        when(graphNodeRepository.getGraphNodeByUuid(uuid2)).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.bindGraphNode(uuid1, uuid2, relation));

        // Then
        verify(graphNodeRepository).bindGraphNodeToGraphNode(uuid1, uuid2, relation, anyString(), any(Date.class));
    }

    @Test
    public void testBindGraphNode_GraphNodeDoesNotExist_ShouldThrowException() {
        // Given
        String uuid1 = "uuid1";
        String uuid2 = "uuid2";
        String relation = "relation";

        when(graphNodeRepository.getGraphNodeByUuid(uuid1)).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.bindGraphNode(uuid1, uuid2, relation));

        // Then
        verify(graphNodeRepository).getGraphNodeByUuid(uuid1);
    }

    @Test
    public void testDeleteByUuids_GraphNodesExist_ShouldDeleteGraphNodes() {
        // Given
        List<String> uuids = List.of("uuid1", "uuid2");

        when(graphNodeRepository.getGraphNodeByUuid("uuid1")).thenReturn(new GraphNode());
        when(graphNodeRepository.getGraphNodeByUuid("uuid2")).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.deleteByUuids(uuids));

        // Then
        verify(graphNodeRepository).deleteByUuids(uuids);
    }

    @Test
    public void testDeleteByUuids_GraphNodeDoesNotExist_ShouldThrowException() {
        // Given
        List<String> uuids = List.of("uuid1", "uuid2");

        when(graphNodeRepository.getGraphNodeByUuid("uuid1")).thenReturn(null);
        when(graphNodeRepository.getGraphNodeByUuid("uuid2")).thenReturn(new GraphNode());

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.deleteByUuids(uuids));

        // Then
        verify(graphNodeRepository).getGraphNodeByUuid("uuid1");
        verify(graphNodeRepository).getGraphNodeByUuid("uuid2");
    }

    @Test
    public void testUpdateGraphNode_GraphNodeExists_ShouldUpdateGraphNode() {
        // Given
        GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid("test-uuid");
        graphUpdateDTO.setTitle("new-title");
        graphUpdateDTO.setDescription("new-description");

        when(graphNodeRepository.getGraphNodeByUuid("test-uuid")).thenReturn(new GraphNode());

        // When
        assertDoesNotThrow(() -> graphNodeService.updateGraphNode(graphUpdateDTO));

        // Then
        verify(graphNodeRepository).updateGraphNodeByUuid("test-uuid", "new-title", "new-description", any(Date.class));
    }

    @Test
    public void testUpdateGraphNode_GraphNodeDoesNotExist_ShouldThrowException() {
        // Given
        GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid("non-existent-uuid");

        when(graphNodeRepository.getGraphNodeByUuid("non-existent-uuid")).thenReturn(null);

        // When & Then
        assertThrows(GraphNodeNullException.class, () -> graphNodeService.updateGraphNode(graphUpdateDTO));

        // Then
        verify(graphNodeRepository).getGraphNodeByUuid("non-existent-uuid");
    }
}
