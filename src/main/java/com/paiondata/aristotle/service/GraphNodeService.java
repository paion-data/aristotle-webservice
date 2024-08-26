package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.entity.GraphNode;
import java.util.Optional;

public interface GraphNodeService {

    Optional<GraphNode> getGraphNodeByElementId(String elementId);
}
