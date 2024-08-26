package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

import java.io.Serializable;

@Data
@RelationshipEntity(type = "Relation")
public class Relation<T extends Serializable, U extends Serializable> extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private T startNode;

    @EndNode
    private U endNode;

    @Property("relation")
    private String relation;
}
