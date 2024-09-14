package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.entity.GraphNode;

import java.util.List;
import java.util.Optional;

public interface GraphNodeService {

    Optional<GraphNode> getNodeByUuid(String uuid);

    void createAndBindGraphAndNode(NodeCreateDTO graphNodeCreateDTO);

    void createGraphAndBindGraphAndNode(GraphAndNodeCreateDTO graphNodeCreateDTO);

    void bindNodes(List<BindNodeDTO> dtos);

    void deleteByUuids(List<String> uuids);

    void updateNode(GraphUpdateDTO graphUpdateDTO);
}
