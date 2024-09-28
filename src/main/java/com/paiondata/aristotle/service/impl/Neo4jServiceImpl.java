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
import com.paiondata.aristotle.common.utils.Neo4jUtil;
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
    private GraphRepository graphRepository;

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
     * Retrieves users and their associated graphs by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     */
    @Transactional(readOnly = true)
    @Override
    public List<Map<String, Object>> getUserAndGraphsByUidcid(final String uidcid) {

        if (userRepository.getUserByUidcid(uidcid) == null) {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }

        final String cypherQuery = "MATCH (u:User)-[r:RELATION]->(g:Graph) WHERE u.uidcid = $uidcid RETURN DISTINCT g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters("uidcid", uidcid));
                final List<Map<String, Object>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    final Record record = result.next();
                    final Map<String, Object> graphNode = Neo4jUtil.extractGraph(record.get("g"));

                    resultList.add(graphNode);
                }
                return resultList;
            });
        }
    }

    /**
     * Retrieves graph nodes and their relationships by graph UUID.
     *
     * @param uuid the UUID of the graph
     */
    @Transactional(readOnly = true)
    @Override
    public List<Map<String, Map<String, Object>>> getGraphNodeByGraphUuid(final String uuid) {

        if (graphRepository.getGraphByUuid(uuid) == null) {
            throw new UserNullException(Message.GRAPH_NULL + uuid);
        }

        final String cypherQuery = "MATCH (g1:Graph { uuid: $uuid }) "
                + "OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode) "
                + "OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
                + "RETURN DISTINCT n1, r, n2";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters(Constants.UUID, uuid));
                final List<Map<String, Map<String, Object>>> resultList = new ArrayList<>();

                while (result.hasNext()) {
                    final Record record = result.next();
                    final Map<String, Object> n1 = Neo4jUtil.extractNode(record.get("n1"));
                    final Map<String, Object> n2 = Neo4jUtil.extractNode(record.get("n2"));
                    final Map<String, Object> relation = Neo4jUtil.extractRelationship(record.get("r"));

                    final Map<String, Map<String, Object>> combinedResult = new HashMap<>();
                    combinedResult.put(Constants.START_NODE, n1);
                    combinedResult.put("relation", relation);
                    combinedResult.put(Constants.END_NODE, n2);

                    resultList.add(combinedResult);
                }

                final Iterator<Map<String, Map<String, Object>>> iterator = resultList.iterator();
                while (iterator.hasNext()) {
                    final Map<String, Map<String, Object>> current = iterator.next();
                    if (current.get(Constants.END_NODE).isEmpty()) {
                        boolean removeFlag = false;
                        for (final Map<String, Map<String, Object>> other : resultList) {
                            if (current.get(Constants.START_NODE).equals(other.get(Constants.END_NODE))
                                    && !other.get(Constants.START_NODE).equals(other.get(Constants.END_NODE))) {
                                removeFlag = true;
                                break;
                            }
                        }
                        if (removeFlag) {
                            iterator.remove();
                        }
                    }
                }

                return resultList;
            });
        }
    }

    /**
     * Updates a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     * @param title the new title of the graph
     * @param description the new description of the graph
     * @param currentTime the current time for update
     */
    @Transactional
    @Override
    public void updateGraphByUuid(final String uuid, final String title,
                                  final String description, final String currentTime) {
        final StringBuilder cypherQuery = new StringBuilder("MATCH (g:Graph { uuid: $uuid }) ");
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constants.UUID, uuid);

        if (title != null) {
            cypherQuery.append("SET g.title = $title ");
            parameters.put(Constants.TITLE, title);
        }

        if (description != null) {
            cypherQuery.append("SET g.description = $description ");
            parameters.put(Constants.DESCRIPTION, description);
        }

        cypherQuery.append("SET g.update_time = $updateTime ");
        parameters.put(Constants.UPDATE_TIME, currentTime);

        cypherQuery.append("RETURN g");

        try (Session session = driver.session()) {
            session.run(cypherQuery.toString(), parameters);
        }
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
