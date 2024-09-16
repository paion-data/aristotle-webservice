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
package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for updating relations in a graph.
 *
 * This DTO is used to encapsulate the data required for updating and deleting relations in a graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object for updating relations in a graph.")
public class RelationUpdateDTO extends BaseEntity {

    /**
     * The UUID of the graph where the relations will be updated.
     *
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The UUID of the graph where the relations will be updated.", required = true)
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    /**
     * A map containing the updates to be applied to the relations.
     *
     * The keys represent the identifiers of the relations, and the values represent the updated values.
     */
    @ApiModelProperty(value = "A map containing the updates to be applied to the relations.")
    private Map<String, String> updateMap;

    /**
     * A list of identifiers of relations to be deleted.
     */
    @ApiModelProperty(value = "A list of identifiers of relations to be deleted.")
    private List<String> deleteList;
}
