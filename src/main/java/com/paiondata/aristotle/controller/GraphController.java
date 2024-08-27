package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @GetMapping("/element/{elementId}")
    public Result<Graph> getGraphByElementId(
            @PathVariable @NotBlank(message = Message.ELEMENT_ID_MUST_NOT_BE_BLANK) String elementId) {
        Optional<Graph> optionalGraph = graphService.getGraphByElementId(elementId);
        return optionalGraph.map(Result::ok).orElseGet(() -> Result.fail(Message.USER_NULL));
    }

    @PostMapping
    public Result<String> createGraph(@RequestBody @Valid GraphCreateDTO graphCreateDTO) {
        graphService.createGraph(graphCreateDTO);
        return Result.ok(Message.CREATE_SUCCESS);
    }

    @PostMapping("/bindUser")
    public Result<String> bindUser(String userElementId, String graphElementId) {
        graphService.bindUserGraph(userElementId, graphElementId);
        return Result.ok(Message.BOUND_SUCCESS);
    }
}
