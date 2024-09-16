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
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NodeCreateDTO extends BaseEntity {

    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    @Valid
    private List<NodeDTO> graphNodeDTO;

    private List<NodeRelationDTO> graphNodeRelationDTO;
}