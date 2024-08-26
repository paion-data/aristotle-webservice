package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

@NodeEntity(label = "GraphNode")
@Data
public class GraphNode extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property("title")
    private String title;

    @Property("description")
    private String description;
}
