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
import com.paiondata.aristotle.common.exception.UserUidcidExistsException;
import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.Neo4jService;
import com.paiondata.aristotle.service.UserService;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphNodeRepository graphNodeRepository;

    @Autowired
    private Neo4jService neo4jService;

    @Override
    @Transactional(readOnly = true)
    public UserVO getUserVOByUidcid(String uidcid) {
        User user = userRepository.getUserByUidcid(uidcid);

        if (user == null) {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }

        UserVO vo = UserVO.builder()
                .uidcid(user.getUidcid())
                .nickName(user.getNickName())
                .graphs(neo4jService.getUserAndGraphsByUidcid(user.getUidcid()))
                .build();

        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUidcid(String uidcid) {
        User user = userRepository.getUserByUidcid(uidcid);
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserVO.builder()
                        .uidcid(user.getUidcid())
                        .nickName(user.getNickName())
                        .graphs(neo4jService.getUserAndGraphsByUidcid(user.getUidcid()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createUser(UserCreateDTO user) {
        String uidcid = user.getUidcid();

        try {
            userRepository.createUser(uidcid, user.getNickName());
        } catch (DataIntegrityViolationException e) {
            throw new UserUidcidExistsException(Message.UIDCID_EXISTS + uidcid);
        }
    }

    @Transactional
    @Override
    public void updateUser(UserUpdateDTO user) {
        String uidcid = user.getUidcid();

        if (userRepository.checkUidcidExists(uidcid) != 0) {
            userRepository.updateUser(uidcid, user.getNickName());
        } else {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }
    }

    @Transactional
    @Override
    public void deleteUser(List<String> uidcids) {
        for (String uidcid : uidcids) {
            if (getUserByUidcid(uidcid).isEmpty()) {
                throw new UserNullException(Message.USER_NULL + uidcid);
            }
        }

        List<String> graphUuids = getRelatedGraphUuids(uidcids);
        List<String> graphNodeUuids = getRelatedGraphNodeUuids(graphUuids);

        userRepository.deleteByUidcids((uidcids));
        graphRepository.deleteByUuids(graphUuids);
        graphNodeRepository.deleteByUuids(graphNodeUuids);
    }

    private List<String> getRelatedGraphUuids(List<String> userUidcids) {
        return userRepository.getGraphUuidsByUserUidcid(userUidcids);
    }

    private List<String> getRelatedGraphNodeUuids(List<String> graphUuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(graphUuids);
    }
}
