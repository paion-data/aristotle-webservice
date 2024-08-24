package com.paiondata.aristotle.model.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("User")
@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uidcid")
    private Long uidcid;

    @Property("username")
    private String username;
}