package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.Relation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends Neo4jRepository<Graph, Long> {

    @Query("MATCH p=(u:User)-[r:RELATION]->(g:Graph) "
            + "WHERE r.uuid = $uuid "
            + "RETURN r")
    List<Relation> getRelationByUuid(@Param("uuid") String uuid);
}
