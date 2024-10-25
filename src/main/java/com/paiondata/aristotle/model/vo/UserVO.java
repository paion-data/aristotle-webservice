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
package com.paiondata.aristotle.model.vo;

import com.paiondata.aristotle.model.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a value object (VO) for a user.
 *
 * This class encapsulates the properties and metadata of a user.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Represents a user")
public class UserVO extends BaseEntity {

    /**
     * The unique identifier (OIDC ID) of the user.
     */
    @ApiModelProperty(value = "The unique identifier (OIDC ID) of the user")
    private String oidcid;

    /**
     * The nickname of the user.
     */
    @ApiModelProperty(value = "The nickname of the user")
    private String nickName;

    /**
     * The list of graphs associated with the user.
     *
     * Each graph is represented as a map containing graph-specific information.
     */
    @ApiModelProperty(value = "The list of graphs associated with the user")
    private List<Map<String, Object>> graphs;
}
