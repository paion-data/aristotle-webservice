package com.paiondata.aristotle.model.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@Node("Graph")
@Data
public class Graph {

    @Id
    @GeneratedValue
    private Long id;

    @Property("title")
    private String title;

    @Property("description")
    private String description;

    @Property("last_modified")
    private String lastModified;
}
