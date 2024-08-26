package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

@NodeEntity(label = "User")
@Data
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uidcid")
    private String uidcid;

    @Property("nick_name")
    private String nickName;
}