package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.common.exception.UserUidcidExistsException;
import com.paiondata.aristotle.model.dto.UserCreateDTO;
import com.paiondata.aristotle.model.dto.UserUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphNodeRepository graphNodeRepository;

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

        try {
            userRepository.createUser(uidcid, user.getNickName());
        } catch (DataIntegrityViolationException e) {
            throw new UserUidcidExistsException(Message.UIDCID_EXISTS + uidcid);
        }
    }

    @Transactional
    @Override
    public void updateUser(UserUpdateDTO user) {
        String uidcid = user.getUidcid();

        if (userRepository.checkUidcidExists(uidcid) != 0) {
            userRepository.updateUser(uidcid, user.getNickName());
        } else {
            throw new UserNullException(Message.USER_NULL);
        }
    }

    @Transactional
    @Override
    public void deleteUser(List<String> uidcids) {
        long l = userRepository.countByUidcids(uidcids);
        if (l != uidcids.size()) {
            throw new UserNullException(Message.USER_NULL);
        }

        List<String> graphUUIDs = getRelatedGraphUUIDs(uidcids);
        List<String> graphNodeUUIDs = getRelatedGraphNodeUUIDs(graphUUIDs);

        userRepository.deleteByUidcids((uidcids));
        graphRepository.deleteByUuids(graphUUIDs);
        graphNodeRepository.deleteByUUIDs(graphNodeUUIDs);
    }

    @Override
    public Optional<List<Graph>> getGraphByUserUidcid(String uidcid) {
        Optional<User> optionalUser = getUserByUidcid(uidcid);
        if (!optionalUser.isPresent()) {
            throw new UserNullException(Message.USER_NULL);
        }

        return Optional.ofNullable(userRepository.getGraphByUserUidcid(uidcid));
    }

    private List<String> getRelatedGraphUUIDs(List<String> userUidcids) {
        return userRepository.getGraphUUIDsByUserUidcid(userUidcids);
    }

    private List<String> getRelatedGraphNodeUUIDs(List<String> graphUUIDs) {
        return graphRepository.getGraphNodeUUIDsByGraphUuids(graphUUIDs);
    }
}
