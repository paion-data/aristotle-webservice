package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;

    @PostMapping("/relate1")
    public Result<String> relate1(@RequestParam String elementId1, @RequestParam String elementId2, @RequestParam String relationName) {
        relationService.bindUserGraph(elementId1, elementId2, relationName);
        return Result.ok(Message.BOUND_SUCCESS);
    }
}
