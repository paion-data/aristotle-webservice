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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.types.Node;
import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.TestConstants;
import com.paiondata.aristotle.model.vo.NodeVO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Test class for verifying the functionality of the {@link NodeExtractor} class.
 */
public class NodeExtractorTest {

    /**
     * The instance of {@link NodeExtractor} being tested.
     */
    private NodeExtractor nodeExtractor;

    /**
     * Initializes the {@link NodeExtractor} instance before each test method.
     */
    @BeforeEach
    void setUp() {
        nodeExtractor = new NodeExtractor();
    }

    /**
     * Tests the behavior of the {@link NodeExtractor#extractGraph(Value)} method with a null input.
     */
    @Test
    void testExtractGraphWithNullInput() {
        // Arrange
        final Value node = null;

        // Act and Assert
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> nodeExtractor.extractGraph(node));
        assertEquals(String.format(Message.VALUE_CAN_NOT_BE_NULL, "Graph"), exception.getMessage());
    }

    /**
     * Tests the behavior of the {@link NodeExtractor#extractGraph(Value)} method with a valid node value input.
     * Expected Result: Returns a {@code Map} containing the node properties.
     */
    @Test
    void testExtractGraphWithNodeValueInput() {
        final Node node = mock(Node.class);
        final Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put(Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION1);
        nodeMap.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        nodeMap.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        nodeMap.put(Constants.TITLE, TestConstants.TEST_TITLE1);
        nodeMap.put(Constants.UUID, TestConstants.TEST_ID1);

        when(node.asMap()).thenReturn(nodeMap);

        final NodeValue nodeValue = mock(NodeValue.class);
        when(nodeValue.asNode()).thenReturn(node);

        final Map<String, Object> result = nodeExtractor.extractGraph(nodeValue);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_DESCRIPTION1, result.get(Constants.DESCRIPTION));
        assertEquals(TestConstants.TEST_TIME_01, result.get(Constants.UPDATE_TIME));
        assertEquals(TestConstants.TEST_TIME_02, result.get(Constants.CREATE_TIME));
        assertEquals(TestConstants.TEST_TITLE1, result.get(Constants.TITLE));
        assertEquals(TestConstants.TEST_ID1, result.get(Constants.UUID));
    }

    /**
     * Tests the behavior of the {@link NodeExtractor#extractNode(Value)} method with a null input.
     */
    @Test
    void testExtractNodeWithNullInput() {
        // Arrange
        final Value node = null;

        // Act and Assert
        final Exception exception = assertThrows(IllegalArgumentException.class, () -> nodeExtractor.extractNode(node));
        assertEquals(String.format(Message.VALUE_CAN_NOT_BE_NULL, "Node"), exception.getMessage());
    }

    /**
     * Tests the behavior of the {@link NodeExtractor#extractNode(Value)} method with a valid node value input.
     * Expected Result: Returns a {@link NodeVO} instance containing the node properties.
     */
    @Test
    void testExtractNodeWithNodeValueInput() {
        final Node node = mock(Node.class);
        final Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put(Constants.UUID, TestConstants.TEST_ID1);
        nodeMap.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        nodeMap.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        nodeMap.put(TestConstants.TEST_KEY1, TestConstants.TEST_VALUE1);
        nodeMap.put(TestConstants.TEST_KEY2, TestConstants.TEST_VALUE2);

        when(node.asMap()).thenReturn(nodeMap);

        final NodeValue nodeValue = mock(NodeValue.class);
        when(nodeValue.asNode()).thenReturn(node);

        final NodeVO result = nodeExtractor.extractNode(nodeValue);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_ID1, result.getUuid());
        assertEquals(TestConstants.TEST_TIME_01, result.getCreateTime());
        assertEquals(TestConstants.TEST_TIME_02, result.getUpdateTime());
        assertEquals(2, result.getProperties().size());
        assertEquals(TestConstants.TEST_VALUE1, result.getProperties().get(TestConstants.TEST_KEY1));
        assertEquals(TestConstants.TEST_VALUE2, result.getProperties().get(TestConstants.TEST_KEY2));
    }

    /**
     * Tests the behavior of the {@link NodeExtractor#extractNodes(Value)} method with a null input.
     */
    @Test
    void testExtractNodesWithNullInput() {
        // Arrange
        final Value nodes = null;

        // Act and Assert
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> nodeExtractor.extractNodes(nodes));
        assertEquals(String.format(Message.VALUE_CAN_NOT_BE_NULL, "Nodes"), exception.getMessage());
    }

    /**
     * Tests the behavior of the  method with multiple nodes.
     * Expected Result: Returns a {@code Set} containing the node properties.
     */
    @Test
    void testExtractNodesWithMultipleNodes() {
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);

        final Map<String, Object> nodeMap1 = new HashMap<>();
        nodeMap1.put(Constants.UUID, TestConstants.TEST_ID1);
        nodeMap1.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        nodeMap1.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        nodeMap1.put(Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION1);

        final Map<String, Object> nodeMap2 = new HashMap<>();
        nodeMap2.put(Constants.UUID, TestConstants.TEST_ID2);
        nodeMap2.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        nodeMap2.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        nodeMap2.put(Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION2);

        when(node1.asMap()).thenReturn(nodeMap1);
        when(node2.asMap()).thenReturn(nodeMap2);

        final List<Node> nodes = Arrays.asList(node1, node2);

        final NodeValue nodesValue = mock(NodeValue.class);
        when(nodesValue.asList(any(Function.class))).thenReturn(nodes);

        final Set<NodeVO> result = nodeExtractor.extractNodes(nodesValue);

        assertNotNull(result);
        assertEquals(2, result.size());

        final NodeVO nodeVO1 = result.stream().filter(n -> TestConstants.TEST_ID1.equals(n.getUuid()))
                .findFirst().orElse(null);
        assertNotNull(nodeVO1);
        assertEquals(TestConstants.TEST_ID1, nodeVO1.getUuid());
        assertEquals(TestConstants.TEST_TIME_01, nodeVO1.getCreateTime());
        assertEquals(TestConstants.TEST_TIME_02, nodeVO1.getUpdateTime());
        assertEquals(1, nodeVO1.getProperties().size());
        assertEquals(TestConstants.TEST_DESCRIPTION1, nodeVO1.getProperties().get(Constants.DESCRIPTION));

        final NodeVO nodeVO2 = result.stream().filter(n -> TestConstants.TEST_ID2.equals(n.getUuid()))
                .findFirst().orElse(null);
        assertNotNull(nodeVO2);
        assertEquals(TestConstants.TEST_ID2, nodeVO2.getUuid());
        assertEquals(TestConstants.TEST_TIME_01, nodeVO2.getCreateTime());
        assertEquals(TestConstants.TEST_TIME_02, nodeVO2.getUpdateTime());
        assertEquals(1, nodeVO2.getProperties().size());
        assertEquals(TestConstants.TEST_DESCRIPTION2, nodeVO2.getProperties().get(Constants.DESCRIPTION));
    }
}
