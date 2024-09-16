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
package com.paiondata.aristotle.service;

import java.util.List;
import java.util.Map;

/**
 * Service implementation for Neo4j operations.
 * This class provides methods for querying and updating data in Neo4j.
 */
public interface Neo4jService {

    /**
     * Retrieves users and their associated graphs by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     *
     * @return a list of maps containing user information and associated graphs
     */
    List<Map<String, Object>> getUserAndGraphsByUidcid(String uidcid);

    /**
     * Retrieves graph nodes and their relationships by graph UUID.
     *
     * @param uuid the UUID of the graph
     *
     * @return a list of maps containing graph node information and relationships
     */
    List<Map<String, Map<String, Object>>> getGraphNodeByGraphUuid(String uuid);

    /**
     * Updates a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     * @param title the new title of the graph
     * @param description the new description of the graph
     * @param currentTime the current time for update
     */
    void updateGraphByUuid(String uuid, String title, String description, String currentTime);

    /**
     * Updates a graph node by its UUID.
     *
     * @param uuid the UUID of the graph node
     * @param title the new title of the graph node
     * @param description the new description of the graph node
     * @param currentTime the current time for update
     */
    void updateNodeByUuid(String uuid, String title, String description, String currentTime);
}
