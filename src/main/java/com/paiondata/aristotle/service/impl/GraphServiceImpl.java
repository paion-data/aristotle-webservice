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
package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.GraphService;
import com.paiondata.aristotle.service.Neo4jService;
import com.paiondata.aristotle.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing graphs.
 * This class provides methods for CRUD operations on graphs and their relationships.
 */
@Service
@AllArgsConstructor
public class GraphServiceImpl implements GraphService {

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GraphNodeRepository graphNodeRepository;

    @Autowired
    private Neo4jService neo4jService;

    /**
     * Retrieves a graph view object (VO) by its UUID.
     *
     * @param uuid the UUID of the graph
     */
    @Transactional(readOnly = true)
    @Override
    public GraphVO getGraphVOByUuid(final String uuid) {
        final Graph graphByUuid = graphRepository.getGraphByUuid(uuid);

        if (graphByUuid == null) {
            throw new GraphNullException(Message.GRAPH_NULL + uuid);
        }

        return GraphVO.builder()
                .uuid(graphByUuid.getUuid())
                .title(graphByUuid.getTitle())
                .description(graphByUuid.getDescription())
                .createTime(graphByUuid.getCreateTime())
                .updateTime(graphByUuid.getUpdateTime())
                .nodes(neo4jService.getGraphNodeByGraphUuid(uuid))
                .build();
    }

    /**
     * Retrieves a graph by its UUID.
     *
     * @param uuid the UUID of the graph
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<Graph> getGraphByUuid(final String uuid) {
        final Graph graph = graphRepository.getGraphByUuid(uuid);
        return Optional.ofNullable(graph);
    }

    /**
     * Creates and binds a new graph using the provided DTO.
     *
     * @param graphCreateDTO the DTO containing details to create a new graph
     */
    @Transactional
    @Override
    public Graph createAndBindGraph(final GraphCreateDTO graphCreateDTO) {
        final String title = graphCreateDTO.getTitle();
        final String description = graphCreateDTO.getDescription();
        final String uidcid = graphCreateDTO.getUserUidcid();
        final String graphUuid = UUID.fastUUID().toString(true);
        final String relationUuid = UUID.fastUUID().toString(true);
        final String now = getCurrentTime();

        final Optional<User> optionalUser = userService.getUserByUidcid(uidcid);
        if (optionalUser.isEmpty()) {
            throw new UserNullException(Message.USER_NULL + uidcid);
        }

        return graphRepository.createAndBindGraph(title, description, uidcid, graphUuid, relationUuid, now);
    }

    /**
     * Deletes graphs by their UUIDs.
     *
     * @param graphDeleteDTO the DTO containing the UUIDs of the graphs to delete
     */
    @Transactional
    @Override
    public void deleteByUuids(final GraphDeleteDTO graphDeleteDTO) {
        final String uidcid = graphDeleteDTO.getUidcid();
        final List<String> uuids = graphDeleteDTO.getUuids();

        for (final String uuid : uuids) {
            if (getGraphByUuid(uuid).isEmpty()) {
                throw new GraphNullException(Message.GRAPH_NULL + uuid);
            }
            if (graphRepository.getGraphByGraphUuidAndUidcid(uuid, uidcid) == null) {
                throw new DeleteException(Message.GRAPH_BIND_ANOTHER_USER + uuid);
            }
        }

        final List<String> relatedGraphNodeUuids = getRelatedGraphNodeUuids(uuids);

        graphNodeRepository.deleteByUuids(relatedGraphNodeUuids);
        graphRepository.deleteByUuids(uuids);
    }

    /**
     * Updates a graph using the provided DTO.
     *
     * @param graphUpdateDTO the DTO containing details to update an existing graph
     */
    @Transactional
    @Override
    public void updateGraph(final GraphUpdateDTO graphUpdateDTO) {
        final String uuid = graphUpdateDTO.getUuid();
        final Optional<Graph> graphByUuid = getGraphByUuid(uuid);
        final String now = getCurrentTime();

        if (graphByUuid.isPresent()) {
            neo4jService.updateGraphByUuid(uuid, graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(), now);
        } else {
            throw new GraphNullException(Message.GRAPH_NULL + uuid);
        }
    }

    /**
     * Retrieves the UUIDs of graph nodes related to a list of graph UUIDs.
     *
     * @param uuids the list of UUIDs of graphs
     *
     * @return the list of UUIDs of graph nodes
     */
    private List<String> getRelatedGraphNodeUuids(final List<String> uuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(uuids);
    }

    /**
     * Gets the current time in the specified format.
     *
     * @return the current time
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
