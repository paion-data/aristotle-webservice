/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import java.util.Map;

/**
 * Represents a node entity within a graph.
 *
 * This class encapsulates the properties and metadata of a graph node.
 */
@Node("GraphNode")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphNode extends BaseEntity {

    /**
     * The unique identifier of the graph node.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The UUID of the graph node.
     *
     * @see Property#uuid
     */
    @Property("uuid")
    private String uuid;

    /**
     * The properties of the graph node.
     *
     * @see Property#properties
     */
    @Property("properties")
    private Map<String, String> properties;

    /**
     * The creation time of the graph node.
     *
     * @see Property#create_time
     */
    @Property("create_time")
    private String createTime;

    /**
     * The last update time of the graph node.
     *
     * @see Property#update_time
     */
    @Property("update_time")
    private String updateTime;
}
