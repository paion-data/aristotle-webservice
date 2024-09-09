package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.service.Neo4jService;
import com.paiondata.aristotle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Neo4jService neo4jService;

    @GetMapping("/{uidcid}")
    public Result<User> getUser(@PathVariable @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK) String uidcid) {
        Optional<User> optionalUser = userService.getUserByUidcid(uidcid);
        return optionalUser.map(Result::ok).orElseGet(() -> Result.fail(Message.USER_NULL));
    }

    @GetMapping("/graph/{uidcid}")
    public Result<List<Map<String, Map<String, Object>>>> getGraphByUserUidcid(
            @PathVariable @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK) String uidcid) {
        List<Map<String, Map<String, Object>>> results = neo4jService.getGraphByUserUidcid(uidcid);
        return Result.ok(results);
    }

    @PostMapping
    public Result<String> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
        return Result.ok(Message.CREATE_SUCCESS);
    }

    @PutMapping
    public Result<String> updateUser(@RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        userService.updateUser(userUpdateDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    @DeleteMapping
    public Result<String> deleteUser(@RequestBody @NotEmpty(message = Message.UIDCID_MUST_NOT_BE_BLANK)
                                         List<String> uidcids) {
        userService.deleteUser(uidcids);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
