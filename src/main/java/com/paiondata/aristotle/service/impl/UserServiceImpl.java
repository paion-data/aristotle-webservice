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
package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.common.exception.UserExistsException;
import com.paiondata.aristotle.model.dto.UserDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.CommonService;
import com.paiondata.aristotle.service.UserService;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for user-related operations.
 * This class provides methods for managing users, including creating, updating, and deleting users.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CommonService commonService;

    /**
     * Retrieves a UserVO by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     * @return the UserVO containing user details and associated graphs
     */
    @Transactional(readOnly = true)
    @Override
    public UserVO getUserVOByUidcid(final String uidcid) {
        final User user = userRepository.getUserByUidcid(uidcid);

        if (user == null) {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }

        return UserVO.builder()
                .uidcid(user.getUidcid())
                .nickName(user.getNickName())
                .graphs(commonService.getGraphsByUidcid(user.getUidcid()))
                .build();
    }

    /**
     * Retrieves all users as UserVOs.
     *
     * @return a list of UserVOs containing user details and associated graphs
     */
    @Transactional(readOnly = true)
    @Override
    public List<UserVO> getAllUsers() {
        final List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserVO.builder()
                        .uidcid(user.getUidcid())
                        .nickName(user.getNickName())
                        .graphs(commonService.getGraphsByUidcid(user.getUidcid()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user.
     * @param user the UserCreateDTO containing user details
     *
     * @return the UserDTO containing the created user details
     */
    @Transactional
    @Override
    public UserDTO createUser(final UserDTO user) {
        final String uidcid = user.getUidcid();

        try {
            final User returnUser = userRepository.createUser(uidcid, user.getNickName());
            return new UserDTO(returnUser.getUidcid(), returnUser.getNickName());
        } catch (final DataIntegrityViolationException e) {
            throw new UserExistsException(Message.UIDCID_EXISTS + uidcid);
        }
    }

    /**
     * Updates an existing user.
     *
     * @param userDTO the UserDTO containing updated user details
     */
    @Transactional
    @Override
    public void updateUser(final UserDTO userDTO) {
        final String uidcid = userDTO.getUidcid();

        if (userRepository.checkUidcidExists(uidcid) != 0) {
            userRepository.updateUser(uidcid, userDTO.getNickName());
        } else {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }
    }

    /**
     * Deletes multiple users along with their related graphs and graph nodes.
     *
     * @param uidcids a list of UIDCIDs of the users to be deleted
     */
    @Transactional
    @Override
    public void deleteUser(final List<String> uidcids) {
        for (final String uidcid : uidcids) {
            if (commonService.getUserByUidcid(uidcid).isEmpty()) {
                throw new UserNullException(Message.USER_NULL + uidcid);
            }
        }

        final List<String> graphUuids = getRelatedGraphUuids(uidcids);
        final List<String> graphNodeUuids = getRelatedGraphNodeUuids(graphUuids);

        userRepository.deleteByUidcids((uidcids));
        graphRepository.deleteByUuids(graphUuids);
        nodeRepository.deleteByUuids(graphNodeUuids);
    }

    /**
     * Retrieves the UUIDs of related graphs for a list of user UIDCIDs.
     *
     * @param userUidcids a list of user UIDCIDs
     * @return a list of related graph UUIDs
     */
    private List<String> getRelatedGraphUuids(final List<String> userUidcids) {
        return userRepository.getGraphUuidsByUserUidcid(userUidcids);
    }

    /**
     * Retrieves the UUIDs of related graph nodes for a list of graph UUIDs.
     *
     * @param graphUuids a list of graph UUIDs
     * @return a list of related graph node UUIDs
     */
    private List<String> getRelatedGraphNodeUuids(final List<String> graphUuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(graphUuids);
    }
}
