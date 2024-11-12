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
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.util.NodeExtractor;
import com.paiondata.aristotle.common.util.RelationShipExtractor;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.GetRelationDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.RelationVO;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repository class for executing Cypher queries related to nodes in Neo4j.
 */
@NoArgsConstructor(force = true)
@Repository
public class NodeMapperImpl implements NodeMapper {

    private static final Logger LOG = LoggerFactory.getLogger(NodeMapperImpl.class);

    private static final String GET_WEAKLY_CONNECTED_NEIGHBORS_CYPHER = "MATCH (g:Graph { uuid: $graphUuid })-" +
            "[:RELATION]->(n:GraphNode { uuid: $nodeUuid}) " +
            "WITH g, n " +
            "MATCH (n)-[relation:RELATION]-(m:GraphNode) " +
            "RETURN m, relation";

    private final Driver driver;

    private final NodeExtractor nodeExtractor;
    private final RelationShipExtractor relationShipExtractor;

    /**
     * Constructs a new NodeMapperImpl object with the specified Driver and NodeExtractor.
     * @param driver the Driver instance
     * @param nodeExtractor the NodeExtractor instance
     * @param relationShipExtractor the RelationShipExtractor instance
     */
    @Autowired
    public NodeMapperImpl(final Driver driver,
                          final NodeExtractor nodeExtractor, final RelationShipExtractor relationShipExtractor) {
        this.driver = driver;
        this.nodeExtractor = nodeExtractor;
        this.relationShipExtractor = relationShipExtractor;
    }

    /**
     * Retrieves a node by its UUID.
     * <p>
     * Constructs a Cypher query to match a node by its UUID and return it.
     * Executes the Cypher query within a read transaction using the Neo4j session.
     * Extracts the node details from the query result and returns a {@link NodeVO} object.
     * If no node is found, returns {@code null}.
     *
     * @param uuid the UUID of the node to retrieve
     *
     * @return a {@link NodeVO} object representing the node, or {@code null} if no node is found
     */
    @Override
    public NodeVO getNodeByUuid(final String uuid) {
        final String cypherQuery = "MATCH (n:GraphNode { uuid: $uuid }) RETURN n";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters(Constants.UUID, uuid));

                NodeVO n = null;
                while (result.hasNext()) {
                    final Record record = result.next();
                    n = nodeExtractor.extractNode(record.get("n"));
                }

