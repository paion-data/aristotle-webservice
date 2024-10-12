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

import com.paiondata.aristotle.model.entity.Graph;

import org.neo4j.driver.Transaction;

import java.util.List;
import java.util.Map;

/**
 * GraphMapper interface for mapping Graph objects.
 */
public interface GraphMapper {

    /**
     * Creates a new graph with the specified properties.
     * @param title the title of the graph
     * @param description the description of the graph
     * @param userUidcid the UIDCID of the user
     * @param graphUuid the UUID of the graph
     * @param relationUuid the UUID of the link between the created node and the graph this node belongs to
     * @param currentTime the current time
     * @param tx the Neo4j transaction
     * @return the created Graph object
     */
    Graph createGraph(String title, String description, String userUidcid,
                      String graphUuid, String relationUuid, String currentTime, Transaction tx);

    /**
     * Retrieves users' associated graphs by UIDCID.
     *
     * @param uidcid the UIDCID of the user
     *
     * @return a list of maps containing user information and associated graphs
     */
    List<Map<String, Object>> getGraphsByUidcid(String uidcid);

    /**
     * Updates a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     * @param title the new title of the graph
     * @param description the new description of the graph
     * @param currentTime the current time for update
     * @param tx the Neo4j transaction
     */
    void updateGraphByUuid(String uuid, String title, String description, String currentTime, Transaction tx);
}
