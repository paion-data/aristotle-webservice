package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("User")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Property("uidcid")
    private String uidcid;

    @Property("nick_name")
    private String nickName;

    @Relationship(type = "RELATION", direction = Relationship.Direction.OUTGOING)
    private List<Graph> graphs;
}