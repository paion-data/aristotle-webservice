package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.UserOnlyDTO;
import com.paiondata.aristotle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Result<String> createComment(@RequestBody UserOnlyDTO userOnlyDTO) {
        userService.createUser(userOnlyDTO);
        return Result.ok("Created successfully!");
    }
}
