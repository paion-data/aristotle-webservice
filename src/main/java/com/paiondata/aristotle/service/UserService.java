package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getUserByElementId(String elementId);

    Optional<User> getUserByUidcid(String uidcid);

    void createUser(UserCreateDTO user);

    void updateUser(UserUpdateDTO user);

    void deleteUser(List<String> elementIds);

    Optional<List<Graph>> getGraphByUserElementId(String elementId);
}
