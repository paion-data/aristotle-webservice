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

@Repository
public interface GraphRepository extends Neo4jRepository<Graph, Long> {

    @Query("MATCH (g:Graph) WHERE g.uuid = $uuid RETURN g")
    Graph getGraphByUuid(String uuid);

    @Query("MATCH (u:User) WHERE u.uidcid = $userUidcid "
            + "CREATE (g:Graph {uuid: $graphUuid, title: $title, description: $description,create_time: $currentTime, update_time: $currentTime}) "
            + "WITH u, g "
            + "CREATE (u)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime, update_time: $currentTime}]->(g) "
            + "RETURN g")
    Graph createAndBindGraph(@Param("title") String title,
                      @Param("description") String description,
                      @Param("userUidcid") String userUidcid,
                      @Param("graphUuid") String graphUuid,
                      @Param("relationUuid") String relationUuid,
                      @Param("currentTime") String currentTime);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids DETACH DELETE g")
    void deleteByUuids(List<String> uuids);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids "
            + "WITH g "
            + "MATCH (g)-[r:RELATION]->(gn:GraphNode) "
            + "RETURN gn.uuid")
    List<String> getGraphNodeUuidsByGraphUuids(List<String> uuids);
}