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
package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface GraphNodeService {

    Optional<GraphNode> getNodeByUuid(String uuid);

    void createAndBindGraphAndNode(NodeCreateDTO graphNodeCreateDTO);

    void createGraphAndBindGraphAndNode(GraphAndNodeCreateDTO graphNodeCreateDTO);

    void bindNodes(List<BindNodeDTO> dtos);

    void deleteByUuids(List<String> uuids);

    void updateNode(GraphUpdateDTO graphUpdateDTO);

    void updateRelation(RelationUpdateDTO relationUpdateDTO);
}
