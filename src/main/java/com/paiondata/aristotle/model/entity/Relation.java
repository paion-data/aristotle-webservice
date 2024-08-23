package com.paiondata.aristotle.model.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

@Data
@Relationship(type = "Relation")
public class Relation {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Node startNode;

    @EndNode
    private Node endNode;

    @Property
    private String relation;
}
