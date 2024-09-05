package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;

import java.util.List;
import java.util.Optional;

public interface GraphService {

    Optional<Graph> getGraphByUuid(String uuid);

    Graph createAndBindGraph(GraphCreateDTO graphCreateDTO);

    void deleteByUuids(List<String> Uuids);

    void updateGraph(GraphUpdateDTO graphUpdateDTO);
}
