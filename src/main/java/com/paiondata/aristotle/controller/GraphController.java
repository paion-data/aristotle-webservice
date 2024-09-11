package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @GetMapping("/{uuid}")
    public Result<GraphVO> getGraphAndNodesByGraphUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) String uuid) {
        return Result.ok(graphService.getGraphVOByUuid(uuid));
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
