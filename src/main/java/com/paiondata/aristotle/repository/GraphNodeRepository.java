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

@Repository
public interface GraphNodeRepository extends Neo4jRepository<GraphNode, Long> {

    @Query("MATCH (gn:GraphNode { uuid: $uuid }) RETURN gn")
    GraphNode getGraphNodeByUuid(String uuid);

    @Query("MATCH (g:Graph) WHERE g.uuid = $graphUuid SET g.update_time = $currentTime "
            +"CREATE (gn:GraphNode{uuid:$graphNodeUuid,title:$title,description:$description,create_time:$currentTime,update_time:$currentTime})"
            + " WITH g, gn "
            + "CREATE (g)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime, update_time: $currentTime}]->(gn) "
            + "RETURN gn")
    GraphNode createAndBindGraphNode(@Param("title") String title,
                                     @Param("description") String description,
                                     @Param("graphUuid") String graphUuid,
                                     @Param("graphNodeUuid") String graphNodeUuid,
                                     @Param("relationUuid") String relationUuid,
                                     @Param("currentTime") String currentTime);

    @Query("MATCH (gn1:GraphNode) WHERE gn1.uuid = $uuid1 SET gn1.update_time = $currentTime WITH gn1 "
            + "MATCH (gn2:GraphNode) WHERE gn2.uuid = $uuid2 SET gn2.update_time = $currentTime "
            + "WITH gn1,gn2 "
            + "CREATE (gn1)-[r:RELATION{name: $relation, uuid: $relationUuid, create_time: $currentTime, update_time: $currentTime}]->(gn2)")
    void bindGraphNodeToGraphNode(@Param("uuid1") String uuid1,
                                  @Param("uuid2") String uuid2,
                                  @Param("relation") String relation,
                                  @Param("relationUuid") String relationUuid,
                                  @Param("currentTime") String currentTime);

    @Query("MATCH (gn:GraphNode) WHERE gn.uuid IN $uuids DETACH DELETE gn")
    void deleteByUuids(List<String> uuids);

    @Query("MATCH (g:Graph)-[r:RELATION]->(gn:GraphNode) WHERE gn.uuid in $uuids RETURN g.uuid")
    List<String> getGraphUuidByGraphNodeUuid(List<String> uuids);

    @Query("MATCH (g:Graph {uuid: $graphUuid})-[:RELATION]->(gn1:GraphNode) " +
            "MATCH (gn1)-[r:RELATION {uuid: $relationUuid}]->(:GraphNode) " +
            "SET r.name = $relationName")
    void updateRelationByUuid(@Param("relationUuid") String relationUuid,
                              @Param("relationName") String relationName,
                              @Param("graphUuid") String graphUuid);

    @Query("MATCH (g:Graph {uuid: $graphUuid})-[:RELATION]->(gn1:GraphNode) " +
            "MATCH (gn1)-[r:RELATION {uuid: $relationUuid}]->(:GraphNode) " +
            "DELETE r")
    void deleteRelationByUuid(String relationUuid, String graphUuid);

    @Query("MATCH (g:Graph)-[:RELATION]->(gn1:GraphNode) " +
            "MATCH (gn1)-[r:RELATION {uuid: $relationUuid}]->(:GraphNode) " +
            "RETURN r.name")
    String getRelationByUuid(String relationUuid);
}
