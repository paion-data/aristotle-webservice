package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.model.entity.Relation;
import com.paiondata.aristotle.repository.RelationRepository;
import com.paiondata.aristotle.service.RelationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RelationServiceImpl implements RelationService {

    @Autowired
    private RelationRepository relationRepository;

    @Override
    public Optional<List<Relation>> getRelationByUuid(String uuid) {
        List<Relation> relation = relationRepository.getRelationByUuid(uuid);
        return Optional.ofNullable(relation);
    }
}
