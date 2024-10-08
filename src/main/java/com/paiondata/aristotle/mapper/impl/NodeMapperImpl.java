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
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.entity.GraphNode;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Repository class for executing Cypher queries related to nodes in Neo4j.
 */
@Repository
public class NodeMapperImpl implements NodeMapper {

    private final Driver driver;

    /**
     * Constructs a NodeMapperImpl object with the specified Driver.
     * @param driver the Driver instance
     */
    public NodeMapperImpl(Driver driver) {
        this.driver = driver;
    }

    /**
     * Creates a node in the Neo4j database.
     * @param graphUuid the UUID of the graph
     * @param nodeUuid the UUID of the node
     * @param relationUuid the UUID of the relation
     * @param currentTime the current time
     * @param nodeDTO the NodeDTO object containing the node properties
     * @param tx the Neo4j transaction
     * @return the created Node object
     */
    @Override
    public GraphNode createNode(final String graphUuid, final String nodeUuid, final String relationUuid,
                                final String currentTime, final NodeDTO nodeDTO, final Transaction tx) {
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
    }

    @Override
    public List<Map<String, Map<String, Object>>> getNodesByGraphUuid(final String uuid) {
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
     * Binds two graph nodes with a specified relationship.
     *
     * @param uuid1           the UUID of the first graph node
     * @param uuid2           the UUID of the second graph node
     * @param relation        the name of the relationship
     * @param relationUuid    the UUID of the relationship
     * @param currentTime     the current timestamp
     * @param tx the Neo4j transaction
     */
    @Override
    public void bindGraphNodeToGraphNode(final String uuid1, final String uuid2, final String relation,
                                         final String relationUuid, final String currentTime, final Transaction tx) {
        final String cypherQuery = "MATCH (gn1:GraphNode) WHERE gn1.uuid = $uuid1 SET gn1.update_time = $currentTime "
                + "WITH gn1 "
                + "MATCH (gn2:GraphNode) WHERE gn2.uuid = $uuid2 SET gn2.update_time = $currentTime "
                + "WITH gn1,gn2 "
                + "CREATE (gn1)-[r:RELATION{name: $relation, uuid: $relationUuid, "
                + "create_time: $currentTime, update_time: $currentTime}]->(gn2)";

        tx.run(cypherQuery, Values.parameters(
                "uuid1", uuid1,
                "uuid2", uuid2,
                Constants.RELATION, relation,
                Constants.CURRENT_TIME, currentTime,
                Constants.RELATION_UUID, relationUuid
        ));
    }

    /**
     * Updates a graph node by its UUID.
     *
     * @param nodeUpdateDTO the NodeUpdateDTO object containing the updated node properties
     * @param currentTime the current time for update
     * @param tx the Neo4j transaction
     */
    @Override
    public void updateNodeByUuid(final NodeUpdateDTO nodeUpdateDTO, final String currentTime, final Transaction tx) {

        final StringBuilder setProperties = new StringBuilder();
        for (final Map.Entry<String, String> entry : nodeUpdateDTO.getProperties().entrySet()) {
            setProperties.append(", ").append(entry.getKey()).append(": '").append(entry.getValue()).append("'");
        }

        final String cypherQuery = "MATCH (gn:GraphNode {uuid: $nodeUuid}) "
                + "SET gn = { uuid: gn.uuid, "
                + "create_time: gn.create_time, "
                + "update_time: $updateTime"
                + setProperties
                + " }";

        System.out.println(cypherQuery);

        tx.run(cypherQuery, Values.parameters(
                Constants.NODE_UUID, nodeUpdateDTO.getUuid(),
                Constants.UPDATE_TIME, currentTime));
    }
}
