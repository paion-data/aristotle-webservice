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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.base.TestConstants;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.impl.CommonServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Transaction;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * Tests for CommonService.
 */
@ExtendWith(MockitoExtension.class)
public class CommonServiceSpec {

    @InjectMocks
    private CommonServiceImpl commonService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private GraphMapper graphMapper;

    /**
     * Tests that getting a user by UIDCID returns the correct user when the user exists.
     */
    @Test
    public void getUserByUidcidUserExistsReturnsUser() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final User expectedUser = User.builder()
                .uidcid(uidcid)
                .nickName(TestConstants.TEST_NAME1)
                .build();

        when(userRepository.getUserByUidcid(uidcid)).thenReturn(expectedUser);

        // Act
        final Optional<User> userOptional = commonService.getUserByUidcid(uidcid);

        // Assert
        Assertions.assertTrue(userOptional.isPresent());
        assertEquals(expectedUser, userOptional.get());
    }

    /**
     * Tests that getting a user by UIDCID returns an empty Optional when the user does not exist.
     */
    @Test
    public void getUserByUidcidUserDoesNotExistReturnsEmptyOptional() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;

        when(userRepository.getUserByUidcid(uidcid)).thenReturn(null);

        // Act
        final Optional<User> userOptional = commonService.getUserByUidcid(uidcid);

        // Assert
        Assertions.assertFalse(userOptional.isPresent());
    }

    /**
     * Tests that getting a Graph by UUID returns the correct Graph when it exists.
     */
    @Test
    void getGraphByUuidGraphExistsReturnGraph() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;
        final String currentTime = getCurrentTime();
        final Graph graph = Graph.builder()
                .uuid(uuid)
                .title(TestConstants.TEST_TILE1)
                .description(TestConstants.TEST_DESCRIPTION1)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(graph);

        // Act
        final Optional<Graph> graphOptional = commonService.getGraphByUuid(uuid);

        // Assert
        Assertions.assertTrue(graphOptional.isPresent());
        assertEquals(graph, graphOptional.get());
    }

    /**
     * Tests that getting a Graph by UUID returns an empty Optional when the graph does not exist.
     */
    @Test
    void getGraphByUuidGraphDoesNotExistReturnsEmptyOptional() {
        // Arrange
        final String uuid = TestConstants.TEST_ID1;

        when(graphRepository.getGraphByUuid(uuid)).thenReturn(null);

        // Act
        final Optional<Graph> graphOptional = commonService.getGraphByUuid(uuid);

        // Assert
        Assertions.assertFalse(graphOptional.isPresent());
    }

    /**
     * Tests that creating and binding a graph with valid input results in successful creation.
     */
    @Test
    void createAndBindGraphValidInputGraphCreatedSuccessfully() {
        // Arrange
        final GraphCreateDTO graphCreateDTO = GraphCreateDTO.builder()
                .title(TestConstants.TEST_TILE1)
                .description(TestConstants.TEST_DESCRIPTION1)
                .userUidcid(TestConstants.TEST_ID1)
                .build();

        final User user = User.builder()
                .uidcid(TestConstants.TEST_ID1)
                .build();

        final Graph graph = Graph.builder()
                .uuid(TestConstants.TEST_ID2)
                .build();

        final Transaction tx = mock(Transaction.class);

        when(userRepository.getUserByUidcid(eq(TestConstants.TEST_ID1))).thenReturn(user);
        when(graphMapper.createGraph(any(String.class), any(String.class), any(String.class),
                any(String.class), any(String.class), any(String.class), eq(tx))).thenReturn(graph);

        // Act
        final Graph result = commonService.createAndBindGraph(graphCreateDTO, tx);

        // Assert
        assertEquals(graph, result);
        verify(userRepository, times(1)).getUserByUidcid(eq(TestConstants.TEST_ID1));
        verify(graphMapper, times(1)).createGraph(any(String.class), any(String.class), any(String.class),
                any(String.class), any(String.class), any(String.class), eq(tx));
    }

    /**
     * Tests that creating and binding a graph with a non-existent user throws UserNullException.
     */
    @Test
    public void createAndBindGraphUserDoesNotExistThrowsUserNullException() {
        // Arrange
        final GraphCreateDTO graphCreateDTO = GraphCreateDTO.builder()
                .title(TestConstants.TEST_TILE1)
                .description(TestConstants.TEST_DESCRIPTION1)
                .userUidcid(TestConstants.TEST_ID1)
                .build();

        final Transaction tx = mock(Transaction.class);

        when(userRepository.getUserByUidcid(eq(TestConstants.TEST_ID1))).thenReturn(null);

        // Act & Assert
        assertThrows(UserNullException.class, () -> commonService.createAndBindGraph(graphCreateDTO, tx));
        verify(userRepository, times(1)).getUserByUidcid(eq(TestConstants.TEST_ID1));
        verify(graphMapper, never()).createGraph(any(String.class), any(String.class), any(String.class),
                any(String.class), any(String.class), any(String.class), eq(tx));
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
