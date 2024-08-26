package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.GraphExistsException;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.GraphService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GraphServiceImpl implements GraphService {

    @Autowired
    private GraphRepository graphRepository;

    @Override
    public Optional<Graph> getGraphByElementId(String elementId) {
        Graph graph = graphRepository.getGraphByElementId(elementId);
        return Optional.ofNullable(graph);
    }

    @Transactional
    @Override
    public void createGraph(GraphCreateDTO graphCreateDTO) {
        String title = graphCreateDTO.getTitle();
        String description = graphCreateDTO.getDescription();
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        if (graphRepository.checkGraphExists(title, description) == 0) {
            graphRepository.createGraph(title, description, now);
        } else {
            throw new GraphExistsException(Message.GRAPH_EXISTS);
        }
    }
}
