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

import java.util.Map;

import javax.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for representing a node.
 *
 * This DTO is used to encapsulate the data required for a node in a graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Represents a node in a graph.")
public class NodeDTO extends BaseEntity {

    /**
     * The temporary identifier of the node.
     *
     * @see Message#TEMPORARY_ID_MUST_NOT_NULL
     */
    @ApiModelProperty(value = "The temporary identifier of the node.", required = true)
    @NotBlank(message = Message.TEMPORARY_ID_MUST_NOT_NULL)
    private String temporaryId;

    /**
     * The properties of the node.
     */
    @ApiModelProperty(value = "properties")
    private Map<String, String> properties;
}
