/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.common.util;

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.RelationVO;

import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Extracts information from graph nodes, relationships, and nodes.
 */
@Component
public class NodeExtractor {

    /**
     * Extracts graph information from a NodeValue object.
     *
     * @param node  the NodeValue object to extract information from.
     * @return a map containing the extracted graph information.
     */
    public Map<String, Object> extractGraph(final Object node) {
        final Map<String, Object> graphInfo = new HashMap<>();
        if (node instanceof NodeValue) {
            final NodeValue nodeValue = (NodeValue) node;
            final Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            graphInfo.put(Constants.DESCRIPTION, nodeMap.get(Constants.DESCRIPTION));
            graphInfo.put(Constants.UPDATE_TIME, nodeMap.get(Constants.UPDATE_TIME_WITHOUT_HUMP));
            graphInfo.put(Constants.CREATE_TIME, nodeMap.get(Constants.CREATE_TIME_WITHOUT_HUMP));
            graphInfo.put(Constants.TITLE, nodeMap.get(Constants.TITLE));
            graphInfo.put(Constants.UUID, nodeMap.get(Constants.UUID));
        }
        return graphInfo;
    }

    /**
     * Extracts relationships from a Value object.
     *
     * @param relationshipsValue  the Value object containing relationships.
     * @return a list of RelationVO objects representing the extracted relationships.
     */
    public List<RelationVO> extractRelationships(final Value relationshipsValue) {
        final List<RelationVO> relations = new ArrayList<>();

        if (relationshipsValue != null) {
            Optional.ofNullable(relationshipsValue.asList(Value::asRelationship))
                    .ifPresent(relationships -> {
                        for (final Relationship relationshipValue : relationships) {
                            final Map<String, Object> relMap = relationshipValue.asMap();
                            final Map<String, String> stringRelMap = relMap.entrySet().stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey,
                                            entry -> String.valueOf(entry.getValue())));

                            final RelationVO relation = RelationVO.builder()
                                    .name(stringRelMap.getOrDefault(Constants.NAME, ""))
                                    .createTime(stringRelMap.getOrDefault(Constants.CREATE_TIME_WITHOUT_HUMP, ""))
                                    .updateTime(stringRelMap.getOrDefault(Constants.UPDATE_TIME_WITHOUT_HUMP, ""))
                                    .uuid(stringRelMap.getOrDefault(Constants.UUID, ""))
                                    .sourceNode(stringRelMap.getOrDefault(Constants.SOURCE_NODE, ""))
                                    .targetNode(stringRelMap.getOrDefault(Constants.TARGET_NODE, ""))
                                    .build();

                            relations.add(relation);
                        }
                    });
        }

        return relations;
    }

    /**
     * Extracts a single node from a Value object.
     *
     * @param node  the Value object representing a node.
     * @return a NodeVO object representing the extracted node.
     */
    public NodeVO extractNode(final Object node) {
        final NodeVO nodeInfo = new NodeVO();

        if (node instanceof NodeValue) {
            final NodeValue nodeValue = (NodeValue) node;
            final Map<String, Object> nodeMap = nodeValue.asNode().asMap();
            final Map<String, String> stringNodeMap = nodeMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));

            setNodeInfo(stringNodeMap, nodeInfo);
        }
        return nodeInfo;
    }

    /**
     * Extracts multiple nodes from a Value object.
     *
     * @param nodesValue  the Value object containing multiple nodes.
     * @return a set of NodeVO objects representing the extracted nodes.
     */
    public Set<NodeVO> extractNodes(final Value nodesValue) {
        final Set<NodeVO> nodes = new HashSet<>();

        if (nodesValue != null) {
            Optional.ofNullable(nodesValue.asList(Value::asNode))
                    .ifPresent(nodeList -> {
                        for (final Node n : nodeList) {
                            final Map<String, Object> nodeMap = n.asMap();
                            final Map<String, String> stringNodeMap = nodeMap.entrySet().stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey,
                                            entry -> String.valueOf(entry.getValue())));

                            final NodeVO nodeInfo = new NodeVO();
                            setNodeInfo(stringNodeMap, nodeInfo);

                            nodes.add(nodeInfo);
                        }
                    });
        }

        return nodes;
    }

    /**
     * Sets the node information in a NodeVO object.
     *
     * @param stringNodeMap  the map containing node information as strings.
     * @param nodeInfo       the NodeVO object to set the information in.
     */
    private void setNodeInfo(final Map<String, String> stringNodeMap, final NodeVO nodeInfo) {
        nodeInfo.setUuid(stringNodeMap.getOrDefault(Constants.UUID, ""));
        nodeInfo.setCreateTime(stringNodeMap.getOrDefault(Constants.CREATE_TIME_WITHOUT_HUMP, ""));
        nodeInfo.setUpdateTime(stringNodeMap.getOrDefault(Constants.UPDATE_TIME_WITHOUT_HUMP, ""));
        nodeInfo.setProperties(stringNodeMap.entrySet().stream()
                .filter(entry -> !Constants.UUID.equals(entry.getKey())
                        && !Constants.UPDATE_TIME_WITHOUT_HUMP.equals(entry.getKey())
                        && !Constants.CREATE_TIME_WITHOUT_HUMP.equals(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                )));
    }
}
