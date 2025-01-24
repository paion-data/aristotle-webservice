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

import com.paiondata.aristotle.common.annotion.Neo4jTransactional;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.util.CaffeineCacheUtil;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.FilterQueryGraphDTO;
import com.paiondata.aristotle.model.dto.GetRelationDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.CommonService;
import com.paiondata.aristotle.service.GraphService;

import org.neo4j.driver.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service implementation for managing graphs.
 * This class provides methods for CRUD operations on graphs and their relationships.
 */
@Service
public class GraphServiceImpl implements GraphService {

    private static final Logger LOG = LoggerFactory.getLogger(GraphServiceImpl.class);

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphMapper graphMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CaffeineCacheUtil caffeineCache;

    /**
     * Retrieves a graph view object (VO) by its UUID and filter params.
     * <p>
     * Retrieves the graph by its UUID using the {@link GraphRepository#getGraphByUuid(String)} method.
     * Throws a {@link NoSuchElementException} if the graph is not found.
     * Retrieves the nodes and relations of the graph using the <br>
     * {@link NodeMapper#getRelationByGraphUuid(String, Map, Integer, Integer)} method.
     * Constructs and returns a {@link GraphVO} object with the graph's details and the retrieved nodes and relations.
     *
     * @param filterQueryGraphDTO The DTO containing the graph UUID and optional properties for filtering. <br>
     *                            It includes the graph UUID and an optional map of properties.
     * @return A {@link GraphVO} object representing the graph and its nodes and relations.
     *
     * @throws NoSuchElementException If the graph with the specified UUID is not found.
     */
    @Override
    public GraphVO getGraphVOByFilterParams(final FilterQueryGraphDTO filterQueryGraphDTO) {
        final String uuid = filterQueryGraphDTO.getUuid();
        final int pageNumber = filterQueryGraphDTO.getPageNumber();
        final int pageSize = filterQueryGraphDTO.getPageSize();

        // generate cache key
        final String cacheKey = generateCacheKey(uuid, pageNumber, pageSize);

        // if cache enabled, check if the graph is cached
        final Optional<GraphVO> optionalCachedGraphVO = caffeineCache.getCache(cacheKey);
        if (optionalCachedGraphVO.isPresent()) {
            return optionalCachedGraphVO.get();
        }

        // if cache disabled or no cache found, retrieve the graph from the database
        final Graph graphByUuid = graphRepository.getGraphByUuid(uuid);

        if (graphByUuid == null) {
            final String message = String.format(Message.GRAPH_NULL, uuid);
            LOG.error(message);
            throw new NoSuchElementException(message);
        }

        final Optional<Map<String, String>> optionalProperties = filterQueryGraphDTO.getProperties();

        final Map<String, String> properties = optionalProperties.orElse(Map.of());

        final GetRelationDTO dto = nodeMapper.getRelationByGraphUuid(uuid, properties, pageNumber, pageSize);

        final GraphVO graphVO = new GraphVO(graphByUuid.getUuid(), graphByUuid.getTitle(), graphByUuid.getDescription(),
                graphByUuid.getCreateTime(), graphByUuid.getUpdateTime(), dto.getNodes(), dto.getRelations(),
                pageNumber, pageSize, dto.getTotalCount());

        // if cache enabled, cache the graphVO
        caffeineCache.setCache(cacheKey, graphVO);

        return graphVO;
    }

    /**
     * Generates a cache key for the k-degree expansion of a graph.
     * <p>
     * This method generates a cache key for the pageNumber and pageSize of a graph by concatenating the graph UUID.
     *
     * @param uuid The UUID of the graph.
     * @param pageNumber The page number for pagination.
     * @param pageSize The page size for pagination.
     *
     * @return A string representing the cache key.
     */
    private String generateCacheKey(final String uuid, final int pageNumber, final int pageSize) {
        return String.format("%s_%d_%d", uuid, pageNumber, pageSize);
    }

