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

    @Query("MATCH (g:Graph { title: $title }) RETURN g")
    Graph getGraphByTitle(String title);

    @Query("MATCH (g:Graph) WHERE elementId(g) = $elementId RETURN g")
    Graph getGraphByElementId(String elementId);

    @Query("MATCH (g:Graph { title: $title, description: $description }) RETURN count(g)")
    long checkGraphExists(@Param("title") String title,
                          @Param("description") String description);

    @Query("CREATE (g:Graph { title: $title, description: $description, update_time: $updateTime }) RETURN g")
    Graph createGraph(@Param("title") String title,
                      @Param("description") String description,
                      @Param("updateTime") Date updateTime);

    @Query("MATCH (u:User) WHERE elementId(u) = $elementId1 MATCH (g:Graph) WHERE elementId(g) = $elementId2 with u,g"
            + " CREATE (u)-[r:RELATION{name:'Have'}]->(g)")
    void bindUsertoGraph(@Param("elementId1") String elementId1,
                         @Param("elementId2") String elementId2);

    @Query("MATCH (g:Graph) WHERE elementId(g) IN $elementIds RETURN count(g)")
    long countByElementIds(List<String> elementIds);

    @Query("MATCH (g:Graph) WHERE elementId(g) IN $elementIds DETACH DELETE g")
    void deleteByElementIds(List<String> elementIds);

    @Query("MATCH (g:Graph) WHERE elementId(g) IN $elementIds "
            + "WITH g "
            + "MATCH (g)-[r:RELATION]->(gn:GraphNode) "
            + "RETURN elementId(gn)")
    List<String> getGraphNodeElementIdsByGraphElementIds(List<String> elementIds);
}