package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

import java.util.Date;

@RelationshipProperties()
@Data
public class Relation extends BaseEntity {

    @RelationshipId
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
