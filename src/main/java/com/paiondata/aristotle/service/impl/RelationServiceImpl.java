package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.model.entity.Relation;
import com.paiondata.aristotle.repository.RelationRepository;
import com.paiondata.aristotle.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelationServiceImpl implements RelationService {

    @Autowired
    private RelationRepository relationRepository;

    @Override
    public Optional<Relation> getRelationByUuid(String uuid) {
        Relation relation = relationRepository.getRelationByUuid(uuid);
        return Optional.ofNullable(relation);
    }
}
