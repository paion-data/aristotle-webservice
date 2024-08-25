package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphRepository extends Neo4jRepository<GraphNode, Long> {
}