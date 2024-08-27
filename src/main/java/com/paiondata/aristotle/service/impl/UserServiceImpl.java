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
    public Optional<User> getUserByElementId(String elementId) {
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

    @Transactional
    @Override
    public void deleteUser(List<String> elementIds) {
        long l = userRepository.countByElementIds(elementIds);
        if (l != elementIds.size()) {
            throw new UserNullException(Message.USER_NULL);
        }

        List<String> graphElementIds = getRelatedGraphElementIds(elementIds);
        List<String> graphNodeElementIds = getRelatedGraphNodeElementIds(graphElementIds);

        userRepository.deleteByElementIds(elementIds);
        graphRepository.deleteByElementIds(graphElementIds);
        graphNodeRepository.deleteByElementIds(graphNodeElementIds);
    }

    @Override
    public Optional<List<Graph>> getGraphByUserElementId(String elementId) {
        Optional<User> optionalUser = getUserByElementId(elementId);
        if (!optionalUser.isPresent()) {
            throw new UserNullException(Message.USER_NULL);
        }

        return Optional.ofNullable(userRepository.getGraphByUserId(elementId));
    }

    private List<String> getRelatedGraphElementIds(List<String> userElementIds) {
        return userRepository.getGraphElementIdsByUserId(userElementIds);
    }

    private List<String> getRelatedGraphNodeElementIds(List<String> userElementIds) {
        return graphRepository.getGraphNodeElementIdsByGraphElementIds(userElementIds);
    }
}
