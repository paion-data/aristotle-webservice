package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.service.Neo4jService;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Neo4jServiceImpl implements Neo4jService {

    private final Driver driver;

    @Autowired
    public Neo4jServiceImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public List<Map<String, Object>> getGraphByUserUidcid(String uidcid) {
        String cypherQuery = "MATCH (u:User)-[r:RELATION]->(g:Graph) WHERE u.uidcid = $uidcid RETURN u, r, g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                var result = tx.run(cypherQuery, Values.parameters("uidcid", uidcid));
                List<Map<String, Object>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> userNode = extractUserNode(record.get("u"));
                    Map<String, Object> graphNode = extractGraphNode(record.get("g"));
                    Map<String, Object> relation = extractRelationship(record.get("r"));

                    Map<String, Object> combinedResult = new HashMap<>();
                    combinedResult.put("user", userNode);
                    combinedResult.put("graph", graphNode);
                    combinedResult.put("relation", relation);

                    resultList.add(combinedResult);
                }
                return resultList;
            });
        }
    }

    private Map<String, Object> extractUserNode(Object node) {
        Map<String, Object> nodeInfo = new HashMap<>();
        if (node instanceof NodeValue) {
            NodeValue nodeValue = (NodeValue) node;
            Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            nodeInfo.put("uidcid", nodeMap.get("uidcid"));
            nodeInfo.put("nickName", nodeMap.get("nick_name"));
        }
        return nodeInfo;
    }

    private Map<String, Object> extractGraphNode(Object node) {
        Map<String, Object> nodeInfo = new HashMap<>();
        if (node instanceof NodeValue) {
            NodeValue nodeValue = (NodeValue) node;
            Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            nodeInfo.put("description", nodeMap.get("description"));
            nodeInfo.put("updateTime", nodeMap.get("update_time"));
            nodeInfo.put("createTime", nodeMap.get("create_time"));
            nodeInfo.put("title", nodeMap.get("title"));
            nodeInfo.put("uuid", nodeMap.get("uuid"));
        }
        return nodeInfo;
    }

    private Map<String, Object> extractRelationship(Object relationship) {
        Map<String, Object> relationshipInfo = new HashMap<>();
        if (relationship instanceof RelationshipValue) {
            RelationshipValue relationshipValue = (RelationshipValue) relationship;
            Map<String, Object> relMap = relationshipValue.asRelationship().asMap();

            relationshipInfo.put("name", relMap.get("name"));
            relationshipInfo.put("createTime", relMap.get("create_time"));
            relationshipInfo.put("updateTime", relMap.get("update_time"));
            relationshipInfo.put("uuid", relMap.get("uuid"));
        }
        return relationshipInfo;
    }
}
