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
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.entity.GraphNode;
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
 * Repository class for executing Cypher queries related to nodes in Neo4j.
 */
@Repository
@Transactional
public class NodeMapperImpl implements NodeMapper {

    private final Driver driver;

    /**
     * Constructs a Neo4jServiceImpl object with the specified Driver.
     * @param driver the Driver instance
     */
    @Autowired
    public NodeMapperImpl(final Driver driver) {
        this.driver = driver;
    }

    /**
     * Creates a node in the Neo4j database.
     * @param graphUuid the UUID of the graph
     * @param nodeUuid the UUID of the node
     * @param relationUuid the UUID of the relation
     * @param currentTime the current time
     * @param nodeDTO the NodeDTO object containing the node properties
     * @return the created Node object
     */
    public GraphNode createNode(final String graphUuid, final String nodeUuid, final String relationUuid,
                                final String currentTime, final NodeDTO nodeDTO) {
        final StringBuilder setProperties = new StringBuilder();
        for (final Map.Entry<String, String> entry : nodeDTO.getProperties().entrySet()) {
            setProperties.append(", ").append(entry.getKey()).append(": '").append(entry.getValue()).append("'");
        }

        final String cypherQuery = "MATCH (g:Graph) WHERE g.uuid = $graphUuid SET g.update_time = $currentTime "
                + "CREATE (gn:GraphNode{uuid:$nodeUuid "
                + setProperties
                + ",create_time:$currentTime,update_time:$currentTime}) "
                + "WITH g, gn "
                + "CREATE (g)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, "
                + "create_time: $currentTime, update_time: $currentTime}]->(gn) "
                + "RETURN gn";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.writeTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters(
                                Constants.GRAPH_UUID, graphUuid,
                                Constants.NODE_UUID, nodeUuid,
                                Constants.CURRENT_TIME, currentTime,
                                Constants.RELATION_UUID, relationUuid
                        )
                );

                final Record record = result.next();
                final Map<String, Object> objectMap = Neo4jUtil.extractNode(record.get("gn"));

                return GraphNode.builder()
                        .id((Long) objectMap.get(Constants.ID))
                        .uuid((String) objectMap.get(Constants.UUID))
                        .properties((Map<String, String>) objectMap.get(Constants.PROPERTIES))
                        .createTime((String) objectMap.get(Constants.CREATE_TIME))
                        .updateTime((String) objectMap.get(Constants.UPDATE_TIME))
                        .build();
            });
        }
    }
}
