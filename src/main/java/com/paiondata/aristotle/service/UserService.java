package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserVO getUserVOByUidcid(String uidcid);

    Optional<User> getUserByUidcid(String uidcid);

    List<UserVO> getAllUsers();

    void createUser(UserCreateDTO user);

    void updateUser(UserUpdateDTO user);

    void deleteUser(List<String> uidcids);
}
