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
import com.paiondata.aristotle.model.dto.UserDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.CommonService;
import com.paiondata.aristotle.service.UserService;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service implementation for user-related operations.
 * This class provides methods for managing users, including creating, updating, and deleting users.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CommonService commonService;

    /**
     * Retrieves a user view object (VO) by their unique identifier (oidcid).
     *
     * Attempts to find the user by their oidcid using the {@link UserRepository#getUserByOidcid(String)} method.
     * Throws a {@link NoSuchElementException} if the user is not found.
     * Constructs and returns a {@link UserVO} object containing the user's details and their associated graphs.
     * The associated graphs are retrieved using the {@link CommonService#getGraphsByOidcid(String)} method.
     *
     * @param oidcid the unique identifier of the user
     *
     * @return a {@link UserVO} object representing the user and their associated graphs
     *
     * @throws NoSuchElementException if the user with the specified oidcid is not found
     */
    @Transactional(readOnly = true)
    @Override
    public UserVO getUserVOByOidcid(final String oidcid) {
        final User user = userRepository.getUserByOidcid(oidcid);

        if (user == null) {
            final String message = String.format(Message.USER_NULL, oidcid);
            LOG.error(message);
            throw new NoSuchElementException(message);
        }

        return UserVO.builder()
                .oidcid(user.getOidcid())
                .nickName(user.getNickName())
                .graphs(commonService.getGraphsByOidcid(user.getOidcid()))
                .build();
    }

    /**
     * Retrieves a list of all users as user view objects (VOs).
     *
     * Retrieves all users from the repository using the {@link UserRepository#findAll()} method.
     * Maps each user to a {@link UserVO} object containing the user's details and their associated graphs.
     * The associated graphs are retrieved using the {@link CommonService#getGraphsByOidcid(String)} method.
     * Returns a list of {@link UserVO} objects.
     *
     * @return a list of {@link UserVO} objects representing all users and their associated graphs
     */
    @Transactional(readOnly = true)
    @Override
    public List<UserVO> getAllUsers() {
        final List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserVO.builder()
                        .oidcid(user.getOidcid())
                        .nickName(user.getNickName())
                        .graphs(commonService.getGraphsByOidcid(user.getOidcid()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user.
     * <p>
     * Attempts to create a new user in the repository using
     * the {@link UserRepository#createUser(String, String)} method.
     * If the user creation fails due to a data integrity violation (e.g., duplicate oidcid),
     * a {@link IllegalArgumentException} is thrown.
     * Returns a {@link UserDTO} object containing the details of the newly created user.
     *
     * @param user the {@link UserDTO} object containing the user's details
     *
     * @return a {@link UserDTO} object representing the newly created user
     *
     * @throws IllegalArgumentException if a user with the same oidcid already exists
     */
    @Transactional
    @Override
    public UserDTO createUser(final UserDTO user) {
        final String oidcid = user.getOidcid();

        try {
            final User returnUser = userRepository.createUser(oidcid, user.getNickName());
            return new UserDTO(returnUser.getOidcid(), returnUser.getNickName());
        } catch (final DataIntegrityViolationException e) {
            final String message = String.format(Message.OIDCID_EXISTS, oidcid);
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Updates an existing user.
     *
     * Checks if a user with the given oidcid exists using the {@link UserRepository#checkOidcidExists(String)} method.
     * If the user exists, updates the user's nickname using
     * the {@link UserRepository#updateUser(String, String)} method.
     * If the user does not exist, throws a {@link NoSuchElementException}.
     *
     * @param userDTO the {@link UserDTO} object containing the updated user details
     *
     * @throws NoSuchElementException if the user with the specified oidcid does not exist
     */
    @Transactional
    @Override
    public void updateUser(final UserDTO userDTO) {
        final String oidcid = userDTO.getOidcid();

        if (userRepository.checkOidcidExists(oidcid) != 0) {
            userRepository.updateUser(oidcid, userDTO.getNickName());
        } else {
            final String message = String.format(Message.USER_NULL, oidcid);
            LOG.error(message);
            throw new NoSuchElementException(message);
        }
    }

    /**
     * Deletes multiple users and their related graphs and nodes.
     * <p>
     * Iterates through the provided list of user identifiers (oidcids) and checks if each user exists using
     * the {@link CommonService#getUserByOidcid(String)} method.
     * Throws a {@link NoSuchElementException} if any user does not exist.
     * Retrieves the UUIDs of graphs related to the users using the {@link #getRelatedGraphUuids(List)} method.
     * Retrieves the UUIDs of nodes related to the graphs using the {@link #getRelatedGraphNodeUuids(List)} method.
     * Deletes the users from the user repository using the {@link UserRepository#deleteByOidcids(List)} method.
     * Deletes the related graph from the graph repository using the {@link GraphRepository#deleteByUuids(List)} method.
     * Deletes the related nodes from the node repository using the {@link NodeRepository#deleteByUuids(List)} method.
     *
     * @param oidcids the list of user identifiers to be deleted
     *
     * @throws NoSuchElementException if any user with the specified oidcid does not exist
     */
    @Transactional
    @Override
    public void deleteUser(final List<String> oidcids) {
        for (final String oidcid : oidcids) {
            if (commonService.getUserByOidcid(oidcid).isEmpty()) {
                final String message = String.format(Message.USER_NULL, oidcid);
                LOG.error(message);
                throw new NoSuchElementException(message);
            }
        }

        final List<String> graphUuids = getRelatedGraphUuids(oidcids);
        final List<String> graphNodeUuids = getRelatedGraphNodeUuids(graphUuids);

        userRepository.deleteByOidcids((oidcids));
        graphRepository.deleteByUuids(graphUuids);
        nodeRepository.deleteByUuids(graphNodeUuids);
    }

    /**
     * Retrieves the UUIDs of related graphs for a list of user OIDC IDs.
     *
     * @param userOidcids a list of user OIDC IDs
     *
     * @return a list of related graph UUIDs
     */
    private List<String> getRelatedGraphUuids(final List<String> userOidcids) {
        return userRepository.getGraphUuidsByUserOidcid(userOidcids);
    }

    /**
     * Retrieves the UUIDs of related graph nodes for a list of graph UUIDs.
     *
     * @param graphUuids a list of graph UUIDs
     *
     * @return a list of related graph node UUIDs
     */
    private List<String> getRelatedGraphNodeUuids(final List<String> graphUuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(graphUuids);
    }
}
