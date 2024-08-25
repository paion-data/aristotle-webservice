package com.paiondata.aristotle.model.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

@NodeEntity(label = "GraphNode")
@Data
public class GraphNode {

    @Id
    @GeneratedValue
    private Long id;

    @Property("title")
    private String title;

    @Property("description")
    private String description;
}
