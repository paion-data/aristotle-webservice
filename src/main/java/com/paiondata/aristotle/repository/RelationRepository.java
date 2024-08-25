package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Relation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends Neo4jRepository<Relation, Long> {
}
