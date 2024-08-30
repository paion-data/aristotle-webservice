package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.GraphNode;
import com.paiondata.aristotle.service.GraphNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@RestController
@RequestMapping("/graphNode")
public class GraphNodeController {

    @Autowired
    private GraphNodeService graphNodeService;

    @GetMapping("/{uuid}")
    public Result<GraphNode> getGraphNodeByUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) String uuid) {
        Optional<GraphNode> optionalGraphNode = graphNodeService.getGraphNodeByUuid(uuid);
        return optionalGraphNode.map(Result::ok).orElseGet(() -> Result.fail(Message.GRAPH_NODE_NULL));
    }

    @PostMapping
    public Result<String> createGraphNode(@RequestBody @Valid GraphCreateDTO graphCreateDTO) {
        graphNodeService.createGraphNode(graphCreateDTO);
        return Result.ok(Message.CREATE_SUCCESS);
    }

    @PostMapping("/bindGraph")
    public Result<String> bindGraph(@RequestParam @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
                                        String graphUuid,
                                    @RequestParam @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
                                    String graphNodeUuid) {
        graphNodeService.bindGraph(graphUuid, graphNodeUuid);
        return Result.ok(Message.BOUND_SUCCESS);
    }

    @PostMapping("/bindGraphNode")
    public Result<String> bindGraphNode(@RequestParam @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
                                            String uuid1,
                                        @RequestParam @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
                                        String uuid2,
                                        @RequestParam @NotBlank(message = Message.RELATION_MUST_NOT_BE_BLANK)
                                            String relation) {
        graphNodeService.bindGraphNode(uuid1, uuid2, relation);
        return Result.ok(Message.BOUND_SUCCESS);
    }
}
