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
package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing graph nodes using Neo4j.
 *
 * This interface provides methods for CRUD operations on graph nodes and relationships.
 */
@Repository
public interface NodeRepository extends Neo4jRepository<GraphNode, Long> {

    /**
     * Deletes graph nodes by their UUIDs.
     *
     * @param uuids the list of UUIDs of the graph nodes to be deleted
     */
    @Query("MATCH (gn:GraphNode) WHERE gn.uuid IN $uuids DETACH DELETE gn")
    void deleteByUuids(List<String> uuids);

    /**
     * Retrieves the UUIDs of graphs that contain the given graph nodes.
     *
     * @param uuids the list of UUIDs of the graph nodes
     * @return the list of UUIDs of the graphs
     */
    @Query("MATCH (g:Graph)-[r:RELATION]->(gn:GraphNode) WHERE gn.uuid in $uuids RETURN g.uuid")
    List<String> getGraphUuidByGraphNodeUuid(Set<String> uuids);

    /**
     * Updates the name of a relationship between a graph and a graph node.
     *
     * @param relationUuid    the UUID of the relationship
     * @param relationName    the new name of the relationship
     * @param graphUuid       the UUID of the graph
     */
    @Query("MATCH (g:Graph {uuid: $graphUuid})-[:RELATION]->(gn1:GraphNode) " +
            "MATCH (gn1)-[r:RELATION {uuid: $relationUuid}]->(:GraphNode) " +
            "SET r.name = $relationName")
    void updateRelationByUuid(@Param("relationUuid") String relationUuid,
                              @Param("relationName") String relationName,
                              @Param("graphUuid") String graphUuid);

    /**
     * Deletes a relationship between a graph and a graph node.
     *
     * @param relationUuid    the UUID of the relationship
     * @param graphUuid       the UUID of the graph
     */
    @Query("MATCH (g:Graph {uuid: $graphUuid})-[:RELATION]->(gn1:GraphNode) " +
            "MATCH (gn1)-[r:RELATION {uuid: $relationUuid}]->(:GraphNode) " +
            "DELETE r")
    void deleteRelationByUuid(String relationUuid, String graphUuid);

    /**
     * Retrieves the name of a relationship by its UUID.
     *
     * @param relationUuid the UUID of the relationship
     * @return the name of the relationship
     */
    @Query("MATCH (g:Graph)-[:RELATION]->(gn1:GraphNode) " +
            "MATCH (gn1)-[r:RELATION {uuid: $relationUuid}]->(:GraphNode) " +
            "RETURN r.name")
    String getRelationByUuid(String relationUuid);

    /**
     * Retrieves the UUID of a graph node associated with a specific graph.
     * @param graphUuid the UUID of the graph
     * @param nodeUuid the UUID of the node
     * @return the UUID of the node
     */
    @Query("MATCH (g:Graph{uuid: $graphUuid})-[:RELATION]->(gn:GraphNode{uuid: $nodeUuid}) RETURN gn.uuid")
    String getNodeByGraphUuidAndNodeUuid(@Param("graphUuid") String graphUuid, @Param("nodeUuid") String nodeUuid);
}
