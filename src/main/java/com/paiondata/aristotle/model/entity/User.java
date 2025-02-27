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
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * Represents a user entity in the system.
 *
 * This class encapsulates the properties and relationships of a user.
 */
@Node("User")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The unique identifier (OIDC ID) of the user.
     */
    @Property("oidcid")
    private String oidcid;

    /**
     * The username of the user.
     */
    @Property("username")
    private String username;

    /**
     * The list of graphs associated with the user.
     */
    @Relationship(type = "RELATION", direction = Relationship.Direction.OUTGOING)
    private List<Graph> graphs;
}
