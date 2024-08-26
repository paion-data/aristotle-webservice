package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @PostMapping
    public Result<String> createGraph(@RequestBody GraphCreateDTO graphCreateDTO) {
        graphService.createGraph(graphCreateDTO);
        return Result.ok(Message.CREATE_SUCCESS);
    }
}
