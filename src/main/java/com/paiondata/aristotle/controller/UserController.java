package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.UserOnlyDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{uidcid}")
    public Result<User> getUser(@PathVariable String uidcid) {

        Optional<User> optionalUser = userService.getUserByUidcid(uidcid);

        return optionalUser.map(Result::ok).orElseGet(() -> Result.fail(Message.USER_NULL));
    }

    @PostMapping
    public Result<String> createComment(@RequestBody UserOnlyDTO userOnlyDTO) {
        userService.createUser(userOnlyDTO);
        return Result.ok("Created successfully!");
    }
}
