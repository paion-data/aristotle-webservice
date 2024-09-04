package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.Relation;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GraphRepository extends Neo4jRepository<Graph, Long> {

    @Query("MATCH (g:Graph { title: $title }) RETURN g")
    Graph getGraphByTitle(String title);

    @Query("MATCH (g:Graph) WHERE g.uuid = $uuid RETURN g")
    Graph getGraphByUuid(String uuid);

    @Query("MATCH (g:Graph { title: $title, description: $description }) RETURN count(g)")
    long checkGraphExists(@Param("title") String title,
                          @Param("description") String description);

    @Query("MATCH (u:User) WHERE u.uidcid = $userUidcid "
            + "CREATE (g:Graph {uuid: $graphUuid, title: $title, description: $description,create_time: $currentTime}) "
            + "WITH u, g "
            + "CREATE (u)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime}]->(g) "
            + "RETURN g")
    Graph createAndBindGraph(@Param("title") String title,
                      @Param("description") String description,
                      @Param("userUidcid") String userUidcid,
                      @Param("graphUuid") String graphUuid,
                      @Param("relationUuid") String relationUuid,
                      @Param("currentTime") Date currentTime);

    @Query("MATCH (u:User) WHERE u.uidcid = $userUidcid MATCH (g:Graph) WHERE g.uuid = $graphUuid "
            + "SET g.update_time = $currentTime with u,g "
            + "CREATE (u)-[r:RELATION{ name: 'HAVE', uuid: $relationUuid, create_time:$ currentTime }]->(g)")
    void bindUsertoGraph(@Param("userUidcid") String userUidcid,
                         @Param("graphUuid") String graphUuid,
                         @Param("relationUuid") String relationUuid,
                         @Param("currentTime") Date currentTime);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids RETURN count(g)")
    long countByUuids(List<String> uuids);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids DETACH DELETE g")
    void deleteByUuids(List<String> uuids);

    @Query("MATCH (g:Graph) WHERE g.uuid IN $uuids "
            + "WITH g "
            + "MATCH (g)-[r:HAVE]->(gn:GraphNode) "
            + "RETURN g.uuid")
    List<String> getGraphNodeUuidsByGraphUuids(List<String> uuids);

    @Query("MATCH (g:Graph { uuid: $uuid }) " +
            "SET g.title = $title ,g.description = $description, g.update_time = $updateTime RETURN g")
    void updateGraphByUuid(@Param("uuid") String uuid,
                               @Param("title") String title,
                               @Param("description") String description,
                           @Param("updateTime") Date updateTime);

    @Query("MATCH (u:User)-[r:RELATION]->(g:Graph) "
            + "WHERE u.uidcid = $uidcid AND g.uuid = $uuid "
            + "RETURN g")
    Graph getRelationByUidcidAndUuid(@Param("uidcid") String uidcid, @Param("uuid") String uuid);
}