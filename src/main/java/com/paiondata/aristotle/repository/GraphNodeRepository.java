package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface GraphNodeRepository extends Neo4jRepository<GraphNode, Long> {

    @Query("MATCH (gn:GraphNode { title: $title }) RETURN gn")
    GraphNode getGraphNodeByTitle(String title);

    @Query("MATCH (gn:GraphNode) WHERE elementId(gn) = $elementId RETURN gn")
    GraphNode getGraphNodeByElementId(String elementId);

    @Query("MATCH (gn:GraphNode { title: $title, description: $description }) RETURN count(gn)")
    long checkGraphNodeExists(@Param("title") String title,
                          @Param("description") String description);

    @Query("CREATE (gn:GraphNode { title: $title, description: $description, update_time: $updateTime }) RETURN gn")
    GraphNode createGraphNode(@Param("title") String title,
                      @Param("description") String description,
                      @Param("updateTime") Date updateTime);

    @Query("MATCH (g:Graph) WHERE elementId(g) = $elementId1 MATCH (gn:GraphNode) "
            + "WHERE elementId(gn) = $elementId2 with g,gn"
            + " CREATE (gn)-[r:RELATION{name:'Have'}]->(g)")
    void bindGraphToGraphNode(@Param("elementId1") String elementId1,
                              @Param("elementId2") String elementId2);


    @Query("MATCH (gn1:GraphNode) WHERE elementId(gn1) = $elementId1 MATCH (gn2:GraphNode) "
            + "WHERE elementId(gn2) = $elementId2 with gn1,gn2"
            + " CREATE (gn2)-[r:RELATION{name:$relation}]->(gn1)")
    void bindGraphNodeToGraphNode(@Param("elementId1") String elementId1,
                                  @Param("elementId2") String elementId2,
                                  @Param("relation") String relation);
}
