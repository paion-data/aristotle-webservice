package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.Date;

@Node("Graph")
@Data
public class Graph extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uuid")
    private String uuid;

    @Property("title")
    private String title;

    @Property("description")
    private String description;

    @Property("create_time")
    private Date createTime;

    @Property("update_time")
    private Date updateTime;
}
