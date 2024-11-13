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
package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.service.NodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Controller for handling graph node-related operations.
 */
@Api(tags = "Node controller for handling graph node-related operations")
@RestController
@RequestMapping("/node")
@Validated
public class NodeController {

    private static final Logger LOG = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    private NodeService nodeService;

    /**
     * Retrieves a node by its UUID.
     *
     * <p>
     * This method handles a GET request to retrieve a node based on the provided UUID.
     * It validates the UUID and calls the node service to fetch the node data.
     * If the node is found, it is wrapped in a {@link Result} object and returned.
     * If the node is not found, a failure result with an appropriate message is returned.
     *
     * @param uuid the UUID of the node to retrieve
     *
     * @return a {@link Result} object containing the node data as a {@link NodeVO},
     * or a failure message if the node is not found
     */
    @ApiOperation(value = "Retrieves a node by UUID")
    @GetMapping("/{uuid}")
    public ResponseEntity<Result<NodeVO>> getNodeByUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) final String uuid) {
        final Optional<NodeVO> optionalNode = nodeService.getNodeByUuid(uuid);

        if (optionalNode.isPresent()) {
            return ResponseEntity.ok(Result.ok(optionalNode.get()));
        } else {
            final String message = String.format(Message.NODE_NULL, uuid);
            LOG.error(message);
            throw new NoSuchElementException(message);
        }
    }

    /**
     * Retrieves a k-degree expansion of a node.
     * <p>
     * This endpoint retrieves the k-degree expansion of a node within a specified graph.
     * If the input degree is less than 0, the maximum depth is directly returned.
     *
     * @param graphUuid The UUID of the graph.
     * @param nodeUuid The UUID of the node.
     * @param degree The degree that needs to be expanded.
     *
     * @return A {@link Result} object containing the expanded graph represented as a {@link GraphVO}.
     */
    @ApiOperation(value = "Retrieves a k-degree expansion of a node",
            notes = "If the input degree is less than 0, the maximum depth is directly returned")
    @GetMapping("/expand")
    public Result<GraphVO> kDegreeExpansion(
            @ApiParam(value = "The UUID of the graph", required = true)
            @RequestParam @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) final String graphUuid,
            @ApiParam(value = "The UUID of the node", required = true)
            @RequestParam @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) final String nodeUuid,
            @ApiParam(value = "The degree that needs to be expanded, "
                    + "if is less than 0, the maximum depth is directly returned")
            @RequestParam @NotNull(message = Message.DEGREE_MUST_NOT_BE_NULL) final Integer degree) {
        return Result.ok(nodeService.getkDegreeExpansion(graphUuid, nodeUuid, degree));
    }

    /**
     * Creates and binds nodes.
     *
     * <p>
     * This method handles a POST request to create and optionally bind nodes.
     * It validates the input DTO and calls the node service to create the nodes.
     * If specified, it also binds the nodes with relationships.
     * The result is wrapped in a {@link Result} object with a success message and the list of created nodes.
     *
     * @param graphNodeCreateDTO the {@link NodeCreateDTO} containing the node creation and binding information
     *
     * @return a {@link Result} object containing a success message and a list of created nodes as {@link NodeVO}
     *
     * @notes The nodes could be created without binding any relations,
     * or create relationships only on existing nodes without creating new nodes
     */
    @ApiOperation(value = "Creates and binds nodes",
            notes = "The nodes could be created without binding any relations, "
                    + "or create relationships only on existing nodes without creating new nodes")
    @PostMapping
    public Result<List<NodeVO>> createAndBindNode(@RequestBody @Valid final NodeCreateDTO graphNodeCreateDTO) {
        return Result.ok(Message.CREATE_SUCCESS, nodeService.createAndBindGraphAndNode(graphNodeCreateDTO, null));
    }

    /**
     * Updates a node.
     *
     * <p>
     * This method handles a POST request to update a node based on the provided update DTO.
     * It validates the input DTO and calls the node service to perform the update.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param nodeUpdateDTO the {@link NodeUpdateDTO} containing the updated node information
     *
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Updates a node")
    @PostMapping("/update")
    public Result<String> updateNode(@RequestBody @Valid final NodeUpdateDTO nodeUpdateDTO) {
        nodeService.updateNode(nodeUpdateDTO, null);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Updates a relation between nodes.
     *
     * <p>
     * This method handles a PUT request to update a relation between nodes based on the provided update DTO.
     * It validates the input DTO and calls the node service to perform the relation update.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param relationUpdateDTO the {@link RelationUpdateDTO} containing the updated relation information
     *
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Updates a relation between nodes")
    @PutMapping("/relate")
    public Result<String> updateNodeRelation(@RequestBody @Valid final RelationUpdateDTO relationUpdateDTO) {
        nodeService.updateRelation(relationUpdateDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Deletes nodes by their UUIDs.
     *
     * <p>
     * This method handles a DELETE request to delete nodes based on the provided UUIDs.
     * It validates the input DTO and calls the node service to perform the deletion.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param nodeDeleteDTO the {@link NodeDeleteDTO} containing the UUIDs of the nodes to delete
     *
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Deletes nodes by their UUIDs")
    @DeleteMapping
    public Result<String> deleteNode(@RequestBody @Valid final NodeDeleteDTO nodeDeleteDTO) {
        nodeService.deleteByUuids(nodeDeleteDTO);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
