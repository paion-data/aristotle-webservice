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
import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNodeRelationException;
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

@Service
@AllArgsConstructor
public class GraphNodeServiceImpl implements GraphNodeService {

    @Autowired
    private GraphNodeRepository graphNodeRepository;

    @Autowired
    private GraphService graphService;

    @Autowired
    private Neo4jService neo4jService;

    @Override
    public Optional<GraphNode> getNodeByUuid(String uuid) {
        GraphNode graphNode = graphNodeRepository.getGraphNodeByUuid(uuid);
        return Optional.ofNullable(graphNode);
    }

    @Transactional
    @Override
    public void createAndBindGraphAndNode(NodeCreateDTO nodeCreateDTO) {
        String graphUuid = nodeCreateDTO.getGraphUuid();

        Optional<Graph> optionalGraph = graphService.getGraphByUuid(graphUuid);
        if (optionalGraph.isEmpty()) {
            throw new GraphNullException(Message.GRAPH_NULL + graphUuid);
        }

        checkInputRelationsAndBindGraphAndNode(nodeCreateDTO.getGraphNodeDTO(),
                nodeCreateDTO.getGraphNodeRelationDTO(), graphUuid);
    }

    @Transactional
    @Override
    public void createGraphAndBindGraphAndNode(GraphAndNodeCreateDTO graphNodeCreateDTO) {
        Graph graph = graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());

        if (graphNodeCreateDTO.getGraphNodeDTO() == null) {
            return;
        }

        String graphUuid = graph.getUuid();

        checkInputRelationsAndBindGraphAndNode(graphNodeCreateDTO.getGraphNodeDTO(),
                graphNodeCreateDTO.getGraphNodeRelationDTO(), graphUuid);
    }

    private void checkInputRelationsAndBindGraphAndNode(List<NodeDTO> nodeDTOs,
                                                        List<NodeRelationDTO> nodeRelationDTOs, String graphUuid) {
        String now = getCurrentTime();
        Map<String, String> uuidMap = new HashMap<>();

        createNodes(nodeDTOs, uuidMap, now, graphUuid);

        if (nodeRelationDTOs == null) {
            return;
        }

        List<String> checkIds = new ArrayList<>();
        for (NodeRelationDTO dto : nodeRelationDTOs) {
            checkIds.add(dto.getFromId());
            checkIds.add(dto.getToId());
        }
        List<String> graphUuidByGraphNodeUuid = graphNodeRepository.getGraphUuidByGraphNodeUuid(checkIds);
        for (String s : graphUuidByGraphNodeUuid) {
            if (!s.equals(graphUuid)) {
                throw new GraphNodeRelationException(Message.BOUND_ANOTHER_GRAPH + s);
            }
        }

        bindNodeRelations(nodeRelationDTOs, uuidMap, now);
    }

    private void createNodes(List<NodeDTO> nodeDTOs, Map<String, String> uuidMap,
                             String now, String graphUuid) {
        for (NodeDTO dto : nodeDTOs) {
            String graphNodeUuid = UUID.fastUUID().toString(true);
            String relationUuid = UUID.fastUUID().toString(true);

            GraphNode createdNode = graphNodeRepository.createAndBindGraphNode(dto.getTitle(), dto.getDescription(),
                    graphUuid, graphNodeUuid, relationUuid, now);

            if (uuidMap.containsKey(dto.getTemporaryId())) {
                throw new TemporaryKeyException(Message.DUPLICATE_KEY + dto.getTemporaryId());
            } else {
                uuidMap.put(dto.getTemporaryId(), createdNode.getUuid());
            }
        }
    }

    private void bindNodeRelations(List<NodeRelationDTO> graphNodeRelationDTO,
                                   Map<String, String> uuidMap, String now) {
        if (graphNodeRelationDTO == null || graphNodeRelationDTO.isEmpty()) {
            return;
        }

        for (NodeRelationDTO dto : graphNodeRelationDTO) {
            String relation = dto.getRelationName();
            String relationUuid = UUID.fastUUID().toString(true);

            String fromId = getNodeId(dto.getFromId(), uuidMap);
            String toId = getNodeId(dto.getToId(), uuidMap);

            graphNodeRepository.bindGraphNodeToGraphNode(fromId, toId, relation, relationUuid, now);
        }
    }

    private String getNodeId(String id, Map<String, String> uuidMap) {
        return uuidMap.getOrDefault(id, id);
    }

    @Transactional
    @Override
    public void bindNodes(List<BindNodeDTO> dtos) {
        for (BindNodeDTO dto : dtos) {
            String startNode = dto.getFromId();
            String endNode = dto.getToId();
            Optional<GraphNode> graphNodeOptional1 = getNodeByUuid(startNode);
            Optional<GraphNode> graphNodeOptional2 = getNodeByUuid(endNode);
            String relationUuid = UUID.fastUUID().toString(true);
            String now = getCurrentTime();

            if (graphNodeOptional1.isEmpty() || graphNodeOptional2.isEmpty()) {
                if (graphNodeOptional1.isEmpty()) {
                    throw new GraphNodeNullException(Message.GRAPH_NODE_NULL + startNode);
                } else {
                    throw new GraphNodeNullException(Message.GRAPH_NODE_NULL + endNode);
                }
            }

            graphNodeRepository.bindGraphNodeToGraphNode(startNode, endNode, dto.getRelationName(), relationUuid, now);
        }
    }

    @Transactional
    @Override
    public void deleteByUuids(List<String> uuids) {
        for (String uuid : uuids) {
            if (getNodeByUuid(uuid).isEmpty()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL + uuid);
            }
        }

        graphNodeRepository.deleteByUuids(uuids);
    }

    @Transactional
    @Override
    public void updateNode(GraphUpdateDTO graphUpdateDTO) {
        String uuid = graphUpdateDTO.getUuid();
        Optional<GraphNode> graphNodeByUuid = getNodeByUuid(uuid);
        String now = getCurrentTime();

        if (graphNodeByUuid.isPresent()) {
            neo4jService.updateNodeByUuid(uuid, graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(), now);
        } else {
            throw new GraphNodeNullException(Message.GRAPH_NODE_NULL + uuid);
        }
    }

    @Override
    public void updateRelation(RelationUpdateDTO relationUpdateDTO) {
        String graphUuid = relationUpdateDTO.getGraphUuid();
        Map<String, String> updateMap = relationUpdateDTO.getUpdateMap();
        List<String> deleteList = relationUpdateDTO.getDeleteList();

        if (updateMap != null && !updateMap.isEmpty()) {
            validateAndUpdateRelations(updateMap, graphUuid);
        }

        if (deleteList != null && !deleteList.isEmpty()) {
            validateAndDeleteRelations(deleteList, graphUuid);
        }
    }

    private void validateAndUpdateRelations(Map<String, String> updateMap, String graphUuid) {
        updateMap.forEach((uuid, newName) -> {
            if (graphNodeRepository.getRelationByUuid(uuid) == null) {
                throw new GraphNodeRelationException(Message.GRAPH_NODE_RELATION_NULL + uuid);
            }
            graphNodeRepository.updateRelationByUuid(uuid, newName, graphUuid);
        });
    }

    private void validateAndDeleteRelations(List<String> deleteList, String graphUuid) {
        deleteList.forEach(uuid -> {
            if (graphNodeRepository.getRelationByUuid(uuid) == null) {
                throw new GraphNodeRelationException(Message.GRAPH_NODE_RELATION_NULL + uuid);
            }
            graphNodeRepository.deleteRelationByUuid(uuid, graphUuid);
        });
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
