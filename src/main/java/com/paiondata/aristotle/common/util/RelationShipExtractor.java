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
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.vo.RelationVO;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extracts information from relationships.
 */
@Component
public class RelationShipExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(RelationShipExtractor.class);

    /**
     * Extracts and converts a relationship value into a {@link RelationVO} object.
     * This method checks if the provided relationship value is null. If it is null,
     * an {@link IllegalArgumentException} is thrown.
     * If the value is a valid relationship, it is converted into a {@link RelationVO} object
     * by calling the `setRelationVOInfo` method.
     *
     * @param relation The relationship value to be extracted.
     *
     * @return A {@link RelationVO} object representing the extracted relationship.
     *
     * @throws IllegalArgumentException If the provided relationship value is null.
     */
    public RelationVO extractRelationship(final Value relation) {
        if (relation == null) {
            final String message = String.format(Message.VALUE_CAN_NOT_BE_NULL, "Relation");
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        RelationVO relationVO = null;
        if (relation instanceof RelationshipValue) {
            final Relationship relationshipValue = relation.asRelationship();
            relationVO = setRelationVOInfo(relationshipValue);
        }
        return relationVO;
    }

    /**
     * Extracts relationships from a given relationships value.
     * If the input relationships value is null, an {@link IllegalArgumentException} is thrown with
     * a specific error message.
     * If the input relationships value is valid, it extracts and returns a list of {@link RelationVO} objects.
     *
     * @param relationshipsValue the relationships value to extract relationships from
     *
     * @return a list of {@link RelationVO} objects representing the relationships
     *
     * @throws IllegalArgumentException if the input relationships value is null
     */
    public List<RelationVO> extractRelationships(final Value relationshipsValue) {
        if (relationshipsValue == null) {
            final String message = String.format(Message.VALUE_CAN_NOT_BE_NULL, "Relations");
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        final List<RelationVO> relations = new ArrayList<>();

        Optional.ofNullable(relationshipsValue.asList(Value::asRelationship))
                .ifPresent(relationships -> {
                    for (final Relationship relationshipValue : relationships) {
                        relations.add(setRelationVOInfo(relationshipValue));
                    }
                });

        return relations;
    }

    /**
     * Sets the node information in a RelationVO object.
     *
     * @param relationshipValue the relationship value to extract node information from
     *
     * @return the RelationVO object
     */
    private RelationVO setRelationVOInfo(final Relationship relationshipValue) {
        final Map<String, Object> relMap = relationshipValue.asMap();
        final Map<String, String> stringRelMap = relMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));

        return RelationVO.builder()
                .name(stringRelMap.getOrDefault(Constants.NAME, ""))
                .createTime(stringRelMap.getOrDefault(Constants.CREATE_TIME_WITHOUT_HUMP, ""))
                .updateTime(stringRelMap.getOrDefault(Constants.UPDATE_TIME_WITHOUT_HUMP, ""))
                .uuid(stringRelMap.getOrDefault(Constants.UUID, ""))
                .sourceNode(stringRelMap.getOrDefault(Constants.SOURCE_NODE, ""))
                .targetNode(stringRelMap.getOrDefault(Constants.TARGET_NODE, ""))
                .build();
    }
}
