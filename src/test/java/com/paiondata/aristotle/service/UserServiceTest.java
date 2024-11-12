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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.common.base.TestConstants;
import com.paiondata.aristotle.model.dto.UserDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.repository.NodeRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GraphRepository graphRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private CommonService commonService;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setup() {
    }

    /**
     * Tests that getting a UserVO by OIDC ID returns the correct UserVO when the user exists.
     */
    @Test
    public void getUserVOByOidcidUserExistsReturnsUserVO() {
        // Arrange
        final String oidcid = TestConstants.TEST_ID1;
        final String username = TestConstants.TEST_NAME1;
        final User user = User.builder()
                .oidcid(oidcid)
                .username(username)
                .build();

        final List<Map<String, Object>> graphs = Collections.singletonList(Collections.singletonMap(
                TestConstants.TEST_KEY1, TestConstants.TEST_VALUE1));

        when(userRepository.getUserByOidcid(oidcid)).thenReturn(user);
        when(commonService.getGraphsByOidcid(oidcid)).thenReturn(graphs);

        // Act
        final UserVO userVO = userService.getUserVOByOidcid(oidcid);

        // Assert
        Assertions.assertEquals(oidcid, userVO.getOidcid());
        Assertions.assertEquals(username, userVO.getUsername());
        Assertions.assertEquals(graphs, userVO.getGraphs());

        verify(userRepository, times(1)).getUserByOidcid(oidcid);
        verify(commonService, times(1)).getGraphsByOidcid(oidcid);
    }

    /**
     * Tests that getting a UserVO by OIDC ID throws a NoSuchElementException when the user does not exist.
     */
    @Test
    public void getUserVOByOidcidUserDoesNotExistThrowsNoSuchElementException() {
        // Arrange
        final String oidcid = TestConstants.TEST_ID1;
        when(userRepository.getUserByOidcid(oidcid)).thenReturn(null);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.getUserVOByOidcid(oidcid));

        verify(userRepository, times(1)).getUserByOidcid(oidcid);
        verify(commonService, never()).getGraphsByOidcid(anyString());
    }

    /**
     * Tests that getting all users returns a list of UserVOs when users exist.
     */
    @Test
    public void getAllUsersUsersExistReturnsListOfUserVOs() {
        // Arrange
        final List<User> users = new ArrayList<>();
        users.add(User.builder().oidcid(TestConstants.TEST_ID1).username(TestConstants.TEST_NAME1).build());
        users.add(User.builder().oidcid(TestConstants.TEST_ID2).username(TestConstants.TEST_NAME2).build());

        final List<Map<String, Object>> graphs1 = Collections.singletonList(Collections.singletonMap(
                TestConstants.TEST_KEY1, TestConstants.TEST_VALUE1));
        final List<Map<String, Object>> graphs2 = Collections.singletonList(Collections.singletonMap(
                TestConstants.TEST_KEY2, TestConstants.TEST_VALUE1));

        when(userRepository.findAll()).thenReturn(users);
        when(commonService.getGraphsByOidcid(TestConstants.TEST_ID1)).thenReturn(graphs1);
        when(commonService.getGraphsByOidcid(TestConstants.TEST_ID2)).thenReturn(graphs2);

        // Act
        final List<UserVO> userVOS = userService.getAllUsers();

        // Assert
        Assertions.assertEquals(2, userVOS.size());
        Assertions.assertEquals(TestConstants.TEST_ID1, userVOS.get(0).getOidcid());
        Assertions.assertEquals(TestConstants.TEST_NAME1, userVOS.get(0).getUsername());
        Assertions.assertEquals(graphs1, userVOS.get(0).getGraphs());

        Assertions.assertEquals(TestConstants.TEST_ID2, userVOS.get(1).getOidcid());
        Assertions.assertEquals(TestConstants.TEST_NAME2, userVOS.get(1).getUsername());
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
        final String oidcid = TestConstants.TEST_ID1;
        final String username = TestConstants.TEST_NAME1;
        final UserDTO user = UserDTO.builder()
                .oidcid(oidcid)
                .username(username)
                .build();

        // Mock the repository call
        when(userRepository.createUser(oidcid, username)).thenReturn(User.builder()
                .oidcid(oidcid).username(username).build());

        // Act
        final UserDTO createdUser = userService.createUser(user);

        // Assert
        verify(userRepository).createUser(oidcid, username);
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(createdUser.getOidcid(), oidcid);
        Assertions.assertEquals(createdUser.getUsername(), username);
    }

    /**
     * Tests that creating a user throws a IllegalArgumentException when the user already exists.
     */
    @Test
    public void createUserUserAlreadyExistsThrowsUserOidcidExistsException() {
        // Arrange
        final String oidcid = TestConstants.TEST_ID1;
        final String username = TestConstants.TEST_NAME1;
        final UserDTO user = UserDTO.builder()
                .oidcid(oidcid)
                .username(username)
                .build();

        doThrow(new DataIntegrityViolationException("Duplicate entry for OIDCID"))
                .when(userRepository)
                .createUser(oidcid, username);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

        // Verify
        verify(userRepository).createUser(oidcid, username);
    }

    /**
     * Tests that updating a user works correctly when the user information is valid.
     */
    @Test
    void testUpdateUserSuccess() {
        // Arrange
        final String oidcid = TestConstants.TEST_ID1;
        final String username = TestConstants.TEST_NAME1;
        final UserDTO userDTO = UserDTO.builder()
                .oidcid(oidcid)
                .username(username)
                .build();

        when(userRepository.checkOidcidExists(oidcid)).thenReturn(1L);

        // Act & Assert
        assertDoesNotThrow(() -> userService.updateUser(userDTO));

        // Verify
        verify(userRepository).checkOidcidExists(oidcid);
        verify(userRepository).updateUser(oidcid, TestConstants.TEST_NAME1);
    }

    /**
     * Tests that updating a user throws an IllegalArgumentException when the username already exists.
     */
    @Test
    void testUpdateUserUsernameExists() {
        // Arrange
        final String oidcid = TestConstants.TEST_ID1;
        final String username = TestConstants.TEST_NAME1;
        final UserDTO userDTO = UserDTO.builder()
                .oidcid(oidcid)
                .username(username)
                .build();

        when(userRepository.checkOidcidExists(TestConstants.TEST_ID1)).thenReturn(1L);
        doThrow(new DataIntegrityViolationException(""))
                .when(userRepository).updateUser(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userDTO));

        // Verify
        verify(userRepository).checkOidcidExists(TestConstants.TEST_ID1);
        verify(userRepository).updateUser(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);
    }

    /**
     * Tests that updating a user throws a NoSuchElementException when the user does not exist.
     */
    @Test
    void testUpdateUserNotFound() {
        // Arrange
        final String oidcid = TestConstants.TEST_ID1;
        final String username = TestConstants.TEST_NAME1;
        final UserDTO userDTO = UserDTO.builder()
                .oidcid(oidcid)
                .username(username)
                .build();

        when(userRepository.checkOidcidExists(TestConstants.TEST_ID1)).thenReturn(0L);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.updateUser(userDTO));

        // Verify
        verify(userRepository).checkOidcidExists(TestConstants.TEST_ID1);
    }

    /**
     * Tests that deleting users works correctly when the users exist.
     */
    @Test
    public void deleteUserUsersExistDeletesUsersAndRelatedData() {
        // Arrange
        final List<String> oidcids = Arrays.asList(TestConstants.TEST_ID1, TestConstants.TEST_ID2);
        final List<User> users = Arrays.asList(User.builder().oidcid(TestConstants.TEST_ID1).build(),
                User.builder().oidcid(TestConstants.TEST_ID2).build());

        final List<String> graphUuids = Arrays.asList("graph1", "graph2");
        final List<String> graphNodeUuids = Arrays.asList("node1", "node2");

        when(commonService.getUserByOidcid(TestConstants.TEST_ID1)).thenReturn(Optional.ofNullable(users.get(0)));
        when(commonService.getUserByOidcid(TestConstants.TEST_ID2)).thenReturn(Optional.ofNullable(users.get(1)));
        when(userRepository.getGraphUuidsByUserOidcid(oidcids)).thenReturn(graphUuids);
        when(graphRepository.getGraphNodeUuidsByGraphUuids(graphUuids)).thenReturn(graphNodeUuids);

        // Act
        userService.deleteUser(oidcids);

        // Assert
        verify(commonService, times(2)).getUserByOidcid(anyString());
        verify(userRepository).deleteByOidcids(oidcids);
        verify(graphRepository).deleteByUuids(graphUuids);
        verify(nodeRepository).deleteByUuids(graphNodeUuids);
    }

    /**
     * Tests that deleting users throws a NoSuchElementException when a user does not exist.
     */
    @Test
    public void deleteUserUserDoesNotExistThrowsNoSuchElementException() {
        // Arrange
        final List<String> oidcids = Arrays.asList(TestConstants.TEST_ID1, TestConstants.TEST_ID2);

        // Mocking commonService.getUserByOidcid to return empty Optional<User> for one user
        when(commonService.getUserByOidcid(TestConstants.TEST_ID1)).thenReturn(Optional.of(new User()));
        when(commonService.getUserByOidcid(TestConstants.TEST_ID2)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(oidcids));

        // Verify
        verify(commonService, times(2)).getUserByOidcid(anyString());
        verify(userRepository, never()).deleteByOidcids(any());
        verify(graphRepository, never()).deleteByUuids(any());
        verify(nodeRepository, never()).deleteByUuids(any());
    }
}
