package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;

import java.util.List;
import java.util.Optional;

public interface GraphService {

    Optional<Graph> getGraphByTitle(String title);

    Optional<Graph> getGraphByUuid(String uuid);

    void createGraph(GraphCreateDTO graphCreateDTO);

    void bindUserGraph(String userUidcid, String graphUuid);

    void deleteByUuids(List<String> Uuids);
}
