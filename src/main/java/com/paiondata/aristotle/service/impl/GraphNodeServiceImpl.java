package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.service.GraphNodeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GraphNodeServiceImpl implements GraphNodeService {

    @Autowired
    private GraphNodeRepository graphNodeRepository;

    @Override
    public Optional<GraphNode> getGraphNodeByElementId(String elementId) {
        GraphNode graphNode = graphNodeRepository.getGraphNodeByElementId(elementId);
        return Optional.ofNullable(graphNode);
    }
}
