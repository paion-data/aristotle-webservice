package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.Relation;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.RelationRepository;
import com.paiondata.aristotle.service.GraphNodeService;
import com.paiondata.aristotle.service.GraphService;
import com.paiondata.aristotle.service.RelationService;
import com.paiondata.aristotle.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RelationServiceImpl implements RelationService {

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GraphService graphService;

    @Autowired
    private GraphNodeService graphNodeService;

    @Override
    public void bindUserGraph(String elementId1, String elementId2, String relationName) {
        Optional<User> optionalUser = userService.getUserByElementId(elementId1);
        Optional<Graph> optionalGraph = graphService.getGraphByElementId(elementId2);

        if (!optionalUser.isPresent() || !optionalGraph.isPresent()) {
            if (!optionalUser.isPresent()) {
                throw new UserNullException(Message.USER_NULL);
            } else {
                throw new GraphNullException(Message.GRAPH_NULL);
            }
        }

        User start = optionalUser.get();
        Graph end = optionalGraph.get();

        Relation relation =new Relation();
        relation.setStartNode(start);
        relation.setEndNode(end);
        relation.setRelation(relationName);

        relationRepository.save(relation);
    }
}
