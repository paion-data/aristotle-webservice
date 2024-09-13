package com.paiondata.aristotle.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.common.exception.UserUidcidExistsException;
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
    Neo4jService neo4jService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void getUserVOByUidcid_UserExists_ReturnsUserVO() {
        // Arrange
        String uidcid = "testUidcid";
        String nickName = "testNickName";
        User user = User.builder()
                .uidcid(uidcid)
                .nickName(nickName)
                .build();

        List<Map<String, Object>> graphs = Collections.singletonList(Collections.singletonMap("key", "value"));

        when(userRepository.getUserByUidcid(uidcid)).thenReturn(user);
        when(neo4jService.getUserAndGraphsByUidcid(uidcid)).thenReturn(graphs);

        // Act
        UserVO userVO = userService.getUserVOByUidcid(uidcid);

        // Assert
        Assertions.assertEquals(uidcid, userVO.getUidcid());
        Assertions.assertEquals(nickName, userVO.getNickName());
        Assertions.assertEquals(graphs, userVO.getGraphs());

        verify(userRepository, times(1)).getUserByUidcid(uidcid);
        verify(neo4jService, times(1)).getUserAndGraphsByUidcid(uidcid);
    }

    @Test
    public void getUserVOByUidcid_UserDoesNotExist_ThrowsUserNullException() {
        // Arrange
        String uidcid = "testUidcid";
        when(userRepository.getUserByUidcid(uidcid)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNullException.class, () -> userService.getUserVOByUidcid(uidcid));

        verify(userRepository, times(1)).getUserByUidcid(uidcid);
        verify(neo4jService, never()).getUserAndGraphsByUidcid(anyString());
    }

    @Test
    public void getUserByUidcid_UserExists_ReturnsUser() {
        // Arrange
        String uidcid = "testUidcid";
        User expectedUser = User.builder()
                .uidcid(uidcid)
                .nickName("testNickName")
                .build();

        when(userRepository.getUserByUidcid(uidcid)).thenReturn(expectedUser);

        // Act
        Optional<User> userOptional = userService.getUserByUidcid(uidcid);

        // Assert
        Assertions.assertTrue(userOptional.isPresent());
        Assertions.assertEquals(expectedUser, userOptional.get());
    }

    @Test
    public void getUserByUidcid_UserDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        String uidcid = "nonExistentUidcid";

        when(userRepository.getUserByUidcid(uidcid)).thenReturn(null);

        // Act
        Optional<User> userOptional = userService.getUserByUidcid(uidcid);

        // Assert
        Assertions.assertFalse(userOptional.isPresent());
    }

    @Test
    public void getAllUsers_UsersExist_ReturnsListOfUserVOs() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(User.builder().uidcid("uid1").nickName("nick1").build());
        users.add(User.builder().uidcid("uid2").nickName("nick2").build());

        List<Map<String, Object>> graphs1 = Collections.singletonList(Collections.singletonMap("key", "value1"));
        List<Map<String, Object>> graphs2 = Collections.singletonList(Collections.singletonMap("key", "value2"));

        when(userRepository.findAll()).thenReturn(users);
        when(neo4jService.getUserAndGraphsByUidcid("uid1")).thenReturn(graphs1);
        when(neo4jService.getUserAndGraphsByUidcid("uid2")).thenReturn(graphs2);

        // Act
        List<UserVO> userVOS = userService.getAllUsers();

        // Assert
        Assertions.assertEquals(2, userVOS.size());
        Assertions.assertEquals("uid1", userVOS.get(0).getUidcid());
        Assertions.assertEquals("nick1", userVOS.get(0).getNickName());
        Assertions.assertEquals(graphs1, userVOS.get(0).getGraphs());

        Assertions.assertEquals("uid2", userVOS.get(1).getUidcid());
        Assertions.assertEquals("nick2", userVOS.get(1).getNickName());
        Assertions.assertEquals(graphs2, userVOS.get(1).getGraphs());
    }

    @Test
    public void getAllUsers_NoUsersExist_ReturnsEmptyList() {
        // Arrange
        List<User> users = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserVO> userVOS = userService.getAllUsers();

        // Assert
        Assertions.assertEquals(0, userVOS.size());
    }

    @Test
    public void createUser_UserInfoValid_CreatesUser() {
        // Arrange
        String uidcid = "testUidcid";
        String nickName = "testNickName";
        UserCreateDTO user = UserCreateDTO.builder()
                .uidcid(uidcid)
                .nickName(nickName)
                .build();

        // Act
        userService.createUser(user);

        // Assert
        verify(userRepository).createUser(uidcid, nickName);
    }

    @Test
    public void createUser_UserAlreadyExists_ThrowsUserUidcidExistsException() {
        // Arrange
        String uidcid = "existingUidcid";
        String nickName = "existingNickName";
        UserCreateDTO user = UserCreateDTO.builder()
                .uidcid(uidcid)
                .nickName(nickName)
                .build();

        doThrow(new DataIntegrityViolationException("Duplicate entry for UIDCID"))
                .when(userRepository)
                .createUser(uidcid, nickName);

        // Act & Assert
        assertThrows(UserUidcidExistsException.class, () -> userService.createUser(user));

        // Verify
        verify(userRepository).createUser(uidcid, nickName);
    }

    @Test
    public void deleteUser_UsersExist_DeletesUsersAndRelatedData() {
        // Arrange
        List<String> uidcids = Arrays.asList("uid1", "uid2");
        List<User> users = new ArrayList<>();
        users.add(User.builder().uidcid("uid1").build());
        users.add(User.builder().uidcid("uid2").build());

        List<String> graphUuids = Arrays.asList("graph1", "graph2");
        List<String> graphNodeUuids = Arrays.asList("node1", "node2");

        when(userRepository.getUserByUidcid("uid1")).thenReturn(users.get(0));
        when(userRepository.getUserByUidcid("uid2")).thenReturn(users.get(1));
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

    @Test
    public void deleteUser_UserDoesNotExist_ThrowsUserNullException() {
        // Arrange
        List<String> uidcids = Arrays.asList("uid1", "uid2");

        when(userRepository.getUserByUidcid("uid1")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNullException.class, () -> userService.deleteUser(uidcids));

        // Verify
        verify(userRepository, times(1)).getUserByUidcid("uid1");
        verify(userRepository, never()).deleteByUidcids(any());
        verify(graphRepository, never()).deleteByUuids(any());
        verify(graphNodeRepository, never()).deleteByUuids(any());
    }
}
