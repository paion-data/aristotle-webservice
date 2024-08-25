package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUidcid(String uidcid);

    void createUser(UserCreateDTO user);

    void updateUser(UserUpdateDTO user);
}
