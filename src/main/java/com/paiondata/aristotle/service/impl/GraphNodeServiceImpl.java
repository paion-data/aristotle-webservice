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
package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.NodeNullException;
import com.paiondata.aristotle.common.exception.NodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.TemporaryKeyException;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.service.GraphNodeService;
import com.paiondata.aristotle.service.GraphService;
import com.paiondata.aristotle.service.Neo4jService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for managing graph nodes.
 *
 * This class provides methods for CRUD operations on graph nodes and their relationships.
 */
@Service
@AllArgsConstructor
public class GraphNodeServiceImpl implements GraphNodeService {

    @Autowired
    private GraphNodeRepository graphNodeRepository;

    @Autowired
    private GraphService graphService;

    @Autowired
    private Neo4jService neo4jService;

    /**
     * Retrieves a graph node by its UUID.
     *
     * @param uuid the UUID of the graph node
     * @return an {@code Optional} containing the graph node if found
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<GraphNode> getNodeByUuid(final String uuid) {
        final GraphNode graphNode = graphNodeRepository.getGraphNodeByUuid(uuid);
        return Optional.ofNullable(graphNode);
    }

    /**
     * Creates and binds a graph and a node based on the provided DTO.
     *
     * @param nodeCreateDTO the DTO containing information for creating the graph and node
     */
    @Transactional
    @Override
    public void createAndBindGraphAndNode(final NodeCreateDTO nodeCreateDTO) {
        final String graphUuid = nodeCreateDTO.getGraphUuid();

        final Optional<Graph> optionalGraph = graphService.getGraphByUuid(graphUuid);
        if (optionalGraph.isEmpty()) {
            throw new GraphNullException(Message.GRAPH_NULL + graphUuid);
        }

        checkInputRelationsAndBindGraphAndNode(nodeCreateDTO.getGraphNodeDTO(),
                nodeCreateDTO.getGraphNodeRelationDTO(), graphUuid);
    }

    /**
     * Creates a graph and binds it with a node based on the provided DTO.
     *
     * @param graphNodeCreateDTO the DTO containing information for creating the graph and node
     */
    @Transactional
    @Override
    public void createGraphAndBindGraphAndNode(final GraphAndNodeCreateDTO graphNodeCreateDTO) {
        final Graph graph = graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());

        if (graphNodeCreateDTO.getGraphNodeDTO() == null) {
            return;
        }

        final String graphUuid = graph.getUuid();

