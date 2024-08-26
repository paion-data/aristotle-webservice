package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

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
            + " CREATE (u)-[r:RELATION{name:Have}]->(g)")
    void createUsertoGraph(@Param("elementId1") String elementId1,
                           @Param("elementId2") String elementId2);
}