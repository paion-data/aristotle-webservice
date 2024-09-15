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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.impl.GraphServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GraphServiceSpec {

    @InjectMocks
    private GraphServiceImpl graphService;

    @Mock
    private UserService userService;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private GraphNodeRepository graphNodeRepository;

    @Mock
    private Neo4jService neo4jService;

    @BeforeEach
    public void setup() {
    }

    @Test
    void getGraphVOByUuidGraphExists_ReturnGraphVO() {
        // Arrange
        String uuid = "test-uuid";
        String title = "test-title";
        String description = "test-description";
        String currentTime = getCurrentTime();
        Graph graph = Graph.builder()
                .uuid(uuid)
                .title(title)
                .description(description)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();

        List<Map<String, Map<String, Object>>> nodes =
                Collections.singletonList(Collections.singletonMap("node",
                        Collections.singletonMap("id", "test-node-id")));

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);
        when(neo4jService.getGraphNodeByGraphUuid(uuid)).thenReturn(nodes);

        // Act
        GraphVO graphVO = graphService.getGraphVOByUuid(uuid);

        // Assert
        Assertions.assertEquals(uuid, graphVO.getUuid());
        Assertions.assertEquals(title, graphVO.getTitle());
        Assertions.assertEquals(description, graphVO.getDescription());
        Assertions.assertEquals(currentTime, graphVO.getCreateTime());
        Assertions.assertEquals(currentTime, graphVO.getUpdateTime());
        Assertions.assertEquals(nodes, graphVO.getNodes());

        verify(graphRepository, times(1)).getGraphByUuid(uuid);
        verify(neo4jService, times(1)).getGraphNodeByGraphUuid(uuid);
    }

    @Test
    void getGraphVOByUuid_GraphDoesNotExist_ThrowsGraphNullException() {
        // Arrange
        String uuid = "non-existing-uuid";
        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.getGraphVOByUuid(uuid));

        verify(graphRepository, times(1)).getGraphByUuid(uuid);
        verify(neo4jService, never()).getGraphNodeByGraphUuid(uuid);
    }

    @Test
    void getGraphByUuid_GraphExists_ReturnGraph() {
        // Arrange
        String uuid = "test-uuid";
        String currentTime = getCurrentTime();
        Graph graph = Graph.builder()
                .uuid(uuid)
                .title("test-title")
                .description("test-description")
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);

        // Act
        Optional<Graph> graphOptional = graphService.getGraphByUuid(uuid);

        // Assert
        Assertions.assertTrue(graphOptional.isPresent());
        Assertions.assertEquals(graph, graphOptional.get());
    }

    @Test
    void getGraphByUuid_GraphDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        String uuid = "test-uuid";

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act
        Optional<Graph> graphOptional = graphService.getGraphByUuid(uuid);

        // Assert
        Assertions.assertFalse(graphOptional.isPresent());
    }

    @Test
    void createAndBindGraph_ValidInput_GraphCreatedSuccessfully() {
        // Arrange
        String uidcid = "testUidcid";
        String title = "Test Title";
        String description = "Test Description";
        GraphCreateDTO graphCreateDTO = GraphCreateDTO.builder()
                .userUidcid(uidcid)
                .title(title)
                .description(description)
                .build();

        User user = User.builder()
                .uidcid(uidcid)
                .build();
        when(userService.getUserByUidcid(uidcid)).thenReturn(Optional.of(user));

        // Act
        graphService.createAndBindGraph(graphCreateDTO);

        // Assert
        verify(graphRepository).createAndBindGraph(
                eq(title),
                eq(description),
                eq(uidcid),
                any(String.class), // graphUuid
                any(String.class), // relationUuid
                any(String.class)  // currentTime
        );
    }

    @Test
    void createAndBindGraph_UserNotFound_ThrowsUserNullException() {
        // Arrange
        GraphCreateDTO graphCreateDTO = GraphCreateDTO.builder()
                        .title("Test Graph")
                                .description("Test Description")
                .userUidcid("non-existing-uidcid")
                .build();

        // Act & Assert
        assertThrows(UserNullException.class, () -> graphService.createAndBindGraph(graphCreateDTO));
    }

    @Test
    void deleteByUuids_GraphExist_DeletesGraphsAndRelateData() {
        // Arrange
        List<String> uuids = Arrays.asList("uuid1", "uuid2");
        List<Graph> graphs = new ArrayList<>();
        graphs.add(Graph.builder().uuid("uuid1").build());
        graphs.add(Graph.builder().uuid("uuid2").build());

        List<String> relatedGraphNodeUuids = Arrays.asList("uuid1", "uuid2");

        when(graphRepository.getGraphByUuid("uuid1")).thenReturn(graphs.get(0));
        when(graphRepository.getGraphByUuid("uuid2")).thenReturn(graphs.get(1));
        when(graphRepository.getGraphNodeUuidsByGraphUuids(uuids)).thenReturn(relatedGraphNodeUuids);

        // Act
        graphService.deleteByUuids(uuids);

        // Assert
        verify(graphRepository, times(1)).deleteByUuids(uuids);
        verify(graphNodeRepository, times(1)).deleteByUuids(relatedGraphNodeUuids);
    }

    @Test
    void deleteByUuids_GraphDoesNotExist_ThrowsGraphNullException() {
        // Arrange
        List<String> uuids = Arrays.asList("uuid1", "uuid2");

        when(graphRepository.getGraphByUuid("uuid1")).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.deleteByUuids(uuids));

        // Verify
        verify(graphRepository, times(1)).getGraphByUuid("uuid1");
        verify(graphRepository, never()).deleteByUuids(any());
        verify(graphNodeRepository, never()).deleteByUuids(any());
    }

    @Test
    void updateGraph_WhenGraphExists_ShouldUpdateGraph() {
        // Arrange
        String uuid = "test-uuid";
        GraphUpdateDTO graphUpdateDTO = new GraphUpdateDTO();
        graphUpdateDTO.setUuid(uuid);
        graphUpdateDTO.setTitle("Updated Title");
        graphUpdateDTO.setDescription("Updated Description");

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(new Graph());

        // Act & Assert
        assertDoesNotThrow(() -> graphService.updateGraph(graphUpdateDTO));

        // Verify
        verify(neo4jService, times(1)).updateGraphByUuid(
                eq(uuid),
                eq("Updated Title"),
                eq("Updated Description"),
                any(String.class)
        );
    }

    @Test
    void updateGraph_GraphNotExists_ThrowsGraphNullException() {
        // Arrange
        GraphUpdateDTO graphUpdateDTO = GraphUpdateDTO.builder()
                .uuid("test-uuid")
                .build();
        String uuid = graphUpdateDTO.getUuid();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act & Assert
        assertThrows(GraphNullException.class, () -> graphService.updateGraph(graphUpdateDTO));
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
