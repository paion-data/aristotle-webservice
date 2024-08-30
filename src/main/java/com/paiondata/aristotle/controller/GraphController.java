package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.BindGraphUserDTO;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
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
    public Result<String> bindUser(@RequestBody @Valid BindGraphUserDTO bindGraphUserDTO) {
        graphService.bindUserGraph(bindGraphUserDTO.getUserUidcid(), bindGraphUserDTO.getGraphUuid());
        return Result.ok(Message.BOUND_SUCCESS);
    }

    @PutMapping
    public Result<String> updateGraph(@RequestBody GraphUpdateDTO graphUpdateDTO) {
        graphService.updateGraph(graphUpdateDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    @DeleteMapping
    public Result<String> deleteGraph(@RequestBody @NotEmpty(message = Message.UUID_MUST_NOT_BE_BLANK)
                                     List<String> uuids) {
        graphService.deleteByUuids(uuids);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
