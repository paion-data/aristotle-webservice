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
package com.paiondata.aristotle.model.vo;

import com.paiondata.aristotle.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a value object (VO) for a graph.
 *
 * This class encapsulates the properties and metadata of a graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphVO extends BaseEntity {

    /**
     * The UUID of the graph.
     */
    private String uuid;

    /**
     * The title of the graph.
     */
    private String title;

    /**
     * The description of the graph.
     */
    private String description;

    /**
     * The creation time of the graph.
     */
    private String createTime;

    /**
     * The last update time of the graph.
     */
    private String updateTime;

    /**
     * The list of nodes in the graph.
     *
     * Each node is represented as a map containing node-specific information.
     */
    private List<Map<String, Map<String, Object>>> nodes;
}