        checkInputRelationsAndBindGraphAndNode(graphNodeCreateDTO.getGraphNodeDTO(),
                graphNodeCreateDTO.getGraphNodeRelationDTO(), graphUuid);
    }

    /**
     * Checks input relations and binds a graph and a node.
     *
     * @param nodeDTOs             the list of DTOs for creating nodes
     * @param nodeRelationDTOs     the list of DTOs for creating node relations
     * @param graphUuid            the UUID of the graph
     */
    private void checkInputRelationsAndBindGraphAndNode(final List<NodeDTO> nodeDTOs,
                                                        final List<NodeRelationDTO> nodeRelationDTOs,
                                                        final String graphUuid) {
        final String now = getCurrentTime();
        final Map<String, String> uuidMap = new HashMap<>();

        createNodes(nodeDTOs, uuidMap, now, graphUuid);

        if (nodeRelationDTOs == null) {
            return;
        }

        final List<String> checkIds = new ArrayList<>();
        for (final NodeRelationDTO dto : nodeRelationDTOs) {
            checkIds.add(dto.getFromId());
            checkIds.add(dto.getToId());
        }
        final List<String> graphUuidByGraphNodeUuid = graphNodeRepository.getGraphUuidByGraphNodeUuid(checkIds);
        for (final String s : graphUuidByGraphNodeUuid) {
            if (!s.equals(graphUuid)) {
                throw new NodeRelationException(Message.BOUND_ANOTHER_GRAPH + s);
            }
        }

        bindNodeRelations(nodeRelationDTOs, uuidMap, now);
    }

    /**
     * Creates nodes based on the provided DTOs.
     *
     * @param nodeDTOs             the list of DTOs for creating nodes
     * @param uuidMap              the map for storing UUID mappings
     * @param now                  the current timestamp
     * @param graphUuid            the UUID of the graph
     *
     * @throws TemporaryKeyException if the temporary ID is duplicated
     */
    private void createNodes(final List<NodeDTO> nodeDTOs, final Map<String, String> uuidMap,
                             final String now, final String graphUuid) {
        for (final NodeDTO dto : nodeDTOs) {
            final String graphNodeUuid = UUID.fastUUID().toString(true);
            final String relationUuid = UUID.fastUUID().toString(true);

            final GraphNode createdNode = graphNodeRepository.createAndBindGraphNode(dto.getTitle(),
                    dto.getDescription(),
                    graphUuid, graphNodeUuid, relationUuid, now);

            if (uuidMap.containsKey(dto.getTemporaryId())) {
                throw new TemporaryKeyException(Message.DUPLICATE_KEY + dto.getTemporaryId());
            } else {
                uuidMap.put(dto.getTemporaryId(), createdNode.getUuid());
            }
        }
    }

    /**
     * Binds node relations based on the provided DTOs.
     *
     * @param graphNodeRelationDTO the list of DTOs for creating node relations
     * @param uuidMap              the map for storing UUID mappings
     * @param now                  the current timestamp
     */
    private void bindNodeRelations(final List<NodeRelationDTO> graphNodeRelationDTO,
                                   final Map<String, String> uuidMap, final String now) {
        if (graphNodeRelationDTO == null || graphNodeRelationDTO.isEmpty()) {
            return;
        }

        for (final NodeRelationDTO dto : graphNodeRelationDTO) {
            final String relation = dto.getRelationName();
            final String relationUuid = UUID.fastUUID().toString(true);

            final String fromId = getNodeId(dto.getFromId(), uuidMap);
            final String toId = getNodeId(dto.getToId(), uuidMap);

            graphNodeRepository.bindGraphNodeToGraphNode(fromId, toId, relation, relationUuid, now);
        }
    }

    /**
     * Retrieves the node ID from the UUID map.
     *
     * @param id           the ID of the node
     * @param uuidMap      the map for storing UUID mappings
     * @return the node ID or the original ID if not found in the map
     */
    private String getNodeId(final String id, final Map<String, String> uuidMap) {
        return uuidMap.getOrDefault(id, id);
    }

    /**
     * Binds nodes based on the provided DTOs.
     *
     * @param dtos the list of DTOs for binding nodes
     */
    @Transactional
    @Override
    public void bindNodes(final List<BindNodeDTO> dtos) {
        for (final BindNodeDTO dto : dtos) {
            final String startNode = dto.getFromId();
            final String endNode = dto.getToId();
            final Optional<GraphNode> graphNodeOptional1 = getNodeByUuid(startNode);
            final Optional<GraphNode> graphNodeOptional2 = getNodeByUuid(endNode);
            final String relationUuid = UUID.fastUUID().toString(true);
            final String now = getCurrentTime();

            if (graphNodeOptional1.isEmpty() || graphNodeOptional2.isEmpty()) {
                if (graphNodeOptional1.isEmpty()) {
                    throw new NodeNullException(Message.GRAPH_NODE_NULL + startNode);
                } else {
                    throw new NodeNullException(Message.GRAPH_NODE_NULL + endNode);
                }
            }

            graphNodeRepository.bindGraphNodeToGraphNode(startNode, endNode, dto.getRelationName(), relationUuid, now);
        }
    }

    /**
     * Deletes graph nodes by their UUIDs.
     *
     * @param uuids the list of UUIDs of the graph nodes to be deleted
     */
    @Transactional
    @Override
    public void deleteByUuids(final List<String> uuids) {
        for (final String uuid : uuids) {
            if (getNodeByUuid(uuid).isEmpty()) {
                throw new NodeNullException(Message.GRAPH_NODE_NULL + uuid);
            }
        }

        graphNodeRepository.deleteByUuids(uuids);
    }

    /**
     * Updates a graph node based on the provided DTO.
     *
     * @param graphUpdateDTO the DTO containing information for updating the graph node
     */
    @Transactional
    @Override
    public void updateNode(final GraphUpdateDTO graphUpdateDTO) {
        final String uuid = graphUpdateDTO.getUuid();
        final Optional<GraphNode> graphNodeByUuid = getNodeByUuid(uuid);
        final String now = getCurrentTime();

        if (graphNodeByUuid.isPresent()) {
            neo4jService.updateNodeByUuid(uuid, graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(), now);
        } else {
            throw new NodeNullException(Message.GRAPH_NODE_NULL + uuid);
        }
    }

    /**
     * Updates graph node relations based on the provided DTO.
     *
     * @param relationUpdateDTO the DTO containing information for updating the graph node relations
     */
    @Transactional
    @Override
    public void updateRelation(final RelationUpdateDTO relationUpdateDTO) {
        final String graphUuid = relationUpdateDTO.getGraphUuid();
        final Map<String, String> updateMap = relationUpdateDTO.getUpdateMap();
        final List<String> deleteList = relationUpdateDTO.getDeleteList();

        if (updateMap != null && !updateMap.isEmpty()) {
            validateAndUpdateRelations(updateMap, graphUuid);
        }

        if (deleteList != null && !deleteList.isEmpty()) {
            validateAndDeleteRelations(deleteList, graphUuid);
        }
    }

    /**
     * Validates and updates graph node relations based on the provided map.
     *
     * @param updateMap  the map containing information for updating the graph node relations
     * @param graphUuid  the UUID of the graph
     *
     * @throws NodeRelationException if the relation UUID is not found
     */
    private void validateAndUpdateRelations(final Map<String, String> updateMap, final String graphUuid) {
        updateMap.forEach((uuid, newName) -> {
            if (graphNodeRepository.getRelationByUuid(uuid) == null) {
                throw new NodeRelationException(Message.GRAPH_NODE_RELATION_NULL + uuid);
            }
            graphNodeRepository.updateRelationByUuid(uuid, newName, graphUuid);
        });
    }

    /**
     * Validates and deletes graph node relations based on the provided list.
     *
     * @param deleteList the list of UUIDs of the graph node relations to be deleted
     * @param graphUuid  the UUID of the graph
     *
     * @throws NodeRelationException if the relation UUID is not found
     */
    private void validateAndDeleteRelations(final List<String> deleteList, final String graphUuid) {
        deleteList.forEach(uuid -> {
            if (graphNodeRepository.getRelationByUuid(uuid) == null) {
                throw new NodeRelationException(Message.GRAPH_NODE_RELATION_NULL + uuid);
            }
            graphNodeRepository.deleteRelationByUuid(uuid, graphUuid);
        });
    }

    /**
     * Retrieves the current timestamp.
     *
     * @return the current timestamp as a string
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
