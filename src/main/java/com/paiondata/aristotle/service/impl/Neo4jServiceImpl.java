package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.Neo4jService;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Neo4jServiceImpl implements Neo4jService {

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private UserRepository userRepository;

    private final Driver driver;

    @Autowired
    public Neo4jServiceImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public List<Map<String, Object>> getUserAndGraphsByUidcid(String uidcid) {

        if (userRepository.getUserByUidcid(uidcid) == null) {
            throw new UserNullException(Message.USER_NULL);
        }

        String cypherQuery = "MATCH (u:User)-[r:RELATION]->(g:Graph) WHERE u.uidcid = $uidcid RETURN DISTINCT g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                var result = tx.run(cypherQuery, Values.parameters("uidcid", uidcid));
                List<Map<String, Object>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> graphNode = extractGraph(record.get("g"));

                    resultList.add(graphNode);
                }
                return resultList;
            });
        }
    }

    @Override
    public List<Map<String, Map<String, Object>>> getGraphNodeByGraphUuid(String uuid) {

        if (graphRepository.getGraphByUuid(uuid) == null) {
            throw new UserNullException(Message.GRAPH_NULL);
        }

        String cypherQuery = "MATCH (g:Graph { uuid: $uuid }) "
                + "OPTIONAL MATCH (g)-[:RELATION]->(n1:GraphNode) "
                + "OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
                + "RETURN DISTINCT n1, r, n2";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                var result = tx.run(cypherQuery, Values.parameters("uuid", uuid));
                List<Map<String, Map<String, Object>>> resultList = new ArrayList<>();

                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> n1 = extractGraphNode(record.get("n1"));
                    Map<String, Object> n2 = extractGraphNode(record.get("n2"));
                    Map<String, Object> relation = extractRelationship(record.get("r"));


                    Map<String, Map<String, Object>> combinedResult = new HashMap<>();
                    combinedResult.put("startNode", n1);
                    combinedResult.put("relation", relation);
                    combinedResult.put("endNode", n2);

                    resultList.add(combinedResult);
                }

                for (int i = 0; i< resultList.size(); i++) {
                    for (int j = 0; j < resultList.size(); j++) {
                        if (resultList.get(i).get("endNode").isEmpty()
                                && resultList.get(i).get("startNode").equals(resultList.get(j).get("endNode"))) {
                            if (!resultList.get(j).get("startNode").equals(resultList.get(j).get("endNode"))) {
                                resultList.remove(i);
                            }
                        }
                    }
                }

                return resultList;
            });
        }
    }

    private Map<String, Object> extractGraph(Object node) {
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
}
