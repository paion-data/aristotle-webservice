package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.repository.GraphNodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.GraphService;
import com.paiondata.aristotle.service.Neo4jService;
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
    private GraphNodeRepository graphNodeRepository;

    @Autowired
    private Neo4jService neo4jService;

    @Override
    public GraphVO getGraphVOByUuid(String uuid) {
        Graph graphByUuid = graphRepository.getGraphByUuid(uuid);

        if (graphByUuid == null) {
            throw new GraphNullException(Message.GRAPH_NULL);
        }

        return GraphVO.builder()
                .uuid(graphByUuid.getUuid())
                .title(graphByUuid.getTitle())
                .description(graphByUuid.getDescription())
                .createTime(graphByUuid.getCreateTime())
                .updateTime(graphByUuid.getUpdateTime())
                .nodes(neo4jService.getGraphNodeByGraphUuid(uuid))
                .build();
    }

    @Override
    public Optional<Graph> getGraphByUuid(String Uuid) {
        Graph graph = graphRepository.getGraphByUuid(Uuid);
        return Optional.ofNullable(graph);
    }

    @Transactional
    @Override
    public Graph createAndBindGraph(GraphCreateDTO graphCreateDTO) {
        String title = graphCreateDTO.getTitle();
        String description = graphCreateDTO.getDescription();
        String uidcid = graphCreateDTO.getUserUidcid();
        String graphUuid = UUID.fastUUID().toString(true);
        String relationUuid = UUID.fastUUID().toString(true);
        Date now = getCurrentTime();

        Optional<User> optionalUser = userService.getUserByUidcid(uidcid);
        if (optionalUser.isEmpty()) {
            throw new UserNullException(Message.USER_NULL);
        }

        return graphRepository.createAndBindGraph(title, description, uidcid, graphUuid, relationUuid, now);
    }

    @Transactional
    @Override
    public void deleteByUuids(List<String> uuids) {
        for (String uuid : uuids) {
            if (getGraphByUuid(uuid).isEmpty()) {
                throw new GraphNullException(Message.GRAPH_NULL);
            }
        }

        List<String> relatedGraphNodeUuids = getRelatedGraphNodeUuids(uuids);

        graphNodeRepository.deleteByUuids(relatedGraphNodeUuids);
        graphRepository.deleteByUuids(uuids);
    }

    @Transactional
    @Override
    public void updateGraph(GraphUpdateDTO graphUpdateDTO) {
        Optional<Graph> graphByUuid = getGraphByUuid(graphUpdateDTO.getUuid());
        Date now = getCurrentTime();

        if (graphByUuid.isPresent()) {
            graphRepository.updateGraphByUuid(graphUpdateDTO.getUuid(),
                    graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(),
                    now);
        } else {
            throw new GraphNullException(Message.GRAPH_NULL);
        }
    }

    private List<String> getRelatedGraphNodeUuids(List<String> uuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(uuids);
    }

    private Date getCurrentTime() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}
