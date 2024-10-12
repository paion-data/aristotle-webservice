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

import com.paiondata.aristotle.model.entity.Graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing graphs using Neo4j.
 *
 * This interface provides methods for CRUD operations on graphs and their relationships.
 */
@Repository
public interface GraphRepository extends Neo4jRepository<Graph, Long> {

    /**
     * Retrieves a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     * @return the graph
     */
    @Query("MATCH (g:Graph) WHERE g.uuid = $uuid RETURN g")
    Graph getGraphByUuid(String uuid);

    /**
     * Deletes graphs by their UUIDs.
     *
     * @param uuids the list of UUIDs of the graphs to be deleted
     */
    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids DETACH DELETE g")
    void deleteByUuids(List<String> uuids);

    /**
     * Retrieves the UUIDs of graph nodes associated with the given graphs.
     *
     * @param uuids the list of UUIDs of the graphs
     * @return the list of UUIDs of the graph nodes
     */
    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids "
            + "WITH g "
            + "MATCH (g)-[r:RELATION]->(gn:GraphNode) "
            + "RETURN gn.uuid")
    List<String> getGraphNodeUuidsByGraphUuids(List<String> uuids);

    /**
     * Retrieves the UUID of a graph associated with a specific user.
     * @param graphUuid the UUID of the graph
     * @param uidcid the UID/CID of the user
     * @return the UUID of the graph
     */
    @Query("MATCH (u:User{uidcid: $uidcid})-[:RELATION]->(g:Graph{uuid: $graphUuid}) " +
            "RETURN g.uuid")
    String getGraphByGraphUuidAndUidcid(@Param("graphUuid") String graphUuid, @Param("uidcid") String uidcid);
}
