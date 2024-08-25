package com.paiondata.aristotle.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("User")
@Data
@Builder
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uidcid")
    private String uidcid;

    @Property("nick_name")
    private String nickName;

    @Relationship(type = "CREATED", direction = Relationship.Direction.OUTGOING)
    private List<Graph> createdGraphs;
}