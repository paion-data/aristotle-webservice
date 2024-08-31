package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphExistsException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.GraphService;
import com.paiondata.aristotle.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GraphServiceImpl implements GraphService {

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GraphRepository graphNodeRepository;

    @Override
    public Optional<Graph> getGraphByTitle(String title) {
        Graph graph = graphRepository.getGraphByTitle(title);
        return Optional.ofNullable(graph);
    }

    @Override
    public Optional<Graph> getGraphByUuid(String Uuid) {
        Graph graph = graphRepository.getGraphByUuid(Uuid);
        return Optional.ofNullable(graph);
    }

    @Transactional
    @Override
    public void createGraph(GraphCreateDTO graphCreateDTO) {
        String title = graphCreateDTO.getTitle();
        String description = graphCreateDTO.getDescription();
        String uuid = UUID.fastUUID().toString(true);
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        if (graphRepository.checkGraphExists(title, description) == 0) {
            graphRepository.createGraph(title, description, uuid, now);
        } else {
            throw new GraphExistsException(Message.GRAPH_EXISTS);
        }
    }

    @Transactional
    @Override
    public void bindUserGraph(String uidcid, String uuid) {
        Optional<User> optionalUser = userService.getUserByUidcid(uidcid);
        Optional<Graph> optionalGraph = getGraphByUuid(uuid);
        String relationUuid = UUID.fastUUID().toString(true);

        if (!optionalUser.isPresent() || !optionalGraph.isPresent()) {
            if (!optionalUser.isPresent()) {
                throw new UserNullException(Message.USER_NULL);
            } else {
                throw new GraphNullException(Message.GRAPH_NULL);
            }
        }

        graphRepository.bindUsertoGraph(uidcid, uuid, relationUuid);
    }

    @Transactional
    @Override
    public void deleteByUuids(List<String> uuids) {
        long l = graphRepository.countByUuids(uuids);
        if (l != uuids.size()) {
            throw new GraphNullException(Message.GRAPH_NULL);
        }

        List<String> relatedGraphNodeUuids = getRelatedGraphNodeUuids(uuids);

        graphNodeRepository.deleteByUuids(relatedGraphNodeUuids);
        graphRepository.deleteByUuids(uuids);
    }

    @Transactional
    @Override
    public void updateGraph(GraphUpdateDTO graphUpdateDTO) {
        Optional<Graph> graphByUuid = getGraphByUuid(graphUpdateDTO.getUuid());

        if (graphByUuid.isPresent()) {
            graphRepository.updateGraphByUuid(graphUpdateDTO.getUuid(),
                    graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription());
        } else {
            throw new GraphNullException(Message.GRAPH_NULL);
        }
    }

    private List<String> getRelatedGraphNodeUuids(List<String> uuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(uuids);
    }
}
