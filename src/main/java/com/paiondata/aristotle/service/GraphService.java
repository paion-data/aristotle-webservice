package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import java.util.Optional;

public interface GraphService {

    Optional<Graph> getGraphByTitle(String title);

    Optional<Graph> getGraphByElementId(String elementId);

    void createGraph(GraphCreateDTO graphCreateDTO);

    void bindUserGraph(String elementId1, String elementId2);
}