                return n;
            });
        }
    }

    /**
     * Creates a new node and associates it with a graph.
     * <p>
     * Constructs a Cypher query to match a graph by its UUID, update its update time, <br>
     * create a new node with the provided details,
     * and establish a relationship between the graph and the new node.
     * Executes the Cypher query using the provided transaction.
     * Extracts the node details from the query result and returns a {@link NodeVO} object.
     *
     * @param graphUuid the UUID of the graph to which the node will be added
     * @param nodeUuid the UUID of the new node
     * @param relationUuid the UUID of the relationship between the graph and the new node
     * @param currentTime the current timestamp for creation and update times
     * @param nodeDTO the DTO containing the properties of the new node
     * @param tx the Neo4j transaction to execute the Cypher query
     *
     * @return a {@link NodeVO} object representing the newly created node
     */
    @Override
    public NodeVO createNode(final String graphUuid, final String nodeUuid, final String relationUuid,
                                final String currentTime, final NodeDTO nodeDTO, final Transaction tx) {
        final StringBuilder setProperties = getSetProperties(nodeDTO.getProperties().entrySet());

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

        return nodeExtractor.extractNode(record.get("gn"));
    }

    /**
     * Retrieves the relationships and nodes associated with a graph by its UUID.
     * <p>
     * Constructs a Cypher query to match a graph by its UUID and find its related nodes and relationships.
     * Optionally filters the nodes based on the provided properties.
     * Executes the Cypher query within a read transaction using the Neo4j session.
     * Extracts the node and relationship details from the query results and <br>
     * returns them in a {@link GetRelationDTO} object.
     *
     * @param uuid the UUID of the graph
     * @param properties a map of properties to filter the nodes (optional)
     *
     * @return a {@link GetRelationDTO} object containing the list of relationships and nodes
     */
    @Override
    public GetRelationDTO getRelationByGraphUuid(final String uuid, final Map<String, String> properties,
                                                 final Integer pageNumber, final Integer pageSize) {
        final int skip = (pageNumber - 1) * pageSize;
        final int limit = pageSize;

        final StringBuilder cypherQuery = new StringBuilder("MATCH (g:Graph { uuid: $uuid })")
                .append(" OPTIONAL MATCH (g)-[:RELATION]->(n:GraphNode) ")
                .append(properties != null && !properties.isEmpty() ?
                        getFilterProperties(Constants.NODE_ALIAS_N, properties) : "")
                .append(" OPTIONAL MATCH (n)-[r:RELATION]->(:GraphNode) ")
                .append(" WITH DISTINCT n, COLLECT(r) AS relations")
                .append(" RETURN DISTINCT n, relations")
                .append(" SKIP $skip ")
                .append(" LIMIT $limit");

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery.toString(), Values.parameters(
                        Constants.UUID, uuid,
                        "skip", skip,
                        "limit", limit
                ));

                final List<RelationVO> relations = new ArrayList<>();
                final List<NodeVO> nodes = new ArrayList<>();
                long totalCount = 0;

                while (result.hasNext()) {
                    final Record record = result.next();
                    final NodeVO n = nodeExtractor.extractNode(record.get(Constants.NODE_ALIAS_N));
                    nodes.add(n);
                    totalCount++;

                    relations.addAll(relationShipExtractor.extractRelationships(record.get(Constants.RELATIONS)));
                }

                return new GetRelationDTO(relations, new ArrayList<>(nodes), totalCount);
            });
        }
    }

    /**
     * Expands the graph from a given node to a specified depth.
     * <p>
     * This method calculates the maximum depth of the graph and ensures that the expansion does not exceed this depth.
     * If the specified depth `k` is negative, it will be set to the maximum depth.
     * The method uses a breadth-first search (BFS) approach to expand the graph and collect nodes and relationships up
     * to the specified depth.
     *
     * @param graphUuid The UUID of the graph.
     * @param nodeUuid The UUID of the starting node.
     * @param k The desired depth of expansion.
     *
     * @return A {@link GraphVO} object containing the expanded nodes and relationships.
     *
     * @throws NoSuchElementException If the starting node with the given UUID does not exist in the graph.
     */
    @Override
    public GraphVO kDegreeExpansion(final String graphUuid, final String nodeUuid, final Integer k) {
        // calculate max depth
        final int maxDepth = calculateMaxDepth(graphUuid, nodeUuid);

        // check if k is greater than max depth
        int effectiveDepth = Math.min(k, maxDepth);

        // check if k is negative
        if (k < 0) {
            effectiveDepth = maxDepth;
        }

        final Queue<String> queue = new LinkedList<>();
        queue.add(nodeUuid);
        final Set<String> visited = new HashSet<>();
        visited.add(nodeUuid);
        final Set<RelationVO> relations = new HashSet<>();
        final Set<NodeVO> nodes = new HashSet<>();

        final String geFirstNodeQuery = "MATCH (g:Graph { uuid: $graphUuid })-[:RELATION]"
                + "->(n:GraphNode { uuid: $nodeUuid}) RETURN n";

        try (Session session = driver.session(SessionConfig.builder().build())) {

            // get first node
            final var firstNodeResult = session.run(geFirstNodeQuery, Values.parameters(
                    Constants.GRAPH_UUID, graphUuid,
                    Constants.NODE_UUID, nodeUuid));
            if (firstNodeResult.hasNext()) {
                final NodeVO node = nodeExtractor.extractNode(firstNodeResult.next().get(Constants.NODE_ALIAS_N));
                nodes.add(node);
            } else {
                final String message = String.format(Message.NODE_NULL, nodeUuid);
                LOG.error(message);
                throw new NoSuchElementException(message);
            }

            for (int depth = 0; depth < effectiveDepth; depth++) {
                final int layerSize = queue.size();

                for (int i = 0; i < layerSize; i++) {
                    final String currentNodeId = queue.poll();

                    final var queryResult = session.run(GET_WEAKLY_CONNECTED_NEIGHBORS_CYPHER, Values.parameters(
                            Constants.GRAPH_UUID, graphUuid,
                            Constants.NODE_UUID, currentNodeId));

                    while (queryResult.hasNext()) {
                        final Record record = queryResult.next();
                        final NodeVO nodeVO = nodeExtractor.extractNode(record.get(Constants.NODE_ALIAS_M));
                        final String neighborId = nodeVO.getUuid();

                        if (!visited.contains(neighborId)) {
                            visited.add(neighborId);
                            queue.add(neighborId);
                            relations.add(relationShipExtractor.extractRelationship(record.get(Constants.RELATION)));
                            nodes.add(nodeVO);
                        }
                    }
                }
            }
        }

        return GraphVO.builder()
                .relations(new ArrayList<>(relations))
                .nodes(new ArrayList<>(nodes))
                .build();
    }

    /**
     * Calculates the maximum depth of the expansion for a given start node in a graph.
     * @param graphUuid the UUID of the graph.
     * @param startNodeUuid the UUID of the start node.
     *
     * @return the maximum depth
     */
    private int calculateMaxDepth(final String graphUuid, final String startNodeUuid) {
        final Queue<String> queue = new LinkedList<>();
        queue.add(startNodeUuid);
        final Set<String> visited = new HashSet<>();
        visited.add(startNodeUuid);
        int depth = 0;

        try (Session session = driver.session(SessionConfig.builder().build())) {
            while (!queue.isEmpty()) {
                final int layerSize = queue.size();

                for (int i = 0; i < layerSize; i++) {
                    final String currentNodeId = queue.poll();

                    final var queryResult = session.run(GET_WEAKLY_CONNECTED_NEIGHBORS_CYPHER, Values.parameters(
                            Constants.GRAPH_UUID, graphUuid,
                            Constants.NODE_UUID, currentNodeId));

                    while (queryResult.hasNext()) {
                        final Record record = queryResult.next();
                        final NodeVO nodeVO = nodeExtractor.extractNode(record.get(Constants.NODE_ALIAS_M));
                        final String neighborId = nodeVO.getUuid();

                        if (!visited.contains(neighborId)) {
                            visited.add(neighborId);
                            queue.add(neighborId);
                        }
                    }
                }
                depth++;
            }
        }

        return depth;
    }

    /**
     * Binds two graph nodes together with a specified relationship.
     * <p>
     * Constructs a Cypher query to match two graph nodes by their UUIDs, update their update times,
     * and create a relationship between them.
     * Executes the Cypher query using the provided transaction.
     *
     * @param uuid1 the UUID of the first graph node
     * @param uuid2 the UUID of the second graph node
     * @param relation the name of the relationship to create
     * @param relationUuid the UUID of the relationship
     * @param currentTime the current timestamp for creation and update times
     * @param tx the Neo4j transaction to execute the Cypher query
     */
    @Override
    public void bindGraphNodeToGraphNode(final String uuid1, final String uuid2, final String relation,
                                         final String relationUuid, final String currentTime, final Transaction tx) {
        final String cypherQuery = "MATCH (gn1:GraphNode) WHERE gn1.uuid = $uuid1 SET gn1.update_time = $currentTime "
                + "WITH gn1 "
                + "MATCH (gn2:GraphNode) WHERE gn2.uuid = $uuid2 SET gn2.update_time = $currentTime "
                + "WITH gn1,gn2 "
                + "CREATE (gn1)-[r:RELATION{name: $relation, uuid: $relationUuid, sourceNode: $uuid1, "
                + "targetNode: $uuid2, create_time: $currentTime, update_time: $currentTime}]->(gn2)";

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
     * <p>
     * Constructs a Cypher query to match a graph node by its UUID and update its properties.
     * The query dynamically includes only the fields that need to be updated based on the provided properties.
     * Executes the Cypher query using the provided transaction.
     *
     * @param nodeUpdateDTO the DTO containing the updated properties of the node
     * @param currentTime the current timestamp for the update time
     * @param tx the Neo4j transaction to execute the Cypher query
     */
    @Override
    public void updateNodeByUuid(final NodeUpdateDTO nodeUpdateDTO, final String currentTime, final Transaction tx) {
        final StringBuilder setProperties = getSetProperties(nodeUpdateDTO.getProperties().entrySet());

        final String cypherQuery = "MATCH (gn:GraphNode {uuid: $nodeUuid}) "
                + "SET gn = { uuid: gn.uuid, "
                + "create_time: gn.create_time, "
                + "update_time: $updateTime"
                + setProperties
                + " }";

        tx.run(cypherQuery, Values.parameters(
                Constants.NODE_UUID, nodeUpdateDTO.getUuid(),
                Constants.UPDATE_TIME, currentTime));
    }

    /**
     * Generates a string builder containing the SET properties clause for a Cypher query.
     * <p>
     * Iterates through the provided map entries and appends each key-value pair to the string builder
     * in the format suitable for a Cypher query's SET clause.
     * The resulting string builder can be used to dynamically construct the SET part of a Cypher query.
     *
     * @param entries the set of map entries containing the properties to set
     *
     * @return a {@link StringBuilder} object containing the SET properties clause
     */
    private StringBuilder getSetProperties(final Set<Map.Entry<String, String>> entries) {
        final StringBuilder setProperties = new StringBuilder();
        for (final Map.Entry<String, String> entry : entries) {
            setProperties.append(", ").append(entry.getKey()).append(": '").append(entry.getValue())
                    .append(Constants.QUOTE);
        }

        return setProperties;
    }

    /**
     * Generates a string builder containing the filter properties clause for a Cypher query.
     * <p>
     * Iterates through the provided map entries and appends each key-value pair to the string builder
     * in the format suitable for a Cypher queries WHERE clause.
     * The resulting string builder can be used to dynamically construct the WHERE part of a Cypher query.
     *
     * @param node the alias of the node to apply the filters to
     * @param entries the map containing the properties to filter
     *
     * @return a {@link StringBuilder} object containing the filter properties clause
     */
    private static StringBuilder getFilterProperties(final String node, final Map<String, String> entries) {
        return new StringBuilder()
                .append("WHERE ")
                .append(
                        entries.entrySet().stream()
                                .map(filter -> String.format("%s.%s = '%s'", node, filter.getKey(),
                                        escapeSingleQuotes(filter.getValue())))
                                .collect(Collectors.joining(" AND "))
                );
    }

    /**
     * Escapes single quotes in a string by replacing them with two single quotes.
     * @param value the string to escape
     *
     * @return the escaped string
     */
    private static String escapeSingleQuotes(final String value) {
        return value.replace("'", "''");
    }
}
