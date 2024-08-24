package com.paiondata.aristotle.model.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@Node("KnowledgeGraph")
@Data
public class KnowledgeGraph {

    @Id
    @GeneratedValue
    private Long id;

    @Property("title")
    private String title;

    @Property("last_modified")
    private String lastModified;

    @Relationship(type = "OWNED_BY", direction = Relationship.Direction.INCOMING)
    private User owner;
}
