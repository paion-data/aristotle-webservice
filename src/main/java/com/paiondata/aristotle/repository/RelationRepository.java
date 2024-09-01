package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.Relation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends Neo4jRepository<Graph, Long> {

    @Query("MATCH ()-[r { uuid: $uuid }]-() "
            + "RETURN DISTINCT r")
    Relation getRelationByUuid(@Param("uuid") String uuid);
}
