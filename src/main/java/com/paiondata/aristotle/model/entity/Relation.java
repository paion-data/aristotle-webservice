package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import lombok.Data;

import java.util.Date;

@RelationshipProperties
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
}
