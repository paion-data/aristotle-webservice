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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.base.TestConstants;
import com.paiondata.aristotle.common.exception.UserExistsException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceSpec {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private GraphNodeRepository graphNodeRepository;

    @Mock
    private Neo4jService neo4jService;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setup() {
    }

    /**
     * Tests that getting a UserVO by UIDCID returns the correct UserVO when the user exists.
     */
    @Test
    public void getUserVOByUidcidUserExistsReturnsUserVO() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String nickName = TestConstants.TEST_NAME1;
        final User user = User.builder()
                .uidcid(uidcid)
                .nickName(nickName)
                .build();

        final List<Map<String, Object>> graphs = Collections.singletonList(Collections.singletonMap(
                TestConstants.TEST_KEY1, TestConstants.TEST_VALUE1));

        when(userRepository.getUserByUidcid(uidcid)).thenReturn(user);
        when(neo4jService.getUserAndGraphsByUidcid(uidcid)).thenReturn(graphs);

        // Act
        final UserVO userVO = userService.getUserVOByUidcid(uidcid);

        // Assert
        Assertions.assertEquals(uidcid, userVO.getUidcid());
        Assertions.assertEquals(nickName, userVO.getNickName());
        Assertions.assertEquals(graphs, userVO.getGraphs());

        verify(userRepository, times(1)).getUserByUidcid(uidcid);
        verify(neo4jService, times(1)).getUserAndGraphsByUidcid(uidcid);
    }

    /**
     * Tests that getting a UserVO by UIDCID throws a UserNullException when the user does not exist.
     */
    @Test
    public void getUserVOByUidcidUserDoesNotExistThrowsUserNullException() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        when(userRepository.getUserByUidcid(uidcid)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNullException.class, () -> userService.getUserVOByUidcid(uidcid));

        verify(userRepository, times(1)).getUserByUidcid(uidcid);
        verify(neo4jService, never()).getUserAndGraphsByUidcid(anyString());
    }

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
        final Optional<User> userOptional = userService.getUserByUidcid(uidcid);

        // Assert
        Assertions.assertTrue(userOptional.isPresent());
        Assertions.assertEquals(expectedUser, userOptional.get());
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
        final Optional<User> userOptional = userService.getUserByUidcid(uidcid);

        // Assert
        Assertions.assertFalse(userOptional.isPresent());
    }

    /**
     * Tests that getting all users returns a list of UserVOs when users exist.
     */
    @Test
    public void getAllUsersUsersExistReturnsListOfUserVOs() {
        // Arrange
        final List<User> users = new ArrayList<>();
        users.add(User.builder().uidcid(TestConstants.TEST_ID1).nickName(TestConstants.TEST_NAME1).build());
        users.add(User.builder().uidcid(TestConstants.TEST_ID2).nickName(TestConstants.TEST_NAME2).build());

        final List<Map<String, Object>> graphs1 = Collections.singletonList(Collections.singletonMap(
                TestConstants.TEST_KEY1, TestConstants.TEST_VALUE1));
        final List<Map<String, Object>> graphs2 = Collections.singletonList(Collections.singletonMap(
                TestConstants.TEST_KEY2, TestConstants.TEST_VALUE1));

        when(userRepository.findAll()).thenReturn(users);
        when(neo4jService.getUserAndGraphsByUidcid(TestConstants.TEST_ID1)).thenReturn(graphs1);
        when(neo4jService.getUserAndGraphsByUidcid(TestConstants.TEST_ID2)).thenReturn(graphs2);

        // Act
        final List<UserVO> userVOS = userService.getAllUsers();

        // Assert
        Assertions.assertEquals(2, userVOS.size());
        Assertions.assertEquals(TestConstants.TEST_ID1, userVOS.get(0).getUidcid());
        Assertions.assertEquals(TestConstants.TEST_NAME1, userVOS.get(0).getNickName());
        Assertions.assertEquals(graphs1, userVOS.get(0).getGraphs());

        Assertions.assertEquals(TestConstants.TEST_ID2, userVOS.get(1).getUidcid());
        Assertions.assertEquals(TestConstants.TEST_NAME2, userVOS.get(1).getNickName());
        Assertions.assertEquals(graphs2, userVOS.get(1).getGraphs());
    }

    /**
     * Tests that getting all users returns an empty list when no users exist.
     */
    @Test
    public void getAllUsersNoUsersExistReturnsEmptyList() {
        // Arrange
        final List<User> users = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(users);

        // Act
        final List<UserVO> userVOS = userService.getAllUsers();

        // Assert
        Assertions.assertEquals(0, userVOS.size());
    }

    /**
     * Tests that creating a user works correctly when the user information is valid.
     */
    @Test
    public void createUserUserInfoValidCreatesUser() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String nickName = TestConstants.TEST_NAME1;
        final UserCreateDTO user = UserCreateDTO.builder()
                .uidcid(uidcid)
                .nickName(nickName)
                .build();

        // Act
        userService.createUser(user);

        // Assert
        verify(userRepository).createUser(uidcid, nickName);
    }

    /**
     * Tests that creating a user throws a UserExistsException when the user already exists.
     */
    @Test
    public void createUserUserAlreadyExistsThrowsUserUidcidExistsException() {
        // Arrange
        final String uidcid = TestConstants.TEST_ID1;
        final String nickName = TestConstants.TEST_NAME1;
        final UserCreateDTO user = UserCreateDTO.builder()
                .uidcid(uidcid)
                .nickName(nickName)
                .build();

        doThrow(new DataIntegrityViolationException("Duplicate entry for UIDCID"))
                .when(userRepository)
                .createUser(uidcid, nickName);

        // Act & Assert
        assertThrows(UserExistsException.class, () -> userService.createUser(user));

        // Verify
        verify(userRepository).createUser(uidcid, nickName);
    }

    /**
     * Tests that deleting users works correctly when the users exist.
     */
    @Test
    public void deleteUserUsersExistDeletesUsersAndRelatedData() {
        // Arrange
        final List<String> uidcids = Arrays.asList(TestConstants.TEST_ID1, TestConstants.TEST_ID2);
        final List<User> users = new ArrayList<>();
        users.add(User.builder().uidcid(TestConstants.TEST_ID1).build());
        users.add(User.builder().uidcid(TestConstants.TEST_ID2).build());

        final List<String> graphUuids = Arrays.asList("graph1", "graph2");
        final List<String> graphNodeUuids = Arrays.asList("node1", "node2");

        when(userRepository.getUserByUidcid(TestConstants.TEST_ID1)).thenReturn(users.get(0));
        when(userRepository.getUserByUidcid(TestConstants.TEST_ID2)).thenReturn(users.get(1));
        when(userRepository.getGraphUuidsByUserUidcid(uidcids)).thenReturn(graphUuids);
        when(graphRepository.getGraphNodeUuidsByGraphUuids(graphUuids)).thenReturn(graphNodeUuids);

        // Act
        userService.deleteUser(uidcids);

        // Assert
        verify(userRepository, times(2)).getUserByUidcid(anyString());
        verify(userRepository).deleteByUidcids(uidcids);
        verify(graphRepository).deleteByUuids(graphUuids);
        verify(graphNodeRepository).deleteByUuids(graphNodeUuids);
    }

    /**
     * Tests that deleting users throws a UserNullException when a user does not exist.
     */
    @Test
    public void deleteUserUserDoesNotExistThrowsUserNullException() {
        // Arrange
        final List<String> uidcids = Arrays.asList(TestConstants.TEST_ID1, TestConstants.TEST_ID2);

        when(userRepository.getUserByUidcid(TestConstants.TEST_ID1)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNullException.class, () -> userService.deleteUser(uidcids));

        // Verify
        verify(userRepository, times(1)).getUserByUidcid(TestConstants.TEST_ID1);
        verify(userRepository, never()).deleteByUidcids(any());
        verify(graphRepository, never()).deleteByUuids(any());
        verify(graphNodeRepository, never()).deleteByUuids(any());
    }
}
