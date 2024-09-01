package com.paiondata.aristotle.model.entity;

import org.neo4j.ogm.annotation.RelationshipEntity;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.Date;

@RelationshipEntity(type = "HAVE")
public class Relation {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uuid")
    private String uuid;

//    @Property("name")
//    private String name;

    @Property("create_time")
    private Date createTime;

//    @Property("update_time")
//    private Date updateTime;
}
