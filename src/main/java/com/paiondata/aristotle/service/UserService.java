package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.UserOnlyDTO;
import com.paiondata.aristotle.model.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUidcid(String uidcid);

    void createUser(UserOnlyDTO user);
}
