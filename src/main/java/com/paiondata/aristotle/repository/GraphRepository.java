package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
                      @Param("currentTime") Date currentTime);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids DETACH DELETE g")
    void deleteByUuids(List<String> uuids);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids "
            + "WITH g "
            + "MATCH (g)-[r:RELATION]->(gn:GraphNode) "
            + "RETURN gn.uuid")
    List<String> getGraphNodeUuidsByGraphUuids(List<String> uuids);

    @Query("MATCH (g:Graph { uuid: $uuid }) " +
            "SET g.title = $title ,g.description = $description, g.update_time = $updateTime RETURN g")
    void updateGraphByUuid(@Param("uuid") String uuid,
                               @Param("title") String title,
                               @Param("description") String description,
                           @Param("updateTime") Date updateTime);
}