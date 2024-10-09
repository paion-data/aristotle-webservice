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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.base.TestConstants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.TransactionException;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.GraphVO;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for the Graph Service.
 * Uses Mockito to mock dependencies and validate graph service operations.
 */
@ExtendWith(MockitoExtension.class)
public class GraphServiceSpec {

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

    /**
     * Setup method to initialize mocks and test data.
     */
    @BeforeEach
    public void setup() {
    }

    /**
     * Tests that getting a GraphVO by UUID returns the correct GraphVO when the graph exists.
     */
    @Test
    void getGraphVOByUuidGraphExistReturnGraphVO() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        final String title = TestConstants.TEST_TILE1;
        final String description = TestConstants.TEST_DESCRIPTION1;
        final String currentTime = getCurrentTime();
        final Graph graph = Graph.builder()
                .uuid(uuid)
                .title(title)
                .description(description)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();

        final List<Map<String, Map<String, Object>>> nodes =
                Collections.singletonList(Collections.singletonMap("node",
                        Collections.singletonMap("id", "test-node-id")));

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);
        when(nodeMapper.getNodesByGraphUuid(uuid)).thenReturn(nodes);

        // Act
        final GraphVO graphVO = graphService.getGraphVOByUuid(uuid);

        // Assert
        assertEquals(uuid, graphVO.getUuid());
        assertEquals(title, graphVO.getTitle());
        assertEquals(description, graphVO.getDescription());
        assertEquals(currentTime, graphVO.getCreateTime());
        assertEquals(currentTime, graphVO.getUpdateTime());
        assertEquals(nodes, graphVO.getNodes());

        verify(graphRepository, times(1)).getGraphByUuid(uuid);
        verify(nodeMapper, times(1)).getNodesByGraphUuid(uuid);
    }

    /**
     * Tests that getting a GraphVO by UUID throws a GraphNullException when the graph does not exist.
     */
    @Test
    void getGraphVOByUuidGraphDoesNotExistThrowsGraphNullException() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.getGraphVOByUuid(uuid));

        verify(graphRepository, times(1)).getGraphByUuid(uuid);
        verify(nodeMapper, never()).getNodesByGraphUuid(uuid);
    }

    /**
     * Tests that deleting a graph throws a GraphNullException when the graph does not exist.
     */
    @Test
    public void deleteByUuidsGraphNotExistThrowsGraphNullException() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String uuid = TestConstants.TEST_ID2;

        final GraphDeleteDTO graphDeleteDTO = GraphDeleteDTO.builder()
                .uidcid(uidcid)
                .uuids(Collections.singletonList(uuid))
                .build();

        when(commonService.getGraphByUuid(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.deleteByUuids(graphDeleteDTO));
        verify(commonService, times(1)).getGraphByUuid(uuid);
        verify(graphRepository, never()).deleteByUuids(anyList());
        verify(nodeRepository, never()).deleteByUuids(anyList());
    }

    /**
     * Tests that deleting a graph throws a DeleteException when the graph is bound to another user.
     */
    @Test
    public void deleteByUuidsGraphBoundToAnotherUserThrowsDeleteException() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String uuid = TestConstants.TEST_ID2;

        final Graph graph = Graph.builder()
                .uuid(uuid)
                .build();

        final GraphDeleteDTO graphDeleteDTO = GraphDeleteDTO.builder()
                .uidcid(uidcid)
                .uuids(Collections.singletonList(uuid))
                .build();

        when(commonService.getGraphByUuid(uuid)).thenReturn(Optional.ofNullable(graph));
        when(graphRepository.getGraphByGraphUuidAndUidcid(uuid, uidcid)).thenReturn(null);

        // Act & Assert
        assertThrows(DeleteException.class, () -> graphService.deleteByUuids(graphDeleteDTO));
        verify(commonService, times(1)).getGraphByUuid(uuid);
        verify(graphRepository, times(1)).getGraphByGraphUuidAndUidcid(uuid, uidcid);
        verify(graphRepository, never()).deleteByUuids(anyList());
        verify(nodeRepository, never()).deleteByUuids(anyList());
    }

    /**
     * Tests that deleting a graph successfully deletes the graph and related graph nodes.
     */
    @Test
    public void deleteByUuidsValidRequestDeletesGraphsAndRelatedGraphNodes() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String graphUuid = TestConstants.TEST_ID2;
        final String nodeUuid = TestConstants.TEST_ID3;

        final Graph graph = Graph.builder()
                .uuid(uidcid)
                .build();

        final GraphDeleteDTO graphDeleteDTO = GraphDeleteDTO.builder()
                .uidcid(uidcid)
                .uuids(Collections.singletonList(graphUuid))
                .build();

        when(commonService.getGraphByUuid(graphUuid)).thenReturn(Optional.ofNullable(graph));
        when(graphRepository.getGraphByGraphUuidAndUidcid(graphUuid, uidcid)).thenReturn(graphUuid);
        when(graphRepository.getGraphNodeUuidsByGraphUuids(anyList())).thenReturn(Collections.singletonList(nodeUuid));

        doNothing().when(nodeRepository).deleteByUuids(anyList());
        doNothing().when(graphRepository).deleteByUuids(anyList());

        graphService.deleteByUuids(graphDeleteDTO);

        // Act & Assert
        verify(nodeRepository, times(1)).deleteByUuids(Collections.singletonList(nodeUuid));
        verify(graphRepository, times(1)).deleteByUuids(Collections.singletonList(graphUuid));
    }

    /**
     * Tests that updating a graph throws a TransactionException when the transaction is null.
     */
    @Test
    public void updateGraphTransactionIsNullShouldThrowTransactionException() {
        // Arrange
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO(TestConstants.TEST_ID1, TestConstants.TEST_TILE1,
                TestConstants.TEST_DESCRIPTION1);

        final TransactionException exception = assertThrows(TransactionException.class, () -> {
            graphService.updateGraph(graphUpdateDTO, null);
        });

        // Act & Assert
        assertEquals(Message.TRANSACTION_NULL, exception.getMessage());
    }

    /**
     * Tests that updating a graph updates the graph when it exists.
     */
    @Test
    void updateGraphWhenGraphExistsShouldUpdateGraph() {
        // Arrange
        final Transaction tx = mock(Transaction.class);
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO(TestConstants.TEST_ID1, TestConstants.TEST_TILE1,
                TestConstants.TEST_DESCRIPTION1);
        final Optional<Graph> graph = Optional.of(new Graph());

        when(commonService.getGraphByUuid(anyString())).thenReturn(graph);

        graphService.updateGraph(graphUpdateDTO, tx);

        // Act & Assert
        verify(graphMapper, times(1)).updateGraphByUuid(eq(graphUpdateDTO.getUuid()), eq(graphUpdateDTO.getTitle()),
                eq(graphUpdateDTO.getDescription()), anyString(), eq(tx));
    }

    /**
     * Tests that updating a graph throws a GraphNullException when the graph does not exist.
     */
    @Test
    void updateGraphGraphNotExistsThrowsGraphNullException() {
        // Arrange
        final Transaction tx = mock(Transaction.class);
        final GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO(TestConstants.TEST_ID1, TestConstants.TEST_TILE1,
                TestConstants.TEST_DESCRIPTION1);

        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.empty());

        final GraphNullException exception = assertThrows(GraphNullException.class, () -> {
            graphService.updateGraph(graphUpdateDTO, tx);
        });

        // Act & Assert
        assertEquals(Message.GRAPH_NULL + graphUpdateDTO.getUuid(), exception.getMessage());
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
