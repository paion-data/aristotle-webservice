package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.common.exception.UserUidcidExistsException;
import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(String elementId) {
        User user = userRepository.getUserByElementId(elementId);
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUidcid(String uidcid) {
        User user = userRepository.getUserByUidcid(uidcid);
        return Optional.ofNullable(user);
    }

    @Transactional
    @Override
    public void createUser(UserCreateDTO user) {
        String uidcid = user.getUidcid();

        if (userRepository.checkUidcidExists(uidcid) == 0) {
            userRepository.createUser(uidcid, user.getNickName());
        } else {
            throw new UserUidcidExistsException(Message.UIDCID_EXISTS + uidcid);
        }
    }

    @Transactional
    @Override
    public void updateUser(UserUpdateDTO user) {
        String elementId = user.getElementId();

        if (userRepository.checkElementIdExists(elementId) != 0) {
            userRepository.updateUser(elementId, user.getNickName());
        } else {
            throw new UserNullException(Message.USER_NULL);
        }
    }
}
