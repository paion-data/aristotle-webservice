package com.paiondata.aristotle.model.entity;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Builder
@Node("Graph")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String createTime;

    @Property("update_time")
    private String updateTime;
}
