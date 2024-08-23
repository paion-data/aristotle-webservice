package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.entity.Node;
import com.paiondata.aristotle.model.entity.Relation;

import java.util.List;

public interface NodeService {
    Node save(Node node);
    void bind(String name1, String name2, String relationName);
    List<Relation> parseAndBind(String sentence);
}
