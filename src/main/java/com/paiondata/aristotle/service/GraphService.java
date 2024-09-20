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

import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.GraphVO;

import java.util.Optional;

/**
 * Service implementation for managing graphs.
 * This class provides methods for CRUD operations on graphs and their relationships.
 */
public interface GraphService {

    /**
     * Retrieves a graph view object (VO) by its UUID.
     *
     * @param uuid the UUID of the graph
     *
     * @return the graph view object
     */
    GraphVO getGraphVOByUuid(String uuid);

    /**
     * Retrieves a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     *
     * @return an optional containing the graph if found, or empty if not found
     */
    Optional<Graph> getGraphByUuid(String uuid);

    /**
     * Creates and binds a new graph using the provided DTO.
     *
     * @param graphCreateDTO the DTO containing details to create a new graph
     *
     * @return the created graph
     */
    Graph createAndBindGraph(GraphCreateDTO graphCreateDTO);

    /**
     * Deletes graphs by their UUIDs.
     *
     * @param graphDeleteDTO the DTO containing the UUIDs of the graphs to delete
     */
    void deleteByUuids(GraphDeleteDTO graphDeleteDTO);

    /**
     * Updates a graph using the provided DTO.
     *
     * @param graphUpdateDTO the DTO containing details to update an existing graph
     */
    void updateGraph(GraphUpdateDTO graphUpdateDTO);
}
