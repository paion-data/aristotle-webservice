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
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.CommonService;

import org.neo4j.driver.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.UUID;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the CommonService interface.
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphMapper graphMapper;

    /**
     * Retrieves an optional user by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     * @return an Optional containing the user if found, or empty otherwise
     */
    @Override
    public Optional<User> getUserByUidcid(final String uidcid) {
        final User user = userRepository.getUserByUidcid(uidcid);
        return Optional.ofNullable(user);
    }

    /**
     * Retrieves a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     */
    @Override
    public Optional<Graph> getGraphByUuid(final String uuid) {
        final Graph graph = graphRepository.getGraphByUuid(uuid);
        return Optional.ofNullable(graph);
    }

    /**
     * Retrieves users' associated graphs by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     *
     * @return a list of maps containing user information and associated graphs
     */
    @Override
    public List<Map<String, Object>> getGraphsByUidcid(final String uidcid) {
        if (userRepository.getUserByUidcid(uidcid) == null) {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }

        return graphMapper.getGraphsByUidcid(uidcid);
    }

    /**
     * Creates and binds a new graph using the provided DTO.
     *
     * @param graphCreateDTO the DTO containing details to create a new graph
     */
    @Override
    public Graph createAndBindGraph(final GraphCreateDTO graphCreateDTO, final Transaction tx) {
        final String title = graphCreateDTO.getTitle();
        final String description = graphCreateDTO.getDescription();
        final String uidcid = graphCreateDTO.getUserUidcid();
        final String graphUuid = UUID.fastUUID().toString(true);
        final String relationUuid = UUID.fastUUID().toString(true);
        final String currentTime = getCurrentTime();

        final Optional<User> optionalUser = getUserByUidcid(uidcid);
        if (optionalUser.isEmpty()) {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }

        return graphMapper.createGraph(title, description, uidcid, graphUuid, relationUuid, currentTime, tx);
    }

    /**
     * Gets the current time in the specified format.
     *
     * @return the current time
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
