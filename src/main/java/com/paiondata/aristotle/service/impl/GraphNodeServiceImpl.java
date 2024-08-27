package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphNodeExistsException;
import com.paiondata.aristotle.common.exception.GraphNodeNullException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.service.GraphNodeService;
import com.paiondata.aristotle.service.GraphService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    public Optional<GraphNode> getGraphNodeByElementId(String elementId) {
        GraphNode graphNode = graphNodeRepository.getGraphNodeByElementId(elementId);
        return Optional.ofNullable(graphNode);
    }

    @Override
    public void createGraphNode(GraphCreateDTO graphCreateDTO) {
        String title = graphCreateDTO.getTitle();
        String description = graphCreateDTO.getDescription();
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        if (graphNodeRepository.checkGraphNodeExists(title, description) == 0) {
            graphNodeRepository.createGraphNode(title, description, now);
        } else {
            throw new GraphNodeExistsException(Message.GRAPH_NODE_EXISTS);
        }
    }

    @Override
    public void bindGraph(String elementId1, String elementId2) {
        Optional<Graph> optionalGraph = graphService.getGraphByElementId(elementId1);
        Optional<GraphNode> graphNodeOptional = getGraphNodeByElementId(elementId2);

        if (!optionalGraph.isPresent() || !graphNodeOptional.isPresent()) {
            if (!optionalGraph.isPresent()) {
                throw new GraphNullException(Message.GRAPH_NULL);
            } else {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        graphNodeRepository.bindGraphToGraphNode(elementId1, elementId2);
    }

    @Override
    public void bindGraphNode(String elementId1, String elementId2, String relation) {
        Optional<GraphNode> graphNodeOptional1 = getGraphNodeByElementId(elementId1);
        Optional<GraphNode> graphNodeOptional2 = getGraphNodeByElementId(elementId2);

        if (!graphNodeOptional1.isPresent() || !graphNodeOptional2.isPresent()) {
            if (!graphNodeOptional1.isPresent()) {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            } else {
                throw new GraphNodeNullException(Message.GRAPH_NODE_NULL);
            }
        }

        graphNodeRepository.bindGraphNodeToGraphNode(elementId1, elementId2, relation);
    }
}
