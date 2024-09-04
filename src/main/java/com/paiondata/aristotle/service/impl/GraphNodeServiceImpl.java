package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphNodeExistsException;
import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.model.dto.GraphGraphNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphNodeDTO;
import com.paiondata.aristotle.model.dto.GraphNodeExistRelationDTO;
import com.paiondata.aristotle.model.dto.GraphNodeTemporaryRelationDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
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

    @Override
    public Optional<GraphNode> getGraphNodeByTitle(String title) {
        GraphNode graphNode = graphNodeRepository.getGraphNodeByTitle(title);
        return Optional.ofNullable(graphNode);
    }

    @Override
    public Optional<GraphNode> getGraphNodeByUuid(String uuid) {
        GraphNode graphNode = graphNodeRepository.getGraphNodeByUuid(uuid);
        return Optional.ofNullable(graphNode);
    }

    @Transactional
    @Override
    public void createAndBindGraphNode(GraphNodeCreateDTO graphNodeCreateDTO) {
        String graphUuid = graphNodeCreateDTO.getGraphUuid();

        Optional<Graph> optionalGraph = graphService.getGraphByUuid(graphUuid);
        if (!optionalGraph.isPresent()) {
            throw new GraphNullException(Message.GRAPH_NULL);
        }

        Date now = getCurrentTime();
        Map<Long, String> uuidMap = new HashMap<>();

        createGraphNode(graphNodeCreateDTO.getGraphNodeDTO(), uuidMap, now, graphUuid);

        bindExistingNodeRelations(graphNodeCreateDTO.getGraphNodeExistRelationDTO(), uuidMap, now);

        bindTemporaryNodeRelations(graphNodeCreateDTO.getGraphNodeTemporaryRelationDTO(), uuidMap, now);
    }

    @Transactional
    @Override
    public void createAndBindGraphGraphNode(GraphGraphNodeCreateDTO graphNodeCreateDTO) {
        Graph graph = graphService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO());
        String graphUuid = graph.getUuid();

        Date now = getCurrentTime();
        Map<Long, String> uuidMap = new HashMap<>();

        createGraphNode(graphNodeCreateDTO.getGraphNodeDTO(), uuidMap, now, graphUuid);

        bindTemporaryNodeRelations(graphNodeCreateDTO.getGraphNodeTemporaryRelationDTO(), uuidMap, now);
    }

    private void createGraphNode (List<GraphNodeDTO> graphNodeDTO, Map<Long, String> uuidMap,
                                  Date now, String graphUuid) {
        for (GraphNodeDTO dto : graphNodeDTO) {
            String graphNodeUuid = UUID.fastUUID().toString(true);
            String relationUuid = UUID.fastUUID().toString(true);

            if (graphNodeRepository.checkGraphNodeExists(dto.getTitle(), dto.getDescription(), graphUuid) > 0) {
                throw new GraphNodeExistsException(Message.GRAPH_NODE_EXISTS);
            }

            GraphNode createdNode = graphNodeRepository.createAndBindGraphNode(dto.getTitle(), dto.getDescription(),
                    graphUuid, graphNodeUuid, relationUuid, now);

            uuidMap.put(dto.getTemporaryId(), createdNode.getUuid());
        }
    }

    private void bindExistingNodeRelations(List<GraphNodeExistRelationDTO> graphNodeExistRelationDTO,
                                           Map<Long, String> uuidMap, Date now) {
        if (graphNodeExistRelationDTO != null && !graphNodeExistRelationDTO.isEmpty()) {
            for (GraphNodeExistRelationDTO dto : graphNodeExistRelationDTO) {
                String temporaryGraphNodeUuid = uuidMap.get(dto.getTemporaryId());
                String existGraphNodeUuid = dto.getExistGraphNodeUuid();
                String relation = dto.getExistGraphNodeRelation();
                String relationUuid = UUID.fastUUID().toString(true);

                if (!getGraphNodeByUuid(existGraphNodeUuid).isPresent()) {
                    throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
                }

                bindGraphNodeRelation(temporaryGraphNodeUuid, existGraphNodeUuid, relation,
                        relationUuid, now, dto.isTarget());
            }
        }
    }

    private void bindTemporaryNodeRelations(List<GraphNodeTemporaryRelationDTO> gntrDTO,
                                            Map<Long, String> uuidMap, Date now) {
        if (gntrDTO != null && !gntrDTO.isEmpty()) {
            for (GraphNodeTemporaryRelationDTO dto : gntrDTO) {
                String uuid1 = uuidMap.get(dto.getTemporaryId1());
                String uuid2 = uuidMap.get(dto.getTemporaryId2());

                checkGraphNodeBindItself(uuid1, uuid2);

                String relation = dto.getTemporaryRelation();
                String relationUuid = UUID.fastUUID().toString(true);

                bindGraphNodeRelation(uuid1, uuid2, relation, relationUuid, now, dto.isTarget());
            }
        }
    }

    private void bindGraphNodeRelation(String uuid1, String uuid2, String relation, String relationUuid,
                                       Date now, boolean isTarget) {
        if (isTarget) {
            graphNodeRepository.bindGraphNodeToGraphNode(uuid1, uuid2, relation, relationUuid, now);
        } else {
            graphNodeRepository.bindGraphNodeToGraphNode(uuid2, uuid1, relation, relationUuid, now);
        }
    }

    @Transactional
    @Override
    public void bindGraphNode(String uuid1, String uuid2, String relation) {
        Optional<GraphNode> graphNodeOptional1 = getGraphNodeByUuid(uuid1);
        Optional<GraphNode> graphNodeOptional2 = getGraphNodeByUuid(uuid2);
        String relationUuid = UUID.fastUUID().toString(true);
        Date now = getCurrentTime();

        if (!graphNodeOptional1.isPresent() || !graphNodeOptional2.isPresent()) {
            if (!graphNodeOptional1.isPresent()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            } else {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        GraphNode relationByDoubleUuid = graphNodeRepository.getRelationByDoubleUuid(uuid1, uuid2);
        if (relationByDoubleUuid != null) {
            throw new GraphNodeExistsException(Message.RELATION_EXISTS);
        }

        graphNodeRepository.bindGraphNodeToGraphNode(uuid1, uuid2, relation, relationUuid, now);
    }

    @Transactional
    @Override
    public void deleteByUuids(List<String> uuids) {
        long l = graphNodeRepository.countByUuids(uuids);
        if (l != uuids.size()) {
            throw new GraphNullException(Message.GRAPH_NULL);
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

    private void checkGraphNodeBindItself(String uuid1, String uuid2) {
        if (uuid1.equals(uuid2)) {
            throw new GraphNodeRelationException(Message.SAME_NODE_RELATIONSHIP_ERROR);
        }
    }
}