    /**
     * Deletes graphs by their UUIDs.
     * <p>
     * Checks if the provided graph UUIDs exist and belong to the specified user.
     * For each UUID:
     * - Retrieves the graph by its UUID using the {@link CommonService#getGraphByUuid(String)} method.
     * - Throws a {@link NoSuchElementException} if the graph is not found.
     * - Throws a {@link IllegalStateException} if the graph is bound to another user.
     * Retrieves the UUIDs of related graph nodes using the {@link #getRelatedGraphNodeUuids(List)} method.
     * Deletes the related graph nodes using the {@link NodeRepository#deleteByUuids(List)} method.
     * Deletes the graphs using the {@link GraphRepository#deleteByUuids(List)} method.
     *
     * @param graphDeleteDTO The DTO containing the user identifier and the list of graph UUIDs to be deleted. <br>
     *                       It includes the user identifier ({@code oidcid}) and the list of graph UUIDs.
     *
     * @throws NoSuchElementException If any of the specified graphs are not found.
     * @throws IllegalStateException If any of the specified graphs are bound to another user.
     */
    @Transactional
    @Override
    public void deleteByUuids(final GraphDeleteDTO graphDeleteDTO) {
        final String oidcid = graphDeleteDTO.getOidcid();
        final List<String> uuids = graphDeleteDTO.getUuids();

        for (final String uuid : uuids) {
            if (commonService.getGraphByUuid(uuid).isEmpty()) {
                final String message = String.format(Message.GRAPH_NULL, uuid);
                LOG.error(message);
                throw new NoSuchElementException(message);
            }
            if (graphRepository.getGraphByGraphUuidAndOidcid(uuid, oidcid) == null) {
                final String message = String.format(Message.GRAPH_BIND_ANOTHER_USER, uuid);
                LOG.error(message);
                throw new IllegalStateException(message);
            }
        }

        final List<String> relatedGraphNodeUuids = getRelatedGraphNodeUuids(uuids);

        nodeRepository.deleteByUuids(relatedGraphNodeUuids);
        graphRepository.deleteByUuids(uuids);

        uuids.forEach(uuid -> caffeineCache.deleteCache(uuid));
    }

    /**
     * Updates an existing graph based on the provided DTO.
     * <p>
     * Checks if the provided Neo4j transaction ({@code tx}) is null, a {@link IllegalArgumentException} is thrown.
     * Retrieves the graph UUID from the {@code graphUpdateDTO}.
     * Attempts to find the graph by its UUID using the {@link CommonService#getGraphByUuid(String)} method.
     * Retrieves the current time.
     * If the graph is found, updates the graph's title and description using the <br>
     * {@link GraphMapper#updateGraphByUuid(String, String, String, String, Transaction)} method.
     *
     * @param graphUpdateDTO The DTO containing the updated information for the graph. <br>
     *                       It includes the graph UUID, title, and description.
     * @param tx The Neo4j transaction object used for the database operation.
     *
     * @throws IllegalArgumentException If the provided transaction is null.
     * @throws NoSuchElementException If the graph with the specified UUID is not found.
     */
    @Neo4jTransactional
    @Override
    public void updateGraph(final GraphUpdateDTO graphUpdateDTO, final Transaction tx) {

        if (tx == null) {
            final String message = Message.TRANSACTION_NULL;
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        final String uuid = graphUpdateDTO.getUuid();
        final Optional<Graph> graphByUuid = commonService.getGraphByUuid(uuid);
        final String now = getCurrentTime();

        if (graphByUuid.isPresent()) {
            graphMapper.updateGraphByUuid(uuid, graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(), now, tx);

            caffeineCache.deleteCache(uuid);
        } else {
            final String message = String.format(Message.GRAPH_NULL, uuid);
            LOG.error(message);
            throw new NoSuchElementException(message);
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
     * Retrieves the current timestamp.
     *
     * @return the current timestamp as a string
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
