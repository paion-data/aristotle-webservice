package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.model.dto.*;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.service.GraphNodeService;
import com.paiondata.aristotle.service.GraphService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
public class GraphNodeServiceImpl implements GraphNodeService {

    @Autowired
    private GraphNodeRepository graphNodeRepository;

    @Autowired
    private GraphService graphService;

    @Override
    public Optional<GraphNode> getGraphNodeByUuid(String uuid) {
        GraphNode graphNode = graphNodeRepository.getGraphNodeByUuid(uuid);
        return Optional.ofNullable(graphNode);
    }

    @Transactional
    @Override
    public void createAndBindGraphNode(NodeCreateDTO graphNodeCreateDTO) {
        String graphUuid = graphNodeCreateDTO.getGraphUuid();

        Optional<Graph> optionalGraph = graphService.getGraphByUuid(graphUuid);
        if (optionalGraph.isEmpty()) {
            throw new GraphNullException(Message.GRAPH_NULL);
        }

        Date now = getCurrentTime();
        Map<String, String> uuidMap = new HashMap<>();

        createGraphNode(graphNodeCreateDTO.getGraphNodeDTO(), uuidMap, now, graphUuid);

        bindExistingNodeRelations(graphNodeCreateDTO.getGraphNodeExistRelationDTO(), uuidMap, now);

        bindTemporaryNodeRelations(graphNodeCreateDTO.getGraphNodeTemporaryRelationDTO(), uuidMap, now);
    }

    @Transactional
    @Override
    public void createAndBindGraphGraphNode(GraphAndNodeCreateDTO graphNodeCreateDTO) {
        Graph graph = graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());
        String graphUuid = graph.getUuid();

        Date now = getCurrentTime();
        Map<String, String> uuidMap = new HashMap<>();

        createGraphNode(graphNodeCreateDTO.getGraphNodeDTO(), uuidMap, now, graphUuid);

        bindTemporaryNodeRelations(graphNodeCreateDTO.getGraphNodeTemporaryRelationDTO(), uuidMap, now);
    }

    private void createGraphNode (List<NodeDTO> graphNodeDTO, Map<String, String> uuidMap,
                                  Date now, String graphUuid) {
        for (NodeDTO dto : graphNodeDTO) {
            String graphNodeUuid = UUID.fastUUID().toString(true);
            String relationUuid = UUID.fastUUID().toString(true);

            GraphNode createdNode = graphNodeRepository.createAndBindGraphNode(dto.getTitle(), dto.getDescription(),
                    graphUuid, graphNodeUuid, relationUuid, now);

            uuidMap.put(dto.getTemporaryId(), createdNode.getUuid());
        }
    }

    private void bindExistingNodeRelations(List<NodeExistRelationDTO> graphNodeExistRelationDTO,
                                           Map<String, String> uuidMap, Date now) {

        if (graphNodeExistRelationDTO == null || graphNodeExistRelationDTO.isEmpty()) {
            return;
        }

        for (NodeExistRelationDTO dto : graphNodeExistRelationDTO) {

            String existGraphNodeUuid =
                    getGraphNodeByUuid(dto.getFromId()).isPresent() ? dto.getFromId() : dto.getToId();

            String temporaryGraphNodeUuid =
                    getGraphNodeByUuid(
                            dto.getFromId()).isPresent() ? uuidMap.get(dto.getToId()) : uuidMap.get(dto.getFromId());

            if (getGraphNodeByUuid(existGraphNodeUuid).isEmpty()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }

            String relation = dto.getRelationName();
            String relationUuid = UUID.fastUUID().toString(true);
            bindGraphNodeRelation(existGraphNodeUuid, temporaryGraphNodeUuid, relation, relationUuid, now);
        }
    }

    private void bindTemporaryNodeRelations(List<NodeTemporaryRelationDTO> gntrDTO,
                                            Map<String, String> uuidMap, Date now) {
        if (gntrDTO == null || gntrDTO.isEmpty()) {
            return;
        }

        for (NodeTemporaryRelationDTO dto : gntrDTO) {
            String uuid1 = uuidMap.get(dto.getFromId());
            String uuid2 = uuidMap.get(dto.getToId());
            String relation = dto.getRelationName();
            String relationUuid = UUID.fastUUID().toString(true);
            bindGraphNodeRelation(uuid1, uuid2, relation, relationUuid, now);
        }
    }

    private void bindGraphNodeRelation(String uuid1, String uuid2, String relation, String relationUuid,
                                       Date now) {
        graphNodeRepository.bindGraphNodeToGraphNode(uuid1, uuid2, relation, relationUuid, now);
    }

    @Transactional
    @Override
    public void bindGraphNode(String uuid1, String uuid2, String relation) {
        Optional<GraphNode> graphNodeOptional1 = getGraphNodeByUuid(uuid1);
        Optional<GraphNode> graphNodeOptional2 = getGraphNodeByUuid(uuid2);
        String relationUuid = UUID.fastUUID().toString(true);
        Date now = getCurrentTime();

        if (graphNodeOptional1.isEmpty() || graphNodeOptional2.isEmpty()) {
            if (graphNodeOptional1.isEmpty()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            } else {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        graphNodeRepository.bindGraphNodeToGraphNode(uuid1, uuid2, relation, relationUuid, now);
    }

    @Transactional
    @Override
    public void deleteByUuids(List<String> uuids) {
        for (String uuid : uuids) {
            if (getGraphNodeByUuid(uuid).isEmpty()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        graphNodeRepository.deleteByUuids(uuids);
    }

    @Transactional
    @Override
    public void updateGraphNode(GraphUpdateDTO graphUpdateDTO) {
        Optional<GraphNode> graphNodeByUuid = getGraphNodeByUuid(graphUpdateDTO.getUuid());
        Date now = getCurrentTime();

        if (graphNodeByUuid.isPresent()) {
            graphNodeRepository.updateGraphNodeByUuid(graphUpdateDTO.getUuid(),
                    graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(),
                    now);
        } else {
            throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
        }
    }

    private Date getCurrentTime() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}
