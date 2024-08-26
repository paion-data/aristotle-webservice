package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphNodeRepository extends Neo4jRepository<GraphNode, Long> {

    @Query("MATCH (gn:GraphNode) WHERE elementId(gn) = $elementId RETURN gn")
    GraphNode getGraphNodeByElementId(String elementId);
}
