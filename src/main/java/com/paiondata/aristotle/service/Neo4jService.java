package com.paiondata.aristotle.service;

import java.util.List;
import java.util.Map;

public interface Neo4jService {

    List<Map<String, Object>> getUserAndGraphsByUidcid(String uidcid);

    List<Map<String, Map<String, Object>>> getGraphNodeByGraphUuid(String uuid);

    void updateGraphByUuid(String uuid, String title, String description, String updateTime);

    void updateNodeByUuid(String uuid, String title, String description, String updateTime);
}
