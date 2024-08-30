package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphNodeExistsException;
import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
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
import java.util.List;
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
    public void createGraphNode(GraphCreateDTO graphCreateDTO) {
        String title = graphCreateDTO.getTitle();
        String description = graphCreateDTO.getDescription();
        String uuid = UUID.fastUUID().toString(true);
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        if (graphNodeRepository.checkGraphNodeExists(title, description) == 0) {
            graphNodeRepository.createGraphNode(title, description, uuid, now);
        } else {
            throw new GraphNodeExistsException(Message.GRAPH_NODE_EXISTS);
        }
    }

    @Transactional
    @Override
    public void bindGraph(String graphUuid, String graphNodeUuid) {
        Optional<Graph> optionalGraph = graphService.getGraphByUuid(graphUuid);
        Optional<GraphNode> graphNodeOptional = getGraphNodeByUuid(graphNodeUuid);

        if (!optionalGraph.isPresent() || !graphNodeOptional.isPresent()) {
            if (!optionalGraph.isPresent()) {
                throw new GraphNullException(Message.GRAPH_NULL);
            } else {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        graphNodeRepository.bindGraphToGraphNode(graphUuid, graphNodeUuid);
    }

    @Transactional
    @Override
    public void bindGraphNode(String uuid1, String uuid2, String relation) {
        Optional<GraphNode> graphNodeOptional1 = getGraphNodeByUuid(uuid1);
        Optional<GraphNode> graphNodeOptional2 = getGraphNodeByUuid(uuid2);

        if (!graphNodeOptional1.isPresent() || !graphNodeOptional2.isPresent()) {
            if (!graphNodeOptional1.isPresent()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            } else {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        graphNodeRepository.bindGraphNodeToGraphNode(uuid1, uuid2, relation);
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

        if (graphNodeByUuid.isPresent()) {
            graphNodeRepository.updateGraphNodeByUuid(graphUpdateDTO.getUuid(),
                    graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription());
        } else {
            throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
        }
    }
}
