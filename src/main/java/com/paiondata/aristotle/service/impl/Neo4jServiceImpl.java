package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.service.GraphNodeService;
import com.paiondata.aristotle.service.GraphService;
import com.paiondata.aristotle.service.Neo4jService;
import com.paiondata.aristotle.service.UserService;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Neo4jServiceImpl implements Neo4jService {

    @Autowired
    private UserService userService;

    @Autowired
    private GraphService graphService;

    private final Driver driver;

    @Autowired
    public Neo4jServiceImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public List<Map<String, Map<String, Object>>> getGraphByUserUidcid(String uidcid) {

        if (userService.getUserByUidcid(uidcid).isEmpty()) {
            throw new UserNullException(Message.USER_NULL);
        }

        String cypherQuery = "MATCH (u:User)-[r:RELATION]->(g:Graph) WHERE u.uidcid = $uidcid RETURN DISTINCT u, r, g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                var result = tx.run(cypherQuery, Values.parameters("uidcid", uidcid));
                List<Map<String, Map<String, Object>>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> userNode = extractUser(record.get("u"));
                    Map<String, Object> graphNode = extractGraph(record.get("g"));
                    Map<String, Object> relation = extractRelationship(record.get("r"));

                    Map<String, Map<String, Object>> combinedResult = new HashMap<>();
                    combinedResult.put("user", userNode);
                    combinedResult.put("graph", graphNode);
                    combinedResult.put("relation", relation);

                    resultList.add(combinedResult);
                }
                return resultList;
            });
        }
    }

    @Override
    public List<Map<String, Map<String, Object>>> getGraphNodeByGraphUuid(String uuid) {

        if (graphService.getGraphByUuid(uuid).isEmpty()) {
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
                    combinedResult.put("graphNode1", n1);
                    combinedResult.put("relation", relation);
                    combinedResult.put("graphNode2", n2);

                    resultList.add(combinedResult);
                }

                for (Map<String, Map<String, Object>> stringObjectMap : resultList) {
                    System.out.println(stringObjectMap);
                }

                for (int i = 0; i< resultList.size(); i++) {
                    for (int j = 0; j < resultList.size(); j++) {
                        if (resultList.get(i).get("graphNode2").isEmpty()
                                && resultList.get(i).get("graphNode1").equals(resultList.get(j).get("graphNode2"))) {
                            if (!resultList.get(j).get("graphNode1").equals(resultList.get(j).get("graphNode2"))) {
                                resultList.remove(i);
                            }
                        }
                    }
                }

                return resultList;
            });
        }
    }

    private Map<String, Object> extractUser(Object node) {
        Map<String, Object> nodeInfo = new HashMap<>();
        if (node instanceof NodeValue) {
            NodeValue nodeValue = (NodeValue) node;
            Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            nodeInfo.put("uidcid", nodeMap.get("uidcid"));
            nodeInfo.put("nickName", nodeMap.get("nick_name"));
        }
        return nodeInfo;
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
