package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.model.dto.UserOnlyDTO;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createUser(UserOnlyDTO user) {

        if (userRepository.checkUidcidExists(user.getUidcid()) == 0) {
            userRepository.createUser(user.getUidcid(), user.getNickName());
        } else {
            throw new IllegalArgumentException("UIDCID already exists: " + user.getUidcid());
        }
    }
}
