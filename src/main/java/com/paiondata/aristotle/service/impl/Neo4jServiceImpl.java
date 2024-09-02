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
                    Map<String, Object> userNode = extractNode(record.get("u"));
                    Map<String, Object> graphNode = extractNode(record.get("g"));
                    Map<String, Object> relation = extractRelationship(record.get("r"));

                    Map<String, Object> combinedResult = new HashMap<>();
                    combinedResult.put("user", userNode);
                    combinedResult.put("graph", graphNode);
                    combinedResult.put("relation", relation);

                    resultList.add(combinedResult);
                }
                System.out.println(resultList);
                return resultList;
            });
        }
    }

    private Map<String, Object> extractNode(Object node) {
        Map<String, Object> nodeInfo = new HashMap<>();
        if (node instanceof NodeValue) {
            NodeValue nodeValue = (NodeValue) node;
            Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            System.out.println("Node Map: " + nodeMap);

            nodeInfo.put("elementId", nodeMap.get("elementId"));
            nodeInfo.put("identity", nodeMap.get("id"));
            nodeInfo.put("labels", nodeMap.get("labels"));
            nodeInfo.put("properties", nodeMap.get("properties"));
        }
        return nodeInfo;
    }

    private Map<String, Object> extractRelationship(Object relationship) {
        Map<String, Object> relationshipInfo = new HashMap<>();
        if (relationship instanceof RelationshipValue) {
            RelationshipValue relationshipValue = (RelationshipValue) relationship;
            Map<String, Object> relMap = relationshipValue.asRelationship().asMap();

            System.out.println("Relationship Map: " + relMap);

            relationshipInfo.put("elementId", relMap.get("elementId"));
            relationshipInfo.put("identity", relMap.get("id"));
            relationshipInfo.put("start", relMap.get("start"));
            relationshipInfo.put("end", relMap.get("end"));
            relationshipInfo.put("type", relMap.get("type"));
            relationshipInfo.put("properties", relMap.get("properties"));
            relationshipInfo.put("startNodeElementId", relMap.get("startNode"));
            relationshipInfo.put("endNodeElementId", relMap.get("endNode"));
        }
        return relationshipInfo;
    }
}
