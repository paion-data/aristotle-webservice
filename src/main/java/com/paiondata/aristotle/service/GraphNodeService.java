package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.GraphGraphNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.GraphNode;

import java.util.List;
import java.util.Optional;

public interface GraphNodeService {

    Optional<GraphNode> getGraphNodeByTitle(String title);

    Optional<GraphNode> getGraphNodeByUuid(String uuid);

    void createAndBindGraphNode(GraphNodeCreateDTO graphNodeCreateDTO);

    void createAndBindGraphGraphNode(GraphGraphNodeCreateDTO graphNodeCreateDTO);

    void bindGraphNode(String uuid1, String uuid2, String relation);

    void deleteByUuids(List<String> uuids);

    void updateGraphNode(GraphUpdateDTO graphUpdateDTO);
}
