package com.paiondata.aristotle.model.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@RelationshipEntity(type = "Relation")
public class Relation {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private GraphNode startNode;

    @EndNode
    private GraphNode endNode;

    @Property
    private String relation;
}
