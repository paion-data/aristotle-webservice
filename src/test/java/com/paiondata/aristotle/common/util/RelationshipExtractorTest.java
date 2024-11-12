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

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.TestConstants;
import com.paiondata.aristotle.model.vo.RelationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Test class for verifying the functionality of the {@link RelationShipExtractor} class.
 */
public class RelationshipExtractorTest {

    /**
     * The instance of {@link RelationShipExtractor} being tested.
     */
    private RelationShipExtractor relationShipExtractor;

    /**
     * Initializes the {@link RelationShipExtractor} instance before each test method.
     */
    @BeforeEach
    void setUp() {
        relationShipExtractor = new RelationShipExtractor();
    }
    /**
     * Tests the behavior of the {@link NodeExtractor#extractNode(Value)} method with a null input.
     */
    @Test
    void testExtractRelationWithNullInput() {
        // Arrange
        final Value relation = null;

        // Act and Assert
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> relationShipExtractor.extractRelationship(relation));
        assertEquals(String.format(Message.VALUE_CAN_NOT_BE_NULL, "Relation"), exception.getMessage());
    }

    /**
     * Tests the behavior of the {@link RelationShipExtractor#extractRelationship(Value)} method
     * with a valid relationship value input.
     */
    @Test
    void testExtractRelationshipWithValidRelationship() {
        final Relationship relationship = mock(Relationship.class);

        final Map<String, Object> relMap = new HashMap<>();
        relMap.put(Constants.NAME, TestConstants.TEST_NAME1);
        relMap.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        relMap.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        relMap.put(Constants.UUID, TestConstants.TEST_ID1);
        relMap.put(Constants.SOURCE_NODE, TestConstants.TEST_ID2);
        relMap.put(Constants.TARGET_NODE, TestConstants.TEST_ID3);

        when(relationship.asMap()).thenReturn(relMap);
        final RelationshipValue relationshipValue = mock(RelationshipValue.class);
        when(relationshipValue.asRelationship()).thenReturn(relationship);

        final RelationVO result = relationShipExtractor.extractRelationship(relationshipValue);

        assertNotNull(result);
        assertEquals(TestConstants.TEST_NAME1, result.getName());
        assertEquals(TestConstants.TEST_TIME_01, result.getCreateTime());
        assertEquals(TestConstants.TEST_TIME_02, result.getUpdateTime());
        assertEquals(TestConstants.TEST_ID1, result.getUuid());
        assertEquals(TestConstants.TEST_ID2, result.getSourceNode());
        assertEquals(TestConstants.TEST_ID3, result.getTargetNode());
    }

    /**
     * Tests the behavior of the {@link RelationShipExtractor#extractRelationships(Value)}} method with a null input.
     */
    @Test
    void testExtractRelationshipsWithNullInput() {
        // Arrange
        final Value relations = null;

        // Act and Assert
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> relationShipExtractor.extractRelationships(relations));
        assertEquals(String.format(Message.VALUE_CAN_NOT_BE_NULL, "Relations"), exception.getMessage());
    }

    /**
     * Tests the behavior of the  method with multiple relationships.
     * Expected Result: Returns a {@code List} containing the relationship properties.
     */
    @Test
    void testExtractRelationshipsWithMultipleRelationships() {
        final List<Relationship> relationships = new ArrayList<>();
        final Relationship relationship1 = mock(Relationship.class);
        final Relationship relationship2 = mock(Relationship.class);

        final Map<String, Object> relMap1 = new HashMap<>();
        relMap1.put(Constants.NAME, TestConstants.TEST_NAME1);
        relMap1.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        relMap1.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        relMap1.put(Constants.UUID, TestConstants.TEST_ID1);
        relMap1.put(Constants.SOURCE_NODE, TestConstants.TEST_ID2);
        relMap1.put(Constants.TARGET_NODE, TestConstants.TEST_ID3);

        final Map<String, Object> relMap2 = new HashMap<>();
        relMap2.put(Constants.NAME, TestConstants.TEST_NAME2);
        relMap2.put(Constants.CREATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_01);
        relMap2.put(Constants.UPDATE_TIME_WITHOUT_HUMP, TestConstants.TEST_TIME_02);
        relMap2.put(Constants.UUID, TestConstants.TEST_ID2);
        relMap2.put(Constants.SOURCE_NODE, TestConstants.TEST_ID3);
        relMap2.put(Constants.TARGET_NODE, TestConstants.TEST_ID4);

        when(relationship1.asMap()).thenReturn(relMap1);
        when(relationship2.asMap()).thenReturn(relMap2);

        relationships.add(relationship1);
        relationships.add(relationship2);

        final ListValue relationshipsValue = mock(ListValue.class);
        when(relationshipsValue.asList(any(Function.class))).thenReturn(relationships);

        final List<RelationVO> result = relationShipExtractor.extractRelationships(relationshipsValue);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TestConstants.TEST_NAME1, result.get(0).getName());
        assertEquals(TestConstants.TEST_TIME_01, result.get(0).getCreateTime());
        assertEquals(TestConstants.TEST_TIME_02, result.get(0).getUpdateTime());
        assertEquals(TestConstants.TEST_ID1, result.get(0).getUuid());
        assertEquals(TestConstants.TEST_ID2, result.get(0).getSourceNode());
        assertEquals(TestConstants.TEST_ID3, result.get(0).getTargetNode());

        assertEquals(TestConstants.TEST_NAME2, result.get(1).getName());
        assertEquals(TestConstants.TEST_TIME_01, result.get(1).getCreateTime());
        assertEquals(TestConstants.TEST_TIME_02, result.get(1).getUpdateTime());
        assertEquals(TestConstants.TEST_ID2, result.get(1).getUuid());
        assertEquals(TestConstants.TEST_ID3, result.get(1).getSourceNode());
        assertEquals(TestConstants.TEST_ID4, result.get(1).getTargetNode());
    }
}
