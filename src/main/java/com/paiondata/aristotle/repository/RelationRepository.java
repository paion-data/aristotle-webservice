package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.model.entity.Relation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;

@Repository
public interface RelationRepository<T extends Serializable, U extends Serializable>
        extends Neo4jRepository<Relation<T, U>, Long> {
    @Query("MATCH p=(n:Person)-[r:Relation]->(m:Person) " +
            "WHERE id(n)={startNode} and id(m)={endNode} and r.relation={relation}" +
            "RETURN p")
    List<Relation> findRelation(@Param("startNode") GraphNode startNode,
                                @Param("endNode") GraphNode endNode,
                                @Param("relation") String relation);
}
