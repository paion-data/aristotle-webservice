package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GraphNodeRepository extends Neo4jRepository<GraphNode, Long> {

    @Query("MATCH (gn:GraphNode { title: $title }) RETURN gn")
    GraphNode getGraphNodeByTitle(String title);

    @Query("MATCH (gn:GraphNode { uuid: $uuid }) RETURN gn")
    GraphNode getGraphNodeByUuid(String uuid);

    @Query("MATCH (gn:GraphNode { title: $title, description: $description }) RETURN count(gn)")
    long checkGraphNodeExists(@Param("title") String title,
                          @Param("description") String description);

    @Query("MATCH (g:Graph) WHERE g.uuid = $graphUuid SET g.update_time = $currentTime "
            +"CREATE (gn:GraphNode{uuid:$graphNodeUuid,title:$title,description:$description,create_time:$currentTime})"
            + " WITH g, gn "
            + "CREATE (g)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime}]->(gn)")
    GraphNode createAndBindGraphNode(@Param("title") String title,
                                     @Param("description") String description,
                                     @Param("graphUuid") String graphUuid,
                                     @Param("graphNodeUuid") String graphNodeUuid,
                                     @Param("relationUuid") String relationUuid,
                                     @Param("currentTime") Date currentTime);

    @Query("MATCH (g:Graph) WHERE g.uuid = $graphUuid SET g.update_time = $currentTime "
            + "MATCH (gn:GraphNode) WHERE gn.uuid = $graphNodeUuid SET gn.update_time = $currentTime with g,gn "
            + "CREATE (g)-[r:RELATION{name: 'HAVE', uuid: $relationUuid, create_time: $currentTime}]->(gn)")
    void bindGraphToGraphNode(@Param("graphUuid") String graphUuid,
                              @Param("graphNodeUuid") String graphNodeUuid,
                              @Param("relationUuid") String relationUuid,
                              @Param("currentTime") Date currentTime);

    @Query("MATCH (gn1:GraphNode) WHERE gn1.uuid = $uuid1 SET gn1.update_time = $currentTime WITH gn1 "
            + "MATCH (gn2:GraphNode) WHERE gn2.uuid = $uuid2 SET gn2.update_time = $currentTime "
            + "WITH gn1,gn2 "
            + "CREATE (gn1)-[r:RELATION{name: $relation, uuid: $relationUuid, create_time: $currentTime}]->(gn2)")
    void bindGraphNodeToGraphNode(@Param("uuid1") String uuid1,
                                  @Param("uuid2") String uuid2,
                                  @Param("relation") String relation,
                                  @Param("relationUuid") String relationUuid,
                                  @Param("currentTime") Date currentTime);

    @Query("MATCH (gn:GraphNode) WHERE gn.uuid IN $uuids RETURN count(gn)")
    long countByUuids(List<String> uuids);

    @Query("MATCH (gn:GraphNode) WHERE gn.uuid IN $uuids DETACH DELETE gn")
    void deleteByUuids(List<String> uuids);

    @Query("MATCH (gn:GraphNode { uuid: $uuid }) " +
            "SET gn.title = $title ,gn.description = $description, gn.update_time = $updateTime RETURN gn")
    void updateGraphNodeByUuid(@Param("uuid") String uuid,
                               @Param("title") String title,
                               @Param("description") String description,
                               @Param("updateTime") Date updateTime);

    @Query("MATCH (g:Graph)-[r:RELATION]->(gn:GraphNode) "
            + "WHERE g.uuid = $uuid1 AND gn.uuid = $uuid2 "
            + "RETURN gn")
    GraphNode getRelationByGraphUuidAndNodeUuid(@Param("uuid1") String uuid1, @Param("uuid2") String uuid2);

    @Query("MATCH (gn1:GraphNode)-[r:RELATION]->(gn2:GraphNode) "
            + "WHERE gn1.uuid = $uuid1 AND gn2.uuid = $uuid2 "
            + "RETURN gn1")
    GraphNode getRelationByDoubleUuid(@Param("uuid1") String uuid1, @Param("uuid2") String uuid2);
}
