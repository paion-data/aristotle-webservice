package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

import lombok.Data;

import java.util.Date;

@RelationshipEntity(type = "RELATION")
@Data
public class Relation extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uuid")
    private String uuid;

    @Property("name")
    private String name;

    @Property("create_time")
    private Date createTime;

    @Property("update_time")
    private Date updateTime;

    @StartNode
    private User user;

    @EndNode
    private Graph graph;
}
