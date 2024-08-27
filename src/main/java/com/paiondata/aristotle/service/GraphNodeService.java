package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.GraphNode;

import java.util.List;
import java.util.Optional;

public interface GraphNodeService {

    Optional<GraphNode> getGraphNodeByTitle(String title);

    Optional<GraphNode> getGraphNodeByElementId(String elementId);

    void createGraphNode(GraphCreateDTO graphCreateDTO);

    void bindGraph(String elementId1, String elementId2);

    void bindGraphNode(String elementId1, String elementId2, String relation);

    void deleteByElementIds(List<String> graphElementIds);
}
