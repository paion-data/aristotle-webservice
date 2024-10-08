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
package com.paiondata.aristotle.mapper;

import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.entity.GraphNode;

import org.neo4j.driver.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Mapper interface for NodeMapper.
 */
public interface NodeMapper {

    /**
     * Creates a node in the Neo4j database.
     * @param graphUuid the UUID of the graph
     * @param nodeUuid the UUID of the node
     * @param relationUuid the UUID of the relation
     * @param currentTime the current time
     * @param nodeDTO the NodeDTO object containing the node properties
     * @param tx the Neo4j transaction
     * @return the created Node object
     */
    GraphNode createNode(String graphUuid, String nodeUuid, String relationUuid,
                                String currentTime, NodeDTO nodeDTO, Transaction tx);

    List<Map<String, Map<String, Object>>> getNodesByGraphUuid(String uuid);
}
