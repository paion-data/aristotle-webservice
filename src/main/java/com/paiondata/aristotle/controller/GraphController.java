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

    @GetMapping("/{uuid}")
    public Result<Graph> getGraphByUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) String uuid) {
        Optional<Graph> optionalGraph = graphService.getGraphByUuid(uuid);
        return optionalGraph.map(Result::ok).orElseGet(() -> Result.fail(Message.USER_NULL));
    }

    @PostMapping
    public Result<String> createGraph(@RequestBody @Valid GraphCreateDTO graphCreateDTO) {
        graphService.createGraph(graphCreateDTO);
        return Result.ok(Message.CREATE_SUCCESS);
    }

    @PostMapping("/bind")
    public Result<String> bindUser(@NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK) String userUidcid,
                                   @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) String graphUuid) {
        graphService.bindUserGraph(userUidcid, graphUuid);
        return Result.ok(Message.BOUND_SUCCESS);
    }
}
