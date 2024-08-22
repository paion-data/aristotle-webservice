package com.paiondata.aristotle.repository;

import com.paiondata.aristotle.model.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends Neo4jRepository<Person,Long> {
}