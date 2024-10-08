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

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.common.util.Neo4jUtil;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.Neo4jService;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Service implementation for Neo4j operations.
 * This class provides methods for querying and updating data in Neo4j.
 */
@Service
public class Neo4jServiceImpl implements Neo4jService {

    @Autowired
    private UserRepository userRepository;

    private final Driver driver;

    /**
     * Constructs a Neo4jServiceImpl object with the specified Driver.
     * @param driver the Driver instance
     */
    @Autowired
    public Neo4jServiceImpl(final Driver driver) {
        this.driver = driver;
    }

    /**
     * Updates a graph node by its UUID.
     *
     * @param uuid the UUID of the graph node
     * @param title the new title of the graph node
     * @param description the new description of the graph node
     * @param currentTime the current time for update
     */
    @Transactional
    @Override
    public void updateNodeByUuid(final String uuid, final String title,
                                 final String description, final String currentTime) {
        final StringBuilder cypherQuery = new StringBuilder("MATCH (gn:GraphNode { uuid: $uuid }) ");
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constants.UUID, uuid);

        if (title != null) {
            cypherQuery.append("SET gn.title = $title ");
            parameters.put(Constants.TITLE, title);
        }

        if (description != null) {
            cypherQuery.append("SET gn.description = $description ");
            parameters.put(Constants.DESCRIPTION, description);
        }

        cypherQuery.append("SET gn.update_time = $updateTime ");
        parameters.put(Constants.UPDATE_TIME, currentTime);

        cypherQuery.append("RETURN gn");

        try (Session session = driver.session()) {
            session.run(cypherQuery.toString(), parameters);
        }
    }
}
