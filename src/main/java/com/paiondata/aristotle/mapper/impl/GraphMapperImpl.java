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
package com.paiondata.aristotle.mapper.impl;

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.util.Neo4jUtil;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.model.entity.Graph;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * GraphMapperImpl class provides methods for interacting with the Neo4j database using Cypher queries.
 */
@Repository
@Transactional
public class GraphMapperImpl implements GraphMapper {

    private final Driver driver;

    /**
     * Constructs a Neo4jServiceImpl object with the specified Driver.
     * @param driver the Driver instance
     */
    @Autowired
    public GraphMapperImpl(final Driver driver) {
        this.driver = driver;
    }

    /**
     * Creates a new graph with the specified properties.
     * @param title the title of the graph
     * @param description the description of the graph
     * @param userUidcid the UIDCID of the user
     * @param graphUuid the UUID of the graph
     * @param relationUuid the UUID of the relation
     * @param currentTime the current time
     * @return the created Graph object
     */
    public Graph createGraph(final String title, final String description, final String userUidcid,
                             final String graphUuid, final String relationUuid, final String currentTime) {
        final String cypherQuery = "MATCH (u:User) WHERE u.uidcid = $uidcid "
                + "CREATE (g:Graph {uuid: $graphUuid, title: $title, description: $description, "
                + "create_time: $currentTime, update_time: $currentTime}) "
                + "WITH u, g "
                + "CREATE (u)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime, "
                + "update_time: $currentTime}]->(g) "
                + "RETURN g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.writeTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters(
                        Constants.TITLE, title,
                                Constants.DESCRIPTION, description,
                                Constants.UIDCID, userUidcid,
                                Constants.GRAPH_UUID, graphUuid,
                                Constants.CURRENT_TIME, currentTime,
                                Constants.RELATION_UUID, relationUuid
                        )
                );

                final Record record = result.next();
                final Map<String, Object> objectMap = Neo4jUtil.extractGraph(record.get("g"));

                return Graph.builder()
                        .id((Long) objectMap.get(Constants.ID))
                        .uuid((String) objectMap.get(Constants.UUID))
                        .title((String) objectMap.get(Constants.TITLE))
                        .description((String) objectMap.get(Constants.DESCRIPTION))
                        .createTime((String) objectMap.get(Constants.CREATE_TIME))
                        .updateTime((String) objectMap.get(Constants.UPDATE_TIME))
                        .build();
            });
        }
    }
}
