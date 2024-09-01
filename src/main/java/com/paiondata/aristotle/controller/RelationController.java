package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.entity.Relation;
import com.paiondata.aristotle.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@RestController
@RequestMapping("/relation")
public class RelationController {

    @Autowired
    private RelationService relationService;

    @GetMapping("/{uuid}")
    public Result<Relation> getRelationByUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) String uuid) {
        Optional<Relation> optionalRelation = relationService.getRelationByUuid(uuid);
        return optionalRelation.map(Result::ok).orElseGet(() -> Result.fail(Message.RELATION_NULL));
    }
}
