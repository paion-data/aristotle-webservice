package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
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
    public Result<String> createComment(@RequestBody UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
        return Result.ok(Message.CREATE_SUCCESS);
    }

    @PutMapping
    public Result<String> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUser(userUpdateDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }
}
