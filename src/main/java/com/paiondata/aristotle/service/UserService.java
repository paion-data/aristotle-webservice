package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.dto.UserOnlyDTO;
import com.paiondata.aristotle.model.entity.User;

public interface UserService {

    void createUser(UserOnlyDTO user);
}
